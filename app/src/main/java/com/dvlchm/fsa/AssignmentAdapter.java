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

import java.util.List;


/**
 * Created by WINDA on 5/23/2017.
 */

public class AssignmentAdapter extends ArrayAdapter<AssignmentObject> {

//    private static final String URL="http://139.59.235.178/pembuatankapal/hapuskomponen.php";

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
        }



//        assignmentId.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                bukaIntent(MakeReportActivity.class,bundle);
//            }
//        });

        return convertView;
    }

    void bukaIntent(Class x,Bundle bundle){
        Intent myIntent = new Intent(getContext(),x);
        myIntent.putExtras(bundle);
        ((Activity)getContext()).startActivityForResult(myIntent,0);
    }
}
