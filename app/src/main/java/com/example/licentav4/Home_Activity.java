package com.example.licentav4;
import androidx.annotation.NonNull;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Home_Activity extends GlobalActivity {

    private BottomNavigationView nav_bar;
    private TextView greetings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        GlobalActivity.is_valid_user=0;
        nav_bar = findViewById(R.id.bottomNavigationView);
        nav_bar.setSelectedItemId(R.id.home);
        nav_bar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.unlock){

                    startActivity(new Intent(getApplicationContext(), NFC_TAG_SCAN.class));
                    overridePendingTransition(0,0);
                    return true;
                }
                else if(item.getItemId() == R.id.profile){
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                }
                else{
                    return true;
                }
            }
        });
        greetings=findViewById(R.id.textView4);
        greetings.setText("                                Welcome,\n\n"+"This application was designed to facilitate access to a building in the easiest way possible by replacing the physical access card with an emulated one within a mobile application.This is a prototype and new features will be implemented in the future.\n\n"+"On the bottom side of the page you can see a navigation bar containing two more buttons that will guide you to your student profile and of course, the unlock door section.\n\n"+"I wish you the best student experience and also a warm sitting in one of the UPT's dorm rooms.");
    }
}