package com.example.android.geo_loco;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GoefenceBroadcastReceiver extends BroadcastReceiver {
    private String ClassRoomNumber;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //Toast.makeText(context, "Geofence Triggered........", Toast.LENGTH_LONG).show();

        NotificationHelper notificationHelper = new NotificationHelper(context);

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        Bundle extras=intent.getExtras();
        if(extras!=null){
            ClassRoomNumber=extras.getString("key");
        }
        if(geofencingEvent.hasError()){
            Log.d("AskPermission", "onReceive: Error Receiving Geofence Event..");
            return;
        }

        List<Geofence> geofences = geofencingEvent.getTriggeringGeofences();
        int transitionType=geofencingEvent.getGeofenceTransition();

        switch (transitionType){
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Toast.makeText(context, "ENTERED INTO GEOFENCE", Toast.LENGTH_SHORT).show();
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Toast.makeText(context, "DWELLING INTO GEOFENCE", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("Entered "+ClassRoomNumber,"",MainActivity.class);
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Toast.makeText(context, "EXITING FROM GEOFENCE", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}