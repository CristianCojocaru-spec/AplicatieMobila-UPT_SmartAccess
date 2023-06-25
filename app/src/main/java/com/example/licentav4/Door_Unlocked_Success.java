package com.example.licentav4;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import pl.droidsonroids.gif.GifImageView;


public class Door_Unlocked_Success extends GlobalActivity {
    private TextView door_status,current_scan_date;
    private GifImageView gif_door_unlocked;
    FirebaseFirestore firebaseFirestore2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door_unlocked_success);

        door_status=findViewById(R.id.door_status_tv);
        current_scan_date=findViewById(R.id.door_status_date_tv);
        gif_door_unlocked = findViewById(R.id.gifImageView2);

        GlobalActivity.firebaseAuth2 = FirebaseAuth.getInstance();
        firebaseFirestore2=FirebaseFirestore.getInstance();
        GlobalActivity.userid2 = GlobalActivity.firebaseAuth2.getCurrentUser().getUid();

        GlobalActivity.listen.setValue("0");

        GlobalActivity.currentDate = new Date();
        GlobalActivity.dateFormat = new SimpleDateFormat("HH:mm:ss - dd MMM yyyy", Locale.getDefault());
        GlobalActivity.formattedDate = GlobalActivity.dateFormat.format(GlobalActivity.currentDate);
        Door_Unlocked_Status();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                startActivity(new Intent(getApplicationContext(), NFC_TAG_SCAN.class));
            }
        }, 3500);
    }


    public void Door_Unlocked_Status(){

        gif_door_unlocked.setVisibility(View.VISIBLE);
        door_status.setVisibility(View.VISIBLE);
        current_scan_date.setVisibility(View.VISIBLE);
        door_status.setText("\t\t\t\t\t\t\t\t\tDoor unlocked");


        if(GlobalActivity.nfc_scan_admin==1) {

        }else {
            DocumentReference documentReference = firebaseFirestore2.collection("LogUnlocks").document(GlobalActivity.userid2);
            Map<String, Object> user = new HashMap<>();
            user.put("Status", "Door Unlocked");
            user.put("Date", GlobalActivity.formattedDate);
            user.put("Student ID", GlobalActivity.userid2);

            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d("UP", "onSuccess: unlock DB successfully created" + GlobalActivity.userid2);
                }
            });
        }
    }
}