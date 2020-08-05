package com.springvuegradle.team6.requests;

import com.springvuegradle.team6.models.ActivityType;

import javax.validation.constraints.NotNull;
import java.util.Set;

public class EditActivityTypeRequest {

    @NotNull
    public Set<ActivityType> activities;

    public Set<ActivityType> getActivities() {
        return activities;
    }
}
