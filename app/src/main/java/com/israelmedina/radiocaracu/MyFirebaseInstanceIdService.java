package com.israelmedina.radiocaracu;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;


/**
 * Created by israel.medina on 11/9/17.
 */

public class MyFirebaseInstanceIdService {
//    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("Firebase", "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
//        sendRegistrationToServer(refreshedToken);
    }
}
