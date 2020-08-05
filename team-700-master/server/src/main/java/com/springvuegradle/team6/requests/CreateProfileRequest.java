package com.springvuegradle.team6.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team6.models.*;
import com.springvuegradle.team6.models.Email;
import com.springvuegradle.team6.models.location.NamedLocation;
import com.springvuegradle.team6.models.location.NamedLocationRepository;
import com.springvuegradle.team6.validators.EmailCollection;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CreateProfileRequest {
    @NotNull
    @NotEmpty
    @Pattern(regexp = "^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$")
    public String firstname;

    @Pattern(regexp = "^$|^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$")
    public String middlename;

    @NotNull
    @NotEmpty
    @Pattern(regexp = "^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$")
    public String lastname;

    @Pattern(regexp = "^$|^[A-Za-z0-9]+$")
    public String nickname;

    @NotNull
    @NotEmpty
    @JsonProperty("primary_email")
    @javax.validation.constraints.Email(message = "Email should be valid")
    public String email;

    @EmailCollection
    @Size(min=0, max=5)
    @JsonProperty("additional_email")
    public List<String> additionalemail;

    @JsonProperty("activities")
    public Set<ActivityType> activityTypes;
    public List<@Length(min = 3, max = 3) String> passports;

    @NotNull
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")
    public String password;

    public String bio = "";

    @NotNull
    @NotEmpty
    @Pattern(regexp = "([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))")
    @JsonProperty("date_of_birth")
    public String dob;

    @NotNull
    @NotEmpty
    @Pattern(regexp = "(male)|(female)|(nonbinary)")
    public String gender;

    @Min(value = 0) @Max(value = 4)
    public Integer fitness = 0;

    public NamedLocation location;

    public Profile generateProfile(
            EmailRepository emailRepository,
            CountryRepository countryRepository,
            NamedLocationRepository locationRepository
    ) {
        Profile profile = new Profile();
        profile.setFirstname(firstname);
        profile.setMiddlename(middlename);
        profile.setLastname(lastname);
        profile.setNickname(nickname);
        Email primaryEmail = new Email(email);
        profile.setPrimaryEmail(primaryEmail);
        profile.setPassword(password);
        profile.setBio(bio);
        profile.setDob(dob);
        profile.setGender(gender);
        profile.setFitness(fitness);

        if (this.passports != null) {
            Set<Country> validPassports = new HashSet<>();

            for (String iso : this.passports) {
                Country country = countryRepository.findByIsoCode(iso);
                if (country != null) {
                    validPassports.add(country);
                }
            }
            profile.setPassports(validPassports);
        }

        if (this.additionalemail != null) {
            Set<Email> emails = new HashSet<>();

            for (String address : this.additionalemail) {
                Email newEmail = new Email(address);

                emails.add(newEmail);
            }

            profile.setEmails(emails);
        }

        if (this.activityTypes != null) {
            profile.setActivityTypes(this.activityTypes);
        }

        if(this.location != null) {
            locationRepository.save(this.location);
            profile.setLocation(this.location);
        }

        return profile;
    }

    /**
     *
     * @param dob
     * @return bool for whether or not date is valid
     */
    public boolean isValidDate(String dob) {
        // Check valid date
        try {
            // Check DOB
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dateNow = sdf.parse(sdf.format(new Date()));
            Date dateGiven = sdf.parse(dob);
            if (dateGiven.after(dateNow)) {
                return false;
            }

            Date dateMin = sdf.parse("1900-01-01");
            if (dateGiven.before(dateMin)) {
                return false;
            }
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
