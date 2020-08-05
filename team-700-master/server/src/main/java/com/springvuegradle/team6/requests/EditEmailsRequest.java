package com.springvuegradle.team6.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team6.models.Email;
import com.springvuegradle.team6.models.EmailRepository;
import com.springvuegradle.team6.models.Profile;
import com.springvuegradle.team6.validators.EmailCollection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class EditEmailsRequest {
    /**
     * The primary email of the user, used for them to log in.
     */
    @NotNull
    @NotEmpty
    @JsonProperty("primary_email")
    @javax.validation.constraints.Email
    public String primaryEmail;

    @EmailCollection
    @Size(max=5)
    @JsonProperty("additional_email")
    public List<String> additionalEmail;

    /**
     * Checks the database for emails and see if the client request doesn't invalidate any database constraints on email
     * and if there are no invalids, then update the repository with the requested emails.
     * @param profile The profile taken from the database that needs to be updated
     * @param emailRepository The repository containing all the emails
     * @param additionalEmail The additional emails from the HTTP request
     * @param primaryEmail The primary email from the HTTP request
     * @return ResponseEntity holding error messages if one of the conditions failed, otherwise return null to indicate success
     */
    public static ResponseEntity<String> editEmails(Profile profile, EmailRepository emailRepository, List<String> additionalEmail, String primaryEmail) {
        // Check that the request additional email is not in the request primary email and vice versa
        if (additionalEmail != null) {
            if (additionalEmail.contains(primaryEmail)) {
                return new ResponseEntity<>("The primary email cannot be an additional email", HttpStatus.BAD_REQUEST);
            }
        }

        // Set Primary Email
        Optional<Email> profilePrimaryEmail = emailRepository.findByAddress(primaryEmail);

        // Check if primary email is being used by another user
        if(profilePrimaryEmail.isPresent() && !profile.getAllEmails().contains(profilePrimaryEmail.get())) {
            return new ResponseEntity<>(primaryEmail + " is already being used", HttpStatus.BAD_REQUEST);
        }

        if(profilePrimaryEmail.isEmpty()) {
            profilePrimaryEmail = Optional.of(new Email(primaryEmail));
        }

        if(additionalEmail != null) {
            Set<Email> profileAdditionalEmails = new HashSet<>();

            for(String email : additionalEmail) {
                Optional<Email> profileEmail = emailRepository.findByAddress(email);

                // Exists and associates another user
                if(profileEmail.isPresent() && !profile.getAllEmails().contains(profileEmail.get())) {
                    return new ResponseEntity<>(email + " is already being used", HttpStatus.BAD_REQUEST);
                }

                if(profileEmail.isEmpty()) {
                    profileEmail = Optional.of(new Email(email));
                }

                profileAdditionalEmails.add(profileEmail.get());
            }

            profile.setEmails(profileAdditionalEmails);
        }

        profile.setPrimaryEmail(profilePrimaryEmail.get());

        return null;
    }

}
