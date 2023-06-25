package com.example.licentav4;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends GlobalActivity{

    private TextView f_name_tv,l_name_tv,email_tv,phone_number_tv, college_tv,dorm_tv;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String userid;
    private Button change_profile_pic,logout_btn;
    private ImageView profile_img;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private StorageReference storageReference;
    private BottomNavigationView nav_bar;
    private SharedPreferences sharedPreferences;
    public final String shared_pref_name = "mypref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        f_name_tv=findViewById(R.id.first_name_tv);
        l_name_tv=findViewById(R.id.last_name_tv);
        email_tv=findViewById(R.id.email_tv);
        phone_number_tv=findViewById(R.id.phone_tv);
        college_tv =findViewById(R.id.college_tv);
        dorm_tv=findViewById(R.id.dorm_tv);
        logout_btn= findViewById(R.id.logout_btn);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        userid=firebaseAuth.getCurrentUser().getUid();
        storageReference= FirebaseStorage.getInstance().getReference();

        change_profile_pic=findViewById(R.id.change_user_picture_btn);
        profile_img=findViewById(R.id.profile_image);

        GlobalActivity.is_valid_user=0;
        GlobalActivity.flag=0;

        sharedPreferences = getSharedPreferences(shared_pref_name,MODE_PRIVATE);

        DocumentReference documentReference=firebaseFirestore.collection("users").document(userid);

        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                f_name_tv.setText(value.getString("First Name"));
                l_name_tv.setText(value.getString("Last Name"));
                email_tv.setText(value.getString("Email"));
                phone_number_tv.setText(value.getString("Phone Number"));
                college_tv.setText(value.getString("College"));
                dorm_tv.setText(value.getString("Dorm"));
            }
        });

            StorageReference profile_reference = storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"/profile.jpg");
            profile_reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(profile_img);
                }
            });

            activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if(result.getResultCode() == RESULT_OK && result.getData() !=null){
                       Uri imageUri = result.getData().getData();
                       uploadImageToFirebase(imageUri);
                    }
                }
            });

            change_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(intent.resolveActivity(getPackageManager()) !=null){
                    activityResultLauncher.launch(intent);
                }
                else{
                    Toast.makeText(ProfileActivity.this, "There's no app to support this action", Toast.LENGTH_SHORT).show();
                }

            }
        });

            logout_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.commit();
                    Toast.makeText(ProfileActivity.this, "LOGOUT SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                    Intent loginPage = new Intent(ProfileActivity.this, LOGIN_PAGE.class);
                    startActivity(loginPage);
                    finish();
                }
            });
            nav_bar = findViewById(R.id.bottomNavigationView);

            nav_bar.setSelectedItemId(R.id.profile);
            nav_bar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.unlock){

                    startActivity(new Intent(getApplicationContext(), NFC_TAG_SCAN.class));
                    overridePendingTransition(0,0);
                    return true;
                }
                else if(item.getItemId() == R.id.home){
                    startActivity(new Intent(getApplicationContext(), Home_Activity.class));
                    overridePendingTransition(0,0);
                    return true;
                }
                else{
                    return true;
                }
            }
        });
    }

    private void uploadImageToFirebase(Uri imageUri){

        StorageReference fileReference = storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(ProfileActivity.this, "User picture successfully uploaded", Toast.LENGTH_SHORT).show();

                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profile_img);
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "User picture unsuccessfully uploaded to firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

