package com.springvuegradle.team6.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
public class ActivityHistoryRepositoryTest {

  @Autowired private ActivityHistoryRepository activityHistoryRepository;
  @Autowired private ActivityRepository activityRepository;
  @Autowired private ProfileRepository profileRepository;

  @Test
  void testSingleHistoryFindByActivity() {
    Set<Email> emails = new HashSet<>();
    Email email = new Email("johnydoe99@gmail.com");
    email.setPrimary(true);
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

    ActivityHistory history = new ActivityHistory();
    history.setActivity(activity);
    history.setTimeDate(LocalDateTime.now());
    history.setMessage("A change to this activity has occurred!!!");
    history = activityHistoryRepository.save(history);

    Profile profile1 = profileRepository.findByEmailsContains(email);
    Activity activity1 =
        activityRepository.findByProfile_IdAndArchivedFalse(profile1.getId()).get(0);
    Set<ActivityHistory> histories = activityHistoryRepository.findByActivity_id(activity1.getId());
    org.junit.jupiter.api.Assertions.assertEquals(1, histories.size());
  }

  @Test
  void testTwoHistoryUpdatesOneActivityFindByActivity() {
    Set<Email> emails = new HashSet<>();
    Email email = new Email("secondemail@gmail.com");
    email.setPrimary(true);
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
    activity.setActivityName("Play rock, paper, scissors");
    activity.setContinuous(true);
    activity = activityRepository.save(activity);

    ActivityHistory history = new ActivityHistory();
    history.setActivity(activity);
    history.setTimeDate(LocalDateTime.now());
    history.setMessage("A change to this activity has occurred!!!");
    history = activityHistoryRepository.save(history);

    ActivityHistory history1 = new ActivityHistory();
    history1.setActivity(activity);
    history1.setTimeDate(LocalDateTime.now());
    history1.setMessage("A second change to this activity has occurred!!!");
    history1 = activityHistoryRepository.save(history1);

    Profile profile1 = profileRepository.findByEmailsContains(email);
    Activity activity1 =
        activityRepository.findByProfile_IdAndArchivedFalse(profile1.getId()).get(0);
    Set<ActivityHistory> histories = activityHistoryRepository.findByActivity_id(activity1.getId());
    org.junit.jupiter.api.Assertions.assertEquals(2, histories.size());
  }

  @Test
  void testGetActivityHistoryBetweenSubscribeStartEndDateTimeAndActivityIdReturnResults() {
    Set<Email> emails = new HashSet<>();
    Email email = new Email("johnydoe99@gmail.com");
    email.setPrimary(true);
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
    activity.setActivityName("Play rock, paper, scissors");
    activity.setContinuous(true);
    activity = activityRepository.save(activity);

    ActivityHistory history = new ActivityHistory();
    history.setActivity(activity);
    history.setTimeDate(LocalDateTime.of(2020, 3, 1, 13, 15));
    history.setMessage("A change to this activity has occurred!!!");
    history = activityHistoryRepository.save(history);

    ActivityHistory history1 = new ActivityHistory();
    history1.setActivity(activity);
    history1.setTimeDate(LocalDateTime.of(2020, 3, 1, 16, 15));
    history1.setMessage("A second change to this activity has occurred!!!");
    history1 = activityHistoryRepository.save(history1);

    List<ActivityHistory> activityHistories = activityHistoryRepository.getActivityHistoryBetweenSubscribeStartEndDateTimeAndActivityId(activity.getId(), LocalDateTime.of(2020, 2, 28, 10, 0), null);
    org.junit.jupiter.api.Assertions.assertEquals(2, activityHistories.size());
  }

}
