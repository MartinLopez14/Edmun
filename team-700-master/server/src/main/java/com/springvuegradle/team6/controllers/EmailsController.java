package com.springvuegradle.team6.controllers;

import com.springvuegradle.team6.models.EmailRepository;
import com.springvuegradle.team6.models.Profile;
import com.springvuegradle.team6.models.ProfileRepository;
import com.springvuegradle.team6.requests.EditEmailsRequest;
import com.springvuegradle.team6.security.UserSecurityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Optional;

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
public class EmailsController {
  private final ProfileRepository repository;
  private final EmailRepository emailRepository;

  EmailsController(ProfileRepository rep, EmailRepository emailRepository) {
    this.repository = rep;
    this.emailRepository = emailRepository;
  }

  /**
   * End point to update the primary email and the additional emails
   *
   * @param profileId Determines the user that is being updated
   * @param request The request with fields indicating the change
   * @param session The session that the user currently runs on
   * @return the response with status code
   */
  @PutMapping("/profiles/{profileId}/emails")
  public ResponseEntity<String> updateEmails(
      @PathVariable Integer profileId,
      @Valid @RequestBody EditEmailsRequest request,
      HttpSession session) {
    Optional<Profile> p = repository.findById(profileId);
    if (p.isPresent()) {
      Profile profile = p.get();

      // Check if authorised
      ResponseEntity<String> authorisedResponse =
          UserSecurityService.checkAuthorised(profileId, session, repository);
      if (authorisedResponse != null) {
        return authorisedResponse;
      }

      ResponseEntity<String> editEmailsResponse =
          EditEmailsRequest.editEmails(
              profile, emailRepository, request.additionalEmail, request.primaryEmail);
      if (editEmailsResponse != null) {
        return editEmailsResponse;
      }
      repository.save(profile);

      return ResponseEntity.ok("Profile updated successfully");
    } else {
      return new ResponseEntity<>("Profile does not exist", HttpStatus.NOT_FOUND);
    }
  }
}
