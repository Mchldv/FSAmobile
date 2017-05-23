package com.dvlchm.fsa;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ParseJson {

    public static String[] nama_list;
    public static String[] id_list;


    public static final String JSON_ARRAY="result";
    public static final String JSON_COLOUMNID ="kolomid";
    public static final String JSON_COLOUMNNAMA ="kolomlocation";

    private String json;

    public ParseJson(String json) {
        this.json = json;
    }

    protected  void lihatAssignmentParse (AssignmentAdapter dummy) {
        JSONObject jsonObject;
        JSONArray array;
        try{
            jsonObject = new JSONObject(json);
            array = jsonObject.getJSONArray(JSON_ARRAY);
            nama_list = new String[array.length()];
            id_list = new String[array.length()];

            for(int i=0;i<array.length();i++){
                JSONObject jo = array.getJSONObject(i);
                nama_list[i]=jo.getString(JSON_COLOUMNNAMA);
                id_list[i]=jo.getString(JSON_COLOUMNID);
                dummy.add(new AssignmentObject(id_list[i],nama_list[i]));

            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

}