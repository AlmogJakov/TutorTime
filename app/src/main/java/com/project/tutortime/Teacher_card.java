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
import com.google.firebase.auth.AuthResult;
import com.project.tutortime.firebase.FireBaseUser;
import com.project.tutortime.firebase.subjectObj;
import com.project.tutortime.firebase.userObj;

import java.util.ArrayList;

import javax.security.auth.Subject;

public class Teacher_card extends AppCompatActivity {
    EditText PhoneNumber, description;
    Button profile, addSub;
    ListView subjectList;
    ArrayList<subjectObj> list = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_card);
        PhoneNumber = findViewById(R.id.editPhoneNumber);
        description = findViewById(R.id.editDescription);
        addSub = findViewById(R.id.editSubject);
        profile = findViewById(R.id.buttonProfile);
        subjectList = (ListView) findViewById(R.id.subList);
        ArrayAdapter a = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        subjectList.setAdapter(a);
        a.notifyDataSetChanged();


        addSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog(a);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lName = PhoneNumber.getText().toString().trim();
                if (TextUtils.isEmpty(lName)) {
                    PhoneNumber.setError("PhoneNumber is required.");
                    return;
                }
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
                subjectObj s = new subjectObj(nameSub, type, Integer.parseInt(price), exp);
                list.add(s);
                a.notifyDataSetChanged();
                d.dismiss();


            }
        });
        d.show();
    }
    }


