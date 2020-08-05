package com.springvuegradle.team6.models;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
class SubscriptionHistoryRepositoryTest {

  @Autowired
  private SubscriptionHistoryRepository subscriptionHistoryRepository;
  @Autowired
  private ActivityRepository activityRepository;
  @Autowired
  private ProfileRepository profileRepository;

  @Test
  void testSingleSubscriptionFindByProfile() {
    Set<Email> emails = new HashSet<>();
    Email email = new Email("johnydoe99@gmail.com");
    emails.add(email);
    Profile profile = new Profile();
    profile.setFirstname("John");
    profile.setLastname("Doe");
    profile.setEmails(emails);
    profile.setDob("2010-01-01");
    profile.setPassword("Password1");
    profile.setGender("male");
    profile = profileRepository.save(profile);

    Activity activity = new Activity();
    activity.setProfile(profile);
    activity.setActivityName("Run at Hagley Park");
    activity.setContinuous(true);
    activity = activityRepository.save(activity);

    SubscriptionHistory subscriptionHistory = new SubscriptionHistory();
    subscriptionHistory.setActivity(activity);
    subscriptionHistory.setProfile(profile);
    subscriptionHistory.setStartDateTime(LocalDateTime.now());
    subscriptionHistory = subscriptionHistoryRepository.save(subscriptionHistory);

    Profile profile1 = profileRepository.findByEmailsContains(email);
    Set<SubscriptionHistory> subscriptionHistories =
        subscriptionHistoryRepository.findByProfile_id(profile1.getId());
    org.junit.jupiter.api.Assertions.assertEquals(1, subscriptionHistories.size());
  }

  @Disabled
  @Test
  void testSingleSubscriptionFindByActivity() {
    Set<Email> emails = new HashSet<>();
    Email email = new Email("secondemail@gmail.com");
    emails.add(email);
    Profile profile = new Profile();
    profile.setFirstname("Gon");
    profile.setLastname("Freecss");
    profile.setEmails(emails);
    profile.setDob("2010-01-01");
    profile.setPassword("Password1");
    profile.setGender("male");
    profile = profileRepository.save(profile);

    Activity activity = new Activity();
    activity.setProfile(profile);
    activity.setActivityName("Fight at heavens arena");
    activity.setContinuous(true);
    activity = activityRepository.save(activity);

    SubscriptionHistory subscriptionHistory = new SubscriptionHistory();
    subscriptionHistory.setActivity(activity);
    subscriptionHistory.setProfile(profile);
    subscriptionHistory.setStartDateTime(LocalDateTime.now());
    subscriptionHistory = subscriptionHistoryRepository.save(subscriptionHistory);

    Profile profile1 = profileRepository.findByEmailsContains(email);
    Set<SubscriptionHistory> subscriptionHistories =
            subscriptionHistoryRepository.findByActivity_id(
                    activityRepository.findByProfile_IdAndArchivedFalse(profile1.getId()).get(0).getId());
    org.junit.jupiter.api.Assertions.assertEquals(1, subscriptionHistories.size());
  }
}
