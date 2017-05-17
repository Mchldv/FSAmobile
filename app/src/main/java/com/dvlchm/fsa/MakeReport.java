package com.dvlchm.fsa;

import android.Manifest;
import android.content.Context;
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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class MakeReport extends AppCompatActivity {

    private static final int MY_REQUEST_FINE_LOC = 867;
    EditText editTextUserName, editTextKebAlatMakan, editTextKebTempatMakan,
            editTextNilaiKualitas, editTextAlasan, editTextLokasi;
    Button btnSaveSurvey;
    public static boolean InternetConnection;

    SurveyDataBaseAdapter surveyDataBaseAdapter;

    protected Location mLastLocation;

    class AddressResultReceiver extends ResultReceiver{

        /**
         * Create a new ResultReceive to receive results.  Your
         * {@link #onReceiveResult} method will be called from the thread running
         * <var>handler</var> if given, or from an arbitrary thread if null.
         *
         * @param handler
         */
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            String hasil= resultData.getString(Constants.RESULT_DATA_KEY);
            editTextLokasi.setText(hasil);
        }
    }

    private AddressResultReceiver mResultReceiver=new AddressResultReceiver(new Handler());

    double longitude, latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_report);

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

        if (MainActivity.InternetConnection) {
            editTextAlasan.setVisibility(View.INVISIBLE);

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
            else
            {
                Log.e("Allow : ","GPS");
                LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

                LocationListener ll = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
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

            }
            //editTextLokasi.setVisibility(View.INVISIBLE);
            btnSaveSurvey.setText("Kirim");
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
                        surveyDataBaseAdapter.insertEntry(userName, kebAM, kebAM, nilK);
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
                        StringBuilder sb= new StringBuilder();
                        sb.append("'Surveyor : '");
                        sb.append(userName);
                        sb.append("'Lokasi : '");
                        sb.append(lokasi);
                        sb.append("'kebAM : '");
                        sb.append(kebAM);
                        sb.append("'kebTM : '");
                        sb.append(kebTM);
                        sb.append("'nilK : '");
                        sb.append(nilK);
                        sb.append("'Alasan : '");
                        sb.append(alasan);

                        String data = sb.toString();
                        writeToFile(data,getApplicationContext());
                        //surveyDataBaseAdapter.insertEntry(userName, kebAM, kebAM, nilK);
                        Toast.makeText(getApplicationContext(), "Data Survey Saved ", Toast.LENGTH_LONG).show();
                        String tulisan = readFromFile(getApplicationContext(),"survey.txt");
                        Toast.makeText(getApplicationContext(), tulisan, Toast.LENGTH_LONG).show();
                    }
                }
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
        /*try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("survey.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }*/
        /*try {
            FileWriter fw = new FileWriter("survey.txt");
            for (int i=0;i<data.length();i++)
            {
                fw.append(data.charAt(i));
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception", "File write failed: " + e.toString());
        }*/
        try {
            FileOutputStream fos = openFileOutput("survey.txt",context.MODE_PRIVATE);
            Log.e("Create", "File open");
            Log.e("Write", "String write" + data);
            for (int i=0;i<data.length();i++)
            {
                fos.write(data.charAt(i));
                Log.e("Write", "File write" + data.charAt(i));
            }
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
            //BufferedReader bf = new BufferedReader(new FileReader(file));
            String ret = convertStreamtoString(fis);
            fis.close();
            //FileInputStream fIS = openFileInput(fileName);
            //Log.e("open",fIS.toString());

            /*InputStreamReader inputStreamReader = new InputStreamReader(context.openFileInput(fileName));
            BufferedReader buffread = new BufferedReader(inputStreamReader);
            while(buffread.readLine()!= null)
            {
                Log.e("baca", String.valueOf(inputStreamReader.read()));
                data.append(buffread.readLine());
            }*/
            /*
            try {
                while(fIS.read()>0)
                {
                    Log.e("baca", String.valueOf(fIS.read()));
                    data.append(fIS.read());
                }*/
            return ret;
        } catch (Exception e) {
            //e.printStackTrace();
            Log.e("Exception", "File empty found: " + e.toString());
        }
        return "Ada yang salah";
        /*
        String filePath = context.getFilesDir() + "/" + fileName;
        StringBuilder data;
        File youFile = new File(filePath);
        FileInputStream is;
        try {
            is= new FileInputStream(youFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while(is.available()>0)
        {

        }
        */
    }

    protected void startIntentService()
    {
        Intent intent = new Intent(this,FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER,mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA,mLastLocation);
        startService(intent);
    }

}