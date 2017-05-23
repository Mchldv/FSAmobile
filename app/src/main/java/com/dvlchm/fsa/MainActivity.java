package com.dvlchm.fsa;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
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
            btnSignIn.setEnabled(false);
        }
    });

    /*private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private void enableMobileData(Context context, boolean enabled) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        final ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final Class conmanClass = Class.forName(cm.getClass().getName());
        final Field connectivityManagerField = conmanClass.getDeclaredField("mService");
        connectivityManagerField.setAccessible(true);
        final Object connectivityManager = connectivityManagerField.get(cm);
        final Class connectivityManagerClass =  Class.forName(connectivityManager.getClass().getName());
        final Method setMobileDataEnabledMethod = connectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
        setMobileDataEnabledMethod.setAccessible(true);
        setMobileDataEnabledMethod.invoke(connectivityManager, enabled);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bundle = new Bundle();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {

            // TODO: Consider calling
            Log.e("lokasi : ","salah");

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_REQUEST_FINE_LOC);
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return;
        }

        // create a instance of SQLite Database
        loginDataBaseAdapter=new LoginDataBaseAdapter(this);
        loginDataBaseAdapter=loginDataBaseAdapter.open();
        loginDataBaseAdapter.insertEntry("vennyc","123");
        loginDataBaseAdapter.insertEntry("afann","123");
        loginDataBaseAdapter.insertEntry("nellac","123");

        // Get The Refference Of Buttons
        btnSignIn=(Button)findViewById(R.id.buttonSignIn);
//        btnMakeReport=(Button)findViewById(R.id.buttonMakeReport);

        //btnMakeReport.setEnabled(isNetworkAvailable());
        // Set OnClick Listener on MakeReportActivity button
//        btnMakeReport.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//
//                /// Create Intent for SignUpActivity  abd Start The Activity
//                Intent intentMakeReport=new Intent(getApplicationContext(),MakeReportActivity.class);
//                startActivity(intentMakeReport);
//            }
//        });
        registerReceiver(networkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    void openIntent(Class x, Bundle bundle){
        Intent myIntent = new Intent(getBaseContext(),x);
        myIntent.putExtras(bundle);
        startActivityForResult(myIntent,0);
    }

    // Methos to handleClick Event of Sign In Button
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
                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    openIntent(AssignmentActivity.class, bundle);
//                    Intent intentMakeReport=new Intent(getApplicationContext(),MakeReportActivity.class);
//                    startActivity(intentMakeReport);
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
        }

    }
}