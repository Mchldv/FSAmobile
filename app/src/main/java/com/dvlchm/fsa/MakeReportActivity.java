package com.dvlchm.fsa;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.os.ResultReceiver;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Locale;

public class MakeReportActivity extends Activity {

    private static final int MY_REQUEST_FINE_LOC = 867;
    EditText editTextUserName, editTextKebAlatMakan, editTextKebTempatMakan,
            editTextNilaiKualitas, editTextAlasan, editTextLokasi;
    Button btnSaveSurvey,btnRefresh;
    public static boolean InternetConnection;
    JSONObject Survey;
    SurveyDataBaseAdapter surveyDataBaseAdapter;
    Bundle bundle;
    String username;

    protected Location mLastLocation;

    LocationManager lm;
    LocationListener ll;

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            String hasil = resultData.getString(Constants.RESULT_DATA_KEY);
            editTextLokasi.setText(hasil);
            btnRefresh.setEnabled(true);
            if (editTextLokasi.getText().toString().equals("Tidak Ditemukan")) {
                Log.e("Intent Service", "jalan");
                checkGPS();
            }
        }
    }

    private AddressResultReceiver mResultReceiver=new AddressResultReceiver(new Handler());

    double longitude, latitude;

    @Override
    protected void onResume() {
        super.onResume();
        //checkGPS();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_report);

        bundle = getIntent().getExtras();
        username = bundle.getString("username");
//        Toast.makeText(getBaseContext(),username, Toast.LENGTH_LONG).show();

        Log.e("muncul", "make report ditampilkan");

        // get Instance  of Database Adapter
        surveyDataBaseAdapter = new SurveyDataBaseAdapter(this);
        surveyDataBaseAdapter = surveyDataBaseAdapter.open();

        // Get Refferences of Views
        editTextUserName = (EditText) findViewById(R.id.editTextUserName);
        editTextKebAlatMakan = (EditText) findViewById(R.id.editTextKebersihanAlatMakan);
        editTextNilaiKualitas = (EditText) findViewById(R.id.editTextNilaiKualitas);
        editTextKebTempatMakan = (EditText) findViewById(R.id.editTextKebersihanTempatMakan);
        editTextAlasan = (EditText) findViewById(R.id.editTextAlasan);
        editTextLokasi = (EditText) findViewById(R.id.editTextLokasi);
        btnSaveSurvey = (Button) findViewById(R.id.buttonSave);
        btnRefresh = (Button)findViewById(R.id.buttonRefresh);

        editTextUserName.setText(username);
        editTextUserName.setEnabled(false);

        if (MainActivity.InternetConnection) {
            editTextAlasan.setVisibility(View.INVISIBLE);
            checkGPS();
            btnSaveSurvey.setText("Send");
            editTextLokasi.setEnabled(false);


        }

        NetworkStateReceiver networkStateReceiver = new NetworkStateReceiver(new NetworkStateReceiver.NetworkListener() {
            @Override
            public void onNetworkAvailable() {
                MainActivity.InternetConnection=true;
            }

            @Override
            public void onNetworkUnavailable() {
                MainActivity.InternetConnection=false;
            }
        });

        btnSaveSurvey.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                String userName=editTextUserName.getText().toString();
                String kebAM=editTextKebAlatMakan.getText().toString();
                String kebTM=editTextKebTempatMakan.getText().toString();
                String nilK=editTextNilaiKualitas.getText().toString();
                String alasan=editTextAlasan.getText().toString();
                String lokasi=editTextLokasi.getText().toString();

                // check if any of the fields are vaccant
                if(MainActivity.InternetConnection){
                    //lokasi = getlocation//;
                    if(userName.equals("")||kebAM.equals("")||kebTM.equals("")||nilK.equals("")) {
                        Toast.makeText(getApplicationContext(), "ada internet : Isi semua kolom", Toast.LENGTH_LONG).show();
                        return;
                    }
                    else
                    {
                        // send the Data to Database
                        //surveyDataBaseAdapter.insertEntry(userName, kebAM, kebAM, nilK);
                        String data = MakeSurvey(userName,lokasi,kebAM,kebTM,nilK,alasan);
                        Toast.makeText(getApplicationContext(), "Data Survey Created ", Toast.LENGTH_LONG).show();
                    }
                }else
                {
                    if(userName.equals("")||kebAM.equals("")||kebTM.equals("")||nilK.equals("")||alasan.equals("")) {
                        Toast.makeText(getApplicationContext(), "tidak ada internet : Isi semua kolom", Toast.LENGTH_LONG).show();
                        return;
                    }
                    else {
                        // save in local directory
                        String data = MakeSurvey(userName,lokasi,kebAM,kebTM,nilK,alasan);

                        writeToFile(data,getApplicationContext());
                        //surveyDataBaseAdapter.insertEntry(userName, kebAM, kebAM, nilK);
                        Toast.makeText(getApplicationContext(), "Data Survey Saved ", Toast.LENGTH_LONG).show();
                        String tulisan = readFromFile(getApplicationContext(),"survey.txt");
                        Toast.makeText(getApplicationContext(), tulisan, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(),TakePhoto.class);
                        startActivity(intent);
                    }
                }
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkGPS();
                btnRefresh.setEnabled(false);
            }
        });
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

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        surveyDataBaseAdapter.close();
    }

    private void writeToFile(String data,Context context) {
        try {
            FileOutputStream fos = openFileOutput("survey.txt",context.MODE_PRIVATE);
            Log.e("Create", "File open");
            Log.e("Write", "String write" + data);
            fos.write(data.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String convertStreamtoString(InputStream is) throws Exception
    {
        StringBuilder data = new StringBuilder();

        BufferedReader bf= new BufferedReader(new InputStreamReader(is));
        while(bf.readLine()!= null)
        {
            data.append(bf.readLine());
        }
        bf.close();
        return data.toString();
    }

    private String readFromFile(Context context, String fileName)
    {
        Log.e("read",fileName);
        try {
            File file = new File(getFilesDir(),fileName);
            FileInputStream fis = openFileInput(fileName);
            int c;
            String temp="";
            while( (c = fis.read()) != -1){
                temp = temp + Character.toString((char)c);
            }
            fis.close();
            return temp;
        } catch (Exception e) {
            //e.printStackTrace();
            Log.e("Exception", "File empty found: " + e.toString());
        }
        return "Ada yang salah";
    }

    protected void startIntentService()
    {
        Intent intent = new Intent(this,FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER,mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA,mLastLocation);
        startService(intent);
    }

    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public boolean checkGPS()
    {
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
            return true;
        }
        else
        {
            Log.e("Allow : ","GPS");
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                ll = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        if(location!=null)
                        {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };

                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,ll);

                mLastLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                startIntentService();
                return true;

            }
            else
            {
                showGPSDisabledAlertToUser();
                return false;
            }
        }
    }

    public String MakeSurvey(String userName,String lokasi,String kebAM,String kebTM,String nilK,String alasan)
    {
        Survey = new JSONObject();
        try {
            if(!userName.equals(""))
                Survey.put("Surveyor",userName);
            if(!lokasi.equals(""))
                Survey.put("Lokasi",lokasi);
            if(!kebAM.equals(""))
                Survey.put("kebAM",kebAM);
            if(!kebTM.equals(""))
                Survey.put("kebTM",kebTM);
            if(!nilK.equals(""))
                Survey.put("nilK",nilK);
            if(!alasan.equals(""))
                Survey.put("Alasan",alasan);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Survey.toString();
    }
}