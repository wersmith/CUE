package com.herokuapp.connectedupdate.appliancewatch;

import android.app.Activity;
import android.app.Notification;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.drive.events.ChangeListener;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okio.Buffer;

/**
 * Created by wersm_000 on 3/18/2015.
 */
public class MainScreenActivity extends Activity{

    private final OkHttpClient client = new OkHttpClient();
    public static final MediaType MEDIA_TYPE_JSON
            = MediaType.parse("application/json; charset=utf-8");
    public static final String TAG = LoginActivity.class.getSimpleName();
    public String json;
    private String mJSONString;
    private ImageView refreshButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainscreen);
        Bundle userDataBundle = getIntent().getExtras();
        final UserDataModel userData =  userDataBundle.getParcelable("UserModel");
        System.out.println("Main Screen:  " + userData.getUsername() + userData.getPassword());
        refreshButton = (ImageView) findViewById(R.id.refresh);

        //Starts a thread to pull in an object
        try {
            getHttpData(userData.getUsername(), userData.getPassword(), "/current_appliances/", "appliance");
            getHttpData(userData.getUsername(), userData.getPassword(), "/home_information_list/1","home");

            updateApplianceTimeLapse(userData.getUsername(), userData.getPassword(), "/appliance_timelapse/");
            //mJSONString = mAPIData.run(userData.getUsername(), userData.getPassword(), "/home_information_list/1");
            //JSONObject homeInfoArray = new JSONObject(mjsonString);
            //mAPIPut.run(userData.getUsername(),userData.getPassword(),"/appliance_timelapse/12");

        } catch (Exception e) {
            e.printStackTrace();
        }

        refreshButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v ) {
                refresh(userData);
            }
        });

    }



    public void getHttpData(String username, String password, String apiEndPoint, String parseType) throws Exception{
        final TextView addressText = (TextView) findViewById(R.id.addressTextDisplay);
        final TextView appliance1 = (TextView) findViewById(R.id.appliance_1);
        final TextView appliance2 = (TextView) findViewById(R.id.appliance_2);
        final String parsingType = parseType;
        String credential = Credentials.basic(username, password);
        String mAPIEndPoint = "http://connectedupdate.herokuapp.com/" + apiEndPoint;


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
        ArrayList<CurrentApplianceDataModel> applianceArray = new ArrayList<>();


        for(int aIterator = 0; aIterator < applianceInfo.length(); aIterator++){
            System.out.println(applianceInfo.getString(aIterator));
            CurrentApplianceDataModel appliance = new CurrentApplianceDataModel();
            JSONObject iteratorJSONObject = new JSONObject(applianceInfo.getString(aIterator));
            appliance.setSessionID(iteratorJSONObject.getInt("sessionID"));

            System.out.println(aIterator + ": " +appliance.getSessionID());
            appliance.setApplianceStartTime(iteratorJSONObject.getString("applianceStartTime"));
            appliance.setApplianceEndTime(iteratorJSONObject.getString("applianceEndTime"));

            JSONObject nestedApplianceName = new JSONObject(iteratorJSONObject.getString("applianceName"));
            appliance.setApplianceName(nestedApplianceName.getString("applianceName"));
            appliance.setInputId(nestedApplianceName.getInt("inputID"));
            appliance.setApplianceTimeLapse(nestedApplianceName.getString("timeLapseAlarm"));

            JSONObject nestedRoomName = new JSONObject(nestedApplianceName.getString("roomID"));
            appliance.setRoomName(nestedRoomName.getString("roomName"));

            applianceArray.add(aIterator, appliance);
        }


        //writes the appliance data to the UI

        for (int object=0; object < applianceArray.size(); object++){
            final String printObject = "Appliance: "
                    + applianceArray.get(object).getApplianceName()
                    + "\nTime Interval: "
                    + applianceArray.get(object).getApplianceTimeLapse()
                    + "\nRoom: "
                    + applianceArray.get(object).getRoomName();

            System.out.println("Printing Array: " + object);
            System.out.println(printObject);

            if (object==0){

                //Sets Address UI Information
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        appliance1.setText(printObject);
                    }
                });
            } else if (object==1){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        appliance2.setText(printObject);
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
        String mAPIEndPoint = "http://connectedupdate.herokuapp.com" + apiEndPoint + "11";

        System.out.println("Put Call, mAPIEndPoint:  " + mAPIEndPoint);


        //Builds a JSON String for the request
        String jsonPost = "'applianceName':Car,"
                +"'timeLapseAlarm':50,"
                + "'inputID':11";

        RequestBody newBody = RequestBody.create(MEDIA_TYPE_JSON, jsonPost);
        System.out.println("Put Call New Body:  "+ newBody.toString());

        // builds POST request
        Request request = new Request.Builder()
                .header("Authorization", credential)
                .url(mAPIEndPoint)
                .put(newBody)
                .build();
        System.out.println("Put Call New Body:  "+ request.body());

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
            System.out.println("Put Call:  "+response.body().string());
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
            getHttpData(userData.getUsername(), userData.getPassword(), "/current_appliances/", "appliance");
            getHttpData(userData.getUsername(), userData.getPassword(), "/home_information_list/1","home");

            updateApplianceTimeLapse(userData.getUsername(), userData.getPassword(), "/appliance_timelapse/");
            //mJSONString = mAPIData.run(userData.getUsername(), userData.getPassword(), "/home_information_list/1");
            //JSONObject homeInfoArray = new JSONObject(mjsonString);
            //mAPIPut.run(userData.getUsername(),userData.getPassword(),"/appliance_timelapse/12");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void wearableNotification(){
        //---------------------------------------
        int notificationId = 001;

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
                .setContentTitle("CUE Warning")
                .setContentText("The stove is unattended.")
                .setSmallIcon(R.mipmap.alert)
                .setVibrate(new long[] {1000})
                .extend(wearableExtender)
                .build();

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        // Issue the notification with notification manager.
        notificationManager.notify(notificationId, notif);

//-----------------------------------------


    }

}


