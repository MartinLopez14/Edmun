package com.springvuegradle.team6.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Role repository interface
 */
@RepositoryRestResource
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Find role by role name
     *
     * @param name role name
     * @return object of Role class
     */
    Role findByName(String name);

    /**
     * Delete role by role object
     *
     * @param role object of Role class
     */
    @Override
    void delete(Role role);

    /**
     * Returns a boolean if a roleName exists
     *
     * @param roleName of Role class
     */
    boolean existsByName(String roleName);

}
