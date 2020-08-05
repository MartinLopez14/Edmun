package com.springvuegradle.team6.controllers;

import com.springvuegradle.team6.models.ActivityRepository;
import com.springvuegradle.team6.models.TagRepository;
import net.minidev.json.JSONObject;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
@RequestMapping("hashtag")
public class TagController {

  private final ActivityRepository activityRepository;
  private final TagRepository tagRepository;

  TagController(ActivityRepository activityRepository, TagRepository tagRepository) {
    this.activityRepository = activityRepository;
    this.tagRepository = tagRepository;
  }

  /**
   * Search for hashtags in the database that starts with the given hashtag e.g searching for cool,
   * would return coolor and coolstory if they exist in the database.
   *
   * @param hashtag the hashtag to search for, does not contain # at the start
   * @param session the current session
   * @return the list of hashtags that match the hashtag
   */
  @GetMapping
  @RequestMapping(value = "/autocomplete")
  public ResponseEntity getHashtagsAutocomplete(
      @RequestParam(name = "hashtag") String hashtag, HttpSession session) {
    Object id = session.getAttribute("id");
    if (id == null) {
      return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
    }

    hashtag = hashtag.toLowerCase();

    if (!hashtag.matches("^[a-zA-Z0-9_]*$")) {
      return new ResponseEntity(
          "Is not a hashtag, can only contain alphanumeric characters and underscore",
          HttpStatus.BAD_REQUEST);
    }

    List<String> resultList;
    if (hashtag.length() < 3) {
      resultList = new ArrayList<>();
      resultList.add(hashtag);
    } else {
      hashtag = hashtag.replace("_", "\\_");
      resultList = tagRepository.findTagsMatchingSearch(hashtag, PageRequest.of(0, 10));
    }

    Map<String, List<String>> result = new HashMap<>();
    result.put("results", resultList);
    JSONObject obj = new JSONObject(result);
    return new ResponseEntity(obj, HttpStatus.OK);
  }

  /**
   * Find all activities that contain the given hashtag
   *
   * @param hashTag the given hashtag
   * @param session the current session
   * @return activities that contain the hashtag
   */
  @GetMapping("/{hashTag}")
  public ResponseEntity getActivitiesByHashtag(@PathVariable String hashTag, HttpSession session) {
    Object id = session.getAttribute("id");
    if (id == null) {
      return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
    }
    hashTag = hashTag.toLowerCase();
    return new ResponseEntity(
        activityRepository.findByTags_NameOrderByCreationDateDesc(hashTag), HttpStatus.OK);
  }
}
