package com.epicodus.alainatraxler.a0x1.models;

import com.epicodus.alainatraxler.a0x1.Constants;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guest on 12/19/16.
 */
@Parcel
public class Exercise {
    public String name;
    public Integer sets = null;
    public Integer reps = null;
    public Double weight = null;
    public Double distance = null;
    public String time = null;
    String pushId;
    List<String> altNames = new ArrayList<>();
    String type;

    public Exercise(){}

    public Exercise(String _name, String type){
        name = _name;
        this.type = type;
        if(type.equals(Constants.TYPE_WEIGHT)){
            sets = 0;
            reps = 0;
            weight = 0.0;
        }else if(type.equals(Constants.TYPE_AEROBIC)){
            distance = 0.0;
            time = "0:00";
        }else if(type.equals(Constants.TYPE_BODYWEIGHT)){
            sets = 0;
            reps = 0;
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

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
