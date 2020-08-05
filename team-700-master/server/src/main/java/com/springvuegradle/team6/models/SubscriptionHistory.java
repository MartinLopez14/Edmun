package com.springvuegradle.team6.models;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Entity to keep track of each time a subscription is change. Used to display this change on home
 * feed. Each time subscription state changes a new row is created with the current timestamp and
 * the current state of the subscription.
 */
@Entity
public class SubscriptionHistory {

  // For testing purposes only
  public SubscriptionHistory() {
    this.profile = null;
    this.activity = null;
    this.startDateTime = null;
    this.endDateTime = null;
  }

  /**
   * Constructor taking the profile and activity involved in the subscription
   * also sets start date to now
   * @param profile the profile that is subscribing to an activity
   * @param activity the activity being subscribed to
   */
  public SubscriptionHistory(Profile profile, Activity activity) {
    this.profile = profile;
    this.activity = activity;
    this.startDateTime = LocalDateTime.now();
    this.endDateTime = null;
  }

  /** Each history item has its own id */
  @Id @GeneratedValue private Integer id;

  /** Link to the profile this sub is associated with */
  @ManyToOne
  @JoinColumn(name = "profile_id", nullable = false)
  private Profile profile;

  /** Link to the activity this sub is associated with */
  @ManyToOne
  @JoinColumn(name = "activity_id", nullable = false)
  private Activity activity;

  /** The time this instance of sub history was created */
  @Column(name = "start_date_time", columnDefinition = "datetime default NOW()")
  private LocalDateTime startDateTime;

  @Column(name = "end_date_time")
  private LocalDateTime endDateTime;

  // ==========GETTERS==========

  public Integer getId() {
    return id;
  }

  public Profile getProfile() {
    return profile;
  }

  public Activity getActivity() {
    return activity;
  }

  public LocalDateTime getStartDateTime() {
    return startDateTime;
  }

  public LocalDateTime getEndDateTime() {
    return endDateTime;
  }

  // ==========SETTERS===========

  public void setId(Integer id) {
    this.id = id;
  }

  public void setProfile(Profile profile) {
    this.profile = profile;
  }

  public void setActivity(Activity activity) {
    this.activity = activity;
  }

  public void setStartDateTime(LocalDateTime startDateTime) {
    this.startDateTime = startDateTime;
  }

  public void setEndDateTime(LocalDateTime endDateTime) {
    this.endDateTime = endDateTime;
  }
}
