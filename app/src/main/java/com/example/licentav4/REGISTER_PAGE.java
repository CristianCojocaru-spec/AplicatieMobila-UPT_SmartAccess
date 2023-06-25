package com.example.licentav4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class REGISTER_PAGE extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView go_to_login_page;
    private EditText first_name_et,last_name_et,email_et, phone_et, passwd_et, confirm_passwd_et;
    private Button create_acc_btn;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private ProgressBar progressBar_reg_page;
    private CheckBox show_passwd,show_confirmed_passwd;
    private String userID;

    private Spinner dorm_et,university_et;

    private SharedPreferences sharedPreferences;
    public final String shared_pref_name = "mypref";


    private String dorm,university,first_name,last_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        GlobalActivity.is_valid_user=0;

        go_to_login_page=findViewById(R.id.button_back_to_login_page);
        go_to_login_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackToLoginPage();
            }
        });


        // firebase working
        first_name_et =findViewById(R.id.first_name_et);
        last_name_et =findViewById(R.id.last_name_et);
        email_et=findViewById(R.id.email_et);
        phone_et =findViewById(R.id.phone_et);
        passwd_et =findViewById(R.id.password_et);
        confirm_passwd_et =findViewById(R.id.confirm_password_et);
        first_name= first_name_et.getText().toString().trim();
        last_name = last_name_et.getText().toString().trim();
        dorm_et =findViewById(R.id.dorm_et);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.valid_dorms, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dorm_et.setAdapter(adapter);
        dorm_et.setOnItemSelectedListener(this);
        university_et =findViewById(R.id.university_et);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,R.array.colleges, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        university_et.setAdapter(adapter2);
        university_et.setOnItemSelectedListener(this);
        create_acc_btn =findViewById(R.id.create_account_button);
        progressBar_reg_page=findViewById(R.id.progressBar_register);
        show_passwd=findViewById(R.id.show_passwd_cb);
        show_confirmed_passwd=findViewById(R.id.show_confirm_passwd_cb);
        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences(shared_pref_name,MODE_PRIVATE);


        create_acc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String first_name= first_name_et.getText().toString().trim();
                String last_name= last_name_et.getText().toString().trim();
                String email=email_et.getText().toString().trim();
                String phone= phone_et.getText().toString().trim();
                String password= passwd_et.getText().toString().trim();
                String confirmed_password= confirm_passwd_et.getText().toString().trim();

                checkEmailExistsOrNot();

                    if (TextUtils.isEmpty(first_name)) {

                        first_name_et.requestFocus();
                        first_name_et.setError("Field is mandatory");
                        return;
                    } else if (!first_name.matches("^[A-Z][a-z]{1,20}([- ])?([A-Z][a-z]{1,20})?")) {

                        first_name_et.requestFocus();
                        first_name_et.setError("It should start with an uppercase letter followed by lowercase letters");
                        return;

                    }else if (TextUtils.isEmpty(last_name)) {
                        last_name_et.requestFocus();
                        last_name_et.setError("Field is mandatory");
                        return;

                    } else if (!last_name.matches("^[A-Z][a-z]{1,20}")) {

                        last_name_et.requestFocus();
                        last_name_et.setError("It should start with an uppercase letter followed by lowercase letters");
                        return;

                    } else if (TextUtils.isEmpty(email)) {

                        email_et.requestFocus();
                        email_et.setError("Field is mandatory");
                        return;
                    }
                    else if ((!email.matches("^[A-Za-z]+\\.[A-Za-z]+@student\\.upt\\.ro$"))) {
                        email_et.requestFocus();
                        email_et.setError("Email address should have the following structure: 'firstname.lastname@student.upt.ro'");
                        return;

                    } else if (TextUtils.isEmpty(phone)) {

                        phone_et.requestFocus();
                        phone_et.setError("Field is mandatory");
                        return;

                    } else if ((!phone.matches("^0\\d{9}$"))) {
                        phone_et.requestFocus();
                        phone_et.setError("It should start with prefix '07' followed by 8 more digits");
                        return;

                    } else if (TextUtils.isEmpty(password)) {
                        passwd_et.requestFocus();
                        passwd_et.setError("Field is mandatory");
                        return;

                    } else if (!(password.length() >=7 && password.length()<=20)) {
                        passwd_et.setError("Password must be between 7 and 20 characters");
                        return;

                    } else if (TextUtils.isEmpty(confirmed_password)) {

                        confirm_passwd_et.requestFocus();
                        confirm_passwd_et.setError("Field is mandatory");
                        return;

                    } else if(!confirmed_password.equals(password)){
                        confirm_passwd_et.setError("Passwords don't match");
                        return;
                    }

                    progressBar_reg_page.setVisibility(View.VISIBLE);
                    // register to firebase
                    fAuth.createUserWithEmailAndPassword(email, confirmed_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                FirebaseUser firebaseUser = fAuth.getCurrentUser();

                                firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(REGISTER_PAGE.this, "User Created Successfully.Verification email sent", Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(REGISTER_PAGE.this, "Error: User unsuccessfully created!", Toast.LENGTH_SHORT).show();
                                    }
                                });


                                userID=fAuth.getCurrentUser().getUid();
                                DocumentReference documentReference = fStore.collection("users").document(userID);
                                Map<String, Object> user= new HashMap<>();
                                user.put("First Name",first_name);
                                user.put("Last Name",last_name);
                                user.put("Email",email);
                                user.put("Phone Number",phone);
                                user.put("College",university);
                                user.put("Dorm",dorm);

                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("UP","onSuccess: user profile successfully created for"+userID);
                                    }
                                });

                                startActivity(new Intent(getApplicationContext(), LOGIN_PAGE.class));
                                finish();

                            }
                            else{
                                progressBar_reg_page.setVisibility(View.GONE);
                            }
                        }

                    });

            }
        });


        show_passwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    passwd_et.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    passwd_et.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });


        show_confirmed_passwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    confirm_passwd_et.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                }else{
                    confirm_passwd_et.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }

    public void BackToLoginPage(){
        startActivity(new Intent(getApplicationContext(), LOGIN_PAGE.class));
        finish();
    }

    void checkEmailExistsOrNot(){

        fAuth.fetchSignInMethodsForEmail(email_et.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                Log.d("Email",""+task.getResult().getSignInMethods().size());
                if (task.getResult().getSignInMethods().size() != 0){
                    Toast.makeText(REGISTER_PAGE.this, "ERROR: Email already exist!", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

       if(parent.getId() == R.id.dorm_et){
           dorm= parent.getItemAtPosition(position).toString();

       } else if (parent.getId() == R.id.university_et) {
           university = parent.getItemAtPosition(position).toString();
           ((TextView) parent.getChildAt(0)).setTextSize(12);
       }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
}