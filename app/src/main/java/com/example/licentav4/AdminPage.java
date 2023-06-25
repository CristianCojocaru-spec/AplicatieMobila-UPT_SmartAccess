package com.example.licentav4;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.Value;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminPage extends GlobalActivity {

    private ListView listView;
    private TextView unlocks_per_day;
    private Button logout_btn_admin,button_list_count,scan_page;
    private SharedPreferences sharedPreferences2;
    public final String shared_pref_name2 = "mypref2";
    private FirebaseFirestore firebaseFirestore2;

    private List<String> list2 =  new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);
        listView = findViewById(R.id.list_view);
        logout_btn_admin= findViewById(R.id.logout_btn_admin);
        scan_page= findViewById(R.id.button_back_to_scan_page);
        button_list_count= findViewById(R.id.button_get_count);
        unlocks_per_day= findViewById(R.id.unlocks_per_day);
        firebaseFirestore2=FirebaseFirestore.getInstance();

        firebaseFirestore2.collection("LogUnlocks").addSnapshotListener(new EventListener<QuerySnapshot>() {
               @Override
               public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                   if (e != null) {

                   }
                   GlobalActivity.list.clear();
                   for (DocumentChange documentChange : documentSnapshots.getDocumentChanges()) {
                       GlobalActivity.list.add("\nStudent ID:   "+documentChange.getDocument().getData().get("Student ID").toString() +"\n\n" +"Door Status:   "+documentChange.getDocument().getData().get("Status").toString()+"\n\n"+"Date:   "+ documentChange.getDocument().getData().get("Date").toString());
                       ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1,  list);
                       listView.setAdapter(adapter);
                   }
               }
           });

        sharedPreferences2 = getSharedPreferences(shared_pref_name2,MODE_PRIVATE);
        logout_btn_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = sharedPreferences2.edit();
                editor.clear();
                editor.commit();
                Toast.makeText(getApplicationContext(), "LOGOUT SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                Intent loginPage = new Intent(getApplicationContext(), LOGIN_PAGE.class);
                startActivity(loginPage);
                finish();
            }
        });

        scan_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginPage = new Intent(getApplicationContext(), NFC_TAG_SCAN.class);
                startActivity(loginPage);
            }
        });

        button_list_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateCountForCurrentDay();
            }
        });

    }

    private void calculateCountForCurrentDay() {

        Date currentDate = new Date();

        GlobalActivity.dateFormat = new SimpleDateFormat("HH:mm:ss - dd MMM yyyy", Locale.getDefault());
        GlobalActivity.formattedDate = GlobalActivity.dateFormat.format(currentDate);

        if (listView.getAdapter().isEmpty()) {
            Toast.makeText(this, "No records to verify", Toast.LENGTH_SHORT).show();
        } else {
            String new_formattedCurrentDate = formattedDate.substring(11);

            int count = 0;
            for (String element : list) {
                String date = element.substring(element.lastIndexOf("-") + 2);
                if (date.equals(new_formattedCurrentDate)) {
                    count++;
                }
            }

            if(count == 0) {
                unlocks_per_day.setText("No student unlocked the door today");
            }
            else if(count ==1){
                unlocks_per_day.setText(count + " student unlocked the door today");
            }
            else{
                unlocks_per_day.setText(count + " students unlocked the door today");
            }
        }
    }

}