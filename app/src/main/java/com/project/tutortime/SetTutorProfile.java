package com.project.tutortime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.tutortime.firebase.FireBaseTeacher;
import com.project.tutortime.firebase.subjectObj;

import java.util.ArrayList;

public class SetTutorProfile extends AppCompatActivity {
    EditText PhoneNumber, description;
    Button profile, addSub, addImage;
    ImageView img;
    ListView subjectList;
    ArrayList<subjectObj> list = new ArrayList<>();
    ArrayList<String> listSub = new ArrayList<>();
    FirebaseAuth fAuth;
    Uri imageData;
    String imgURL;
    private DatabaseReference mDatabase;
    private static final int GALLERY_REQUEST_COD = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_tutor_profile);
        fAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        PhoneNumber = findViewById(R.id.editPhoneNumber);
        description = findViewById(R.id.editDescription);
        addSub = findViewById(R.id.editSubject);
        profile = findViewById(R.id.buttonProfile);
        addImage = findViewById(R.id.btnAddImage);
        img = findViewById(R.id.imageView);
        subjectList = (ListView) findViewById(R.id.subList);
        ArrayAdapter a = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        subjectList.setAdapter(a);
        a.notifyDataSetChanged();

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, GALLERY_REQUEST_COD);
            }
        });

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
                //fileUploader();
                String userID = fAuth.getCurrentUser().getUid();
                String pNum = PhoneNumber.getText().toString().trim();
                String descrip = description.getText().toString().trim();
                if (TextUtils.isEmpty(pNum)) {
                    PhoneNumber.setError("PhoneNumber is required.");
                    return;
                }
                if (list.isEmpty()) {
                    Toast.makeText(SetTutorProfile.this, "You must choose at least one subject",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                FireBaseTeacher t = new FireBaseTeacher();
                /* set isTeacher to teacher status (1=teacher,0=customer) */
                mDatabase.child("users").child(userID).child("isTeacher").setValue(1);
                /* add the teacher to database */
                t.addTeacherToDB(pNum, descrip, userID, list, imgURL);
                /* were logging in as tutor (tutor status value = 1).
                     pass 'Status' value (1) to MainActivity. */
                final ArrayList<Integer> arr = new ArrayList<Integer>();
                arr.add(1);
                //Intent intent = new Intent(SetTutorProfile.this, MainActivity.class);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                /* disable returning to SetTutorProfile class after opening main
                 * activity, since we don't want the user to re-choose Profile
                 * because the tutor profile data still exists with no use!
                 * (unless we implementing method to remove the previous data) */
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("status",arr);
                /* finish last activities to prevent last MainActivity to run with Customer view */
                finishAffinity();
                startActivity(intent);
                finish();
            }
        });
    }

    public void createDialog(ArrayAdapter a) {
        final Dialog d = new Dialog(this);
        EditText priceEdit, expEdit;
        Button addBtn, closeBtn;
        Spinner nameSpinner, typeSpinner;
        d.setContentView(R.layout.subject_add_dialog);
        d.setTitle("Add Subject");
        d.setCancelable(true);
        priceEdit = d.findViewById(R.id.editPrice);
        expEdit = d.findViewById(R.id.editExp);
        addBtn = d.findViewById(R.id.btnAddS);
        closeBtn = d.findViewById(R.id.btnCloseS);
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
//                if (nameSub.isEmpty() || nameSub.equals(subjectObj.SubName.HINT)) {
//                    Toast.makeText(SetTutorProfile.this, "Subject is required.", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (type.isEmpty() || type.equals(subjectObj.Type.HINT)) {
//                    Toast.makeText(SetTutorProfile.this, "Learning type is required.", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                if (listSub.contains(nameSub)) {
                    Toast.makeText(SetTutorProfile.this, "You already have selected this subject.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (nameSub == "Select subject") {
                    Toast.makeText(SetTutorProfile.this, "Subject is required.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (type == "Select learning type") {
                    Toast.makeText(SetTutorProfile.this, "Learning type is required.", Toast.LENGTH_SHORT).show();
                    return;
                }
                subjectObj s = new subjectObj(nameSub, type, Integer.parseInt(price), exp);
                list.add(s);
                a.notifyDataSetChanged();
                listSub.add(nameSub);
                d.dismiss();
            }
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                a.notifyDataSetChanged();
                d.dismiss();
            }
        });
        d.show();
    }

    public void createEditDialog(ArrayAdapter a, subjectObj currSub){
        final Dialog d = new Dialog(SetTutorProfile.this);
        EditText priceEdit, expEdit;
        Button saveBtn, deleteBtn, closeBtn;
        Spinner nameSpinner, typeSpinner;
        d.setContentView(R.layout.subject_edit_dialog);
        d.setTitle("Edit Subject");
        d.setCancelable(true);
        priceEdit = d.findViewById(R.id.PriceEdit);
        expEdit = d.findViewById(R.id.ExpEdit);
        saveBtn = d.findViewById(R.id.btnSave);
        closeBtn = d.findViewById(R.id.btnClose);
        deleteBtn = d.findViewById(R.id.btnDelete);
        nameSpinner = d.findViewById(R.id.spinSubName);
        ArrayAdapter nameAd = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
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
                    Toast.makeText(SetTutorProfile.this, "Subject is required.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (type.isEmpty() || type.equals(subjectObj.Type.HINT)) {
                    Toast.makeText(SetTutorProfile.this, "Learning type is required.", Toast.LENGTH_SHORT).show();
                    return;
                }
                /* if the subject already exists BUT IN THE ENTRY THAT CURRENTLY EDITING - IT'S OK!  */
                if (listSub.contains(nameSub) && nameSub != currSub.getsName()) {
                    Toast.makeText(SetTutorProfile.this, "You already have selected this subject.", Toast.LENGTH_SHORT).show();
                    return;
                }
                subjectObj s = new subjectObj(nameSub, type, Integer.parseInt(price), exp);
                /* remove the last entry (before the edit) */
                list.remove(currSub);
                /*  */
                list.add(s);
                listSub.add(nameSub);
                a.notifyDataSetChanged();
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
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                a.notifyDataSetChanged();
                d.dismiss();
            }
        });
        d.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_COD && resultCode == RESULT_OK && data != null) {
            imageData = data.getData();
            img.setImageURI(imageData);
        }
    }
    private String getExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }
    private void fileUploader(){
        imgURL=System.currentTimeMillis()+"."+getExtension(imageData);
        StorageReference Ref= FirebaseStorage.getInstance().getReference().child(imgURL);
        Ref.putFile(imageData)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), "Upload imageFailed", Toast.LENGTH_LONG).show();
                    }
                });
    }
}