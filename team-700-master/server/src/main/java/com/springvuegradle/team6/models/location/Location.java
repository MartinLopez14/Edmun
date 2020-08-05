package com.springvuegradle.team6.models.location;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Location is a base class for defining a location in the world. It has three members: locationID:
 * The stored generated id for the database name: The associated name of the location lat & lon: The
 * geo coordinates of the feature This class can be used to define a feature point. Right now it is
 * used to define a users location but it can be extended to be used for instance for vertices in
 * event routes.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Location implements Serializable {

  @Id @GeneratedValue private long locationID = 0;

  private String name = "";

  private double latitude = 0;
  private double longitude = 0;

  public Location() {}

  public Location(String name) {
    setName(name);
  }

  /**
   * Sets a location using a specific latitude and longitude
   *
   * @param latitude Global coordinate latitude
   * @param longitude Global coordinate longitude
   */
  public Location(double latitude, double longitude) {
    setLatitude(latitude);
    setLongitude(longitude);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }
}
