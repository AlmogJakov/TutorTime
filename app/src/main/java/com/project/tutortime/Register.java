package com.project.tutortime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.FirebaseDatabase;
import com.project.tutortime.firebase.FireBaseUser;

import java.util.HashMap;

public class Register extends AppCompatActivity {
    EditText mFName, mLName, mEmail, mPassword; // mCity
    Button mRegisterBtn;
    Spinner mCityspinner;
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
        mCityspinner = (Spinner)findViewById(R.id.selectCity);

        /* Select City Spinner Code () */
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (position == 0) { // Hint
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(0)); }
                return v; }
            @Override
            public int getCount() { return super.getCount(); }
            @Override /* Disable selection of the Hint (first selection) */
            public boolean isEnabled(int position) { return ((position == 0) ? false : true); }
            @Override /* Set the color of the Hint (first selection) to Grey */
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView)view;
                if (position == 0) tv.setTextColor(Color.GRAY); else tv.setTextColor(Color.BLACK);
                return view; }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        String[] cities = getResources().getStringArray(R.array.Cities);
        adapter.add("Choose City");
        adapter.addAll(cities);
        mCityspinner.setAdapter(adapter);
        mCityspinner.setSelection(0); //display hint
        /* END Select City Spinner Code () */



        mRegisterBtn = findViewById(R.id.buttonAcount);
        mLoginBtn = findViewById(R.id.loginPage);
        fAuth = FirebaseAuth.getInstance();

        /* if the user already logged in */
        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), ChooseStatus.class));
            finish();
        }


        mRegisterBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String fName = mFName.getText().toString().trim();
                String lName = mLName.getText().toString().trim();
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                //String city = mCity.getText().toString().trim();
                String city = mCityspinner.getSelectedItem().toString();

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

                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is required.");
                    return;
                }

                if (password.length() < 6) {
                    mPassword.setError("Password must contains at least 6 digits.");
                    return;
                }

                /* if City Box == City Hint then the user didn't choose city */
                if (TextUtils.equals(city,"Choose City")) {
                    Toast.makeText(Register.this, "City is required. ", Toast.LENGTH_SHORT).show();
                    TextView errorText = (TextView)mCityspinner.getSelectedView();
                    errorText.setError("City is required.");
                    errorText.setTextColor(Color.RED);//just to highlight that this is an error
                    //errorText.setText("City is required");
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