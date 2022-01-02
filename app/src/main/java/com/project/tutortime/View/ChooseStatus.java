package com.project.tutortime.View;

        import androidx.appcompat.app.AppCompatActivity;
        import androidx.cardview.widget.CardView;

        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.content.pm.ActivityInfo;
        import android.content.res.Configuration;
        import android.os.Bundle;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.Button;

        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.project.tutortime.MainActivity;
        import com.project.tutortime.Model.firebase.FirebaseManager;
        import com.project.tutortime.R;

        import java.util.ArrayList;
        import java.util.Locale;

        import belka.us.androidtoggleswitch.widgets.ToggleSwitch;

public class ChooseStatus extends AppCompatActivity {
    Button teacher, student;
    CardView tutor, customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_status);
        /* DISABLE landscape orientation  */
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        tutor = findViewById(R.id.cardViewTutor);
        customer = findViewById(R.id.cardViewCustomer);
        tutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SetTutorProfile.class));
            }
        });
        customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String userID = fAuth.getCurrentUser().getUid();
                //mDatabase.child("users").child(userID).child("isTeacher").setValue(0);
                FirebaseManager fm = new FirebaseManager();
                fm.setUserType(0);
                /* were logging in as customer (customer status value = 0).
                     pass 'Status' value (0) to MainActivity. */
                final ArrayList<Integer> arr = new ArrayList<Integer>();
                arr.add(0);
                Intent intent = new Intent(ChooseStatus.this, MainActivity.class);
                /* disable returning to ChooseStatus class after opening main
                 * activity, since we don't want the user to choose Create Tutor
                 * Profile from navigation bar and then after returning to
                 * ChooseStatus class - re-choose Create Tutor Profile from ChooseStatus
                 * because the previous tutor profile data still exists with no use!
                 * (unless we implementing method to remove the previous data) */
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("status",arr);
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        final MenuItem toggleservice = menu.findItem(R.id.lang_switch);
        final ToggleSwitch langSwitch = toggleservice.getActionView().findViewById(R.id.lan);
        // TODO: https://stackoverflow.com/questions/32813934/save-language-chosen-by-user-android
        langSwitch.setOnToggleSwitchChangeListener(new ToggleSwitch.OnToggleSwitchChangeListener() {
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