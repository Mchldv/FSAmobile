package com.dvlchm.fsa;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity
{
    public static boolean InternetConnection=false;
    private static final int MY_REQUEST_FINE_LOC = 867;
    private static final int MY_REQUEST_CAMERA = 243;

    Button btnSignIn,btnMakeReport;
    LoginDataBaseAdapter loginDataBaseAdapter;
    Bundle bundle;

    NetworkStateReceiver networkStateReceiver = new NetworkStateReceiver(new NetworkStateReceiver.NetworkListener() {
        @Override

        public void onNetworkAvailable() {
//            btnMakeReport.setEnabled(false);
            InternetConnection=true;
            btnSignIn.setEnabled(true);
        }

        @Override
        public void onNetworkUnavailable() {
//            btnMakeReport.setEnabled(true);
            InternetConnection=false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bundle = new Bundle();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            Log.e("request location : ","disabled");

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_REQUEST_FINE_LOC);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {

            // TODO: Consider calling
            Log.e("request camerqa : ","disabled");

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},MY_REQUEST_CAMERA);
        }

        // create a instance of SQLite Database
        loginDataBaseAdapter=new LoginDataBaseAdapter(this);
        loginDataBaseAdapter=loginDataBaseAdapter.open();
        loginDataBaseAdapter.insertEntry("vennyc","123");
        loginDataBaseAdapter.insertEntry("afann","123");
        loginDataBaseAdapter.insertEntry("nellac","123");

        //Log.e("id_user afann",loginDataBaseAdapter.cek_username("afann").toString());
        //Log.e("id_user vennyc",loginDataBaseAdapter.cek_username("vennyc"));
        //Log.e("id_user nellac",loginDataBaseAdapter.cek_username("nellac"));

        // Get The Refference Of Buttons
        btnSignIn=(Button)findViewById(R.id.buttonSignIn);

        registerReceiver(networkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    void openIntent(Class x, Bundle bundle){
        Intent myIntent = new Intent(getBaseContext(),x);
        myIntent.putExtras(bundle);
        startActivityForResult(myIntent,0);
    }

    // MethoD to handleClick Event of Sign In Button
    public void signIn(View V)
    {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.sign_up);
        dialog.setTitle("Login Form");

        // get the Refferences of views
        final EditText editTextUserName=(EditText)dialog.findViewById(R.id.editTextUserNameToLogin);
        final EditText editTextPassword=(EditText)dialog.findViewById(R.id.editTextPasswordToLogin);

        Button btnSignIn=(Button)dialog.findViewById(R.id.buttonSignIn);

        // Set On ClickListener
        btnSignIn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // get The User name and Password
                String userName=editTextUserName.getText().toString();
                String password=editTextPassword.getText().toString();

                bundle.putString("username",userName);

                // fetch the Password form database for respective user name
                String storedPassword=loginDataBaseAdapter.getSinlgeEntry(userName);

                // check if the Stored password matches with  Password entered by user
                if(password.equals(storedPassword))
                {
                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    openIntent(AssignmentActivity.class, bundle);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Username or Password does not match", Toast.LENGTH_LONG).show();
                }
            }
        });

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close The Database
        loginDataBaseAdapter.close();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_REQUEST_FINE_LOC:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    return;
                }
            }
            case MY_REQUEST_CAMERA:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    return;
                }
            }
        }

    }
}