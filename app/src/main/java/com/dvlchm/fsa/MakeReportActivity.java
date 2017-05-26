package com.dvlchm.fsa;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.os.ResultReceiver;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.security.AccessController.getContext;

public class MakeReportActivity extends Activity {

    private static final int MY_REQUEST_FINE_LOC = 867;
    private static final String URL="https://winda1996.000webhostapp.com/FSA/action/actionMobile_MakeReport.php";

    EditText editTextUserName, editTextKebAlatMakan, editTextKebTempatMakan,
            editTextNilaiKualitas, editTextAlasan, editTextLokasi;
    Button btnSaveSurvey,btnRefresh, btnTakePhoto;
    public static boolean InternetConnection;
    SurveyDataBaseAdapter surveyDataBaseAdapter;
    Bundle bundle;
    String username, id_assignment, id_lokasi, id_surveyor;
    private static final int CAMERA_REQUEST = 1888;
    public static int count = 0;
    ImageView image;
    Bitmap mphoto;

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
//                checkGPS();
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
        id_assignment = bundle.getString("idAssignment");
        id_lokasi = bundle.getString("idLokasi");
        id_surveyor = bundle.getString("idSurveyor");

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
        btnTakePhoto = (Button)findViewById(R.id.buttonTakePhoto);
        image = (ImageView)findViewById(R.id.imageView);

        editTextUserName.setText(username);
        editTextUserName.setEnabled(false);
        image.setVisibility(View.GONE);

        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
        File newdir = new File(dir);
        newdir.mkdirs();

        if (MainActivity.InternetConnection) {
            editTextAlasan.setVisibility(View.GONE);
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
                if(MainActivity.InternetConnection)
                {
                    SubmitReport();
                }
                else
                {
                    if(userName.equals("")||kebAM.equals("")||kebTM.equals("")||nilK.equals("")||alasan.equals("")) {
                        Toast.makeText(getApplicationContext(), "tidak ada internet : Isi semua kolom", Toast.LENGTH_LONG).show();
                        return;
                    }
                    else {
                        // save in local directory
//                        String data = MakeSurvey(userName,lokasi,kebAM,kebTM,nilK,alasan);

//                        writeToFile(data,getApplicationContext());
//                        //surveyDataBaseAdapter.insertEntry(userName, kebAM, kebAM, nilK);
//                        Toast.makeText(getApplicationContext(), "Data Survey Saved ", Toast.LENGTH_LONG).show();
//                        String tulisan = readFromFile(getApplicationContext(),"survey.txt");
//                        Toast.makeText(getApplicationContext(), tulisan, Toast.LENGTH_LONG).show();
//                        Intent intent = new Intent(getApplicationContext(),TakePhoto.class);
//                        startActivity(intent);
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

    void openIntent(Class x, Bundle bundle){
        Intent myIntent = new Intent(getBaseContext(),x);
        myIntent.putExtras(bundle);
        startActivityForResult(myIntent,0);
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

    public void SubmitReport()
    {
        final String address = editTextLokasi.getText().toString();
        final String foodH = editTextNilaiKualitas.getText().toString();
        final String cutleryH = editTextKebAlatMakan.getText().toString();
        final String surroundingH = editTextKebTempatMakan.getText().toString();
        final String stringImage = getStringImage(mphoto);
        final String lat_ = String.valueOf(latitude);
        final String long_ = String.valueOf(longitude);

        if (address.equals("") || foodH.equals("") || cutleryH.equals("") || surroundingH.equals("") || stringImage.equals("")) {
            Toast.makeText(getBaseContext(),"All field and photo is required.",Toast.LENGTH_LONG).show();
        }
        else {
            StringRequest stringRequest = new StringRequest (Request.Method.POST, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(getBaseContext(),response, Toast.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getBaseContext(),error.toString(),Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String,String> getParams() {
                    Map<String,String> params = new HashMap<>();
                    params.put("user", username);
                    params.put("address",address);
                    params.put("foodHy", foodH);
                    params.put("cutleryHy",cutleryH);
                    params.put("surroundingHy",surroundingH);
                    params.put("image",stringImage);
                    params.put("idAssignment",id_assignment);
                    params.put("idLokasi",id_lokasi);
                    params.put("idSurveyor",id_surveyor);
                    params.put("latitude",lat_);
                    params.put("longitude",long_);

                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
            requestQueue.add(stringRequest);
        }
    }

    public void takeImageFromCamera(View view) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            mphoto = (Bitmap) data.getExtras().get("data");
            image.setVisibility(View.VISIBLE);
            image.setImageBitmap(mphoto);
        }
    }

    public String getStringImage(Bitmap bmp){
        if (bmp!=null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] imageBytes = baos.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            return encodedImage;
        }
        else return "";
    }


}