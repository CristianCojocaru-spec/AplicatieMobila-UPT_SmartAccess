package com.example.licentav4;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import java.text.SimpleDateFormat;
import java.util.Date;

import pl.droidsonroids.gif.GifImageView;


public class NFC_TAG_SCAN extends GlobalActivity {

    private Button show_records;
    private TextView door_status, current_scan_date, read_tag_message;
    private Date currentDate;
    private SimpleDateFormat dateFormat;
    private String formattedDate;
    private BottomNavigationView nav_bar;
    private GifImageView gif_read_tag, gif_door_unlocked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_nfc_tag_scan);
            gif_read_tag = findViewById(R.id.gifImageView);
            read_tag_message = findViewById(R.id.textView5);
            GlobalActivity.is_valid_user = 1;

           show_records = findViewById(R.id.show_records_btn);

            Log.d("HCC", "NFC_TAG_SCAN LISTEN VALUE: " + GlobalActivity.listen.getValue());
            //Toast.makeText(getApplicationContext(), "LISTEN VALUE: "+GlobalActivity.listen.getValue(), Toast.LENGTH_SHORT).show();
            GlobalActivity.listen.observe(NFC_TAG_SCAN.this, new Observer<String>() {
                @Override
                public void onChanged(String changedValue) {
                    if (GlobalActivity.listen.getValue().equals("1")) {
                        startActivity(new Intent(getApplicationContext(), Door_Unlocked_Success.class));
                    }
                }
            });

            nav_bar = findViewById(R.id.bottomNavigationView);
            nav_bar.setSelectedItemId(R.id.unlock);
            nav_bar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    if (item.getItemId() == R.id.home) {
                        startActivity(new Intent(getApplicationContext(), Home_Activity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    } else if (item.getItemId() == R.id.profile) {
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    } else {
                        return true;
                    }

                }
            });


        if(GlobalActivity.nfc_scan_admin==1){
            show_records.setVisibility(View.VISIBLE);
            nav_bar.setVisibility(View.INVISIBLE);
        }

        show_records.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AdminPage.class));
            }
        });
    }

}