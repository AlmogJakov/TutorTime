package com.project.tutortime.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.tutortime.MainActivity;
import com.project.tutortime.Model.firebase.FireBaseUser;
import com.project.tutortime.Model.firebase.FirebaseManager;
import com.project.tutortime.R;

import java.util.ArrayList;
import java.util.Locale;

import belka.us.androidtoggleswitch.widgets.ToggleSwitch;


public class Login extends AppCompatActivity {
    EditText mEmail,mPassword;
    Button mLoginBtn;
    TextView mRegisterBtn, mResetPass;
    FirebaseAuth fAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /* DISABLE landscape orientation  */
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mEmail = findViewById(R.id.etemail);
        mPassword = findViewById(R.id.mypass);
        mRegisterBtn = findViewById(R.id.registerPage);
        mLoginBtn = findViewById(R.id.btnlogin);
        fAuth = FirebaseAuth.getInstance();
        mResetPass = findViewById(R.id.resetpass);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseManager fm = new FirebaseManager();
        /* if the user already logged in */
        if (fm.isLoggedIn()) {
            /* get user ID */
            String userID = fAuth.getCurrentUser().getUid();
            /* redirects to the appropriate page depending on the user status */
            fm.getInside(userID,(Activity)Login.this);
            //testMessage(userID);
        }


        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("email is required.");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("password is required.");
                    return;
                }
                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final FirebaseUser user = fAuth.getCurrentUser();
                            if (!user.isEmailVerified()) { /* Email Not Verified! - alert the user */
                                final AlertDialog.Builder emailVerification = new AlertDialog.Builder(Login.this);
                                emailVerification.setTitle("You need to verify your email.");
                                emailVerification.setMessage("Re-send email verification?");
                                /* manage buttons */
                                emailVerification.setPositiveButton("RE-SEND", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(Login.this, "Verification email has been sent.", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Login.this,"Error! email not sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                                emailVerification.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {/* close dialog */}
                                });
                                emailVerification.create().show();
                                fAuth.signOut(); /* don't let user in */
                            } else { /* Email Verified! - let user in */
                                Toast.makeText(Login.this, "Logged in Successfully!", Toast.LENGTH_SHORT).show();
                                /* get user ID */
                                String userID = fAuth.getCurrentUser().getUid();
                                /* redirects to the appropriate page depending on the user status */
                                fm.getInside(userID,(Activity)Login.this);
                                //testMessage(userID);
                            }
                        } else {
                            Toast.makeText(Login.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Register.class));
            }
        });

        /* Reset Password Case */
        mResetPass.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetMail = new EditText(v.getContext());
                final AlertDialog.Builder PasswordResetDialog = new AlertDialog.Builder(v.getContext());
                PasswordResetDialog.setTitle("Reset Password?");
                PasswordResetDialog.setMessage("Enter your email to receive reset link.");
                PasswordResetDialog.setView(resetMail);
                PasswordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mail = resetMail.getText().toString();
                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(Login.this, "Reset link send to your mail.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login.this,"Error! The password was not sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                PasswordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                });
                PasswordResetDialog.create().show();
            }
        }));
        /* END Reset Password Case */

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        final MenuItem toggleservice = menu.findItem(R.id.lang_switch);
        final ToggleSwitch langSwitch = toggleservice.getActionView().findViewById(R.id.lan);
        // TODO: https://stackoverflow.com/questions/32813934/save-language-chosen-by-user-android
        langSwitch.setOnToggleSwitchChangeListener(new ToggleSwitch.OnToggleSwitchChangeListener(){

            @Override
            public void onToggleSwitchChangeListener(int position, boolean isChecked) {
                if(position==0){
                    //English
                    setLocale("en");
                    recreate();
                }
                if(position==1){
                    //Hebrew
                    setLocale("iw");
                    recreate();
                }
            }
        });

        return true;
    }


    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        /* save data to shared  preferences */
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }


}