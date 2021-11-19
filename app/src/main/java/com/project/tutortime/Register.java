package com.project.tutortime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.project.tutortime.firebase.FireBaseUser;

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
                            FireBaseUser u = new FireBaseUser();
                            String userID = fAuth.getCurrentUser().getUid();
                            u.addUserToDB(fName, lName, email, city, userID);
                            startActivity(new Intent(getApplicationContext(), ChooseOne.class));

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
}