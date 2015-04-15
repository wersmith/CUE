package com.herokuapp.connectedupdate.appliancewatch;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by wersm_000 on 4/14/2015.
 */
public class TimeLapseAlarmUpdateDialog extends DialogFragment {


    private EditText mChangeTimeLapse;
    private Button update_timelapse;
    public static final MediaType MEDIA_TYPE_JSON
            = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();


    public TimeLapseAlarmUpdateDialog(){
        // Empty constructor required for DialogFragment
    }

    public static TimeLapseAlarmUpdateDialog newInstance(String title,
                                                         int applianceId,
                                                         String applianceName) {

        TimeLapseAlarmUpdateDialog frag = new TimeLapseAlarmUpdateDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("applianceId", applianceId);
        args.putString("applianceName", applianceName);
        frag.setArguments(args);

        return frag;
    }


    public View onCreateView (LayoutInflater inflater,
                              ViewGroup container,
                              Bundle savedInstanceState){


        View view = inflater.inflate(R.layout.timelapse_alert_dialog,
                container);

        mChangeTimeLapse = (EditText) view.findViewById(R.id.applianceTimeLapseInput);
        final TextView mQuestionText = (TextView) view.findViewById(R.id.textTimeLapseQuestion);

        //Get variables passed
        String title = getArguments().getString("title", "Enter Time");
        final int applianceId = getArguments().getInt("applianceId");
        final String applianceName = getArguments().getString("applianceName");
        final String applianceIdString = String.valueOf(getArguments().getInt("applianceId"));



        getDialog().setTitle(title);

        update_timelapse = (Button) view.findViewById(R.id.update_timelapse);


        update_timelapse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int mChangeTimeLapseTime;
                try {
                    //Turns the timelapse from a string to an integer.
                    mChangeTimeLapseTime = Integer.parseInt(mChangeTimeLapse.getText().toString());

                    //Sets the question to customize for the item clicked.
                    String questionTextConcat = "How many minutes would you like to wait for the " + applianceName + " before you are warned?";
                    mQuestionText.setText(questionTextConcat);

                    //sends a PUT call to the address.
                    putHttpDataTimeLapse("wersmith", "ReggieMiller31!", "appliance_timelapse", applianceIdString,
                            mChangeTimeLapseTime, applianceId, applianceName);

                } catch (Exception e){
                    e.printStackTrace();
                }

                //closes the dialog box
                getDialog().dismiss();
            }
        });

        mChangeTimeLapse.requestFocus();

        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return view;

    }

    public void putHttpDataTimeLapse(String username, String password,
                                     String apiEndPoint, String applianceNumber,
                                     int timeLapseAlarm, int inputID,
                                     String applianceName){
        //sets login parameter
        String credential = Credentials.basic(username, password);

        //make sure to include the slash at the end of the endpoint.
        String mAPIEndPoint = "http://connectedupdate.herokuapp.com/" +
                apiEndPoint + "/" + applianceNumber +"/";

        System.out.println("Put Call, mAPIEndPoint:  " + mAPIEndPoint);
        System.out.println("Put Call, mTimeLapseVar:  " + timeLapseAlarm);


        // Use to test and see what a JSON Object for a put should look like
        // Create a JSONObject
        JSONObject json = new JSONObject();

        // Add fields
        try {
            json.put("applianceName", applianceName);
            json.put("timeLapseAlarm", timeLapseAlarm);
            json.put("inputID", inputID);
            json.put("homeID",1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody newBody = RequestBody.create(MEDIA_TYPE_JSON, json.toString());
        System.out.println("Put Call New Body:  "+ newBody.toString());

        // builds PUT request
        Request request = new Request.Builder()
                .header("Authorization", credential)
                .url(mAPIEndPoint)
                .put(newBody)
                .build();
        System.out.println("Put Call Request String:  "+ request.body().toString());

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
}
