package com.project.tutortime.Model.firebase;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.SpannableString;
import android.util.Log;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.tutortime.MainActivity;
import com.project.tutortime.Model.adapter.TutorAdapter;
import com.project.tutortime.Model.adapter.TutorAdapterItem;
import com.project.tutortime.View.ChooseStatus;
import com.project.tutortime.View.LoadingDialog;
import com.project.tutortime.View.LoadingScreen;
import com.project.tutortime.View.Login;
import com.project.tutortime.View.Register;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class FirebaseManager {
    protected DatabaseReference myRef;
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();;

    public FirebaseManager(){
        myRef= FirebaseDatabase.getInstance().getReference();
    }

    public boolean isLoggedIn() {
        return fAuth.getCurrentUser()!= null;
    }

    public void setUserType(int type) {
        String userID = fAuth.getCurrentUser().getUid();
        mDatabase.child("users").child(userID).child("isTeacher").setValue(type);
    }

    public void setImageURL(String teacherID, String imgURL) {
        mDatabase.child("teachers").child(teacherID).child("imgUrl").setValue(imgURL);
    }

    public String getCurrentUserID() {
        return fAuth.getCurrentUser().getUid();
    }

    /* after confirmed as connected this method redirects to the appropriate page depending on the user status */
    final ArrayList<Integer> userType = new ArrayList<Integer>();
    public void getUserInsideApp(Activity activity) {
        String userID = fAuth.getCurrentUser().getUid();
        mDatabase.child("users").child(userID).child("isTeacher").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getValue()!= null) {
                    int status = dataSnapshot.getValue(Integer.class);
                    if (status == -1) { /* if the status is not selected */
                        activity.startActivity(new Intent(activity, ChooseStatus.class));
                    } else { /* status entered - pass the status to Main Activity */
                        userType.add(dataSnapshot.getValue(Integer.class));
                        Intent intent = new Intent(activity, MainActivity.class);
                        intent.putExtra("status", userType);
                        activity.startActivity(intent);
                    }
                    ((Activity)activity).finish();
                } else {
                    Toast.makeText(activity, "Could not retrieve value from database.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
        /* loading screen section (showing loading screen until data received from FireBase) */
        Intent intent = new Intent(activity, LoadingScreen.class);
        /* prevent going back to this loading screen (from the next screen) */
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        activity.startActivity(intent);
        /* END loading screen section */
        ((Activity)activity).finish();
    }

    public void setUnreadedChats(MaterialButton m) {
        String userID = fAuth.getCurrentUser().getUid();
        mDatabase.child("chats").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int result = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    int val = ds.child("read").getValue(Integer.class);
                    if (val==0) result++;
                }
                SpannableString ss = new SpannableString("("+result+")");
                m.setText(ss);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    final int TUTORS_TO_SHOW = 5;
    public void setRandomTutorsAndCloseLoadingDialog(Context context, ArrayList<TutorAdapter> adapter,
                                              ListView listview, LoadingDialog ld) {
        ValueEventListener listViewListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /* Init 'TutorAdapterItem' list for the adapter */
                List<TutorAdapterItem> tutorsToShow = new ArrayList<>();
                Random random = new Random();
                int tutorsNum = (int)dataSnapshot.child("teachers").getChildrenCount();
                /* Init set to store 3 random numbers */
                Set<Integer> data = new LinkedHashSet<>();
                /* add 3 random numbers in range (0,teachersNum) [each indicates tutor index] */
                while (data.size()<TUTORS_TO_SHOW && tutorsNum>data.size()) {
                    int rand = random.nextInt(tutorsNum);
                    data.add(rand);
                }
                int counter = 0;
                for (DataSnapshot ds : dataSnapshot.child("teachers").getChildren()) {
                    /* If the pointer indicates one of the random numbers - add the tutor to the list */
                    if (data.contains(counter)) {
                        data.remove(counter);
                        tutorObj teacher = ds.getValue(tutorObj.class);
                        userObj user = dataSnapshot.child("users").child(teacher.getUserID()).getValue(userObj.class);
                        int subsNum = 0;
                        if (teacher.getSub() != null) {
                            subsNum = teacher.getSub().size();
                            Object[] subs = teacher.getSub().keySet().toArray();
                            String sub = (String) subs[random.nextInt(subsNum)];

                            if (user == null) {
                                System.out.println("Found tutor without user information. Tutor ID:" + ds.getKey());
                                continue;
                            }
                            TutorAdapterItem item = new TutorAdapterItem(user, teacher, sub);
                            tutorsToShow.add(item);
                        }
                        /* done adding all random tutors */
                        if (data.size()==0) break;
                    }
                    counter++;
                }
                /* Shuffle the chronological order (of the tutors' keys) */
                Collections.shuffle(tutorsToShow);
                /* init the adapter with the 'tutorsToShow' list */
                adapter.add(new TutorAdapter(context, tutorsToShow));
                listview.setAdapter(adapter.get(0));
                adapter.get(0).updateListViewHeight(listview);
                closeLoadingDialog(adapter,ld);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        };
        myRef.addListenerForSingleValueEvent(listViewListener);
    }

    /* close Loading Dialog when all fragment resources ready */
    public void closeLoadingDialog(ArrayList<TutorAdapter> adapter, LoadingDialog ld) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                /* wait until the adapter created & loaded all images */
                while(!adapter.get(0).isAllResourcesReady()) { }
                /* hide loading dialog (fragment resources ready) */
                ld.cancel();
            }
        });
    }



    public void signInWithEmailAndPassword(Activity activity, String email, String password) {
        fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    final FirebaseUser user = fAuth.getCurrentUser();
                    if (!user.isEmailVerified()) { /* Email Not Verified! - alert the user */
                        final AlertDialog.Builder emailVerification = new AlertDialog.Builder(activity);
                        emailVerification.setTitle("You need to verify your email.");
                        emailVerification.setMessage("Re-send email verification?");
                        /* manage buttons */
                        emailVerification.setPositiveButton("RE-SEND", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(activity, "Verification email has been sent.", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(activity,"Error! email not sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(activity, "Logged in Successfully!", Toast.LENGTH_SHORT).show();
                        /* redirects to the appropriate page depending on the user status */
                        FirebaseManager fm = new FirebaseManager();
                        fm.getUserInsideApp((Activity)activity);
                    }
                } else {
                    Toast.makeText(activity, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void sendPasswordResetEmail(Activity activity,String mail) {
        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(activity, "Reset link send to your mail.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity,"Error! The password was not sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void createUserWithEmailAndPassword(Activity activity, userObj userObject, String email, String password) {
        fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isComplete()&&task.isSuccessful()) {
                    Toast.makeText(activity, "User Created", Toast.LENGTH_SHORT).show();
                    /* send verification link */
                    FirebaseUser user = fAuth.getCurrentUser();
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(activity, "Verification email has been sent.", Toast.LENGTH_SHORT).show();
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
                    u.addUserToDB(userID, userObject);
                    /* add welcome notification to user */
                    FireBaseNotifications.sendNotification(userID,"Register","");
                    fAuth.signOut();
                    // startActivity(new Intent(getApplicationContext(), ChooseOne.class));
                    activity.startActivity(new Intent(activity, Login.class));
                } else {
                    Toast.makeText(activity, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
