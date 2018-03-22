package me.peterjiang.testfinal;

import android.util.Log;

import java.util.ArrayList;



/**
 * Created by PeterJiang on 12/6/16.
 */

public class EventObject {
    public String EID;
    public String Name;
    public String Owner;
    public String fromDate;
    public String toDate;
    public String fromTime;
    public String toTime;
    public String Place;
    public String Desc;
    public double longitude;
    public double latitude;
    public ArrayList<String> RSVP_attend = new ArrayList<>();
    public ArrayList<String> RSVP_not = new ArrayList<>();


    public EventObject(){
    }

    public EventObject(String EID, String Name, String Owner, String fromDate, String toDate, String fromTime, String toTime, String Place, String Desc){
        this.EID = EID;
        this.Name = Name;
        this.Owner = Owner;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.Place = Place;
        this.Desc = Desc;
        //  this.longitude=longitude;
        //  this.latitude=latitude;
//        RSVP_attend.add("test");
//        RSVP_not.add("test");
    }


//    public String getName(){
//        return Name;
//    }
//
//    public String getEID(){
//        return EID;
//    }
//
//    public String getOwner(){
//        return Owner;
//    }
//
//    public String getDate(){
//        return Date;
//    }
//
//    public String getTime(){
//        return Time;
//    }
//
//    public String getPlace(){
//        return Place;
//    }
//
//    public String getDesc(){
//        return Desc;
//    }
//
//    public void setName(String Name){
//        this.Name = Name;
//    }
//
//    public void setEID(String EID){
//        this.EID = EID;
//    }
//
//    public void setOwner(String Owner){
//        this.Owner = Owner;
//    }
//
//    public void setDate(String Date){
//        this.Date = Date;
//    }
//
//    public void setTime(String Time){
//        this.Time = Time;
//    }
//
//    public void setPlace(String Place){
//        this.Place = Place;
//    }
//
//    public void setDesc(String Desc){
//        this.Desc = Desc;
//    }
//
//    public ArrayList<String> getAttend() {
//        return RSVP_attend;
//    }
//
//    public ArrayList<String> getNot(){
//        return RSVP_not;
//    }

    public void attend(String UName){
        if(RSVP_attend != null) {
            RSVP_attend.add(UName);

        }
        else{
            RSVP_attend = new ArrayList<>();
            RSVP_attend.add(UName);
        }
        Log.e("EventObject","add");

    }

    public void notgo(String UName){
        if(RSVP_not != null) {
            RSVP_not.add(UName);
        }
        else{
            RSVP_not = new ArrayList<>();
            RSVP_not.add(UName);
        }
        Log.e("EventObject","notgo");

    }

    public void cancel(String UName){
        if(RSVP_attend!=null) {
            RSVP_attend.remove(UName);
        }
        if(RSVP_not != null) {
            RSVP_not.remove(UName);
        }
        Log.e("EventObject","cancel");

    }

//    public void setAttend(ArrayList<String> RSVP_attend){
//        this.RSVP_attend = RSVP_attend;
//    }
//
//    public void setNot(ArrayList<String> RSVP_not){
//        this.RSVP_not = RSVP_not;
//    }
}
