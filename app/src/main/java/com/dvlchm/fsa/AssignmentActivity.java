package com.dvlchm.fsa;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
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
import java.util.Map;

public class AssignmentActivity extends Activity {

    static final String JSON_URL = "https://winda1996.000webhostapp.com/FSA/action/actionMobile_ListAssignment.php";
    ListView listView;
    Bundle bundle;
    public static String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);

        bundle = getIntent().getExtras();
        username = bundle.getString("username");

        listView = (ListView)findViewById(R.id.listViewAssignment);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        showAssignment(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getBaseContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("username",username);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
        requestQueue.add(stringRequest);

    }

    void showAssignment(String response)
    {
        ParseJson pj = new ParseJson(response);
        ArrayList<AssignmentObject> assignmentObjects = new ArrayList <AssignmentObject>();
        AssignmentAdapter dummy = new AssignmentAdapter(this,0,assignmentObjects);
//        Toast.makeText(getBaseContext(),response,Toast.LENGTH_LONG).show();
        pj.lihatAssignmentParse(dummy);

        listView.setAdapter(dummy);
    }
}
