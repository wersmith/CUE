package com.herokuapp.connectedupdate.appliancewatch;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

/**
 * Created by wersm_000 on 4/9/2015.
 */
public class AlarmReceiver extends BroadcastReceiver{

    public UserDataModel userData = new UserDataModel();

    @Override
    public void onReceive(Context context, Intent intent) {
        String username = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");

        userData.setPassword(password);
        userData.setUsername(username);

        System.out.println("Broadcast received" + username + password);




        //When the alarm goes off, here is the a message gets posted to the
        //notification and watch
        Toast.makeText(context, "A device alarm is set by" + userData.getUsername(), Toast.LENGTH_SHORT).show();
        System.out.println("Alarm Receiver");


    }





}
