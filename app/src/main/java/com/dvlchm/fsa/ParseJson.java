package com.dvlchm.fsa;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ParseJson {

    public static String[] nama_list;
    public static String[] id_list;
    public static String[] idlokasi_list;
    public static String[] idsurveyor_list;


    public static final String JSON_ARRAY="result";
    public static final String JSON_COLOUMNID ="kolomid";
    public static final String JSON_COLOUMNNAMA ="kolomlocation";
    public static final String JSON_COLOUMIDLOKASI ="kolomidlokasi";
    public static final String JSON_COLOUMIDSURVEYOR ="kolomidsurveyor";

    private String json;
    private Context context;

    public ParseJson(String json) {
        this.json = json;
    }

    protected  void lihatAssignmentParse (Context context,String userName) {
        this.context= context;
        JSONObject jsonObject;
        JSONArray array;
        try{
            jsonObject = new JSONObject(json);
            array = jsonObject.getJSONArray(JSON_ARRAY);
            nama_list = new String[array.length()];
            id_list = new String[array.length()];
            idlokasi_list = new String[array.length()];
            idsurveyor_list = new String[array.length()];

            AssignmentDataBaseAdapter datasource = new AssignmentDataBaseAdapter(context);
            datasource.open();

            LoginDataBaseAdapter logindatasource = new LoginDataBaseAdapter(context);
            logindatasource.open();

            for(int i=0;i<array.length();i++){
                JSONObject jo = array.getJSONObject(i);
                nama_list[i]=jo.getString(JSON_COLOUMNNAMA);
                id_list[i]=jo.getString(JSON_COLOUMNID);
                idlokasi_list[i]=jo.getString(JSON_COLOUMIDLOKASI);
                idsurveyor_list[i]=jo.getString(JSON_COLOUMIDSURVEYOR);


                logindatasource.insertID_USER(idsurveyor_list[i],userName);

                if(datasource.cek_id_list(id_list[i])) {
                    Log.e("id_list",id_list[i]);
                    Log.e("tempat",nama_list[i]);
                    Log.e("idlokasi_list",idlokasi_list[i]);
                    Log.e("id_surveyor",idsurveyor_list[i]);
                    datasource.insertEntry(id_list[i], nama_list[i], idlokasi_list[i], idsurveyor_list[i]);
                }
                //dummy.add(new AssignmentObject(id_list[i],nama_list[i],idlokasi_list[i],idsurveyor_list[i]));
            }
            logindatasource.close();
            datasource.close();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

}

