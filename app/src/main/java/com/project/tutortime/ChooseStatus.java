package com.project.tutortime;

        import androidx.appcompat.app.AppCompatActivity;

        import android.content.Intent;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;

        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;

public class ChooseStatus extends AppCompatActivity {
    Button teacher, student;
    FirebaseAuth fAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_one);

        fAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        teacher = findViewById(R.id.btnTeacher);
        student = findViewById(R.id.btnStudent);

        teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SetTutorProfile.class));
            }
        });

        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = fAuth.getCurrentUser().getUid();
                mDatabase.child("users").child(userID).child("isTeacher").setValue(0);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }
}