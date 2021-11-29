package com.project.tutortime;

        import androidx.appcompat.app.AppCompatActivity;
        import androidx.cardview.widget.CardView;

        import android.content.Intent;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;

        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;

        import java.util.ArrayList;

public class ChooseStatus extends AppCompatActivity {
    Button teacher, student;
    FirebaseAuth fAuth;
    private DatabaseReference mDatabase;
    CardView tutor, customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_status);

        fAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //teacher = findViewById(R.id.btnTeacher);
        //student = findViewById(R.id.btnStudent);
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
                String userID = fAuth.getCurrentUser().getUid();
                mDatabase.child("users").child(userID).child("isTeacher").setValue(0);
                /* were logging in as customer (customer status value = 0).
                     pass 'Status' value (0) to MainActivity. */
                final ArrayList<Integer> arr = new ArrayList<Integer>();
                arr.add(0);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("status",arr);
                startActivity(intent);
            }
        });
    }
}