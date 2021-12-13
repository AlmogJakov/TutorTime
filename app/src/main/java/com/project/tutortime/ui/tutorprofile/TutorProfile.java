package com.project.tutortime.ui.tutorprofile;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.tutortime.LoadingScreen;
import com.project.tutortime.MainActivity;
import com.project.tutortime.R;
import com.project.tutortime.SetTutorProfile;
import com.project.tutortime.databinding.FragmentTutorProfileBinding;
import com.project.tutortime.firebase.FireBaseTeacher;
import com.project.tutortime.firebase.FireBaseUser;
import com.project.tutortime.firebase.subjectObj;
import com.project.tutortime.firebase.userObj;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TutorProfile extends Fragment {

    private TutorProfileViewModel TutorProfileViewModel;
    private FragmentTutorProfileBinding binding;
    EditText fname, lname, pnumber, description;
    Button saveProfile, addSub, updateImage;
    String teacherID;
    Spinner citySpinner;
    ImageView img;
    ListView subjectList;
    ArrayList<subjectObj> list = new ArrayList<>();
    ArrayList<String> listSub = new ArrayList<>();
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    Uri imageData;
    String imgURL;
    boolean del = false;

    private static final int GALLERY_REQUEST_COD = 1;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        TutorProfileViewModel = new ViewModelProvider(this).get(TutorProfileViewModel.class);
        binding = FragmentTutorProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.myTutorProfile;
//        TutorProfileViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        fname = binding.myFName;
        lname = binding.myLName;
        pnumber = binding.myPhoneNumber;
        description = binding.editDescription.getEditText();
        saveProfile = binding.btnSaveProfile;
        addSub = binding.addSubject;
        updateImage = binding.btnUpdateImage;
        img = binding.imageView;
        subjectList = binding.subList;
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.dropdown_menu_popup_item);
        //AutoCompleteTextView editTextFilledExposedDropdown = binding.spinnerCity;
        //editTextFilledExposedDropdown.setAdapter(adapter);
        citySpinner = binding.spinnerCity;
        ArrayAdapter a = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, list);
        subjectList.setAdapter(a);
        a.notifyDataSetChanged();

        /* Disable all Buttons & Text Edit Fields - until all data received from FireBase */
        fname.setEnabled(false);
        lname.setEnabled(false);
        pnumber.setEnabled(false);
        description.setEnabled(false);
        saveProfile.setEnabled(false);
        addSub.setEnabled(false);
        updateImage.setVisibility(View.GONE);
        citySpinner.setEnabled(false);
        subjectList.setEnabled(false);
        /* END Disable all Buttons & Text Edit Fields */

        /* Select City Spinner Code () */
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (position == 0) { // Hint
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(0)); }
                return v; }
            @Override
            public int getCount() { return super.getCount(); }
            @Override /* Disable selection of the Hint (first selection) */
            public boolean isEnabled(int position) { return (position != 0); }
            @Override /* Set the color of the Hint (first selection) to Grey */
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView)view;
                if (position == 0) tv.setTextColor(Color.GRAY); else tv.setTextColor(Color.BLACK);
                return view; }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        String[] cities = getResources().getStringArray(R.array.Cities);
        adapter.add("Choose City");
        adapter.addAll(cities);
        citySpinner.setAdapter(adapter);
        citySpinner.setSelection(0); //display hint
        /* END Select City Spinner Code () */

//        delImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                StorageReference imgRef = FirebaseStorage.getInstance().getReferenceFromUrl(imgURL);
//                imgRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        // File deleted successfully
//                        Log.d("Picture", "onSuccess: deleted file");
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        // Uh-oh, an error occurred!
//                        Log.d("Picture", "onFailure: did not delete file");
//                    }
//                });
//            }
//        });

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
                if (imgURL != null && del == false) {
                    final Dialog d = new Dialog(getActivity());
                    Button editImage, deleteImage;
                    d.setContentView(R.layout.image_dialog);
                    //d.setTitle("Add Subject");
                    d.setCancelable(true);
                    editImage = d.findViewById(R.id.btnEditImage);
                    deleteImage = d.findViewById(R.id.DeleteImage);
                    editImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(intent, GALLERY_REQUEST_COD);
                            del = false;
                            d.dismiss();
                        }
                    });

                    deleteImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            img.setImageDrawable(null);
                            img.setBackgroundResource(R.mipmap.ic_launcher_round);
                            del = true;
                            d.dismiss();
                        }
                    });
                    d.show();
                }
                else{
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, GALLERY_REQUEST_COD);
                    del = false;
                }
            }
        });

        saveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String pNum = pnumber.getText().toString().trim();
                String descrip = description.getText().toString().trim();
                String firstName = fname.getText().toString().trim();
                String lastName = lname.getText().toString().trim();
                String city = citySpinner.getSelectedItem().toString();
                if (TextUtils.isEmpty(firstName)) {
                    fname.setError("First name is required.");
                    return; }
                if (TextUtils.isEmpty(lastName)) {
                    lname.setError("Last name is required.");
                    return; }
                if (TextUtils.isEmpty(pNum)) {
                    pnumber.setError("PhoneNumber is required.");
                    return; }
                if (pNum.length() != 10 && pNum.charAt(0) != 0 &&  pNum.charAt(1) != 5) {
                    pnumber.setError("Invalid phoneNumber.");
                    return; }
                if (citySpinner.getSelectedItemPosition()==0) {
                    TextView errorText = (TextView)citySpinner.getSelectedView();
                    errorText.setError("City is required.");
                    Toast.makeText(getActivity(), "City is required.",
                            Toast.LENGTH_SHORT).show();
                    return; }
                if (list.isEmpty()) {
                    Toast.makeText(getActivity(), "You must choose at least one subject",
                            Toast.LENGTH_SHORT).show();
                    return; }
                new FireBaseUser().getUserRef().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        /* get teacher ID */
                        teacherID = dataSnapshot.child("teacherID").getValue(String.class);
                        /* Make a list of all the RealTime DataBase commands to execute
                            (for the purpose of executing all the commands at once) */
                        Map<String, Object> childUpdates = new HashMap<>();
                        if (imgURL != null) childUpdates.put("teachers/" + teacherID + "/imgUrl", imgURL);
                        childUpdates.put("teachers/" + teacherID + "/phoneNum", pNum);
                        childUpdates.put("teachers/" + teacherID + "/description", descrip);
                        childUpdates.put("users/" + userID + "/fName", firstName);
                        childUpdates.put("users/" + userID + "/lName", lastName);
                        childUpdates.put("users/" + userID + "/city", city);
                        /* If the user deleted the image - delete it from the storage and add
                            a delete command to childUpdates (to delete it URL from the RealTime DataBase) */
                        if (del) {
                            if (imgURL != null) childUpdates.put("teachers/" + teacherID + "/imgUrl", null);
                            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                            StorageReference storageReference = firebaseStorage.getReference(imgURL);
                            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.e("Picture", "#deleted");
                                    imgURL = null; }
                            });
                        }
                        /* Finally, execute all RealTime DataBase commands in one command (safely). */
                        myRef.updateChildren(childUpdates);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
//                /* were logging in as tutor (tutor status value = 1).
//                     pass 'Status' value (1) to MainActivity. */
//                final ArrayList<Integer> arr = new ArrayList<Integer>();
//                arr.add(1);
//                //Intent intent = new Intent(SetTutorProfile.this, MainActivity.class);
//                Intent intent = new Intent(getActivity(), MainActivity.class);
//                /* disable returning to SetTutorProfile class after opening main
//                 * activity, since we don't want the user to re-choose Profile
//                 * because the tutor profile data still exists with no use!
//                 * (unless we implementing method to remove the previous data) */
//                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                intent.putExtra("status",arr);
//                /* finish last activities to prevent last MainActivity to run with Customer view */
//                getActivity().finish();
//                startActivity(intent);
//                getActivity().getFragmentManager().popBackStack();
                if (imageData==null) {
                    goToTutorMain(requireActivity());
                } else {
                    uploadImageAndGoToMain(teacherID);
                }
            }
        });
        return root;
    }

    /* get fragment activity (to do actions on the activity) */
    private void goToTutorMain(Activity currentActivity) {
        //Activity currentActivity = getContext();
        /* were logging in as tutor (tutor status value = 1).
         * therefore, pass 'Status' value (1) to MainActivity. */
        final ArrayList<Integer> arr = new ArrayList<Integer>();
        arr.add(1);
        //Intent intent = new Intent(SetTutorProfile.this, MainActivity.class);
        Intent intent = new Intent(currentActivity, MainActivity.class);
        /* disable returning to SetTutorProfile class after opening main
         * activity, since we don't want the user to re-choose Profile
         * because the tutor profile data still exists with no use!
         * (unless we implementing method to remove the previous data) */
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("status",arr);
        /* finish last activities to prevent last MainActivity to run with Customer view */
        currentActivity.finishAffinity();
        currentActivity.startActivity(intent);
        currentActivity.finish();
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fname.setText(dataSnapshot.child("users").child(userID).child("fName").getValue(String.class));
                lname.setText(dataSnapshot.child("users").child(userID).child("lName").getValue(String.class));
                teacherID = dataSnapshot.child("users").child(userID).child("teacherID").getValue(String.class);
                String currCity = dataSnapshot.child("users").child(userID).
                        child("city").getValue(String.class);
                String[] cities = getResources().getStringArray(R.array.Cities);
                for (int i = 0; i < cities.length; i++) {
                    if (citySpinner.getItemAtPosition(i).equals(currCity)) {
                        citySpinner.setSelection(i);
                        break;
                    }
                }
                pnumber.setText(dataSnapshot.child("teachers").child(teacherID).
                        child("phoneNum").getValue(String.class));
                description.setText(dataSnapshot.child("teachers").child(teacherID).
                        child("description").getValue(String.class));

                imgURL = dataSnapshot.child("teachers").child(teacherID).child("imgUrl").getValue(String.class);
                if (imgURL != null) { /* The image exists! */
                    StorageReference storageReference = storage.getReference().child(imgURL);
                    Glide.with(getContext()).load(storageReference).into(img);

//                    /* The image has already been downloaded from the server for display
//                     * in the navigation bar, so take the existing image. */
//                    NavigationView navigationView = (NavigationView)getActivity().findViewById(R.id.nav_view);
//                    View navHeaderView = navigationView.getHeaderView(0);
//                    ImageView prof = (ImageView)navHeaderView.findViewById(R.id.imageView);
//                    img.setImageDrawable(prof.getDrawable());
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


                /* Enable all Buttons & Text Edit Fields - data already received from FireBase */
                fname.setEnabled(true);
                lname.setEnabled(true);
                pnumber.setEnabled(true);
                description.setEnabled(true);
                saveProfile.setEnabled(true);
                addSub.setEnabled(true);
                updateImage.setVisibility(View.VISIBLE);
                citySpinner.setEnabled(true);
                subjectList.setEnabled(true);
                /* END Enable all Buttons & Text Edit Fields */

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "onCreate error. " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
        Spinner priceEdit;
        EditText expEdit;
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

        priceEdit.setAdapter(new ArrayAdapter<>
                (getActivity(), android.R.layout.simple_spinner_item, subjectObj.Prices.values()));

        nameSpinner.setAdapter(new ArrayAdapter<>
                (getActivity(), android.R.layout.simple_spinner_item, subjectObj.SubName.values()));
        typeSpinner = d.findViewById(R.id.spinnerLType);
        typeSpinner.setAdapter(new ArrayAdapter<>
                (getActivity(), android.R.layout.simple_spinner_item, subjectObj.Type.values()));
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String price = priceEdit.getText().toString().trim();
                String price = priceEdit.getSelectedItem().toString().trim();
                String exp = expEdit.getText().toString().trim();
                String nameSub = nameSpinner.getSelectedItem().toString().trim();
                String type = typeSpinner.getSelectedItem().toString().trim();
                if (price == "Select Price") {
                    //priceEdit.setError("Price is required.");
                    Toast.makeText(getActivity(), "Please select a price.", Toast.LENGTH_SHORT).show();
                    return; }
                if (listSub.contains(nameSub)) {
                    Toast.makeText(getActivity(), "You already have selected this subject.", Toast.LENGTH_SHORT).show();
                    return; }
                if (nameSub == "Select subject") {
                    Toast.makeText(getActivity(), "Subject is required.", Toast.LENGTH_SHORT).show();
                    return; }
                if (type == "Select learning type") {
                    Toast.makeText(getActivity(), "Learning type is required.", Toast.LENGTH_SHORT).show();
                    return; }
                subjectObj s = new subjectObj(nameSub, type, Integer.parseInt(price), exp);
                list.add(s);
                updateList();
                listSub.add(nameSub);
                d.dismiss();
            }
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { d.dismiss(); }
        });
        d.show();
    }

    public void createEditDialog(ArrayAdapter a, subjectObj currSub) {
        final Dialog d = new Dialog(getActivity());
        Spinner priceEdit;
        EditText expEdit;
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

        priceEdit.setAdapter(new ArrayAdapter<>
                (getActivity(), android.R.layout.simple_spinner_item, subjectObj.Prices.values()));

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
        priceEdit.setSelection(currSub.getPricesEnumPosition(currSub.getPrice()));
        expEdit.setText((currSub.getExperience()));

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String price = priceEdit.getText().toString().trim();
                String price = priceEdit.getSelectedItem().toString().trim();
                String exp = expEdit.getText().toString().trim();
                String nameSub = nameSpinner.getSelectedItem().toString().trim();
                String type = typeSpinner.getSelectedItem().toString().trim();
                if (price == "Select Price") {
                    //priceEdit.setError("Price is required.");
                    Toast.makeText(getActivity(), "Please select a price.", Toast.LENGTH_SHORT).show();
                    return; }
                if (nameSub.isEmpty() || nameSub.equals(subjectObj.SubName.HINT)) {
                    Toast.makeText(getActivity(), "Subject is required.", Toast.LENGTH_SHORT).show();
                    return; }
                if (type.isEmpty() || type.equals(subjectObj.Type.HINT)) {
                    Toast.makeText(getActivity(), "Learning type is required.", Toast.LENGTH_SHORT).show();
                    return; }
                /* if the subject already exists BUT IN THE ENTRY THAT CURRENTLY EDITING - IT'S OK!  */
                if (listSub.contains(nameSub) && !nameSub.equals(currSub.getsName())) {
                    Toast.makeText(getActivity(), "You already have selected this subject.", Toast.LENGTH_SHORT).show();
                    return; }
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
                listSub.remove(currSub.getsName());
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
            //imageData = data.getData();
            //img.setImageURI(imageData);
            try {
                imageData = data.getData();
                /* crop the image bmp to square */
                InputStream imageStream = getContext().getContentResolver().openInputStream(imageData);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                selectedImage = getResizedBitmap(selectedImage, 200);// 400 is for example, replace with desired size
                /* show the new image on screen */
                //img.setImageResource(0); // clear image view
                img.setImageBitmap(selectedImage);






                /* convert the new bmp to Uri & assign the new Uri to 'imageData' */
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                // https://stackoverflow.com/questions/61654022/java-lang-illegalstateexception-failed-to-build-unique-file-storage-emulated
                String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), selectedImage, "IMG_" + Calendar.getInstance().getTime(), null);
                if (path!=null) imageData = Uri.parse(path);
                else {
                    imageData = null;
                    Toast.makeText(getActivity(), "Upload image Failed", Toast.LENGTH_LONG).show();
                }


//                File tempDir= Environment.getExternalStorageDirectory();
//                tempDir=new File(tempDir.getAbsolutePath()+"/.temp/");
//                tempDir.mkdir();
//                File tempFile = File.createTempFile("some", ".jpg", tempDir);
//                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//                byte[] bitmapData = bytes.toByteArray();
//                //write the bytes in file
//                FileOutputStream fos = new FileOutputStream(tempFile);
//                fos.write(bitmapData);
//                fos.flush();
//                fos.close();
//                imageData = Uri.fromFile(tempFile);


                /* Note that a new image has been created in the gallery
                 * but the image will be deleted after uploading it to the server.
                 * (In the 'fileUploader' method below) */
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap getResizedBitmap(Bitmap bitmap, int maxSize) {
        int width  = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = (height > width) ? width : height;
        int newHeight = (height > width)? height - ( height - width) : height;
        int cropW = (width - height) / 2;
        cropW = (cropW < 0)? 0: cropW;
        int cropH = (height - width) / 2;
        cropH = (cropH < 0)? 0: cropH;
        Bitmap cropImg = Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight);
        Bitmap resizedImg = Bitmap.createScaledBitmap(bitmap, maxSize, maxSize, false);
        return resizedImg;
    }

    private String getExtension(Uri uri) {
        ContentResolver cr = getActivity().getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void uploadImageAndGoToMain(String teacherID) {
        /* get fragment activity (to do actions on the activity) */
        Activity currentActivity = requireActivity();
        imgURL = System.currentTimeMillis()+"."+getExtension(imageData);
        StorageReference Ref= FirebaseStorage.getInstance().getReference().child(imgURL);
        Ref.putFile(imageData)
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override // onProgress show loading screen
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        /* loading screen section (showing loading screen until data received from FireBase) */
                        Intent intent = new Intent(currentActivity, LoadingScreen.class);
                        /* prevent going back to this loading screen (from the next screen) */
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        currentActivity.finishAffinity();
                        currentActivity.startActivity(intent);
                        /* END loading screen section */
                        currentActivity.finish();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override // on success set image URL on tutor database & go to main
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        /* Set the image URL AFTER After the image has been successfully uploaded */
                        mDatabase.child("teachers").child(teacherID).child("imgUrl").setValue(imgURL);
                        /* remove the cropped image from gallery */
                        if (imageData!=null) currentActivity.getContentResolver().delete(imageData, null, null);
                        /* pass the activity forward */
                        goToTutorMain(currentActivity);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getContext(), "Upload image Failed", Toast.LENGTH_LONG).show(); }
                });

    }
}