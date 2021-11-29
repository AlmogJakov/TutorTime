package com.project.tutortime;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.tutortime.databinding.ActivityMainBinding;


import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    FirebaseAuth fAuth;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

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
        /* hide options - Here you can hide options from navigation bar! */
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        if (status == 0) { /* default user (customer) */
            nav_Menu.findItem(R.id.nav_tutor_profile).setVisible(false);
        } else { /* tutor */
            nav_Menu.findItem(R.id.nav_new_tutor_profile).setVisible(false);
        }
        /* END hide options */

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        /* get user from firebase */
        fAuth = FirebaseAuth.getInstance();
        FirebaseUser user = fAuth.getCurrentUser();
        /* show hello massage in main with user information */
        View navHeaderView = navigationView.getHeaderView(0);
        TextView EmailHello = (TextView)navHeaderView.findViewById(R.id.emailHello);
        String hello = "";
        if(user != null) { hello = hello.concat("").concat(user.getEmail()); }
        EmailHello.setText(hello);
        /* END show hello massage in main with user information */
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
}
