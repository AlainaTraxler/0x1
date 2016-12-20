package com.epicodus.alainatraxler.a0x1.models;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Guest on 12/20/16.
 */
public class Workout {
    public ArrayList<Exercise> exercises = new ArrayList<Exercise>();
    public String completed;
    public String pushId;

    public Workout(){}

    public Workout(ArrayList<Exercise> exercises){
        this.exercises = exercises;
        completed = DateFormat.getDateTimeInstance().format(new Date());
    }

    public ArrayList<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(ArrayList<Exercise> exercises) {
        this.exercises = exercises;
    }

    public String getCompleted() {
        return completed;
    }

    public void setCompleted(String completed) {
        this.completed = completed;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }
}
