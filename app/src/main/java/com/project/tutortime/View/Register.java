package com.project.tutortime.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.project.tutortime.Model.firebase.FireBaseUser;
import com.project.tutortime.R;

import java.util.HashMap;
import java.util.Locale;

import belka.us.androidtoggleswitch.widgets.ToggleSwitch;

public class Register extends AppCompatActivity {
    EditText mFName, mLName, mEmail, mPassword; // mCity
    Button mRegisterBtn;
    Spinner mCityspinner, mGenderspinner;
    TextView mLoginBtn;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        /* DISABLE landscape orientation  */
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mFName = findViewById(R.id.editFName);
        mLName = findViewById(R.id.editLName);
        mEmail = findViewById(R.id.editEmail);
        mPassword = findViewById(R.id.editPass);
        mCityspinner = (Spinner)findViewById(R.id.selectCity);
        mGenderspinner = (Spinner)findViewById(R.id.selectGender);

//        ArrayAdapter<CharSequence> a = ArrayAdapter.createFromResource(this, R.array.Gender, android.R.layout.simple_spinner_item);
//        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mGenderspinner.setAdapter(a);

        /* Select Gender Spinner Code () */
        ArrayAdapter<String> a = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item) {
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
            public boolean isEnabled(int position) { return (position != 0); }
            @Override /* Set the color of the Hint (first selection) to Grey */
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView)view;
                if (position == 0) tv.setTextColor(Color.GRAY); else tv.setTextColor(Color.BLACK);
                return view; }
        };
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        String[] select_gender = getResources().getStringArray(R.array.Gender);
        a.add("Choose Gender");
        a.addAll(select_gender);
        mGenderspinner.setAdapter(a);
        mGenderspinner.setSelection(0); //display hint
        /* END Select City Spinner Code () */


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
            public boolean isEnabled(int position) { return (position != 0); }
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
                String gender = mGenderspinner.getSelectedItem().toString();

                if (TextUtils.isEmpty(fName)) {
                    mFName.setError("First name is required.");
                    return; }
                if (TextUtils.isEmpty(lName)) {
                    mLName.setError("Last name is required.");
                    return; }
                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required.");
                    return; }
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is required.");
                    return; }
                if (password.length() < 6) {
                    mPassword.setError("Password must contains at least 6 digits.");
                    return; }

                /* if Gender Box == Gender Hint then the user didn't choose gender */
                if (TextUtils.equals(gender,"Choose Gender")) {
                    Toast.makeText(Register.this, "Gender is required. ", Toast.LENGTH_SHORT).show();
                    TextView errorText = (TextView)mGenderspinner.getSelectedView();
                    errorText.setError("Gender is required.");
                    errorText.setTextColor(Color.RED);//just to highlight that this is an error
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
                            u.addUserToDB(fName, lName, email, city, gender, userID);
                            /* add welcome notification to user */
                            addNotification(userID, email);
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        final MenuItem toggleservice = menu.findItem(R.id.lang_switch);
        final ToggleSwitch langSwitch = toggleservice.getActionView().findViewById(R.id.lan);

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

    private void addNotification(String userID,String email){
        HashMap<String,Object> map = new HashMap<>();
        String key = FirebaseDatabase.getInstance().getReference().child("notifications").child(userID).push().getKey();
        map.put("title","Register");
        map.put("FormOfLearning","");
        map.put("Remarks","Welcome to TutorTime!");
        map.put("RequestStatus","");
        map.put("PhoneNumber","");
        map.put("sendTo","");
        map.put("sentFrom",fAuth.getCurrentUser().getUid());
        map.put("NotificationKey",key);
        if(key!=null)FirebaseDatabase.getInstance().getReference().child("notifications").child(userID).child(key).setValue(map);
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