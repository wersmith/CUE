package com.herokuapp.connectedupdate.appliancewatch;

import java.util.Calendar;

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
    private String mApplianceState;
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

    public String getApplianceState() {
        return mApplianceState;
    }

    public void setApplianceState(String applianceEndTime) {
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


}
