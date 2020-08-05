package com.springvuegradle.team6.controllers;

import com.springvuegradle.team6.models.Email;
import com.springvuegradle.team6.models.EmailRepository;
import com.springvuegradle.team6.models.Profile;
import com.springvuegradle.team6.models.ProfileRepository;
import com.springvuegradle.team6.responses.SearchProfileResponse;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
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
public class SearchProfileController {

  private final ProfileRepository profileRepository;
  private final EmailRepository emailRepository;

  SearchProfileController(ProfileRepository profileRepository, EmailRepository emailRepository) {
    this.profileRepository = profileRepository;
    this.emailRepository = emailRepository;
  }

  /**
   * Searches for the user based on the full name given as best as possible using the implementation
   * of hibernate search
   *
   * @param fullName the full name to search for
   * @param offset the number of results to skip
   * @param limit the number of results to return
   * @param session the current logged in user session
   * @return the results of the search containing profiles that match the full name roughly
   */
  @GetMapping()
  //@RequestMapping(params = "fullname")
  public ResponseEntity getProfileByFullName(
      @RequestParam(name = "fullname", required = false) String fullName,
      @RequestParam(name = "activity", required = false) String activityType,
      @RequestParam(name = "method", required = false) String method,
      @RequestParam(name = "offset", required = false) Integer offset,
      @RequestParam(name = "limit", required = false) Integer limit,
      HttpSession session) {
    Object id = session.getAttribute("id");
    if (id == null) {
      return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
    }
    if (fullName == null && activityType == null) {
      return new ResponseEntity("Must specify some search parameters", HttpStatus.BAD_REQUEST);
    }
    if (offset == null) {
      offset = -1;
    }
    if (limit == null) {
      limit = -1;
    }
    String fullNameWithSpaces;
    if (fullName == null) {
      fullNameWithSpaces = null;
    } else {
      fullNameWithSpaces = fullName.replaceAll("%20", " ");
    }
    String activityTypesWithSpaces;
    if (activityType == null) {
      activityTypesWithSpaces = null;
    } else {
      activityTypesWithSpaces = activityType.replaceAll("%20", " ");
    }

    List<Profile> profiles = profileRepository.searchFullname(fullNameWithSpaces, activityTypesWithSpaces, method, limit, offset);
    System.out.println(fullNameWithSpaces);
    System.out.println(profiles.size());
    List<SearchProfileResponse> results = new ArrayList<>();
    for (Profile profile : profiles) {
      SearchProfileResponse result =
          new SearchProfileResponse(
              profile.getId(),
              profile.getLastname(),
              profile.getFirstname(),
              profile.getMiddlename(),
              profile.getNickname(),
              profile.getPrimaryEmail().getAddress(),
              profile.getActivityTypes().toString());
      results.add(result);
    }
    JSONObject resultsObject = new JSONObject();
    resultsObject.put("results", results);
    return new ResponseEntity(resultsObject, HttpStatus.OK);
  }

  /**
   * Searches for the user based on the full name given as best as possible and return the number of
   * results
   *
   * @param fullName the full name to search for
   * @param session the current logged in user session
   * @return the total number of results that matches fullname
   */
  @GetMapping
  @RequestMapping(value = "/count")
  public ResponseEntity getProfileByFullNameCount(
      @RequestParam(name = "fullname", required = false) String fullName,
      @RequestParam(name = "activity", required = false) String activityType,
      @RequestParam(name = "method", required = false) String method, HttpSession session) {
    Object id = session.getAttribute("id");
    if (id == null) {
      return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
    }

    if (fullName == null && activityType == null) {
      return new ResponseEntity("Must specify some search parameters", HttpStatus.BAD_REQUEST);
    }

    String fullNameWithSpaces;
    if (fullName == null) {
      fullNameWithSpaces = null;
    } else {
      fullNameWithSpaces = fullName.replaceAll("%20", " ");
    }

    String activityTypesWithSpaces;
    if (activityType == null) {
      activityTypesWithSpaces = null;
    } else {
      activityTypesWithSpaces = activityType.replaceAll("%20", " ");
    }

    Integer count = profileRepository.searchFullnameCount(fullNameWithSpaces, activityTypesWithSpaces, method);
    return new ResponseEntity(count, HttpStatus.OK);
  }

  /**
   * Given the email search for a profile that has this email
   *
   * @param searchedEmail the email to search for
   * @param session the current logged in user session
   * @return the results of the search that matches the email exactly
   */
  @GetMapping
  @RequestMapping(params = "email")
  public ResponseEntity getProfileByEmail(
      @RequestParam(name = "email") String searchedEmail, HttpSession session) {
    Object id = session.getAttribute("id");
    if (id == null) {
      return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
    }
    JSONObject resultsObject = new JSONObject();
    List<SearchProfileResponse> results = new ArrayList<>();

    Profile profile = null;
    Optional<Email> optionalEmail = emailRepository.findByAddress(searchedEmail);
    if (optionalEmail.isPresent()) {
      profile = profileRepository.findByEmailsContains(optionalEmail.get());
    }

    if (profile != null) {
      SearchProfileResponse result =
          new SearchProfileResponse(
              profile.getId(),
              profile.getLastname(),
              profile.getFirstname(),
              profile.getMiddlename(),
              profile.getNickname(),
              profile.getPrimaryEmail().getAddress(),
              profile.getActivityTypes().toString());
      results.add(result);
    }

    resultsObject.put("results", results);
    return new ResponseEntity(resultsObject, HttpStatus.OK);
  }

  /**
   * Given the nickname search for profiles that have this nickname
   *
   * @param nickname the nickname to search for
   * @param offset the number of results to skip
   * @param limit the number of results to return
   * @param session the current logged in user session
   * @return the results of the search that matches the nickname exactly
   */
  @GetMapping
  @RequestMapping(params = "nickname")
  public ResponseEntity getProfileByNickname(
      @RequestParam(name = "nickname") String nickname,
      @RequestParam(name = "activity", required = false) String activityType,
      @RequestParam(name = "method", required = false) String method,
      @RequestParam(name = "offset", required = false) Integer offset,
      @RequestParam(name = "limit", required = false) Integer limit,
      HttpSession session) {
    Object id = session.getAttribute("id");
    if (id == null) {
      return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
    }
    if (nickname == null && activityType == null) {
      return new ResponseEntity("Must specify some search parameters", HttpStatus.BAD_REQUEST);
    }
    if (offset == null) {
      offset = -1;
    }
    if (limit == null) {
      limit = -1;
    }

    String activityTypesWithSpaces;
    if (activityType == null) {
      activityTypesWithSpaces = null;
    } else {
      activityTypesWithSpaces = activityType.replaceAll("%20", " ");
    }
    JSONObject resultsObject = new JSONObject();
    List<Profile> profiles = profileRepository.searchNickname(nickname, activityTypesWithSpaces, method, limit, offset);
    List<SearchProfileResponse> results = new ArrayList<>();
    for (Profile profile : profiles) {
      SearchProfileResponse result =
          new SearchProfileResponse(
              profile.getId(),
              profile.getLastname(),
              profile.getFirstname(),
              profile.getMiddlename(),
              profile.getNickname(),
              profile.getPrimaryEmail().getAddress(),
              profile.getActivityTypes().toString());
      results.add(result);
    }

    resultsObject.put("results", results);
    return new ResponseEntity(resultsObject, HttpStatus.OK);
  }

  /**
   * Given the nickname search for profiles that have this nickname and return the number of results
   * that matches the nickname
   *
   * @param nickname the nickname to search for
   * @param session the current logged in user session
   * @return the total number of results that matches nickname
   */
  @GetMapping
  @RequestMapping(value = "/count", params = "nickname")
  public ResponseEntity getProfileByNickNameCount(
      @RequestParam(name = "nickname") String nickname,
      @RequestParam(name = "activity", required = false) String activityType,
      @RequestParam(name = "method", required = false) String method, HttpSession session) {
    Object id = session.getAttribute("id");
    if (id == null) {
      return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
    }

    String activityTypesWithSpaces;
    if (activityType == null) {
      activityTypesWithSpaces = null;
    } else {
      activityTypesWithSpaces = activityType.replaceAll("%20", " ");
    }


    Integer count = profileRepository.searchNicknameCount(nickname, activityTypesWithSpaces, method);
    return new ResponseEntity(count, HttpStatus.OK);
  }
}
