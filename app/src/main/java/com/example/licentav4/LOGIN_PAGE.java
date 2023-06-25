package com.example.licentav4;
import androidx.annotation.NonNull;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.google.firebase.ktx.Firebase;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;

public class LOGIN_PAGE extends GlobalActivity {

    private TextView go_to_register_page,forgot_passwd,resend_cf_email;
    private Button login_btn,login_btn_admin;
    private EditText passwd_et,email_et;
    private FirebaseAuth fAuth;
    private ProgressBar progressBar_login_page;
    private CheckBox show_passwd;
    private String email,password,admin_email;
    private SharedPreferences sharedPreferences,sharedPreferences2;
    private final String shared_pref_name = "mypref";
    private final String key_email = "email";
    private final String key_password = "password";
    private final String shared_pref_name2 = "mypref2";
    private final String key_email2 = "email";
    private final String key_password2 = "password";
    private int logout_from_student=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        GlobalActivity.is_valid_user=0;
        email_et=findViewById(R.id.email_login_et2);
        passwd_et =findViewById(R.id.passwd_login_et);
        progressBar_login_page=findViewById(R.id.progressBar_Login);
        fAuth=FirebaseAuth.getInstance();
        login_btn=findViewById(R.id.button_Login);
        login_btn_admin=findViewById(R.id.button_Login_admin);
        resend_cf_email=findViewById(R.id.button_confirm_email);
        show_passwd=findViewById(R.id.show_passwd_cb);
        forgot_passwd = findViewById(R.id.button_forgot_passwd);
        go_to_register_page=findViewById(R.id.button_back_to_login_page);
        sharedPreferences = getSharedPreferences(shared_pref_name,MODE_PRIVATE);
        sharedPreferences2 = getSharedPreferences(shared_pref_name2,MODE_PRIVATE);
        String email_spref = sharedPreferences.getString(key_email,null);
        String email_spref2 = sharedPreferences2.getString(key_email2,null);


        go_to_register_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegisterPage();}
        });


        if(email_spref !=null){
            startActivity(new Intent(getApplicationContext(), NFC_TAG_SCAN.class));
            finish();
        }
        else if(email_spref2 !=null) {
            startActivity(new Intent(getApplicationContext(), AdminPage.class));
            finish();
        }

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalActivity.nfc_scan_admin=0;
                logout_from_student=0;
                email=email_et.getText().toString().trim();
                password= passwd_et.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {

                    email_et.requestFocus();
                    email_et.setError("Field is mandatory");
                    return;
                }
                else if(TextUtils.isEmpty(password)){
                    passwd_et.setError("Field is mandatory");
                    passwd_et.requestFocus();
                    return;
                }else if((!email.matches("^[A-Za-z]+\\.[A-Za-z]+@student\\.upt\\.ro$"))){
                    email_et.setError("Admins can't log in as students!");
                    email_et.requestFocus();
                    return;
                }

                progressBar_login_page.setVisibility(View.VISIBLE);
                resend_cf_email.setVisibility(View.INVISIBLE);

                sign_in_user();
            }
        });



        login_btn_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email=email_et.getText().toString().trim();
                password= passwd_et.getText().toString().trim();
                admin_email = email_et.getText().toString().trim();
                GlobalActivity.nfc_scan_admin=1;
                logout_from_student=1;

                if (TextUtils.isEmpty(email)) {

                    email_et.requestFocus();
                    email_et.setError("Field is mandatory");
                    return;
                }
                else if(TextUtils.isEmpty(password)){
                    passwd_et.setError("Field is mandatory");
                    passwd_et.requestFocus();
                    return;
                }else if(admin_email.matches("^[A-Za-z0-9]+\\.[A-Za-z0-9]+@student\\.upt\\.ro$")){
                    email_et.setError("Students can't log in as admins!");
                    email_et.requestFocus();
                    return;
                }

                progressBar_login_page.setVisibility(View.VISIBLE);
                resend_cf_email.setVisibility(View.INVISIBLE);

                sign_in_user();
            }
        });



        resend_cf_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = fAuth.getCurrentUser();
                    if(!user.isEmailVerified()) {
                        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(LOGIN_PAGE.this, "Verification email successfully sent", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LOGIN_PAGE.this, "Error:Couldn't sent verification email", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

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

        forgot_passwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email=email_et.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    email_et.requestFocus();
                    email_et.setError("Field is mandatory");
                    return;
                }
                else{
                    ResetPasswd(email);
                }
            }
        });
    }

    public void openRegisterPage(){
        startActivity(new Intent(getApplicationContext(), REGISTER_PAGE.class));
        finish();
    }

    public void ResetPasswd(String email){

        fAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Toast.makeText(LOGIN_PAGE.this, "Request successfully sent.Check your email in order to reset the password", Toast.LENGTH_SHORT).show();
            }
        }) .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(LOGIN_PAGE.this, "Couldn't find this email address in our database", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sign_in_user(){

        fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful() && GlobalActivity.nfc_scan_admin==0 && logout_from_student==0){

                    FirebaseUser user = fAuth.getCurrentUser();
                    if((user.isEmailVerified())) {
                        Toast.makeText(LOGIN_PAGE.this, "User authenticated successfully", Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(key_email, email);
                        editor.putString(key_password, password);
                        editor.apply();
                        startActivity(new Intent(getApplicationContext(), NFC_TAG_SCAN.class));
                    }else{
                        resend_cf_email.setVisibility(View.VISIBLE);
                        email_et.requestFocus();
                        email_et.setError("Check your inbox in order to verify the email address");
                        progressBar_login_page.setVisibility(View.GONE);
                    }
                }else if(task.isSuccessful() && GlobalActivity.nfc_scan_admin==1 && logout_from_student==1){
                    FirebaseUser user = fAuth.getCurrentUser();
                    if(user.isEmailVerified()) {
                        Toast.makeText(LOGIN_PAGE.this, "Admin authenticated successfully", Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = sharedPreferences2.edit();
                        editor.putString(key_email2, email);
                        editor.putString(key_password2, password);
                        editor.apply();
                        startActivity(new Intent(getApplicationContext(), NFC_TAG_SCAN.class));
                    }else{
                        resend_cf_email.setVisibility(View.VISIBLE);
                        email_et.requestFocus();
                        email_et.setError("Check your inbox in order to verify the email address");
                        progressBar_login_page.setVisibility(View.GONE);
                    }
                }
                else{
                    Toast.makeText(LOGIN_PAGE.this, "Error: Email and password don't match!", Toast.LENGTH_SHORT).show();
                    progressBar_login_page.setVisibility(View.GONE);
                }
            }
        });
    }
}