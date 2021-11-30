package com.project.tutortime.ui.tutorprofile;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.project.tutortime.R;
import com.project.tutortime.SetTutorProfile;
import com.project.tutortime.databinding.FragmentTutorProfileBinding;
import com.project.tutortime.firebase.FireBaseTeacher;
import com.project.tutortime.firebase.FireBaseUser;
import com.project.tutortime.firebase.firebaseBaseModel;
import com.project.tutortime.firebase.subjectObj;
import com.project.tutortime.firebase.userObj;

import java.util.ArrayList;

public class TutorProfile extends Fragment {

    private TutorProfileViewModel TutorProfileViewModel;
    private FragmentTutorProfileBinding binding;
    EditText fname, lname, pnumber, description;
    Button saveProfile, addSub, updateImage;
    String teacherID;
    ImageView img;
    ListView subjectList;
    ArrayList<subjectObj> list = new ArrayList<>();
    ArrayList<String> listSub = new ArrayList<>();
    FirebaseAuth fAuth;
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    FireBaseUser fbUser = new FireBaseUser();
    FireBaseTeacher fbTeacher = new FireBaseTeacher();
    userObj user_obj;
    Uri imageData;
    String imgURL;
    private static final int GALLERY_REQUEST_COD = 1;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        TutorProfileViewModel = new ViewModelProvider(this).get(TutorProfileViewModel.class);
        binding = FragmentTutorProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.myTutorProfile;
        TutorProfileViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        fname = binding.myFName;
        lname = binding.myLName;
        pnumber = binding.myPhoneNumber;
        description = binding.editDescription;
        saveProfile = binding.btnSaveProfile;
        addSub = binding.addSubject;
        updateImage = binding.btnUpdateImage;
        img = binding.imageView;
        subjectList = binding.subList;
        ArrayAdapter a = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, list);
        subjectList.setAdapter(a);
        a.notifyDataSetChanged();

        addSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog(a);
            }
        });

        subjectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                subjectObj s = (subjectObj) subjectList.getItemAtPosition(i);
                createEditDialog(a, s);
            }
        });

        updateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, GALLERY_REQUEST_COD);
            }
        });

        return root;
    }

    public void onResume() {
        super.onResume();
        String userID = fAuth.getInstance().getCurrentUser().getUid();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fname.setText(dataSnapshot.child("users").child(userID).child("fName").getValue(String.class));
                lname.setText(dataSnapshot.child("users").child(userID).child("lName").getValue(String.class));
                teacherID = dataSnapshot.child("users").child(userID).child("teacherID").getValue(String.class);
                pnumber.setText(dataSnapshot.child("teachers").child(teacherID).
                        child("phoneNum").getValue(String.class));
                description.setText(dataSnapshot.child("teachers").child(teacherID).
                        child("description").getValue(String.class));
                String imgAdd = dataSnapshot.child("teachers").child(teacherID).child("imgUrl").getValue(String.class);
                if (imgAdd != null) {
                    Glide.with(getActivity()).load(dataSnapshot.child("teachers").child(teacherID).
                            child("imgUrl").getValue(String.class)).into(img);
                }

                for (DataSnapshot subSnapsot : dataSnapshot.child("teachers").child(teacherID).
                        child("sub").getChildren()) {
                    subjectObj sub = new subjectObj(subSnapsot.child("sName").getValue(String.class),
                            subSnapsot.child("type").getValue(String.class),
                            subSnapsot.child("price").getValue(Integer.class),
                            subSnapsot.child("experience").getValue(String.class));
                    list.add(sub);
                    listSub.add(sub.getsName());
                }
                ArrayAdapter a = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, list);
                subjectList.setAdapter(a);
                a.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void createDialog(ArrayAdapter a) {
        final Dialog d = new Dialog(getActivity());
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
                (getActivity(), android.R.layout.simple_spinner_item, subjectObj.SubName.values()));
        typeSpinner = d.findViewById(R.id.spinnerLType);
        typeSpinner.setAdapter(new ArrayAdapter<>
                (getActivity(), android.R.layout.simple_spinner_item, subjectObj.Type.values()));
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
                if (listSub.contains(nameSub)) {
                    Toast.makeText(getActivity(), "You already have selected this subject.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (nameSub == "Select subject") {
                    Toast.makeText(getActivity(), "Subject is required.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (type == "Select learning type") {
                    Toast.makeText(getActivity(), "Learning type is required.", Toast.LENGTH_SHORT).show();
                    return;
                }
                subjectObj s = new subjectObj(nameSub, type, Integer.parseInt(price), exp);
                list.add(s);
                updateList();
                listSub.add(nameSub);
                d.dismiss();
            }

        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
            }
        });
        d.show();
    }

    public void createEditDialog(ArrayAdapter a, subjectObj currSub) {
        final Dialog d = new Dialog(getActivity());
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
        ArrayAdapter nameAd = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                subjectObj.SubName.values());
        nameSpinner.setAdapter(nameAd);
        nameSpinner.setSelection(subjectObj.SubName.valueOf(currSub.getsName().
                replaceAll("\\s+", "")).ordinal());

        ArrayAdapter typeAd = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                subjectObj.Type.values());
        typeSpinner = d.findViewById(R.id.spinType);
        typeSpinner.setAdapter(typeAd);
        typeSpinner.setSelection(subjectObj.Type.valueOf(currSub.getType().
                replaceAll("/", "")).ordinal());
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
                    Toast.makeText(getActivity(), "Subject is required.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (type.isEmpty() || type.equals(subjectObj.Type.HINT)) {
                    Toast.makeText(getActivity(), "Learning type is required.", Toast.LENGTH_SHORT).show();
                    return;
                }
                /* if the subject already exists BUT IN THE ENTRY THAT CURRENTLY EDITING - IT'S OK!  */
                if (listSub.contains(nameSub) && nameSub != currSub.getsName()) {
                    Toast.makeText(getActivity(), "You already have selected this subject.", Toast.LENGTH_SHORT).show();
                    return;
                }
                subjectObj s = new subjectObj(nameSub, type, Integer.parseInt(price), exp);
                /* remove the last entry (before the edit) */
                list.remove(currSub);
                /*  */
                list.add(s);
                updateList();
                listSub.add(nameSub);

                d.dismiss();
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.remove(currSub);
                updateList();
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

    public void updateList() {
        new FireBaseUser().getUserRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                teacherID = dataSnapshot.child("teacherID").getValue(String.class);
                new FireBaseTeacher().setSubList(teacherID, list);
                ArrayAdapter a = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, list);
                subjectList.setAdapter(a);
                a.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_COD && resultCode == Activity.RESULT_OK && data != null) {
            imageData = data.getData();
            img.setImageURI(imageData);
        }
    }

    private String getExtension(Uri uri) {
        ContentResolver cr = getActivity().getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void fileUploader() {
        imgURL = System.currentTimeMillis() + "." + getExtension(imageData);
        StorageReference Ref = FirebaseStorage.getInstance().getReference().child(imgURL);
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
                        Toast.makeText(getActivity(), "Upload imageFailed", Toast.LENGTH_LONG).show();
                    }
                });
    }
}