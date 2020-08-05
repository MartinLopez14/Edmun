package com.springvuegradle.team6.models;

import javax.persistence.*;
import java.util.Collection;

/**
 * Role class
 */

@Entity
public class Role {
    /**
     * Role id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Role name
     */
    private String name;

    /**
     * Map role id to user id to create user_roles table in database
     */
    @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
    private Collection<Profile> users;


    public Role() {
        super();
    }

    /**
     * Constructor for Role class
     *
     * @param name role name
     */
    public Role(final String name) {
        super();
        this.name = name;
    }

    /**
     * Getter for role name
     *
     * @return role name
     */
    public String getRoleName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Role role = (Role) o;

        return this.id == role.id;
    }

}
