package com.project.tutortime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.project.tutortime.ui.notifications.Notifications;
import com.project.tutortime.ui.notifications.NotificationsAdapter;

import java.util.HashMap;

import java.util.ArrayList;
import java.util.Locale;

public class Login extends AppCompatActivity {
    EditText mEmail,mPassword;
    Button mLoginBtn, mChangeLang;
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
        mChangeLang = findViewById(R.id.btnlanguage);
        fAuth = FirebaseAuth.getInstance();
        mResetPass = findViewById(R.id.resetpass);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        /* if the user already logged in */
        if (fAuth.getCurrentUser() != null) {
            /* get user ID */
            String userID = fAuth.getCurrentUser().getUid();
            /* redirects to the appropriate page depending on the user status */
            getInside(userID);
            //testMessage(userID);
        }

        mChangeLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* show alertdialog to display list of languages, one can be selected */
                showChangeLanguageDialog();
            }

        });



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
                                getInside(userID);
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

    private void showChangeLanguageDialog() {
        /* array of languages to display in alert dialog */
        final String[] listItems = {"English", "עברית"};
        AlertDialog.Builder mbuilder = new AlertDialog.Builder(Login.this);
        mbuilder.setTitle("Choose language");
        mbuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if(i==0){
                    //Hebrew
                    setLocale("en");
                    recreate();
                }
                if(i==1){
                    //English
                    setLocale("iw");
                    recreate();
                }
                dialog.dismiss();

            }
        });
        AlertDialog mDialog = mbuilder.create();
        mDialog.show();
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

    /* load language saved in shared preferences */
    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("Setting", MainActivity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        setLocale(language);
    }

    /* after confirmed as connected this method redirects to the appropriate page depending on the user status */
    final ArrayList<Integer> arr = new ArrayList<Integer>();
    protected void getInside(String userID) {
        mDatabase.child("users").child(userID).child("isTeacher").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getValue()!= null) {
                    int status = dataSnapshot.getValue(Integer.class);
                    if (status == -1) { /* if the status is not selected */
                        startActivity(new Intent(getApplicationContext(), ChooseStatus.class));
                    } else { /* status entered - pass the status to Main Activity */
                        arr.add(dataSnapshot.getValue(Integer.class));
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("status", arr);
                        startActivity(intent);

                    }
                    finish();
                } else {
                    Toast.makeText(Login.this, "Could not retrieve value from database.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
        /* loading screen section (showing loading screen until data received from FireBase) */
        Intent intent = new Intent(getApplicationContext(), LoadingScreen.class);
        /* prevent going back to this loading screen (from the next screen) */
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        /* END loading screen section */
        finish();


    }
//    private void testMessage(String userID){
//            //send contact request
//            String studentid = "JyoevIP7cpZGMocgpWk5g4pLwM43";//student
//            String teacherid = "L9g6rBsZqigUC4zTPSBwGNBK5p73";//teacher
//            HashMap<String, Object> map = new HashMap<>();
//            String key = FirebaseDatabase.getInstance().getReference().child("notifications").child(userID).push().getKey();
//            map.put("TeacherEmail","shalevasor@gmail.com");
//            map.put("TeacherName","Shalev");
//            map.put("UserEmail","shaevsor@gmail.com");
//            map.put("Subject","Math");
//            map.put("FormOfLearning","online");
//            map.put("Remarks","math teaching request");
//            map.put("RequestStatus","Waiting for response");//to remove
//            map.put("PhoneNumber","0547774447");
//            map.put("sendTo",teacherid);//to remove
//            map.put("sentFrom",studentid);
//            map.put("NotificationKey",key);
//            if (key != null)
//                FirebaseDatabase.getInstance().getReference().child("notifications").child(userID).child(key).setValue(map);
//            //after the user accept we adding to the chats
//
//    }

}