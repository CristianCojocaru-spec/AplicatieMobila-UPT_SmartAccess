package com.example.licentav4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public abstract class GlobalActivity extends AppCompatActivity {
    static int is_valid_user=0;
    static int door_unlocked=0;
    static int door_unlocked2=0;
    static  int flag=0;
    static MutableLiveData<String> listen = new MutableLiveData<>("0");
    static Date currentDate;
    static SimpleDateFormat dateFormat;
    static String formattedDate;
    static List<String> list =  new ArrayList<>();
    static Set<String> linkedHashSet = new LinkedHashSet<>();
    static String userid2;
    static FirebaseAuth firebaseAuth2;
    static int nfc_scan_admin;
}