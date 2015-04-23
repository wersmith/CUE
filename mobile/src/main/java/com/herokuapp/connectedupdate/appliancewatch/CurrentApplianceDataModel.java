package com.herokuapp.connectedupdate.appliancewatch;

import android.graphics.Color;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by wersm_000 on 3/26/2015.
 */
public class CurrentApplianceDataModel {
    private int mSessionID;
    private Calendar mCalendar = Calendar.getInstance();
    private String mRoomName;
    private String mApplianceName;
    private String mApplianceTimeLapse;
    private String mApplianceTime;
    private int mApplianceState;
    private int mInputId;

    public int getInputId() {
        return mInputId;
    }

    public void setInputId(int inputId) {
        mInputId = inputId;
    }

    public String getApplianceTime() {
        return mApplianceTime;
    }

    public void setApplianceTime(String applianceStartTime) {
        mApplianceTime = applianceStartTime;
    }

    public int getApplianceState() {
        return mApplianceState;
    }

    public void setApplianceState(int applianceEndTime) {
        mApplianceState = applianceEndTime;
    }

    public int getSessionID() {
        return mSessionID;
    }

    public void setSessionID(int sessionID) {
        mSessionID = sessionID;
    }

    public Calendar getCalendar() {
        return mCalendar;
    }

    public void setCalendar(Calendar calendar) {
        mCalendar = calendar;
    }

    public String getRoomName() {
        return mRoomName;
    }

    public void setRoomName(String roomName) {
        mRoomName = roomName;
    }

    public String getApplianceName() {
        return mApplianceName;
    }

    public void setApplianceName(String applianceName) {
        mApplianceName = applianceName;
    }

    public String getApplianceTimeLapse() {
        return mApplianceTimeLapse;
    }

    public void setApplianceTimeLapse(String applianceTimeLapse) {
        mApplianceTimeLapse = applianceTimeLapse;
    }

    public int getBgColor(){
        int mRedColor = 0xFFFB020B;
        int mYellowColor = 0xFFD3C729;
        int mBlueColor = 0xFF02A5C2;

        if (mApplianceState == 0){
            return mBlueColor;
        } if (mApplianceState == 1){
            return mYellowColor;
        } else {
            return mRedColor;
        }

    }

    public int getTimeToAlarm() {
        int timeDiffInt = 500;

        String format = "yyyy-MM-dd'T'HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        String newTime = mApplianceTime.substring(0, mApplianceTime.length()-5);
                //+"-"+mApplianceTime.substring(mApplianceTime.length()-5);


        System.out.println("New Time String UTC: " + newTime);




        try {
            Date applianceOnUnattendedTime = simpleDateFormat.parse(newTime);
            System.out.println("Current Appliance Time (UTC):  " + applianceOnUnattendedTime);

            SimpleDateFormat newTimeZoneTime = new SimpleDateFormat(format);
            newTimeZoneTime.setTimeZone(TimeZone.getTimeZone("America/Atlanta"));

            String convertedTimeZoneTime = newTimeZoneTime.format(applianceOnUnattendedTime);
            Date applianceOnUnattendedTimeEastern = simpleDateFormat.parse(convertedTimeZoneTime);
            System.out.println("Current Appliance Time (Eastern):  " + applianceOnUnattendedTimeEastern);

            Calendar timeCal = Calendar.getInstance();
            timeCal.setTime(applianceOnUnattendedTimeEastern);
            timeCal.add(Calendar.MINUTE, Integer.parseInt(mApplianceTimeLapse));
            System.out.println("Current Appliance Time (Eastern) + " + mApplianceTimeLapse
                    + ":  " + timeCal.getTime());

            Calendar currentTimeCal = Calendar.getInstance();
            System.out.println("Current Device Time:  " + currentTimeCal.getTime());


            String currentTimeCalString;
            int year = currentTimeCal.get(Calendar.YEAR);
            int month = currentTimeCal.get(Calendar.MONTH);
            int day = currentTimeCal.get(Calendar.DAY_OF_MONTH);
            int hour = currentTimeCal.get(Calendar.HOUR_OF_DAY);
            int minute = currentTimeCal.get(Calendar.MINUTE);
            currentTimeCalString = String.valueOf(year)+ String.valueOf(month)+String.valueOf(day);

            String applianceOnUnattendedTimeString;
            int aYear = timeCal.get(Calendar.YEAR);
            int aMonth = timeCal.get(Calendar.MONTH);
            int aDay = timeCal.get(Calendar.DAY_OF_MONTH);
            int aHour = timeCal.get(Calendar.HOUR_OF_DAY);
            int aMinute = timeCal.get(Calendar.MINUTE);
            applianceOnUnattendedTimeString = String.valueOf(aYear)+ String.valueOf(aMonth)+String.valueOf(aDay);

            if (currentTimeCalString.equals(applianceOnUnattendedTimeString)){
                if (hour==aHour){
                    timeDiffInt = aMinute - minute;
                    System.out.println(mApplianceName + "timeDiffInt 1: " + timeDiffInt);

                } else {
                    timeDiffInt = (60 - aMinute) + minute;
                    System.out.println(mApplianceName + "timeDiffInt 2: " + timeDiffInt);
                }

            } else {
                timeDiffInt = 0;
                System.out.println(mApplianceName + "timeDiffInt 3: " + timeDiffInt);
            }

        } catch (ParseException e) {
            System.out.println(mApplianceName + "Current Appliance Time Error:  " + e);
        }

        if (timeDiffInt >= Integer.parseInt(mApplianceTimeLapse)){
            return 0;
        } else {
            return Integer.parseInt(mApplianceTimeLapse) - timeDiffInt;
        }

    };

}
