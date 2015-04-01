package com.herokuapp.connectedupdate.appliancewatch;

import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.security.PublicKey;


public class LoginActivity extends ActionBarActivity {

    public EditText username;
    public EditText password;
    public Button login;
    public httpLogin mHttpLogin = new httpLogin();
    public UserDataModel userData = new UserDataModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        /** default fragment  use for later UI nav
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        */

        //stores login form variables to text
        username = (EditText) findViewById(R.id.editUsername);
        password = (EditText) findViewById(R.id.editPassword);
        login = (Button) findViewById(R.id.buttonLogin);

    }

    public void login(View view) throws Exception {
        final TextView btnClickTxt = (TextView) findViewById(R.id.textButtonClick);

        //Creates a context for navigation
        final Context context = this;
        String newText = mHttpLogin.run(username.getText().toString(),password.getText().toString());


        if(newText.equals("Success")){
            //If the log in is successful
            //set logged in user data to pass on to activities
            userData.setPassword(password.getText().toString());
            userData.setUsername(username.getText().toString());

            //Creates intent to take the user to the main screen
            Intent intent = new Intent(context, MainScreenActivity.class);

            //Sends object to MainScreenActivity
            intent.putExtra("UserModel", userData);
            startActivity(intent);
        }

        btnClickTxt.setText(newText);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */

    /**
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_login, container, false);
            return rootView;
       }
    }
    */
}
