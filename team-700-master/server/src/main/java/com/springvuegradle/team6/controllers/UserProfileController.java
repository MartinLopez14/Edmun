package com.springvuegradle.team6.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.springvuegradle.team6.models.*;
import com.springvuegradle.team6.models.location.NamedLocation;
import com.springvuegradle.team6.models.location.NamedLocationRepository;
import com.springvuegradle.team6.requests.*;
import com.springvuegradle.team6.security.UserSecurityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
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
@RequestMapping("/profiles")
public class UserProfileController {

  private final ProfileRepository repository;
  private final CountryRepository countryRepository;
  private final RoleRepository roleRepository;
  private final EmailRepository emailRepository;
  private final NamedLocationRepository locationRepository;

  UserProfileController(
      ProfileRepository rep,
      CountryRepository countryRepository,
      EmailRepository emailRepository,
      RoleRepository roleRep,
      NamedLocationRepository locationRepository) {
    this.repository = rep;
    this.countryRepository = countryRepository;
    this.roleRepository = roleRep;
    this.emailRepository = emailRepository;
    this.locationRepository = locationRepository;
  }

  /**
   * Gets the user where the provided id. Otherwise return unauthorized if id doesnt exist or not
   * logged in.
   *
   * @param id The requested id
   * @param session The current Http Session
   * @return The response code and message/Json
   * @throws JsonProcessingException
   */
  @GetMapping("/{id}")
  public ResponseEntity getProfile(@PathVariable Integer id, HttpSession session)
      throws JsonProcessingException {
    ResponseEntity<String> canViewResponse =
        UserSecurityService.canViewPermission(id, session, repository);
    if (canViewResponse != null) {
      return canViewResponse;
    }
    Optional<Profile> p = repository.findById(id);
    if (p.isPresent()) {
      return ResponseEntity.ok(p.get());
    } else {
      return new ResponseEntity<>("User does not exist", HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Put request to: Update a user profile with specified Id given in request body, this user must
   * be logged in. New data is contained in request body, empty fields are unchanged
   *
   * @param request EditProfileRequest form with Id of profile to edit and new info to update
   * @return returns response entity with details of update
   */
  @PutMapping("/{id}")
  public ResponseEntity<String> updateProfile(
      @PathVariable Integer id,
      @Valid @RequestBody EditProfileRequest request,
      HttpSession session) {
    Optional<Profile> p = repository.findById(id);
    if (p.isPresent()) {
      Profile edit = p.get();

      // Check if authorised
      ResponseEntity<String> authorisedResponse =
          UserSecurityService.checkAuthorised(id, session, repository);
      if (authorisedResponse != null) {
        return authorisedResponse;
      }

      // Edit profile
      request.editProfileFromRequest(edit, countryRepository, emailRepository, locationRepository);
      ResponseEntity<String> editEmailsResponse =
          EditEmailsRequest.editEmails(
              edit, emailRepository, request.additionalemail, request.primaryemail);
      if (editEmailsResponse != null) {
        return editEmailsResponse;
      }
      repository.save(edit);

      return ResponseEntity.ok("User " + edit.getFirstname() + "'s profile was updated.");
    } else {
      return new ResponseEntity<>("Profile does not exist", HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Get all profile's information in a string
   *
   * @return response entity with user profile information
   */
  @GetMapping("/")
  public ResponseEntity<String> getAll() {
    List<Profile> all = repository.findAll();
    StringBuilder response = new StringBuilder();
    for (Profile p : all) {
      response.append(p.toString());
    }
    return ResponseEntity.ok(response.toString());
  }
  /**
   * Get request to return the current user that is logged in the system
   *
   * @return ResponseEntity which contains which user is logged in
   * @return ResponseEntity which can be success(2xx) with the users name or error(4xx) user is not
   *     logged in
   */
  @GetMapping("/user")
  public ResponseEntity getProfile2(HttpSession session) throws JsonProcessingException {
    Object id = session.getAttribute("id");
    if (id == null) {
      return new ResponseEntity("Not logged in", HttpStatus.EXPECTATION_FAILED);
    } else {
      int intId = (int) session.getAttribute("id");
      return ResponseEntity.ok(repository.findById(intId));
    }
  }

  /**
   * Get request to return the current user's firstname that is logged in the system
   *
   * @return ResponseEntity which contains which user is logged in
   * @return ResponseEntity which can be success(2xx) with the users name or error(4xx) user is not
   *     logged in
   */
  @GetMapping("/firstname")
  public ResponseEntity getProfileFirstName(HttpSession session) throws JsonProcessingException {
    Object id = session.getAttribute("id");
    if (id == null) {
      return new ResponseEntity("Not logged in", HttpStatus.EXPECTATION_FAILED);
    } else {
      int intId = (int) session.getAttribute("id");
      return ResponseEntity.ok(repository.findById(intId).getFirstname());
    }
  }

  /**
   * Get request to return the id of the current user logged into the session
   *
   * @param session the current Http session
   * @return response entity with the current logged in user id
   */
  @GetMapping("/id")
  public ResponseEntity<String> getUserId(HttpSession session) throws JsonProcessingException {
    Object id = session.getAttribute("id");
    if (id == null) {
      return new ResponseEntity("Not logged in", HttpStatus.EXPECTATION_FAILED);
    } else {
      return ResponseEntity.ok().body(id.toString());
    }
  }

  /**
   * Get request to return logged in user's role
   *
   * @param session the current Http session
   * @return response entity with the logged in user's role
   * @throws JsonProcessingException
   */
  @GetMapping("/role")
  public ResponseEntity getRole(HttpSession session) throws JsonProcessingException {
    Object id = session.getAttribute("id");
    if (id == null) {
      return new ResponseEntity("Not logged in", HttpStatus.EXPECTATION_FAILED);
    } else {
      int intId = Integer.parseInt(session.getAttribute("id").toString());
      return ResponseEntity.ok(repository.findById(intId).getRoles());
    }
  }

  /**
   * Creates a new user Takes JSON post data, checks the data and adds it to DB
   *
   * @param request the request entity
   * @return ResponseEntity which can be success(2xx) or error(4xx)
   */
  @PostMapping("")
  @ResponseBody
  public ResponseEntity createProfile(
      @Valid @RequestBody CreateProfileRequest request, HttpSession session) {
    Profile profile =
        request.generateProfile(emailRepository, countryRepository, locationRepository);
    profile.setRoles(Arrays.asList(roleRepository.findByName("ROLE_USER")));

    // Check if primary email is being used by another user
    String emailAddress = profile.getPrimaryEmail().getAddress();

    if (emailRepository.findByAddress(emailAddress).isPresent()) {
      return new ResponseEntity<>(emailAddress + " is already being used", HttpStatus.BAD_REQUEST);
    }

    if (profile.getEmails() != null) {
      // Check that none of the additional emails are in the repository already
      for (Email email : profile.getEmails()) {
        if (emailRepository.findByAddress(email.getAddress()).isPresent()) {
          return new ResponseEntity<>(
              email.getAddress() + " is already being used", HttpStatus.BAD_REQUEST);
        }
      }
    }

    if (request.isValidDate(profile.getDob()) == false) {
      return new ResponseEntity("Date must be less than current date", HttpStatus.BAD_REQUEST);
    }

    repository.save(profile);

    session.setAttribute("id", profile.getId());
    return new ResponseEntity(profile.getId(), HttpStatus.CREATED);
  }

  /**
   * Edit user password, user must be logged in Takes JSON patch data, checks passwords match, then
   * hashed and added to DB
   *
   * @param request the request entity
   * @return ResponseEntity which can be success(2xx) or error(4xx)
   */
  @PutMapping("/{profileId}/password")
  public ResponseEntity<String> editPassword(
      @PathVariable Integer profileId,
      @Valid @RequestBody EditPasswordRequest request,
      HttpSession session) {
    ResponseEntity<String> authorisedResponse =
        UserSecurityService.checkAuthorised(profileId, session, repository);
    if (authorisedResponse != null) {
      return authorisedResponse;
    }

    Profile profile = repository.findById(profileId).get();
    if (!profile.comparePassword(request.oldpassword)) {
      return new ResponseEntity<>("Old password incorrect", HttpStatus.UNAUTHORIZED);
    }

    if (!request.newpassword.equals(request.repeatedpassword)) {
      return new ResponseEntity<>("Passwords don't match", HttpStatus.BAD_REQUEST);
    }

    profile.setPassword(request.newpassword);
    repository.save(profile);
    return ResponseEntity.ok("Password Edited Successfully");
  }

  /**
   * Put request to update user's location
   *
   * @param id user id under query
   * @param location location of type NamedLocation
   * @param session Http session
   * @return ResponseEntity will return 200 success if user is authorised to update location, else
   *     return 404 response if user is not found
   */
  @PutMapping("/{id}/location")
  public ResponseEntity<String> updateLocation(
      @PathVariable Integer id,
      @Valid @RequestBody LocationUpdateRequest location,
      HttpSession session) {

    Optional<Profile> p = repository.findById(id);
    if (p.isPresent()) {
      Profile profile = p.get();

      // Check if authorised
      ResponseEntity<String> authorisedResponse =
          UserSecurityService.checkAuthorised(id, session, repository);
      if (authorisedResponse != null) {
        return authorisedResponse;
      }

      // Update location
      Optional<NamedLocation> optionalNamedLocation =
          locationRepository.findByCountryAndStateAndCity(
              location.country, location.state, location.city);
      if (optionalNamedLocation.isPresent()) {
        profile.setLocation(optionalNamedLocation.get());
      } else {
        NamedLocation newLocation =
            new NamedLocation(location.country, location.state, location.city);
        locationRepository.save(newLocation);
        profile.setLocation(newLocation);
      }

      repository.save(profile);

      return ResponseEntity.ok("OK");
    } else {
      return new ResponseEntity<>("Profile does not exist", HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Delete request to delete user's location
   *
   * @param id user id under query
   * @param session http session
   * @return Response entity will return 200 success if user is authorised to delete location, else
   *     return 404 if user profile is not found
   */
  @DeleteMapping("/{id}/location")
  public ResponseEntity<String> deleteLocation(@PathVariable Integer id, HttpSession session) {
    Optional<Profile> p = repository.findById(id);
    if (p.isPresent()) {
      Profile profile = p.get();

      // Check if authorised
      ResponseEntity<String> authorisedResponse =
          UserSecurityService.checkAuthorised(id, session, repository);
      if (authorisedResponse != null) {
        return authorisedResponse;
      }

      // Update location
      profile.setLocation(null);
      repository.save(profile);

      return ResponseEntity.ok("OK");
    } else {
      return new ResponseEntity<>("Profile does not exist", HttpStatus.NOT_FOUND);
    }
  }
}
