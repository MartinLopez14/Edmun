package com.springvuegradle.team6.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
public class ActivityRoleRepositoryTest {

  @Autowired
  private TagRepository tagRepository;

  @Autowired
  private ActivityRepository activityRepository;

  @Autowired
  private ProfileRepository profileRepository;

  @Autowired
  private ActivityRoleRepository activityRoleRepository;

  @BeforeEach
  void setup() {
    //tagRepository.deleteAll();
    //activityRepository.deleteAll();

    Set<Email> emails = new HashSet<>();
    Email email = new Email("johnydoe1@gmail.com");
    emails.add(email);
    Profile profile = new Profile();
    profile.setFirstname("John");
    profile.setLastname("Doe1");
    profile.setEmails(emails);
    profile.setDob("2010-01-01");
    profile.setPassword("Password1");
    profile.setGender("male");
    profile = profileRepository.save(profile);

    Activity activity = new Activity();
    activity.setProfile(profile);
    activity.setActivityName("Run at Hagley Park");
    activity.setContinuous(true);
    activityRepository.save(activity);

    ActivityRole role = new ActivityRole();
    role.setProfile(profile);
    role.setActivityRoleType(ActivityRoleType.Creator);
    role.setActivity(activity);
    activityRoleRepository.save(role);
  }

  @Test
  void testGetCreator() {
    List<ActivityRole> roles = activityRoleRepository.findByActivityRoleType(ActivityRoleType.Creator);
    org.junit.jupiter.api.Assertions.assertEquals("Run at Hagley Park", roles.get(0).getActivity().getActivityName());
  }
}
