package com.springvuegradle.team6.models;

import javax.persistence.*;
import java.util.Collection;

@Entity
public class Tag {

  @Id
  @GeneratedValue
  @Column(name = "id")
  private Integer id;

  @Column(name = "name", unique = true)
  private String name;

  @ManyToMany(
      mappedBy = "tags",
      cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  private Collection<Activity> activities;

  public Tag() {}

  public Tag(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
