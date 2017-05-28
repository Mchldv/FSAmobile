package com.dvlchm.fsa;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssignmentActivity extends Activity {

    static final String JSON_URL = "https://winda1996.000webhostapp.com/FSA/action/actionMobile_ListAssignment.php";
    ListView listView;
    Bundle bundle;
    public static String username;
    StringRequest stringRequest;
    Button btnRefresh;

    @Override
    protected void onResume() {
        super.onResume();
        //setContentView(R.layout.activity_assignment);

        if(MainActivity.InternetConnection) {
            RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
            stringRequest = new StringRequest(Request.Method.POST,JSON_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            showAssignment(response);
                            Log.e("receive","true");
                            btnRefresh.setEnabled(true);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("error","error_response");
                            Toast.makeText(getBaseContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                            btnRefresh.setEnabled(true);
                            showAssignment();
                        }
                    }){
                @Override
                protected Map<String,String> getParams() {
                    Map<String,String> params = new HashMap<>();
                    params.put("username",username);

                    return params;
                }
            };

            Log.e("request again",stringRequest.toString());
            requestQueue.add(stringRequest);
            Toast.makeText(getBaseContext(),"Fetch Data Online",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getBaseContext(),"No Internet Connection",Toast.LENGTH_LONG).show();
            showAssignment();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);

        bundle = getIntent().getExtras();
        //Button btnSend = (Button)findViewById(R.id.btnSendOffline);
        //btnSend.setEnabled(false);
        username = bundle.getString("username");
        btnRefresh = (Button)findViewById(R.id.btnRefreshList);

        listView = (ListView)findViewById(R.id.listViewAssignment);

        stringRequest = new StringRequest(Request.Method.POST,JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        showAssignment(response);
                        Log.e("receive","true");
                        btnRefresh.setEnabled(true);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error","error_response");
                        Toast.makeText(getBaseContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                        btnRefresh.setEnabled(true);
                        showAssignment();
                    }
                }){
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("username",username);

                return params;
            }
        };



        if(MainActivity.InternetConnection) {
            RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
            Log.e("request",stringRequest.toString());
            Toast.makeText(getBaseContext(),"Fetch Data Online",Toast.LENGTH_SHORT).show();
            requestQueue.add(stringRequest);
        }
        else {
            Toast.makeText(getBaseContext(),"No Internet Connection",Toast.LENGTH_LONG).show();
            //enable internet
        }

    }

    public void Refresh(View view)
    {
        btnRefresh.setEnabled(false);
        this.onResume();
    }

    void showAssignment(String response)
    {
        ParseJson pj = new ParseJson(response);
        pj.lihatAssignmentParse(getApplicationContext(),username);
        add_to_adapter();
    }

    private void add_to_adapter() {
        List<AssignmentObject> assignmentObjects = new ArrayList <AssignmentObject>();

        AssignmentDataBaseAdapter datasource = new AssignmentDataBaseAdapter(this);
        datasource.open();
        assignmentObjects = datasource.getAllEntry(username);
        datasource.close();
        AssignmentAdapter dummy = new AssignmentAdapter(this,0,assignmentObjects);

        listView.setAdapter(dummy);
    }

    void showAssignment()
    {
        add_to_adapter();
    }

}
