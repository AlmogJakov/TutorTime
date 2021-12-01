package com.project.tutortime;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project.tutortime.databinding.ActivityMainBinding;
import com.project.tutortime.firebase.teacherObj;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    FirebaseAuth fAuth;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_new_tutor_profile, R.id.nav_tutor_profile, R.id.nav_search, R.id.nav_notifications)
                .setOpenableLayout(drawer).build();

        /* get the array list contains the status of the user (0=customer/1=tutor) */
        ArrayList<Integer> arr = getIntent().getExtras().getIntegerArrayList("status");
        if (arr.isEmpty() || (arr.get(0) != 0 && arr.get(0) != 1)) {
            Toast.makeText(MainActivity.this, "Could not retrieve value from database.", Toast.LENGTH_SHORT).show();
            return;
        }
        /* assign status value from the received array list */
        int status = arr.get(0);
        //Toast.makeText(MainActivity.this, "Status: "+status, Toast.LENGTH_SHORT).show();


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        /* Adjust the view to the user status */
        fAuth = FirebaseAuth.getInstance();
        /* get user from firebase */
        FirebaseUser user = fAuth.getCurrentUser();
        View navHeaderView = navigationView.getHeaderView(0);
        /* show hello massage in main with user information */
        loadUserEmailToNavigation(user,navHeaderView);
        Menu nav_Menu = navigationView.getMenu();
        if (status == 0) { /* default user (customer) */
            /* hide options - Here you can hide options from navigation bar! */
            nav_Menu.findItem(R.id.nav_tutor_profile).setVisible(false);
        } else { /* tutor */
            /* hide options - Here you can hide options from navigation bar! */
            nav_Menu.findItem(R.id.nav_new_tutor_profile).setVisible(false);
            /* load Teacher Image To Navigation bar */
            loadTutorImageToNavigation(navHeaderView);
        }
        /* END Adjust the view to the user status */


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void loadTutorImageToNavigation(View navHeaderView) {
        String userID = fAuth.getCurrentUser().getUid();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String teacherID = dataSnapshot.child("users").child(userID).child("teacherID").getValue(String.class);
                if (teacherID!=null) {
                    DataSnapshot imageLink = dataSnapshot.child("teachers").child(teacherID).child("imgUrl");
                    /* if the link exists (case of Tutor with no profile image) */
                    if (imageLink!=null&&imageLink.getValue()!=null) {
                        System.out.println(imageLink.getValue());
                        ImageView profile = (ImageView)navHeaderView.findViewById(R.id.imageView);
                        StorageReference imagesRef = storageRef.child(imageLink.getValue().toString());
                        /* allow 1MB (1024*1024) KB download */
                        imagesRef.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                profile.setImageBitmap(bmp);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                /* If the user registers as a tutor with an image,
                                then the address of the image will be saved immediately
                                in the database, but the image will not be uploaded immediately
                                 - so an error will occur and we will get here. */

                                //Toast.makeText(getApplicationContext(), "No Such file or Path found!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    private void loadUserEmailToNavigation(FirebaseUser user,View navHeaderView) {
        TextView firstHello = (TextView)navHeaderView.findViewById(R.id.firstHello);
        //String hello = "";
        if(user != null) {
            mDatabase.child("users").child(user.getUid()).child("fName").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String first_name = snapshot.getValue(String.class);
                    firstHello.setText("Hello, "+first_name+"!");
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
            //hello = hello.concat("").concat(user.getEmail());
        }
        TextView EmailHello = (TextView)navHeaderView.findViewById(R.id.emailHello);
        String hello = "";
        hello = hello.concat("").concat(user.getEmail());
        EmailHello.setText(hello);
    }
}
