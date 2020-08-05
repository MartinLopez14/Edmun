package com.springvuegradle.team6.models;

import javax.persistence.*;
import java.util.Objects;
import java.util.Collection;

@Entity
public class Country {
    public static final int ISO_LENGTH = 3;

    @Id
    @Column(length = 3)
    private String isoCode;

    private String countryName;

    public Country() {}

    public Country(String isoCode, String name) {
        this.isoCode = isoCode;
        this.countryName = name;
    }

    @ManyToMany(mappedBy = "passports", fetch = FetchType.EAGER)
    private Collection<Profile> users;

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    @Override
    public String toString() {
        return isoCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Country country = (Country) o;
        return isoCode.equals(country.isoCode) &&
                Objects.equals(countryName, country.countryName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isoCode);
    }
}
