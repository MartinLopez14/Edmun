package com.springvuegradle.team6.models;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Email {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @javax.validation.constraints.Email
    @Column(unique=true)
    private String address;

    @ColumnDefault("false")
    @Column(name="is_primary")
    private boolean primary;

    public Email() { }

    public Email(String email) {
        this.address = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String email) {
        this.address = email;
    }

    @Override
    public String toString() {
        return getAddress();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(address, email.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public boolean isPrimary() {
        return this.primary;
    }
}
