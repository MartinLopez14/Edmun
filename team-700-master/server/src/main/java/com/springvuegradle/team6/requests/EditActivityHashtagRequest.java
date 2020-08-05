package com.springvuegradle.team6.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team6.models.Tag;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

public class EditActivityHashtagRequest {

    @NotNull
    @Size(max = 30)
    @JsonProperty("hashtags")
    private Set<Tag> hashtags;

    public Set<Tag> getHashtags() {
        return this.hashtags;
    }
}
