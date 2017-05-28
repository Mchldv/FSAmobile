package com.dvlchm.fsa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by WINDA on 5/23/2017.
 */

public class AssignmentAdapter extends ArrayAdapter<AssignmentObject> {

//    private static final String URL="http://139.59.235.178/pembuatankapal/hapuskomponen.php";
    private static final String URL="https://winda1996.000webhostapp.com/FSA/action/actionMobile_MakeReport.php";

    public AssignmentAdapter(Context context, int resource, List<AssignmentObject> objects) {
        super(context, resource, objects);
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AssignmentObject assignmentObject = (AssignmentObject) getItem(position);
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.assignment_layout,parent,false);
        }
//        TextView assignmentId = (TextView) convertView.findViewById(R.id.tvAssignmentId);
        TextView locationName = (TextView) convertView.findViewById(R.id.tvLocation);
        Button btnSend = (Button) convertView.findViewById(R.id.btnSendOffline);
        Button makeReport = (Button) convertView.findViewById(R.id.btnMakereport);

        final String assignment_id = assignmentObject.getAssignmentId();
        final String user = AssignmentActivity.username;
        final String id_lokasi = assignmentObject.getIdLokasi();
        final String id_surveyor = assignmentObject.getIdSurveyor();
        final Bundle bundle = new Bundle();
        if(assignmentObject.getDone()==1) {
            if(MainActivity.InternetConnection)
            btnSend.setEnabled(true);
            makeReport.setEnabled(false);
            bundle.putString("surroundingHy", assignmentObject.getKeb_TM().toString());
            bundle.putString("cutlerHy", assignmentObject.getKeb_AM().toString());
            bundle.putString("foodH", assignmentObject.getNil_K().toString());
            bundle.putString("address", assignmentObject.getAddress());
            bundle.putString("alasan", assignmentObject.getExcuse());
            bundle.putString("image", assignmentObject.getImage());
            bundle.putString("latitude",assignmentObject.getLat());
            bundle.putString("longitude",assignmentObject.getLongitude());
        }
        bundle.putString("idAssignment",assignment_id);
        bundle.putString("username",user);
        bundle.putString("idLokasi",id_lokasi);
        bundle.putString("idSurveyor",id_surveyor);

        Toast.makeText(getContext(),user,Toast.LENGTH_LONG).show();

        if(assignmentObject.getLocationName().equals("0")){
            locationName.setText("There is no assignment.");
            btnSend.setVisibility(View.GONE);
            makeReport.setVisibility(View.GONE);
//            locationName.setText("");
        }
        else {
//            assignmentId.setText(assignmentObject.getAssignmentId());
            locationName.setText(assignmentObject.getLocationName());
            makeReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bukaIntent(MakeReportActivity.class,bundle);
                }
            });
            btnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SendOffline(bundle);
                }
            });
        }



//        assignmentId.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                bukaIntent(MakeReportActivity.class,bundle);
//            }
//        });

        return convertView;
    }

    private void SendOffline(Bundle b) {
        final String surroundingH= b.getString("surroundingHy");
        final String address= b.getString("address");
        final String username= b.getString("username");
        final String foodH= b.getString("foodH");
        final String cutleryH= b.getString("cutlerHy");
        final String stringImage= b.getString("image");
        final String id_assignment= b.getString("idAssignment");
        final String id_lokasi= b.getString("idLokasi");
        final String id_surveyor= b.getString("idSurveyor");
        //final String lat= b.getString("surroundingHy");
        //final String long_= b.getString("surroundingHy");


        StringRequest stringRequest = new StringRequest (Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getContext(),response, Toast.LENGTH_LONG).show();
                        //deleteTask(id_assignment);
                        updateDone(id_assignment);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(),error.toString(),Toast.LENGTH_LONG).show();
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
                //params.put("latitude",lat_);
                //params.put("longitude",long_);

                return params;
            }
        };

        if(MainActivity.InternetConnection){
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
        }
    }

    private void updateDone(String id_assignment) {
        AssignmentDataBaseAdapter datasource = new AssignmentDataBaseAdapter(getContext());
        datasource.open();
        datasource.updateSend(id_assignment);
        datasource.close();
    }

    private void deleteTask(String id_assignment) {
        AssignmentDataBaseAdapter datasource = new AssignmentDataBaseAdapter(getContext());
        datasource.open();
        datasource.delteTask(id_assignment);
        datasource.close();
    }

    void bukaIntent(Class x,Bundle bundle){
        Intent myIntent = new Intent(getContext(),x);
        myIntent.putExtras(bundle);
        ((Activity)getContext()).startActivityForResult(myIntent,0);
    }
}
