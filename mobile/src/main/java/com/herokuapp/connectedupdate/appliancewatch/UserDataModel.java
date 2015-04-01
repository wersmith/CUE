package com.herokuapp.connectedupdate.appliancewatch;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wersm_000 on 3/23/2015.
 */
public class UserDataModel implements Parcelable {

    //creates member variables to store
    private String mUsername;
    private String mPassword;

    //creates basic constructor for object creation
    public UserDataModel() { ; };

    //The constructor to use when and an re-constructing object
     public UserDataModel(Parcel in) {
        readFromParcel(in);
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        //writes data to parcel to pass around app
        dest.writeString(mUsername);
        dest.writeString(mPassword);
    }

    private void readFromParcel(Parcel in) {

        //reads back data to object from parcel
        mUsername = in.readString();
        mPassword = in.readString();
    }

    //creates parcel
    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public UserDataModel createFromParcel(Parcel in) {
            return new UserDataModel(in);
        }

        public UserDataModel[] newArray(int size) {
            return new UserDataModel[size];
        }
    };

}
