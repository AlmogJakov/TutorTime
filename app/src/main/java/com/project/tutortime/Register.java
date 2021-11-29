package com.project.tutortime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.database.FirebaseDatabase;
import com.project.tutortime.firebase.FireBaseUser;

import java.util.HashMap;

public class Register extends AppCompatActivity {
    EditText mFName, mLName, mEmail, mPassword, mCity;
    Button mRegisterBtn;
    TextView mLoginBtn;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFName = findViewById(R.id.editFName);
        mLName = findViewById(R.id.editLName);
        mEmail = findViewById(R.id.editEmail);
        mPassword = findViewById(R.id.editPass);
        mCity = findViewById(R.id.editCity);
        mRegisterBtn = findViewById(R.id.buttonAcount);
        mLoginBtn = findViewById(R.id.loginPage);
        fAuth = FirebaseAuth.getInstance();

        /* if the user already logged in */
        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(),ChooseOne.class));
            finish();
        }


        mRegisterBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String fName = mFName.getText().toString().trim();
                String lName = mLName.getText().toString().trim();
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String city = mCity.getText().toString().trim();

                if (TextUtils.isEmpty(fName)) {
                    mFName.setError("First name is required.");
                    return;
                }

                if (TextUtils.isEmpty(lName)) {
                    mLName.setError("Last name is required.");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required.");
                    return;
                }

                if (TextUtils.isEmpty(city)) {
                    mCity.setError("City is required.");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is required.");
                    return;
                }

                if (password.length() < 6) {
                    mPassword.setError("Password must contains at least 6 digits.");
                    return;
                }

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isComplete()&&task.isSuccessful()) {
                            Toast.makeText(Register.this, "User Created", Toast.LENGTH_SHORT).show();
                            /* send verification link */
                            FirebaseUser user = fAuth.getCurrentUser();
                            user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(Register.this, "Verification email has been sent.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("myTAG","onFailure: email not sent. " + e.getMessage());
                                }
                            });
                            /* END send verification link */
                            FireBaseUser u = new FireBaseUser();
                            String userID = fAuth.getCurrentUser().getUid();
                            u.addUserToDB(fName, lName, email, city, userID);
                            addNotification(userID,email);
                            fAuth.signOut();
                            // startActivity(new Intent(getApplicationContext(), ChooseOne.class));
                            startActivity(new Intent(getApplicationContext(), Login.class));
                        } else {
                            Toast.makeText(Register.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });


    }
            private void addNotification(String userid,String email){
            HashMap<String,Object> map = new HashMap<>();
            map.put("UserID",userid);
            map.put("Remarks","Welcome to TutorTime!");
            map.put("TeacherEmail","");
            map.put("UserEmail",email);
            map.put("Subject","Welcome");
            map.put("RequestStatus","");
                FirebaseDatabase.getInstance().getReference().child("notifications").child(userid).push().setValue(map);
        }
}