package com.springvuegradle.team6.models.location;

import net.bytebuddy.implementation.bind.annotation.IgnoreForBinding;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class NamedLocation {
  @Id @GeneratedValue @JsonIgnore private long locationID = 0;

  private String country;
  private String state;
  private String city;

  public NamedLocation() {}

  public NamedLocation(String country, String state, String city) {
    this.country = country;
    this.state = state;
    this.city = city;
  }

  public long getLocationID() {
    return locationID;
  }

  public void setLocationID(long locationID) {
    this.locationID = locationID;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  @Override
  public String toString() {
    return "NamedLocation{"
        + "country='"
        + country
        + '\''
        + ", state='"
        + state
        + '\''
        + ", city='"
        + city
        + '\''
        + '}';
  }
}
