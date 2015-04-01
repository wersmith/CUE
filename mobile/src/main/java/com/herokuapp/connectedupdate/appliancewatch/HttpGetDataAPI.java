package com.herokuapp.connectedupdate.appliancewatch;

import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by wersm_000 on 3/22/2015.
 */
public class HttpGetDataAPI {
    private final OkHttpClient client = new OkHttpClient();
    public static final String TAG = LoginActivity.class.getSimpleName();
    public String json;

    public void run(String username, String password, String apiEndPoint) throws Exception {
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
                System.out.println(json);
            }
        });

    }
}
