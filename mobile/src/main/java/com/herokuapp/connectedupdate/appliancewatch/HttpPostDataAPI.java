package com.herokuapp.connectedupdate.appliancewatch;

import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by wersm_000 on 3/25/2015.
 */
public class HttpPostDataAPI {

    //Sets the Request Body string type
    public static final MediaType MEDIA_TYPE_JSON
            = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();
    public static final String TAG = LoginActivity.class.getSimpleName();

    public void run(final String username, final String password, String apiEndPoint) throws Exception {
        //sets login parameter
        String credential = Credentials.basic(username, password);
        String mAPIEndPoint = "http://connectedupdate.herokuapp.com/" + apiEndPoint;

        //Builds a JSON String for the request
        String jsonPost = "{'applianceName'='Car',"
           +"'timeLapseAlarm'=30"
           + "}";

        RequestBody newBody = RequestBody.create(MEDIA_TYPE_JSON, jsonPost);

        // builds POST request
        Request request = new Request.Builder()
                .header("Authorization", credential)
                .url(mAPIEndPoint)
                .post(newBody)
                .build();

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

    }
}
