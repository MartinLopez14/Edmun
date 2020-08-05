package com.springvuegradle.team6.models;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableScheduling
public class CountryUpdate {
    private final CountryRepository countryRepository;

    public CountryUpdate(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @PostConstruct
    public void init() {
        updateCountryRepository();
    }

    @Scheduled(cron = "0 0 0 ? * ?")
    public void updateCountryRepository() {
        // Get valid countries from api
        final String url = "https://restcountries.eu/rest/v2/all";

        try {
            RestTemplate restTemplate = new RestTemplate();
            JsonNode result = restTemplate.getForObject(url, JsonNode.class);
            // Convert json to country set
            Set<Country> newCountries = new HashSet<>();
            for (JsonNode countryJson : result) {
                String name = countryJson.get("name").asText();
                String isoCode = countryJson.get("alpha3Code").asText();

                newCountries.add(new Country(isoCode, name));
            }

            // Add all new countries to database
            // Use countryRepository.add(country)
            for (Country country : newCountries) {
                countryRepository.save(country);
            }

            // Remove all countries not in newCountries
            for (Country country : countryRepository.findAll()) {
                if (!newCountries.contains(country)) {
                    countryRepository.delete(country);
                }
            }
            System.getLogger("SystemEvents").log(System.Logger.Level.INFO,"Country repository updated");

        } catch(RestClientException e) {
            System.getLogger("SystemEvents").log(System.Logger.Level.ERROR,"Country repository failed to update");
            System.getLogger("SystemEvents").log(System.Logger.Level.TRACE, e.getMessage());
        }
    }
}