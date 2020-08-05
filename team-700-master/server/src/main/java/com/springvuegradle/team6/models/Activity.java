package com.springvuegradle.team6.models;

import com.springvuegradle.team6.models.location.NamedLocation;
import com.springvuegradle.team6.requests.CreateActivityRequest;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Entity
public class Activity {

  // For testing purposes only
  public Activity() {
    Set<ActivityType> myEmptySet = Collections.emptySet();
    this.profile = null;
    this.activityName = null;
    this.description = null;
    this.activityTypes = null;
    this.activityTypes = myEmptySet;
    this.continuous = true;
  }

  public Activity(CreateActivityRequest request, Profile profile) {
    this.profile = profile;
    this.activityName = request.activityName;
    this.description = request.description;
    this.activityTypes = request.activityTypes;
    this.tags = request.hashTags;
    this.continuous = request.continuous;
    if (!this.continuous) {
      this.startTime = request.startTime;
      this.endTime = request.endTime;
    }
    if (request.location != null) {
      NamedLocation location =
              new NamedLocation(
                      request.location.country, request.location.state, request.location.city);
      this.location = location;
    }
    if (request.visibility != null) {
      setVisibilityType(request.visibility);
    } else {
      this.visibilityType = VisibilityType.Public;
    }

  }

  @Id
  @GeneratedValue
  @Column(name = "id")
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "author_id", nullable = false)
  private Profile profile;

  private String activityName;

  private String description;

  @ElementCollection(targetClass = ActivityType.class)
  @Enumerated(EnumType.ORDINAL)
  private Set<ActivityType> activityTypes;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
          name = "activity_tags",
          joinColumns = @JoinColumn(name = "activity_id", referencedColumnName = "id"),
          inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id"))
  private Set<Tag> tags;

  private boolean continuous;

  private String startTime;

  private String endTime;

  @ManyToOne private NamedLocation location;

  @Column(columnDefinition = "datetime default NOW()")
  private LocalDateTime creationDate;

  /** Map activity id to user id to create profile_subscriptions table in database */
  @ManyToMany(mappedBy = "subscriptions", fetch = FetchType.LAZY)
  private Collection<Profile> subscribers;

  @Column(columnDefinition = "boolean default false")
  private boolean archived;

  @OneToMany(mappedBy = "activity")
  private List<ActivityRole> activityRole;

  @Enumerated(EnumType.ORDINAL)
  private VisibilityType visibilityType;

  public String getActivityName() {
    return activityName;
  }

  public void setActivityName(String activityName) {
    this.activityName = activityName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Set<ActivityType> getActivityTypes() {
    return activityTypes;
  }

  public void setActivityTypes(Set<ActivityType> activityTypes) {
    this.activityTypes = activityTypes;
  }

  public boolean isContinuous() {
    return continuous;
  }

  public void setContinuous(boolean continuous) {
    this.continuous = continuous;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getEndTime() {
    return endTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Profile getProfile() {
    return profile;
  }

  public void setProfile(Profile profile) {
    this.profile = profile;
  }

  public NamedLocation getLocation() {
    return location;
  }

  public void setLocation(NamedLocation location) {
    this.location = location;
  }

  public LocalDateTime getCreationDate() {
    return creationDate;
  }

  public Set<Tag> getTags() {
    return tags;
  }

  public void setTags(Set<Tag> tags) {
    this.tags = tags;
  }

  public boolean isArchived() {
    return archived;
  }

  public void setArchived(boolean archived) {
    this.archived = archived;
  }

  public VisibilityType getVisibilityType() {
    return this.visibilityType;
  }

  public void setVisibilityType(String type) {
    String toCamelCase = type.substring(0, 1).toUpperCase() + type.substring(1);
    this.visibilityType = VisibilityType.valueOf(toCamelCase);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Activity activity = (Activity) o;

    return this.id == activity.getId();
  }

  public Collection<Profile> getSubscribers() {
    return subscribers;
  }

  public void setSubscribers(Collection<Profile> subscribers) {
    this.subscribers = subscribers;
  }

  public void setCreationDate(LocalDateTime creationDate) {
    this.creationDate = creationDate;
  }
}
