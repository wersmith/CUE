package com.herokuapp.connectedupdate.appliancewatch;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okio.Buffer;

/**
 * Created by wersm_000 on 3/18/2015.
 */
public class MainScreenActivity extends ActionBarActivity  {

    private final OkHttpClient client = new OkHttpClient();
    public static final MediaType MEDIA_TYPE_JSON
            = MediaType.parse("application/json; charset=utf-8");
    public static final String TAG = LoginActivity.class.getSimpleName();
    public String json;
    private String mJSONString;

    //Sets the button variables
    private ImageView refreshButton;
    private ImageView editStove;
    private ImageView editTV;

    //Sets notificaiton warning variables
    private int notificationID;
    private PendingIntent applianceWarningPendingIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainscreen);

        //Gets the user data model in the app
        Bundle userDataBundle = getIntent().getExtras();
        final UserDataModel userData =  userDataBundle.getParcelable("UserModel");
        System.out.println("Main Screen:  " + userData.getUsername() + userData.getPassword());

        //Retrieves a PendingIntent that will perform a broadcast to the Alarm Receiver
        final Intent applianceWarningIntent = new Intent (MainScreenActivity.this, AlarmReceiver.class);
        applianceWarningIntent.putExtra("userData",userData);
        applianceWarningIntent.putExtra("username", userData.getUsername());
        applianceWarningIntent.putExtra("password", userData.getPassword());


        //sets refresh image and will be used onClick
        refreshButton = (ImageView) findViewById(R.id.refresh);
        editStove = (ImageView) findViewById(R.id.editStoveIcon);
        editTV = (ImageView) findViewById(R.id.editTVIcon);

        //Starts a thread to pull in an object
        try {
            //sets the current data on the dashboard.
            refresh(userData);

        } catch (Exception e) {
            e.printStackTrace();
        }

        refreshButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v ) {
                refresh(userData);
                try{
                    //phoneNotification();
                    int x = 3;
                    startAlarm(applianceWarningIntent);
                } catch (Exception e) {
                    Log.e(TAG, "Problem with the phone notification: ", e);
                }
                try{wearableNotification(userData);} catch (Exception e){
                    Log.e(TAG, "Problem with the watch notification: ", e);
                }
            }
        });

        editStove.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v ) {
                showEditTimeDialog(11, "Stove", userData);
            }
        });

        editTV.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v ) {
                showEditTimeDialog(12, "TV", userData);
            }
        });

    }


    public void getHttpData(String username, String password, String apiEndPoint, String parseType) throws Exception{
        final TextView addressText = (TextView) findViewById(R.id.addressTextDisplay);
        final TextView appliance1 = (TextView) findViewById(R.id.appliance_1);
        final TextView appliance2 = (TextView) findViewById(R.id.appliance_2);
        final String parsingType = parseType;
        String credential = Credentials.basic(username, password);
        String mAPIEndPoint = "http://connectedupdate.herokuapp.com" + apiEndPoint;

        Request request = new Request.Builder()
                .header("Authorization", credential)
                .url(mAPIEndPoint)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                Log.e(TAG, "Exception caught: ", e);
                json="#";
            }

            @Override public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                Headers responseHeaders = response.headers();
                for (int i = 0; i < responseHeaders.size(); i++) {
                    System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }

                json = response.body().string();
                System.out.println("Response:  " + json);

                try {
                    if(parsingType.equals("home")){setHomeInfo(json, addressText);}else if(parsingType.equals("appliance")){setApplianceInfo(json, appliance1,appliance2);}

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setHomeInfo(String jsonString, final TextView addressText) throws Exception {
        JSONObject homeInfo = new JSONObject(jsonString);
        String address = homeInfo.getString("homeStreetAddress");
        String city = homeInfo.getString("homeCity");
        String zip = homeInfo.getString("homeZIP");
        String state = homeInfo.getString("homeState");

        final String concatAddress = address + "\n" + city + ", " + state + " " + zip;
        System.out.println(concatAddress);


        //Sets Address UI Information
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addressText.setText(concatAddress);
            }
        });
    }

    public void setApplianceInfo(String jsonString, final TextView appliance1, final TextView appliance2) throws Exception{
        JSONArray applianceInfo = new JSONArray(jsonString);

        //creates an empty array for individual appliance data
        ArrayList<CurrentApplianceDataModel> applianceArray = new ArrayList<>();


        for(int aIterator = 0; aIterator < applianceInfo.length(); aIterator++){
            System.out.println(applianceInfo.getString(aIterator));
            CurrentApplianceDataModel appliance = new CurrentApplianceDataModel();
            JSONObject iteratorJSONObject = new JSONObject(applianceInfo.getString(aIterator));
            appliance.setSessionID(iteratorJSONObject.getInt("sessionID"));

            System.out.println(aIterator + ": " +appliance.getSessionID());
            appliance.setApplianceTime(iteratorJSONObject.getString("applianceTime"));
            appliance.setApplianceState(iteratorJSONObject.getString("applianceState"));

            JSONObject nestedApplianceName = new JSONObject(iteratorJSONObject.getString("applianceName"));
            appliance.setApplianceName(nestedApplianceName.getString("applianceName"));
            appliance.setInputId(nestedApplianceName.getInt("inputID"));
            appliance.setApplianceTimeLapse(nestedApplianceName.getString("timeLapseAlarm"));

            JSONObject nestedRoomName = new JSONObject(nestedApplianceName.getString("roomID"));
            appliance.setRoomName(nestedRoomName.getString("roomName"));

            applianceArray.add(aIterator, appliance);
        }


        //writes the appliance data to the UI
        //static boxes
        //need to convert to grid or list view

        for (int object=0; object < applianceArray.size(); object++){
            //Sets the state of the object from an INT to On or Off text.
            final String stateText;
            if (applianceArray.get(object).getApplianceState().equals("1")){stateText="ON";}else {stateText="OFF";};

            final String printObject = "Appliance: "
                    + applianceArray.get(object).getApplianceName()
                    + "\nTime Interval: "
                    + applianceArray.get(object).getApplianceTimeLapse()
                    + "\nRoom: "
                    + applianceArray.get(object).getRoomName()
                    + "\nState:  "
                    + stateText;

            System.out.println("Printing Array: " + object);
            System.out.println(printObject);

            final int backgroundObjectColor;
            if(applianceArray.get(object).getApplianceTimeLapse().equals("20")){
                backgroundObjectColor = 0xFFFB020B;
            }else{
                backgroundObjectColor = 0xFF02A5C2;
            }

            if (object==0){

                //Sets Address UI Information
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        appliance1.setText(printObject);
                        appliance1.setBackgroundColor(backgroundObjectColor);
                    }
                });
            } else if (object==1){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        appliance2.setText(printObject);
                        appliance2.setBackgroundColor(backgroundObjectColor);
                    }
                });
            }

        }


        //String address = homeInfo.getString("homeStreetAddress");
        //String city = homeInfo.getString("homeCity");
        // String zip = homeInfo.getString("homeZIP");
        // String state = homeInfo.getString("homeState");

        //final String concatAddress = address + "\n" + city + ", " + state + " " + zip;
        System.out.println("In Appliance Method");
        System.out.println(applianceInfo);


        //Sets Address UI Information
        //runOnUiThread(new Runnable() {
            //@Override
            //public void run() {
                //addressText.setText(concatAddress);
            //}
        //});



    }

    public void updateApplianceTimeLapse(String username, String password, String apiEndPoint){

        //Sets the Request Body string type


        //sets login parameter
        String credential = Credentials.basic(username, password);

        //make sure to include the slash at the end of the endpoint.
        String mAPIEndPoint = "http://connectedupdate.herokuapp.com" + apiEndPoint + "11/";

        System.out.println("Put Call, mAPIEndPoint:  " + mAPIEndPoint);


        // Use to test and see what a JSON Object for a put should look like
        // Create a JSONObject
        JSONObject json = new JSONObject();

       // Add fields
        try {
            json.put("applianceName", "Stove");
            json.put("timeLapseAlarm", 45);
            json.put("inputID", 11);
            json.put("homeID",1);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //Builds a JSON String for the request
        //String jsonPost = "{'inputID':11,"
        //        +"timeLapseAlarm':50,"
        //        +"'applianceName':'Stove'"
        //        +"'homeID':1}";

        RequestBody newBody = RequestBody.create(MEDIA_TYPE_JSON, json.toString());
        System.out.println("Put Call New Body:  "+ newBody.toString());

        // builds PUT request
        Request request = new Request.Builder()
                .header("Authorization", credential)
                .url(mAPIEndPoint)
                .put(newBody)
                .build();
        System.out.println("Put Call Request String:  "+ request.body().toString());


/**
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e(TAG, "Exception caught: ", e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                System.out.println("Success:  " + response.body().string());
            }

        });
 */

        Response response = null;
        try {
            response = client.newCall(request).execute();
            System.out.println("Put Call Response:  "+response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!response.isSuccessful()) try {
            throw new IOException("Unexpected code " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void refresh(UserDataModel userData){
        //Starts a thread to pull in an object
        try {
            getHttpData(userData.getUsername(), userData.getPassword(), "/get_current_appliances/", "appliance");
            getHttpData(userData.getUsername(), userData.getPassword(), "/home_information_list/1","home");
            //startAlarm();
            //updateApplianceTimeLapse(userData.getUsername(), userData.getPassword(), "/appliance_timelapse/");
            //showEditTimeDialog(11, "Stove");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showEditTimeDialog(int applianceId, String applianceName, UserDataModel userdata){
        // used to show an edit timelapse dialog box
        FragmentManager fm = getSupportFragmentManager();
        TimeLapseAlarmUpdateDialog editTimeDialog = TimeLapseAlarmUpdateDialog.newInstance("Some Title", applianceId, applianceName);
        editTimeDialog.show(fm, "timelapse_alert_dialog");

        refresh(userdata);
    }




    public void wearableNotification(UserDataModel userData){
        //---------------------------------------
        int notificationId = 001;
        //Sets a notification action to go to the dashboard and view more detail
        Intent mainActivityIntent = new Intent(this, MainScreenActivity.class);

        //Sends object to MainScreenActivity
        mainActivityIntent.putExtra("UserModel", userData);

        //creates the pending intent for the notification action
        PendingIntent mainActivityPendingIntent =
                PendingIntent.getActivity(this, 0, mainActivityIntent, 0);

        // Create a wearable only action
        NotificationCompat.Action wearableOnlyAction =
                new NotificationCompat.Action.Builder(R.drawable.abc_ic_menu_moreoverflow_mtrl_alpha,
                        "Check Dashboard", mainActivityPendingIntent)
                        .build();

        // Create a WearableExtender to add functionality for wearables
        NotificationCompat.WearableExtender wearableExtender =
                new NotificationCompat.WearableExtender()
                        .setCustomSizePreset(Notification.WearableExtender.SIZE_FULL_SCREEN)
                        .setContentIcon(R.mipmap.alert)
                        .setHintHideIcon(true);



        // Create a NotificationCompat.Builder to build a standard notification
        // then extend it with the WearableExtender
        Notification notif = new NotificationCompat.Builder(this)
                .setPriority(1)
                .setContentTitle(getString(R.string.warning_title))
                .setContentText("The stove is unattended.")
                .setSmallIcon(R.mipmap.alert)
                .setAutoCancel(true)
                .setVibrate(new long[] {1000})
                .extend(wearableExtender.addAction(wearableOnlyAction))
                .build();

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        // Issue the notification with notification manager.
        notificationManager.notify(notificationId, notif);

        //-----------------------------------------


    }

    public void phoneNotification(){
        //sets the notification
        NotificationCompat.Builder mAlarmNotificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.alert)
                .setContentTitle("Something is on!")
                .setContentText("Hey your stove is on!")
                .setTicker("Your stove is on!!!!");

        //build an intent here to take to an application activity

        //app issues the notification
        NotificationManager mApplianceWarningNotificationManager =
                (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        //notificationID allows us to update the notification
        //best used with sleep, etc.
        mApplianceWarningNotificationManager.notify(notificationID, mAlarmNotificationBuilder.build());


    }

    public void startAlarm(Intent applianceWarningIntent) {
        //cancels existing alarms
        cancelAlarm();

        //determines future alarms
        applianceWarningPendingIntent = PendingIntent.getBroadcast(MainScreenActivity.this,0,applianceWarningIntent, 0);

        //Sets new alarm
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 8000;
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, applianceWarningPendingIntent);
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();


    }

    public void cancelAlarm(){
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(applianceWarningPendingIntent);
        Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();

    }

}


