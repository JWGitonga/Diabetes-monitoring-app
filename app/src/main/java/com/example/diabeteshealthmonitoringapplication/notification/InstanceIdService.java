package com.example.diabeteshealthmonitoringapplication.notification;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.Objects;

public class InstanceIdService extends FirebaseInstanceIdService {
    private final String TAG = "InstanceIdService";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();

        if (FirebaseAuth.getInstance().getUid()!=null){
            updateToken(token);
        }
    }

    protected void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("device-tokens");
        Token mToken = new Token(token);
        reference.child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).setValue(mToken);
        updateTokenNode(token);
    }

    protected void updateTokenNode(String token) {
        FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getUid() + "/deviceToken")
                .setValue(token)
                .addOnSuccessListener(aVoid -> Log.i(TAG, "updateTokenNode: Token in user node updated")
                ).addOnFailureListener(aVoid ->
                Log.i(TAG, "updateTokenNode: Token in user nod not updated")
        );
    }
}
