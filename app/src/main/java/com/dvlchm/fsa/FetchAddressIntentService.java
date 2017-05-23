package com.dvlchm.fsa;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by User on 5/16/2017.
 */
public class FetchAddressIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    protected android.support.v4.os.ResultReceiver mReceiver;
    public FetchAddressIntentService(){
        super("FetchAddress");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Location loc = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);
        Log.e("RECEIVER",mReceiver.toString());
        Geocoder geocoder;
        List<Address> address_list = null;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {

            Log.e("lokasi : ", "mencari lokasi");
            if(loc!=null)
                address_list = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
        } catch (IOException e) {
            Log.e("lokasi : ", "tidak menemukan lokasi " + e.toString());
            e.printStackTrace();
        }

        if(address_list==null || address_list.size()==0)
        {
            String error= "Tidak Ditemukan";
            Log.e("Alamat",error);
            deliverResultToReceiver(Constants.FAILURE_RESULT,error);
        }
        else
        {
            String address = address_list.get(0).getAddressLine(0);
            deliverResultToReceiver(Constants.SUCCESS_RESULT,address);
        }
    }
    private void deliverResultToReceiver(int resultCode, String message)
    {
        Bundle bundle= new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY,message);
        mReceiver.send(resultCode,bundle);
    }
}
