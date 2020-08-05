package com.springvuegradle.team6.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team6.exceptions.DuplicateRoleException;
import com.springvuegradle.team6.exceptions.DuplicateSubscriptionException;
import com.springvuegradle.team6.exceptions.RoleNotFoundException;
import com.springvuegradle.team6.exceptions.SubscriptionNotFoundException;
import com.springvuegradle.team6.models.location.NamedLocation;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.standard.StandardFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.search.annotations.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.util.*;

@Indexed
@Entity
@AnalyzerDef(
    name = "profileAnalyzer",
    tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
    filters = {
      @TokenFilterDef(factory = LowerCaseFilterFactory.class),
      @TokenFilterDef(factory = StandardFilterFactory.class)
    })
public class Profile {

  @Id
  @GeneratedValue
  @Column(name = "id")
  @SortableField
  private Integer id;

  @Field(
      index = org.hibernate.search.annotations.Index.YES,
      analyze = Analyze.YES,
      store = Store.NO,
      analyzer = @Analyzer(definition = "profileAnalyzer"))
  private String firstname;

  @Field(
      index = org.hibernate.search.annotations.Index.YES,
      analyze = Analyze.YES,
      store = Store.NO,
      analyzer = @Analyzer(definition = "profileAnalyzer"))
  private String middlename;

  @Field(
      index = org.hibernate.search.annotations.Index.YES,
      analyze = Analyze.YES,
      store = Store.NO,
      analyzer = @Analyzer(definition = "profileAnalyzer"))
  private String lastname;

  @Field(
      index = org.hibernate.search.annotations.Index.YES,
      analyze = Analyze.YES,
      store = Store.NO,
      analyzer = @Analyzer(definition = "profileAnalyzer"))
  private String nickname;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  private Set<Email> emails = new HashSet<>();

  ;

  private String password;

  private String bio;

  private String dob;

  private String gender;

  private Integer fitness;

  @ManyToOne private NamedLocation location;

  @IndexedEmbedded
  @Field(analyze = Analyze.YES, store = Store.NO)
  @ElementCollection(targetClass = ActivityType.class)
  @Enumerated(EnumType.ORDINAL)
  private Set<ActivityType> activityTypes;

  @ManyToMany(
      fetch = FetchType.EAGER,
      cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH})
  @JoinTable(
      name = "users_countries",
      joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "country_code", referencedColumnName = "isoCode"))
  private Set<Country> passports;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "users_roles",
      joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
  private Collection<Role> roles;

  @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
  private List<Activity> activities;

  public Profile() {}

  /** Maps users to the activities they have subscribed to/followed */
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "profile_subscriptions",
      joinColumns = @JoinColumn(name = "profile_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "activity_id"))
  private Collection<Activity> subscriptions;

  public Integer getId() {
    return this.id;
  }

  /**
   * Hashes and sets a plaintext password
   *
   * @param password
   */
  public void setPassword(String password) {
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    this.password = passwordEncoder.encode(password);
  }

  public String getFirstname() {
    return this.firstname;
  }

  public String getMiddlename() {
    return this.middlename;
  }

  public String getLastname() {
    return this.lastname;
  }

  public String getNickname() {
    return this.nickname;
  }

  @JsonProperty("primary_email")
  public Email getPrimaryEmail() {
    for (Email email : emails) {
      if (email.isPrimary()) return email;
    }

    return new Email("null");
  }

  public String getPassword() {
    return this.password;
  }

  public String getBio() {
    return this.bio;
  }

  @JsonProperty("date_of_birth")
  public String getDob() {
    return this.dob;
  }

  public String getGender() {
    return this.gender;
  }

  public Set<Country> getPassports() {
    return this.passports;
  }

  public Collection<Role> getRoles() {
    return roles;
  }

  @JsonProperty("additional_email")
  public Set<Email> getEmails() {
    Set<Email> additionalEmails = new HashSet<>(this.emails);
    additionalEmails.removeIf(Email::isPrimary);
    return additionalEmails;
  }

  public Collection<Activity> getSubscriptions() {
    return subscriptions;
  }

  public Set<Email> getAllEmails() {
    return this.emails;
  }

  @JsonProperty("activities")
  public Set<ActivityType> getActivityTypes() {
    return this.activityTypes;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  @JsonProperty("date_of_birth")
  public void setDob(String dob) {
    this.dob = dob;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getFitness() {
    return fitness;
  }

  @JsonProperty("primary_email")
  public void setPrimaryEmail(Email email) {
    getPrimaryEmail().setPrimary(false);

    email.setPrimary(true);
    this.emails.add(email);
  }

  @JsonProperty("additional_email")
  public void setEmails(Set<Email> emails) {
    clearNonPrimaryEmails();
    this.emails.addAll(emails);
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  public void setFitness(Integer fitness) {
    this.fitness = fitness;
  }

  public void setPassports(Set<Country> passports) {
    this.passports = passports;
  }

  @JsonProperty("activities")
  public void setActivityTypes(Set<ActivityType> activityTypes) {
    this.activityTypes = activityTypes;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  public void setMiddlename(String middlename) {
    this.middlename = middlename;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public void setRoles(final Collection<Role> roles) {
    this.roles = roles;
  }

  public void addRole(Role newRole) throws DuplicateRoleException {
    for (Role role : roles) {
      if (role.getRoleName().equals(newRole.getRoleName())) {
        throw new DuplicateRoleException();
      }
    }
    roles.add(newRole);
  }

  public void removeRole(String roleName) throws RoleNotFoundException {
    List<Role> roleList = new ArrayList(roles);
    for (int i = 0; i < roles.size(); i++) {
      if (roleList.get(i).getRoleName().equals(roleName)) {
        roles.remove(roleList.get(i));
        return;
      }
    }
    throw new RoleNotFoundException();
  }

  public NamedLocation getLocation() {
    return location;
  }

  public void setSubscriptions(final Collection<Activity> subscriptions) {
    this.subscriptions = subscriptions;
  }

  public void addSubscription(Activity newSubscription) throws DuplicateSubscriptionException {
    for (Activity activity : activities) {
      if (activity.getId().equals(newSubscription.getId())) {
        throw new DuplicateSubscriptionException();
      }
    }
    subscriptions.add(newSubscription);
  }

  public void removeSubscription(Activity oldSubscription) throws SubscriptionNotFoundException {
    if (subscriptions.contains(oldSubscription)) {
      subscriptions.remove(oldSubscription);
    } else {
      throw new SubscriptionNotFoundException();
    }
  }

  public void setLocation(NamedLocation location) {
    this.location = location;
  }

  /**
   * This is used by the searching profile by full name functionality
   *
   * @return The combined string of first, middle and last name
   */
  @javax.persistence.Transient
  @Field(
      index = org.hibernate.search.annotations.Index.YES,
      analyze = Analyze.YES,
      store = Store.YES,
      analyzer = @Analyzer(definition = "profileAnalyzer"))
  public String getFullname() {
    if (middlename == null) {
      return firstname + " " + lastname;
    }
    return firstname + " " + middlename + " " + lastname;
  }
  /**
   * Hashes a plain text password and compares to stored password hash. If the hashes match returns
   * True.
   *
   * @param password plaintext password
   * @return True on hash match
   */
  public boolean comparePassword(String password) {
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    return passwordEncoder.matches(password, this.password);
  }

  @Override
  public String toString() {
    return "Profile{"
        + "id="
        + id
        + ", firstname='"
        + firstname
        + '\''
        + ", middlename='"
        + middlename
        + '\''
        + ", lastname='"
        + lastname
        + '\''
        + ", nickname='"
        + nickname
        + '\''
        + ", email='"
        + getPrimaryEmail()
        + '\''
        + ", password='"
        + password
        + '\''
        + ", bio='"
        + bio
        + '\''
        + ", dob='"
        + dob
        + '\''
        + ", gender='"
        + gender
        + '\''
        + ", fitness="
        + fitness
        + '\''
        + ", additionalemail='"
        + getEmails()
        + '\''
        + ", passports='"
        + passports
        + '\''
        + '}';
  }

  public List<Activity> getActivities() {
    return activities;
  }

  public void setActivities(List<Activity> activities) {
    this.activities = activities;
  }

  /** Clears all emails except for the primary email in emails */
  private void clearNonPrimaryEmails() {
    emails.removeIf(email -> !email.isPrimary());
  }
}
