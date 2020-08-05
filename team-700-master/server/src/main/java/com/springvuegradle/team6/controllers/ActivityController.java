package com.springvuegradle.team6.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.team6.models.*;
import com.springvuegradle.team6.models.location.NamedLocation;
import com.springvuegradle.team6.models.location.NamedLocationRepository;
import com.springvuegradle.team6.requests.*;
import com.springvuegradle.team6.security.UserSecurityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@CrossOrigin(
    origins = {
      "http://localhost:9000",
      "http://localhost:9500",
      "https://csse-s302g7.canterbury.ac.nz/test",
      "https://csse-s302g7.canterbury.ac.nz/prod"
    },
    allowCredentials = "true",
    allowedHeaders = "://",
    methods = {
      RequestMethod.GET,
      RequestMethod.POST,
      RequestMethod.DELETE,
      RequestMethod.PUT,
      RequestMethod.PATCH
    })
@RestController
@RequestMapping("")
public class ActivityController {
  private final ProfileRepository profileRepository;
  private final ActivityRepository activityRepository;
  private final ActivityRoleRepository activityRoleRepository;
  private final NamedLocationRepository locationRepository;
  private final TagRepository tagRepository;
  private final SubscriptionHistoryRepository subscriptionHistoryRepository;
  private final ActivityHistoryRepository activityHistoryRepository;

  ActivityController(
      ProfileRepository profileRepository,
      ActivityRepository activityRepository,
      ActivityRoleRepository activityRoleRepository,
      NamedLocationRepository locationRepository,
      TagRepository tagRepository,
      SubscriptionHistoryRepository subscriptionHistoryRepository,
      ActivityHistoryRepository activityHistoryRepository) {
    this.profileRepository = profileRepository;
    this.activityRepository = activityRepository;
    this.activityRoleRepository = activityRoleRepository;
    this.locationRepository = locationRepository;
    this.tagRepository = tagRepository;
    this.subscriptionHistoryRepository = subscriptionHistoryRepository;
    this.activityHistoryRepository = activityHistoryRepository;
  }

  /**
   * This method is required to enable direct field access for activity controller, due to a weird
   * bug where the fields of LocationUpdateRequest not being able to be accessed even with getters.
   *
   * @param dataBinder
   */
  @InitBinder
  private void activateDirectFieldAccess(DataBinder dataBinder) {
    dataBinder.initDirectFieldAccess();
  }

  /**
   * Check if user is authorised to edit activity.
   *
   * @param activity activity to be edited
   * @param session http session
   * @return ResponseEntity or null
   */
  private ResponseEntity<String> checkAuthorisedToEditActivity(
      Activity activity, HttpSession session) {
    Object id = session.getAttribute("id");

    if (id == null) {
      return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
    }

    Collection<SimpleGrantedAuthority> userRoles =
        (Collection<SimpleGrantedAuthority>)
            SecurityContextHolder.getContext().getAuthentication().getAuthorities();
    boolean isAdmin =
        userRoles.stream()
            .anyMatch(
                simpleGrantedAuthority ->
                    (simpleGrantedAuthority.getAuthority().equals("ROLE_ADMIN")
                        || simpleGrantedAuthority.getAuthority().equals("ROLE_USER_ADMIN")));
    if (!(id.toString().equals(activity.getProfile().getId().toString())) && !isAdmin) {
      return new ResponseEntity<>("You can only edit your own activity", HttpStatus.UNAUTHORIZED);
    }

    if (!activityRepository.existsById(activity.getId())) {
      return new ResponseEntity<>("No such activity", HttpStatus.NOT_FOUND);
    }
    return null;
  }

  /**
   * Update/replace list of user activity types
   *
   * @param profileId user id to be updated
   * @param request EditActivityTypeRequest
   * @param session HTTPSession
   * @return unauthorised response or 201/400 HttpStatus Response
   */
  @PutMapping("/profiles/{profileId}/activity-types")
  public ResponseEntity<String> updateActivityTypes(
      @PathVariable Integer profileId,
      @Valid @RequestBody EditActivityTypeRequest request,
      HttpSession session) {
    Optional<Profile> profile = profileRepository.findById(profileId);
    if (profile.isPresent()) {
      Profile user = profile.get();

      ResponseEntity<String> authorisedResponse =
          UserSecurityService.checkAuthorised(profileId, session, profileRepository);
      if (authorisedResponse != null) {
        return authorisedResponse;
      }

      user.setActivityTypes(request.getActivities());
      profileRepository.save(user);
    }
    return ResponseEntity.ok("User activity types updated");
  }
  /**
   * Get all activity types
   *
   * @return 200 response with headers
   */
  @GetMapping("/profiles/activity-types")
  public ResponseEntity getActivityTypes() {
    List<String> activityList = new ArrayList();
    for (ActivityType myVar : ActivityType.values()) {
      activityList.add(myVar.toString());
    }
    return ResponseEntity.ok(activityList);
  }

  /**
   * Helper function used by createActivity to check if the date/time provided for start and end
   * date are valid
   *
   * @param activity The activity that the user wants to create or update
   * @return null if there are no errors, otherwise return ResponseEntity with error message and
   *     status code
   */
  public ResponseEntity<String> checkCreateActivityDateTime(Activity activity) {
    if (!activity.isContinuous()) {
      if (activity.getStartTime() == null) {
        return new ResponseEntity<>(
            "Start date/time cannot be empty for a non continuous activity",
            HttpStatus.BAD_REQUEST);
      }
      if (activity.getEndTime() == null) {
        return new ResponseEntity<>(
            "End date/time cannot be empty for a non continuous activity", HttpStatus.BAD_REQUEST);
      }

      // Stand and End date/time validations
      LocalDateTime startDateTime;
      LocalDateTime endDateTime;
      try {
        startDateTime =
            LocalDateTime.parse(
                activity.getStartTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"));
        if (startDateTime.isBefore(LocalDateTime.now())) {
          return new ResponseEntity(
              "Start date/time cannot be before the current time", HttpStatus.BAD_REQUEST);
        }
      } catch (DateTimeParseException e) {
        System.out.println(e);
        return new ResponseEntity(
            "Start date/time must be in correct format of yyyy-MM-dd'T'HH:mm:ssZ",
            HttpStatus.BAD_REQUEST);
      }

      try {
        endDateTime =
            LocalDateTime.parse(
                activity.getEndTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"));
        if (endDateTime.isBefore(LocalDateTime.now())) {
          return new ResponseEntity(
              "End date/time cannot be before the current time", HttpStatus.BAD_REQUEST);
        }
      } catch (DateTimeParseException e) {
        return new ResponseEntity(
            "End date/time must be in correct format of yyyy-MM-dd'T'HH:mm:ssZ",
            HttpStatus.BAD_REQUEST);
      }

      if (startDateTime.isAfter(endDateTime)) {
        return new ResponseEntity(
            "Start date/time cannot be after End date/time", HttpStatus.BAD_REQUEST);
      }
    }
    return null;
  }

  /**
   * Helper function used by editActivity to check if the date/time provided for start and end date
   * are valid
   *
   * @param activity The activity that the user wants to create or update
   * @return null if there are no errors, otherwise return ResponseEntity with error message and
   *     status code
   */
  public ResponseEntity<String> checkEditActivityDateTime(EditActivityRequest request, Activity activity) {
    if (!request.continuous) {
      if (request.startTime == null) {
        return new ResponseEntity<>(
            "Start date/time cannot be empty for a non continuous activity",
            HttpStatus.BAD_REQUEST);
      }
      if (request.endTime == null) {
        return new ResponseEntity<>(
            "End date/time cannot be empty for a non continuous activity", HttpStatus.BAD_REQUEST);
      }

      // Start and End date/time validations
      LocalDateTime newStartDateTime;
      LocalDateTime oldStartDateTime;
      LocalDateTime endDateTime;

      // Checks if new start time is before now.
      try {
        newStartDateTime =
            LocalDateTime.parse(
                request.startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"));
        oldStartDateTime =
            LocalDateTime.parse(
                activity.getStartTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"));
        // If the activity has already begun but the author changes the activity name, then it
        // needs to make sure the to accept the old start time which is before now
        if (newStartDateTime.isBefore(LocalDateTime.now())
            && !newStartDateTime.isEqual(oldStartDateTime)) {
          return new ResponseEntity(
              "Start date/time cannot be before the current time", HttpStatus.BAD_REQUEST);
        }
      } catch (DateTimeParseException e) {
        return new ResponseEntity(
            "Start date/time must be in correct format of yyyy-MM-dd'T'HH:mm:ssZ",
            HttpStatus.BAD_REQUEST);
      }

      // Checks if end time is before now.
      try {
        endDateTime =
            LocalDateTime.parse(
                request.endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"));
        if (endDateTime.isBefore(LocalDateTime.now())) {
          return new ResponseEntity(
              "End date/time cannot be before the current time", HttpStatus.BAD_REQUEST);
        }
      } catch (DateTimeParseException e) {
        return new ResponseEntity(
            "End date/time must be in correct format of yyyy-MM-dd'T'HH:mm:ssZ",
            HttpStatus.BAD_REQUEST);
      }

      if (!(newStartDateTime.isEqual(endDateTime)) && newStartDateTime.isAfter(endDateTime)) {
        // Checks if start time is after end time.
        return new ResponseEntity(
            "Start date/time cannot be after End date/time", HttpStatus.BAD_REQUEST);
      }
    }
    return null;
  }

  /**
   * Helper function to check if tags associated to the activity is not more that 30 and check if
   * each tag only contains alphanumeric characters
   *
   * @param hashtags set of hashtags associated to the activity
   * @return return ResponseEntity with personalised error message if error is found, otherwise null
   */
  private ResponseEntity<String> checkAllTagsValidity(Set<Tag> hashtags) {
    if (hashtags != null) {
      if (hashtags.size() > 30) {
        return new ResponseEntity<>(
            "More than 30 hashtags per activity is not allowed", HttpStatus.BAD_REQUEST);
      }
      for (Tag tag : hashtags) {
        formatHashtagToStoreInDb(tag);

        // check if tag name only contains alphanumeric characters and underscores
        if (!tag.getName().matches("^[a-zA-Z0-9_]*$")) {
          return new ResponseEntity<>(
              "Tag name "
                  + tag.getName()
                  + " contains characters other than alphanumeric characters and underscores",
              HttpStatus.BAD_REQUEST);
        }
      }
    }

    return null;
  }

  /**
   * Helper function to convert hashtag to lower case and remove '#'
   *
   * @param hashtag
   */
  private void formatHashtagToStoreInDb(Tag hashtag) {
    hashtag.setName(hashtag.getName().toLowerCase().substring(1));
  }


  /**
   * Put request to edit the hashtags associated to the activity specified in the path
   *
   * @param profileId  author of the activity
   * @param activityId activity id
   * @param request    EditActivityHashtagRequest
   * @param session    HttpSession
   * @return The response code and message
   */
  @PutMapping("/profiles/{profileId}/activities/{activityId}/hashtags")
  public ResponseEntity<String> editActivityHashTag(@PathVariable Integer profileId,
                                                    @PathVariable Integer activityId,
                                                    @Valid @RequestBody EditActivityHashtagRequest request,
                                                    HttpSession session) {
    Optional<Activity> optionalActivity = activityRepository.findById(activityId);

    if (optionalActivity.isPresent()) {
      Activity activity = optionalActivity.get();


      // Check if authorised
      ResponseEntity<String> authorisedResponse =
              UserSecurityService.checkAuthorised(profileId, session, profileRepository);
      if (authorisedResponse != null) {
        return authorisedResponse;
      }

      ResponseEntity<String> activityAuthorizedResponse =
              this.checkAuthorisedToEditActivity(activity, session);
      if (activityAuthorizedResponse != null) {
        return activityAuthorizedResponse;
      }

      // Check hashtags
      ResponseEntity<String> checkHashtagsValidityResponse = checkAllTagsValidity(request.getHashtags());
      if (checkHashtagsValidityResponse != null) {
        return checkHashtagsValidityResponse;
      }

      Set<Tag> hashtags = new HashSet<>();
      if (request.getHashtags() != null) {
        for (Tag tag : request.getHashtags()) {
          Tag dbTag = tagRepository.findByName(tag.getName());
          if (dbTag == null) {
            tag = tagRepository.save(tag);
            hashtags.add(tag);
          } else {
            hashtags.add(dbTag);
          }
        }
        activity.setTags(hashtags);
      }
      activityRepository.save(activity);

      return ResponseEntity.ok("Hashtags of Activity '" + activity.getActivityName() + "' were updated.");
    } else {
      return new ResponseEntity<>("Activity does not exist", HttpStatus.NOT_FOUND);
    }
  }


  /**
   * Post Request to create an activity for the given profile based on the request
   *
   * @param profileId The id of the profile where the activity is created for
   * @param request   The request with values to create the activity
   * @param session   The session of the currently logged in user
   * @return The response code and message
   */
  @PostMapping("/profiles/{profileId}/activities")
  public ResponseEntity<String> createActivity(
      @PathVariable Integer profileId,
      @Valid @RequestBody CreateActivityRequest request,
      HttpSession session) {
    ResponseEntity<String> checkAuthorisedResponse =
        UserSecurityService.checkAuthorised(profileId, session, profileRepository);
    if (checkAuthorisedResponse != null) {
      return checkAuthorisedResponse;
    }
    Optional<Profile> optionalProfile = profileRepository.findById(profileId);
    if (optionalProfile.isEmpty()) {
      return new ResponseEntity<>("Profile does not exist", HttpStatus.BAD_REQUEST);
    }

    Profile profile = optionalProfile.get();

    ResponseEntity<String> checkHashtagsValidityResponse = checkAllTagsValidity(request.hashTags);
    if (checkHashtagsValidityResponse != null) {
      return checkHashtagsValidityResponse;
    }

    Activity activity = new Activity(request, profile);
    if (activity.getLocation() != null) {
      Optional<NamedLocation> optionalNamedLocation =
          locationRepository.findByCountryAndStateAndCity(
              activity.getLocation().getCountry(),
              activity.getLocation().getState(),
              activity.getLocation().getCity());
      if (optionalNamedLocation.isPresent()) {
        activity.setLocation(optionalNamedLocation.get());
      } else {
        locationRepository.save(activity.getLocation());
      }
    }
    ResponseEntity<String> checkActivityDateTimeResponse = checkCreateActivityDateTime(activity);
    if (checkActivityDateTimeResponse != null) {
      return checkActivityDateTimeResponse;
    }

    Set<Tag> hashtags = new HashSet<>();
    if (activity.getTags() != null) {
      for (Tag tag : activity.getTags()) {
        Tag dbTag = tagRepository.findByName(tag.getName());
        if (dbTag == null) {
          tag = tagRepository.save(tag);
          hashtags.add(tag);
        } else {
          hashtags.add(dbTag);
        }
      }
    }
    activity.setTags(hashtags);

    // Set creation date to now
    activity.setCreationDate(LocalDateTime.now());



    activityRepository.save(activity);

    SubscriptionHistory subscriptionHistory = new SubscriptionHistory(profile, activity);

    subscriptionHistoryRepository.save(subscriptionHistory);

    //Set creator of activity in ActivityRoleRepository
    ActivityRole creator = new ActivityRole();
    creator.setActivity(activity);
    creator.setProfile(profile);
    creator.setActivityRoleType(ActivityRoleType.Creator);
    activityRoleRepository.save(creator);

    ResponseEntity<String> editActivityRolesResponse = editActivityRoles(request.emails, activity, activity.getId());
    if (editActivityRolesResponse != null) {
      return editActivityRolesResponse;
    }

    return new ResponseEntity(activity.getId(), HttpStatus.CREATED);
  }

  /**
   * Given a HTTP request, change the values of activity to those that are in request
   *
   * @param request The request containing changes to activity
   * @param activity The activity that is currently present in the database
   */
  public void editActivityFromRequest(EditActivityRequest request, Activity activity) {
    activity.setActivityName(request.activityName);
    activity.setDescription(request.description);
    activity.setActivityTypes(request.activityTypes);
    activity.setContinuous(request.continuous);
    if (request.visibility != null) {
      activity.setVisibilityType(request.visibility);
    }
    if (activity.isContinuous()) {
      activity.setStartTime(null);
      activity.setEndTime(null);
    } else {
      activity.setStartTime(request.startTime);
      activity.setEndTime(request.endTime);
    }
    if (request.visibility != null) {
      activity.setVisibilityType(request.visibility);
    }

    if (request.location != null) {
      NamedLocation location =
              new NamedLocation(
                      request.location.country, request.location.state, request.location.city);
      activity.setLocation(location);

      if (activity.getLocation() != null) {
        Optional<NamedLocation> optionalNamedLocation =
                locationRepository.findByCountryAndStateAndCity(
                        activity.getLocation().getCountry(),
                        activity.getLocation().getState(),
                        activity.getLocation().getCity());
        if (optionalNamedLocation.isPresent()) {
          activity.setLocation(optionalNamedLocation.get());
        } else {
          locationRepository.save(location);
          activity.setLocation(location);
        }
      }
    }

    Set<Tag> hashtags = new HashSet<>();
    if (request.hashTags != null) {
      for (Tag tag : request.hashTags) {
        Tag dbTag = tagRepository.findByName(tag.getName());
        if (dbTag == null) {
          tag = tagRepository.save(tag);
          hashtags.add(tag);
        } else {
          hashtags.add(dbTag);
        }
      }
      activity.setTags(hashtags);
    }
  }

  /**
   * Put Request to update an activity for the given profile based on the request
   *
   * @param profileId The id of the author of the activity
   * @param request   The request with values to update the activity
   * @param session   The session of the currently logged in user
   * @return The response code and message
   */
  @PutMapping("/profiles/{profileId}/activities/{activityId}")
  public ResponseEntity<String> editActivity(
          @PathVariable Integer profileId,
          @PathVariable Integer activityId,
          @Valid @RequestBody EditActivityRequest request,
          HttpSession session) throws JsonProcessingException {

    Optional<Activity> optionalActivity = activityRepository.findById(activityId);

    if (optionalActivity.isPresent()) {
      Activity activity = optionalActivity.get();

      // Check if authorised
      ResponseEntity<String> authorisedResponse =
              UserSecurityService.checkAuthorised(profileId, session, profileRepository);
      if (authorisedResponse != null) {
        return authorisedResponse;
      }

      ResponseEntity<String> activityAuthorizedResponse =
          this.checkAuthorisedToEditActivity(activity, session);
      if (activityAuthorizedResponse != null) {
        return activityAuthorizedResponse;
      }

      // Check hashtags
      ResponseEntity<String> checkHashtagsValidityResponse = checkAllTagsValidity(request.hashTags);
      if (checkHashtagsValidityResponse != null) {
        return checkHashtagsValidityResponse;
      }

      ResponseEntity<String> checkActivityDateTimeResponse = checkEditActivityDateTime(request, activity);
      if (checkActivityDateTimeResponse != null) {
        return checkActivityDateTimeResponse;
      }


      ObjectMapper mapper = new ObjectMapper();
      String preJson = mapper.writeValueAsString(activity);

      editActivityFromRequest(request, activity);
      activityRepository.save(activity);

      ResponseEntity<String> editActivityRolesResponse = editActivityRoles(request.emails, activity, activityId);
      if (editActivityRolesResponse != null) {
        return editActivityRolesResponse;
      }

      String postJson = mapper.writeValueAsString(activity);
      if (!preJson.equals(postJson)) {
        String editorName = profileRepository.findById(Integer.parseInt(session.getAttribute("id").toString())).getFullname();
        ActivityHistory activityHistory = new ActivityHistory(activity,
                "Activity '" + activity.getActivityName() + "' was updated by " + editorName);
        activityHistoryRepository.save(activityHistory);
      }


      return ResponseEntity.ok("Activity: " + activity.getActivityName() + " was updated.");
    } else {
      return new ResponseEntity<>("Activity does not exist", HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Delete Request to delete an activity from a given profile id and activity id
   *
   * @param profileId The id of the profile where the activity is deleted from
   * @param activityId The id of the activity
   * @param session The session of the currently logged in user
   * @return The response code and message
   */
  @DeleteMapping("/profiles/{profileId}/activities/{activityId}")
  public ResponseEntity<String> deleteActivity(
      @PathVariable Integer profileId, @PathVariable Integer activityId, HttpSession session) {
    ResponseEntity<String> checkAuthorisedResponse =
        UserSecurityService.checkAuthorised(profileId, session, profileRepository);
    if (checkAuthorisedResponse != null) {
      return checkAuthorisedResponse;
    }
    Optional<Activity> optionalActivity = activityRepository.findById(activityId);
    if (optionalActivity.isPresent()) {
      Activity activity = optionalActivity.get();
      if (!activity.getProfile().getId().equals(profileId)) {
        return new ResponseEntity<>(
                "You are not the author of this activity", HttpStatus.UNAUTHORIZED);
      }
      activity.setArchived(true);
      activityRepository.save(activity);

      String editorName = profileRepository.findById(Integer.parseInt(session.getAttribute("id").toString())).getFullname();
      String activityArchivedMessage = "Activity '" + activity.getActivityName() + "' was archived by " + editorName;
      ActivityHistory activityHistory = new ActivityHistory(activity, activityArchivedMessage);
      activityHistoryRepository.save(activityHistory);

      return new ResponseEntity<>("Activity is now archived", HttpStatus.OK);
    } else {
      return new ResponseEntity<>("Activity does not exist", HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Get activity data by ID
   *
   * @param activityId The id of the requested activity
   * @return 200 response with headers
   */
  @GetMapping("/activities/{activityId}")
  public ResponseEntity<String> getActivity(@PathVariable int activityId, HttpSession session) {
    Optional<Activity> optionalActivity = activityRepository.findById(activityId);
    if (optionalActivity.isEmpty()) {
      return new ResponseEntity<>("Activity does not exist", HttpStatus.NOT_FOUND);
    }
    Activity activity = optionalActivity.get();
    if (activity.isArchived()) {
      return new ResponseEntity<>("Activity is archived", HttpStatus.OK);
    }
    Object profileId = session.getAttribute("id");
    if (!profileId.toString().equals(activity.getProfile().getId().toString())) {
      if (activity.getVisibilityType() == VisibilityType.Private) {
        return new ResponseEntity<>("Activity is private", HttpStatus.UNAUTHORIZED);
      }

      if (activity.getVisibilityType() == VisibilityType.Restricted) {
        ActivityRole activityRoles = activityRoleRepository.findByProfile_IdAndActivity_Id(Integer.parseInt(profileId.toString()), activityId);
        if (activityRoles == null) {
          return new ResponseEntity<>("Activity is restricted", HttpStatus.UNAUTHORIZED);
        }
      }
    }
    try {
      ObjectMapper mapper = new ObjectMapper();
      String postJson = mapper.writeValueAsString(activity);
      return ResponseEntity.ok(postJson);
    } catch (Exception e) {
      return new ResponseEntity<>("Activity does not exist", HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Given the activity id, find the creator of the activity and return the id of the creator
   * @param activityId id of the activity
   * @return the id of the creator of the activity
   */
  @GetMapping("/activities/{activityId}/creatorId")
  public ResponseEntity<String> getActivityCreator(@PathVariable int activityId) {
    Optional<Activity> optionalActivity = activityRepository.findById(activityId);
    if (optionalActivity.isEmpty()) {
      return new ResponseEntity<>("Activity does not exist", HttpStatus.NOT_FOUND);
    }
    Activity activity = optionalActivity.get();
    if (activity.isArchived()) {
      return new ResponseEntity<>("Activity is archived", HttpStatus.OK);
    }
    try {
      ObjectMapper mapper = new ObjectMapper();
      String postJson = mapper.writeValueAsString(activity.getProfile().getId());
      return ResponseEntity.ok(postJson);
    } catch (Exception e) {
      return new ResponseEntity<>("Activity does not exist", HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Get all activity that a user has created by their userID that is not archived
   *
   * @param profileId The id of the user profile
   * @param session The session of the current user logged in
   * @return 200 response with headers
   */
  @GetMapping("/profiles/{profileId}/activities")
  public ResponseEntity getUserActivities(@PathVariable int profileId, HttpSession session) {
    Object id = session.getAttribute("id");

    if (id == null) {
      return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
    }

    Profile profile = profileRepository.findById(profileId);
    if (profile == null) {
      return new ResponseEntity("User does not exist", HttpStatus.NOT_FOUND);
    }

    return ResponseEntity.ok(activityRepository.findByProfile_IdAndArchivedFalse(profileId));
  }

  /**
   * Increases the activity role of each accessor to Access, and decreases the activity role to standard user if
   * they no longer belongs to the list of accessor
   *
   * @param emails     list of accessor emails
   * @param activity   the activity to be edited
   * @param activityId the activity id to be edited
   * @return a NOT FOUND response entity if accessor is not found, otherwise return null
   */
  private ResponseEntity<String> editActivityRoles(List<String> emails, Activity activity, int activityId) {
    // Returns if list of accessor is not passed
    // or visibility type of activity is public (everyone can access) or private (no one can access)
    if (emails == null || activity.getVisibilityType() == VisibilityType.Public || activity.getVisibilityType() == VisibilityType.Private) {
      return null;
    }
    List<ActivityRole> activityRoles = activityRoleRepository.findByActivity_Id(activityId);
    if (activityRoles.size() > 0) {
      for (var i = 0; i < activityRoles.size(); i++) {
        Profile profile = activityRoles.get(i).getProfile();
        if (!(emails.contains(profile.getPrimaryEmail().getAddress()))) {
          activityRoleRepository.delete(activityRoles.get(i));
        }
      }
    }
    if (emails.size() > 0) {
      for (String email : emails) {
        Profile profile = profileRepository.findByEmails_address(email);
        if (profile == null) {
          return new ResponseEntity("User with email " + email + " does not exist", HttpStatus.NOT_FOUND);
        }
        ActivityRole role = activityRoleRepository.findByProfile_IdAndActivity_Id(profile.getId(), activityId);
        if (role == null) {
          ActivityRole activityRole = new ActivityRole();
          activityRole.setActivity(activity);
          activityRole.setProfile(profile);
          activityRole.setActivityRoleType(ActivityRoleType.Access);
          activityRoleRepository.save(activityRole);
        }
      }
    }
    return null;
  }

  /**
   * Changes the visibility of a users activity
   *
   * @param profileId  The id of the user profile
   * @param activityId The id of the activity
   * @param session    The current session of the logged in user
   * @return
   */
  @PutMapping("/profiles/{profileId}/activities/{activityId}/visibility")
  public ResponseEntity changeVisibility(
          @PathVariable int profileId,
          @PathVariable int activityId,
          @RequestBody @Valid EditActivityVisibilityRequest request,
          HttpSession session) {
    Object id = session.getAttribute("id");

    if (id == null) {
      return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
    }

    Optional<Activity> optionalActivity = activityRepository.findById(activityId);
    if (optionalActivity.isPresent()) {
      Activity activity = optionalActivity.get();
      if (!activity.getProfile().getId().equals(profileId)) {
        return new ResponseEntity<>(
                "You are not the author of this activity", HttpStatus.UNAUTHORIZED);
      }

      activity.setVisibilityType(request.getVisibility());
      activityRepository.save(activity);

      ResponseEntity<String> editActivityRolesResponse = editActivityRoles(request.getEmails(), activity, activityId);
      if (editActivityRolesResponse != null) {
        return editActivityRolesResponse;
      }

      return new ResponseEntity<>("Activity visibility has been saved", HttpStatus.OK);
    } else {
      return new ResponseEntity<>("Activity does not exist", HttpStatus.NOT_FOUND);
    }
  }
}
