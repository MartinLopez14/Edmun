package com.springvuegradle.team6.models.location;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.client.RestTemplate;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * OpenStreetMap Element Identifier,
 * Elements can be uniquely identified using a 64 bit ID and the elements type.
 */

@Embeddable
public class OSMElementID implements Serializable {

    @NotNull
    private Long id;

    @NotNull
    private OSMElementType type;

    public OSMElementID() {}

    public OSMElementID(Long id, OSMElementType type) {
        this.id = id;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OSMElementType getType() {
        return type;
    }

    public void setType(OSMElementType type) {
        this.type = type;
    }


    public String uniqueCode() {
        return String.format("%c%d", type.code, id);
    }

    @Override
    public String toString() {
        return uniqueCode();
    }

    /**
     * finds the first associated Open Street Map node relating to search query.
     * For instance if findBySearch("Christchurch") the first result would be:
     * {
     * 	"location":{
     * 		"id": 2730349,
     * 		"type": "RELATION"
     *        }
     * }
     * This is a utility function that may be used for the api to speed up some location queries
     * Keep in mind this function currently operates by using api calls to nomination.openstreetmap.org
     * and so repeated queries can be slow. It is therefore suggested to offload initial queries on the frontend
     * to this api directly and send the OSMElementID directly instead.
     * @param query Open Street Map Search Query
     * @return A boolean if query was successful
     */
    public boolean findBySearch(String query) {
        String url = "https://nominatim.openstreetmap.org/search?q=\"%s\"&format=json&limit=1";
        url = String.format(url, query);

        // Query OSM
        RestTemplate restTemplate = new RestTemplate();
        JsonNode result = restTemplate.getForObject(url, JsonNode.class);


        if (result != null && !result.isEmpty()) {
            JsonNode osmObject = result.get(0);

            long jsonId = osmObject.get("osm_id").asLong(0);
            OSMElementType jsonType = OSMElementType.getFromStr(osmObject.get("osm_type").asText(""));

            // Check that both elements are found in the json
            if (jsonId != 0 && jsonType != null) {
                setId(jsonId);
                setType(jsonType);
            } else {
                return false;
            }
        } else {
            return false;
        }

        return true;
    }
}
