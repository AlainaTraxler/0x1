package com.epicodus.alainatraxler.a0x1.models;

import com.epicodus.alainatraxler.a0x1.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guest on 12/19/16.
 */
public class Exercise {
    public String name;
    public Integer sets = null;
    public Integer reps = null;
    public Integer weight = null;
    public Integer distance = null;
    public Integer time = null;
    String pushId;
    List<String> altNames = new ArrayList<>();

    public Exercise(){}

    public Exercise(String _name, String type){
        name = _name;
        if(type.equals(Constants.TYPE_WEIGHT)){
            sets = 0;
            reps = 0;
            weight = 0;
        }else if(type.equals(Constants.TYPE_AEROBIC)){
            distance = 0;
            time = 0;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSets() {
        return sets;
    }

    public void setSets(Integer sets) {
        this.sets = sets;
    }

    public Integer getReps() {
        return reps;
    }

    public void setReps(Integer reps) {
        this.reps = reps;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public List<String> getAltNames() {
        return altNames;
    }

    public void setAltNames(List<String> altNames) {
        this.altNames = altNames;
    }

    public void addAltName(String altName){
        altNames.add(altName);
    }
}
