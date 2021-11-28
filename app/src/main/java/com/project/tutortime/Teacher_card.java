package com.project.tutortime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.tutortime.firebase.FireBaseTeacher;
import com.project.tutortime.firebase.FireBaseUser;
import com.project.tutortime.firebase.subjectObj;
import com.project.tutortime.firebase.userObj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.security.auth.Subject;

public class Teacher_card extends AppCompatActivity {
    EditText PhoneNumber, description;
    Button profile, addSub;
    ListView subjectList;
    ArrayList<subjectObj> list = new ArrayList<>();
    ArrayList<String> listSub = new ArrayList<>();
    FirebaseAuth fAuth;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_card);
        fAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        PhoneNumber = findViewById(R.id.editPhoneNumber);
        description = findViewById(R.id.editDescription);
        addSub = findViewById(R.id.editSubject);
        profile = findViewById(R.id.buttonProfile);
        subjectList = (ListView) findViewById(R.id.subList);
        ArrayAdapter a = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        subjectList.setAdapter(a);
        a.notifyDataSetChanged();

        subjectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                subjectObj s = (subjectObj) subjectList.getItemAtPosition(i);
                createEditDialog(a,s);
            }
        });


        addSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog(a);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pNum = PhoneNumber.getText().toString().trim();
                String descrip = description.getText().toString().trim();
                if (TextUtils.isEmpty(pNum)) {
                    PhoneNumber.setError("PhoneNumber is required.");
                    return;
                }
                if (list.isEmpty()) {
                    Toast.makeText(Teacher_card.this, "You must choose at least one subject",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                FireBaseTeacher t = new FireBaseTeacher();
                Toast.makeText(Teacher_card.this, descrip,
                        Toast.LENGTH_SHORT).show();
                String userID = fAuth.getCurrentUser().getUid();
                t.addTeacherToDB(pNum, descrip, userID, list);
                mDatabase.child("users").child(userID).child("isTeacher").setValue(1);
                startActivity(new Intent(getApplicationContext(), MainActivityTutor.class));
            }
        });
    }
    public void createDialog(ArrayAdapter a){
        final Dialog d = new Dialog(this);
        EditText priceEdit, expEdit;
        Button addBtn;
        Spinner nameSpinner, typeSpinner;
        d.setContentView(R.layout.add_sub_dialog);
        d.setTitle("Add Subject");
        d.setCancelable(true);

        priceEdit = d.findViewById(R.id.editPrice);
        expEdit = d.findViewById(R.id.editExp);
        addBtn = d.findViewById(R.id.btnAddS);
        nameSpinner = d.findViewById(R.id.spinnerSubName);
        nameSpinner.setAdapter(new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item, subjectObj.SubName.values()));
        typeSpinner = d.findViewById(R.id.spinnerLType);
        typeSpinner.setAdapter(new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item, subjectObj.Type.values()));
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String price = priceEdit.getText().toString().trim();
                String exp = expEdit.getText().toString().trim();
                String nameSub = nameSpinner.getSelectedItem().toString().trim();
                String type = typeSpinner.getSelectedItem().toString().trim();

                if (price.isEmpty()) {
                    priceEdit.setError("Price is required.");
                    return;
                }
                if (nameSub.isEmpty() || nameSub.equals(subjectObj.SubName.HINT)) {
                    Toast.makeText(getApplicationContext(), "Subject is required.", Toast.LENGTH_LONG);
                    return;
                }
                if (type.isEmpty() || type.equals(subjectObj.Type.HINT)) {
                    Toast.makeText(getApplicationContext(), "Learning type is required.", Toast.LENGTH_LONG);
                    return;
                }
                if (listSub.contains(nameSub)) {
                    Toast.makeText(getApplicationContext(), "You already have selected this subject.", Toast.LENGTH_LONG);
                    return;
                }
                subjectObj s = new subjectObj(nameSub, type, Integer.parseInt(price), exp);
                list.add(s);
                a.notifyDataSetChanged();
                listSub.add(nameSub);
                d.dismiss();
            }
        });
        d.show();
    }

    public void createEditDialog(ArrayAdapter a, subjectObj currSub){
        final Dialog d = new Dialog(Teacher_card.this);
        EditText priceEdit, expEdit;
        Button saveBtn, deleteBtn;
        Spinner nameSpinner, typeSpinner;
        d.setContentView(R.layout.edit_dialog);
        d.setTitle("Edit Subject");
        d.setCancelable(true);

        priceEdit = d.findViewById(R.id.PriceEdit);
        expEdit = d.findViewById(R.id.ExpEdit);
        saveBtn = d.findViewById(R.id.btnSave);
        deleteBtn = d.findViewById(R.id.btnDelete);/////////////////////////////////////////////////
        nameSpinner = d.findViewById(R.id.spinSubName);
        ArrayAdapter nameAd= new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                subjectObj.SubName.values());
        nameSpinner.setAdapter(nameAd);
        nameSpinner.setSelection(subjectObj.SubName.valueOf(currSub.getsName().
                replaceAll("\\s+","")).ordinal());

        ArrayAdapter typeAd = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                subjectObj.Type.values());
        typeSpinner = d.findViewById(R.id.spinType);
        typeSpinner.setAdapter(typeAd);
        typeSpinner.setSelection(subjectObj.Type.valueOf(currSub.getType().
                replaceAll("/","")).ordinal());

        priceEdit.setText(Integer.toString((currSub.getPrice())));
        expEdit.setText((currSub.getExperience()));


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String price = priceEdit.getText().toString().trim();
                String exp = expEdit.getText().toString().trim();
                String nameSub = nameSpinner.getSelectedItem().toString().trim();
                String type = typeSpinner.getSelectedItem().toString().trim();

                if (price.isEmpty()) {
                    priceEdit.setError("Price is required.");
                    return;
                }
                if (nameSub.isEmpty() || nameSub.equals(subjectObj.SubName.HINT)) {
                    Toast.makeText(getApplicationContext(), "Subject is required.", Toast.LENGTH_LONG);
                    return;
                }
                if (type.isEmpty() || type.equals(subjectObj.Type.HINT)) {
                    Toast.makeText(getApplicationContext(), "Learning type is required.", Toast.LENGTH_LONG);
                    return;
                }
                if (listSub.contains(nameSub)) {
                    Toast.makeText(getApplicationContext(), "You already have selected this subject.", Toast.LENGTH_LONG);
                    return;
                }
                subjectObj s = new subjectObj(nameSub, type, Integer.parseInt(price), exp);
                list.add(s);
                a.notifyDataSetChanged();
                listSub.add(nameSub);
                d.dismiss();
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.remove(currSub);
                a.notifyDataSetChanged();
                d.dismiss();
            }
        });
        d.show();
    }

}


