package com.springvuegradle.team6.controllers;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.springvuegradle.team6.models.*;
import com.springvuegradle.team6.requests.DeleteSubscriptionRequest;
import com.springvuegradle.team6.requests.EditSubscriptionRequest;
import com.springvuegradle.team6.requests.SubscriptionRequest;
import com.springvuegradle.team6.security.UserSecurityService;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;

@CrossOrigin(
        origins = "http://localhost:9500",
        allowCredentials = "true",
        allowedHeaders = "://",
        methods = {
                RequestMethod.GET,
                RequestMethod.POST,
                RequestMethod.DELETE
        })
@RestController
@RequestMapping("")
public class FollowController {
    private ProfileRepository profileRepository;
    private ActivityRepository activityRepository;
    private ActivityRoleRepository activityRoleRepository;
    private SubscriptionHistoryRepository subscriptionHistoryRepository;
    private EmailRepository emailRepository;

    /**
     * Constructor for FollowController class which gets the profile, email and role repository
     *
     * @param profileRepository             the profile repository
     * @param activityRepository            the activity repository
     * @param subscriptionHistoryRepository the subscriptionHistory repository
     */
    FollowController(ProfileRepository profileRepository,
                     ActivityRepository activityRepository,
                     ActivityRoleRepository activityRoleRepository,
                     SubscriptionHistoryRepository subscriptionHistoryRepository,
                     EmailRepository emailRepository) {
        this.profileRepository = profileRepository;
        this.activityRepository = activityRepository;
        this.activityRoleRepository = activityRoleRepository;
        this.subscriptionHistoryRepository = subscriptionHistoryRepository;
        this.emailRepository = emailRepository;
    }

    /**
     * Creates new subscriptionHistory between user and activity
     *
     * @param profileId  id of the user subscribing
     * @param activityId id of the activity being subscribed to
     * @param session current http session
     * @return Response entity if successful will be ok (2xx) or (4xx) if unsuccessful
     */
    @PostMapping("profiles/{profileId}/subscriptions/activities/{activityId}")
    public ResponseEntity<String> followActivity(@PathVariable int profileId,
                                                 @PathVariable int activityId,
                                                 HttpSession session) {
        // Check Authorisation
        ResponseEntity<String> authorisedResponse =
                UserSecurityService.checkAuthorised(profileId, session, profileRepository);
        if (authorisedResponse != null) {
            return authorisedResponse;
        }

        Profile profile = profileRepository.findById(profileId);
        if (profile == null) {
            return new ResponseEntity("No such user", HttpStatus.NOT_FOUND);
        }
        Optional<Activity> activity = activityRepository.findById(activityId);
        if (activity.isEmpty()) {
            return new ResponseEntity("No such activity", HttpStatus.NOT_FOUND);
        }

        // Check if already subbed
        if (!subscriptionHistoryRepository.findActive(activityId, profileId).isEmpty()) {
            return new ResponseEntity<>("User already follows this activity", HttpStatus.BAD_REQUEST);
        }

        Activity subTo = activity.get();

        // Create subscriptionHistory object
        SubscriptionHistory subscriptionHistory = new SubscriptionHistory(profile, subTo);

        subscriptionHistoryRepository.save(subscriptionHistory);

        return ResponseEntity.ok("User is now subscribed");
    }

    /**
     * Checks if the user is subscribed to the activity
     *
     * @param profileId  id of the user that is unsubscribing
     * @param activityId id of the activity being unsubscribed from
     * @param session    current http session
     * @return Boolean true if user is subscribed, false if the user is unsubscribed
     */
    @GetMapping("profiles/{profileId}/subscriptions/activities/{activityId}")
    public ResponseEntity<String> getFollowingActivity(@PathVariable int profileId,
                                                       @PathVariable int activityId,
                                                       HttpSession session) {
        Profile profile = profileRepository.findById(profileId);
        if (profile == null) {
            return new ResponseEntity("No such user", HttpStatus.NOT_FOUND);
        }
        Optional<Activity> activity = activityRepository.findById(activityId);
        if (activity.isEmpty()) {
            return new ResponseEntity("No such activity", HttpStatus.NOT_FOUND);
        }
        boolean subscribed;
        // Check if already subbed
        subscribed = !subscriptionHistoryRepository.findActive(activityId, profileId).isEmpty();
        JSONObject obj = new JSONObject();
        obj.appendField("subscribed", subscribed);
        return new ResponseEntity(obj, HttpStatus.OK);
    }

    /**
     * Unsubscribes user from activity
     *
     * @param profileId  id of the user that is unsubscribing
     * @param activityId id of the activity being unsubscribed from
     * @param session    current http session
     * @return Response entity if successfull will be ok (2xx) or (4xx) if unsuccessful
     */
    @DeleteMapping("profiles/{profileId}/subscriptions/activities/{activityId}")
    public ResponseEntity<String> unfollowActivity(@PathVariable int profileId,
                                                   @PathVariable int activityId,
                                                   HttpSession session) {
        // Check Authorisation
        ResponseEntity<String> authorisedResponse =
                UserSecurityService.checkAuthorised(profileId, session, profileRepository);
        if (authorisedResponse != null) {
            return authorisedResponse;
        }

        Profile profile = profileRepository.findById(profileId);
        if (profile == null) {
            return new ResponseEntity("No such user", HttpStatus.NOT_FOUND);
        }
        Optional<Activity> activity = activityRepository.findById(activityId);
        if (activity.isEmpty()) {
            return new ResponseEntity("No such activity", HttpStatus.NOT_FOUND);
        }

        if (activity.get().getProfile().getId() == profile.getId()) {
            return new ResponseEntity("Cannot unfollow an activity you created", HttpStatus.BAD_REQUEST);
        }

        // Gets the active subscription history object for this user and activity if one exists
        List<SubscriptionHistory> activeSubscriptions = subscriptionHistoryRepository.findActive(activityId, profileId);

        // Check if already subbed
        if (activeSubscriptions.isEmpty()) {
            return new ResponseEntity<>("User does not follow this activity", HttpStatus.BAD_REQUEST);
        }

        SubscriptionHistory activeSubscription = activeSubscriptions.get(0);
        activeSubscription.setEndDateTime(LocalDateTime.now());

        subscriptionHistoryRepository.save(activeSubscription);

        return ResponseEntity.ok("User unsubscribed from activity");
    }

    /**
     * Endpoint for creator of the activity to set activity roles for users.
     * @param profileId id of creator of activity
     * @param activityId if of activity
     * @param editSubscriptionRequest information to edit subscription
     * @param session the session of the active user
     * @return response entity, 200 if successful.
     */
    @PutMapping("/profiles/{profileId}/activities/{activityId}/subscriber")
    public ResponseEntity editSubscription(
            @PathVariable int profileId,
            @PathVariable int activityId,
            @RequestBody @Valid EditSubscriptionRequest editSubscriptionRequest,
            HttpSession session) {
        SubscriptionRequest request = editSubscriptionRequest.getSubscription();

        ResponseEntity<String> authorisedResponse =
                UserSecurityService.checkAuthorised(profileId, session, profileRepository);
        if (authorisedResponse != null) {
            return authorisedResponse;
        }

        Profile creatorProfile = profileRepository.findById(profileId);
        if (creatorProfile == null) {
            return new ResponseEntity("No such user", HttpStatus.NOT_FOUND);
        }
        Optional<Activity> activityOptional = activityRepository.findById(activityId);
        if (activityOptional.isEmpty()) {
            return new ResponseEntity("No such activity", HttpStatus.NOT_FOUND);
        }

        Activity activity = activityOptional.get();
        if (!activity.getProfile().getId().equals(profileId)) {
            return new ResponseEntity<>(
                    "You are not the author of this activity", HttpStatus.UNAUTHORIZED);
        }
        Optional<Email> optionalEmail = emailRepository.findByAddress(request.getEmail());
        if (optionalEmail.isEmpty()) {
            return new ResponseEntity("No such email", HttpStatus.NOT_FOUND);
        }

        Profile roleProfile = profileRepository.findByEmailsContains(optionalEmail.get());
        if (roleProfile == null) {
            return new ResponseEntity("No such user", HttpStatus.NOT_FOUND);
        }

        ActivityRole activityRoleFound = activityRoleRepository.findByProfile_IdAndActivity_Id(roleProfile.getId(), activityId);
        String toCamelCase = request.getRole().substring(0, 1).toUpperCase() + request.getRole().substring(1);
        ActivityRoleType activityRoleType = ActivityRoleType.valueOf(toCamelCase);
        if (activityRoleFound == null) {
            // Create activity role
            ActivityRole activityRole = new ActivityRole();
            activityRole.setProfile(roleProfile);
            activityRole.setActivity(activity);
            activityRole.setActivityRoleType(activityRoleType);
            activityRoleRepository.save(activityRole);
            // Create Subscription history row
            List<SubscriptionHistory> optionalSubscription = subscriptionHistoryRepository.findActive(activityId, profileId);
             if (optionalSubscription.isEmpty()) {
                 SubscriptionHistory subscriptionHistory = new SubscriptionHistory(roleProfile, activity);
                 subscriptionHistoryRepository.save(subscriptionHistory);
                 return new ResponseEntity("Activity role created and user is now subscribed", HttpStatus.OK);
             } else {
                 return new ResponseEntity("Activity role created", HttpStatus.OK);
             }
        } else {
            activityRoleFound.setActivityRoleType(activityRoleType);
            activityRoleRepository.save(activityRoleFound);
        }

        return new ResponseEntity("Activity role updated", HttpStatus.OK);
    }

    /**
     * To delete the activity role of the email in the request from the activity
     * @param profileId the creator of the activity
     * @param activityId the id of the activity
     * @param request the request with the email to delete
     * @param session the session of the active user
     * @return the response, 200 if successfully deleted.
     */
    @DeleteMapping("/profiles/{profileId}/activities/{activityId}/subscriber")
    public ResponseEntity deleteSubscription(
            @PathVariable int profileId,
            @PathVariable int activityId,
            @RequestBody @Valid DeleteSubscriptionRequest request,
            HttpSession session) {
        ResponseEntity<String> authorisedResponse =
                UserSecurityService.checkAuthorised(profileId, session, profileRepository);
        if (authorisedResponse != null) {
            return authorisedResponse;
        }

        Profile creatorProfile = profileRepository.findById(profileId);
        if (creatorProfile == null) {
            return new ResponseEntity("No such user", HttpStatus.NOT_FOUND);
        }
        Optional<Activity> activityOptional = activityRepository.findById(activityId);
        if (activityOptional.isEmpty()) {
            return new ResponseEntity("No such activity", HttpStatus.NOT_FOUND);
        }

        Activity activity = activityOptional.get();
        if (!activity.getProfile().getId().equals(profileId)) {
            return new ResponseEntity<>(
                    "You are not the author of this activity", HttpStatus.UNAUTHORIZED);
        }
        Optional<Email> optionalEmail = emailRepository.findByAddress(request.getEmail());
        if (optionalEmail.isEmpty()) {
            return new ResponseEntity("No such email", HttpStatus.NOT_FOUND);
        }

        Profile roleProfile = profileRepository.findByEmailsContains(optionalEmail.get());
        if (roleProfile == null) {
            return new ResponseEntity("No such user", HttpStatus.NOT_FOUND);
        }

        ActivityRole activityRoleFound = activityRoleRepository.findByProfile_IdAndActivity_Id(roleProfile.getId(), activityId);

        if (activityRoleFound == null) {
            return new ResponseEntity("User is not subscribed", HttpStatus.NOT_FOUND);
        } else {
            activityRoleRepository.delete(activityRoleFound);

            List<SubscriptionHistory> activeSubscriptions = subscriptionHistoryRepository.findActive(activityId, roleProfile.getId());

            // Check if already subbed
            if (activeSubscriptions.isEmpty()) {
                return new ResponseEntity<>("User does not follow this activity", HttpStatus.BAD_REQUEST);
            }

            SubscriptionHistory activeSubscription = activeSubscriptions.get(0);
            activeSubscription.setEndDateTime(LocalDateTime.now());

            subscriptionHistoryRepository.save(activeSubscription);
        }

        return new ResponseEntity("Activity role deleted", HttpStatus.OK);
    }

    @GetMapping("/profiles/{profileId}/activities/{activityId}/subscriber")
    public ResponseEntity getSubscription(
            @PathVariable int profileId,
            @PathVariable int activityId,
            @RequestBody @Valid DeleteSubscriptionRequest request,
            HttpSession session) {
        ResponseEntity<String> authorisedResponse =
                UserSecurityService.checkAuthorised(profileId, session, profileRepository);
        if (authorisedResponse != null) {
            return authorisedResponse;
        }

        Profile creatorProfile = profileRepository.findById(profileId);
        if (creatorProfile == null) {
            return new ResponseEntity("No such user", HttpStatus.NOT_FOUND);
        }
        Optional<Activity> activityOptional = activityRepository.findById(activityId);
        if (activityOptional.isEmpty()) {
            return new ResponseEntity("No such activity", HttpStatus.NOT_FOUND);
        }

        Activity activity = activityOptional.get();
        if (!activity.getProfile().getId().equals(profileId)) {
            return new ResponseEntity<>(
                    "You are not the author of this activity", HttpStatus.UNAUTHORIZED);
        }

        Optional<Email> optionalEmail = emailRepository.findByAddress(request.getEmail());
        if (optionalEmail.isEmpty()) {
            return new ResponseEntity("No such email", HttpStatus.NOT_FOUND);
        }

        Profile roleProfile = profileRepository.findByEmailsContains(optionalEmail.get());
        if (roleProfile == null) {
            return new ResponseEntity("No such user", HttpStatus.NOT_FOUND);
        }

        ActivityRole activityRole = activityRoleRepository.findByProfile_IdAndActivity_Id(roleProfile.getId(), activityId);
        if (activityRole == null) {
            return new ResponseEntity("User is not subscribed", HttpStatus.NOT_FOUND);
        }

        JSONObject obj = new JSONObject();
        obj.appendField("role", activityRole.getRole());
        return new ResponseEntity(obj, HttpStatus.OK);
    }


    /**
     * Retrieves all users accosiated with the activity and their role
     *
     * @param activityId id of the activity
     * @param session    current http session
     * @return Lists with users associated to a specific role
     */
    @GetMapping("activities/{activityId}/members")
    public ResponseEntity<String> getConnectedUsers(@PathVariable int activityId,
                                                    @RequestParam(name = "offset", required = false) Integer offset,
                                                    @RequestParam(name = "limit", required = false) Integer limit,
                                                    @RequestParam(name = "type", required = false) String type,
                                                    HttpSession session) {
        if (offset == null) {
            offset = 0;
        }
        if (limit == null) {
            limit = 10;
        }
        List<String> acceptedTypes = Arrays.asList("organiser", "participant", "accessor", "follower", "creator");
        if (type != null && !acceptedTypes.contains(type)) {
            return new ResponseEntity("Invalid member type", HttpStatus.BAD_REQUEST);
        }

        Optional<Activity> activity = activityRepository.findById(activityId);
        if (activity.isEmpty()) {
            return new ResponseEntity("No such activity", HttpStatus.NOT_FOUND);
        }
        JSONObject response = new JSONObject();

        if (type == null || type.equals("organiser")) {
            response.appendField("Organiser",activityRoleRepository.findMembers(activityId, ActivityRoleType.Organiser.ordinal(), limit, offset));
        }
        if (type == null || type.equals("participant")) {
            response.appendField("Participant",activityRoleRepository.findMembers(activityId, ActivityRoleType.Participant.ordinal(), limit, offset));
        }
        if (type == null || type.equals("accessor")) {
            response.appendField("Access",activityRoleRepository.findMembers(activityId, ActivityRoleType.Access.ordinal(), limit, offset));
        }
        if (type == null || type.equals("follower")) {
            response.appendField("Follower",activityRoleRepository.findMembers(activityId, ActivityRoleType.Follower.ordinal(), limit, offset));
        }
        if (type == null || type.equals("creator")) {
            response.appendField("Creator",activityRoleRepository.findMembers(activityId, ActivityRoleType.Creator.ordinal(), limit, offset));
        }
        return new ResponseEntity(response, HttpStatus.OK);
    }

    /**
     * Retrieves count of all types of members accociated with an activity
     *
     * @param activityId id of the activity
     * @param session current http session
     * @return Count of users accociated with on an activity
     */
    @GetMapping("activities/{activityId}/membercount")
    public ResponseEntity<String> getConnectedUsersCount(@PathVariable int activityId,
                                                    HttpSession session) {

        Optional<Activity> activity = activityRepository.findById(activityId);
        if (activity.isEmpty()) {
            return new ResponseEntity("No such activity", HttpStatus.NOT_FOUND);
        }
        JSONObject response = new JSONObject();
        response.appendField("participants", activityRoleRepository.findMembersCount(activityId, ActivityRoleType.Participant.ordinal()));
        response.appendField("creators", activityRoleRepository.findMembersCount(activityId, ActivityRoleType.Creator.ordinal()));
        response.appendField("organisers", activityRoleRepository.findMembersCount(activityId, ActivityRoleType.Organiser.ordinal()));
        response.appendField("followers", activityRoleRepository.findMembersCount(activityId, ActivityRoleType.Follower.ordinal()));
        response.appendField("accessors", activityRoleRepository.findMembersCount(activityId, ActivityRoleType.Access.ordinal()));
        return new ResponseEntity(response, HttpStatus.OK);
    }
}
