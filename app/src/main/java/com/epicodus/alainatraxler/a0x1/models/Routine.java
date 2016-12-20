package com.epicodus.alainatraxler.a0x1.models;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Guest on 12/19/16.
 */
public class Routine {
    public String name;
    public List<Exercise> exercises = new ArrayList<Exercise>();
    public String created;
    public String pushId;

    public Routine(){}

    public Routine(String name, List<Exercise> exercises){
        this.name = name;
        this.exercises = exercises;
        created = DateFormat.getDateTimeInstance().format(new Date());

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }
}
