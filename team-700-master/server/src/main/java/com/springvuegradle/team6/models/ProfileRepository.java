package com.springvuegradle.team6.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface ProfileRepository
    extends JpaRepository<Profile, Integer>, CustomizedProfileRepository {
  Profile findById(int id);

  Profile findByEmailsContains(Email email);

  boolean existsByEmailsContaining(Email email);

  Integer removeById(int id);

  Profile findByEmails_address(String email);
}
