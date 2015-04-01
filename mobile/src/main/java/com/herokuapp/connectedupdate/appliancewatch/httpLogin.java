package com.herokuapp.connectedupdate.appliancewatch;

import android.util.Log;

import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.Proxy;

/**
 * Created by wersm_000 on 3/17/2015.
 */
public class httpLogin {

    //Member variable
    private final OkHttpClient client = new OkHttpClient();
    public static final String TAG = LoginActivity.class.getSimpleName();

    // Methods
    public String run(final String username, final String password) throws Exception {

        client.setAuthenticator(new Authenticator() {
            @Override
            public Request authenticate(Proxy proxy, Response response) {
                System.out.println("Authenticating for response: " + response);
                System.out.println("Challenges: " + response.challenges());
                System.out.println("Username: " + username + "Passoword: " + password);
                String credential = Credentials.basic("wersmith", "ReggieMiller31!");

                return response.request().newBuilder()
                        .header("Authorization", credential)
                        .build();
            }

            @Override
            public Request authenticateProxy(Proxy proxy, Response response) {
                return null;  //null indicates no attempt to authenticate
            }
        });
        System.out.println("Username: " + username + " Passoword: " + password);
        String credential = Credentials.basic(username, password);

        Request request = new Request.Builder()
                .header("Authorization", credential)
                .url("http://connectedupdate.herokuapp.com/")
                .build();

        Response response;
        String returnText;
        try {
            response = client.newCall(request).execute();
            System.out.println(response.body().string());
            returnText = "Success";
        } catch (Exception e){
            Log.e(TAG, "Exception caught: ", e);
            returnText = "Fail";
        }

        return returnText;
    }
}
