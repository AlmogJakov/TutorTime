package com.project.tutortime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
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
import com.project.tutortime.firebase.FireBaseTeacher;
import com.project.tutortime.firebase.FireBaseUser;
import com.project.tutortime.firebase.subjectObj;
import com.project.tutortime.ui.notifications.Notifications;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SetTutorProfile extends AppCompatActivity {
    TextView citySpinner;
    EditText PhoneNumber, description;
    Button profile, addSub, addImage;
    //Spinner citySpinner;
    ImageView img;
    ListView subjectList;
    ArrayList<subjectObj> list = new ArrayList<>();
    ArrayList<String> listSub = new ArrayList<>();
    FirebaseAuth fAuth;
    Uri imageData;
    String imgURL;
    private DatabaseReference mDatabase;
    private static final int GALLERY_REQUEST_COD = 1;
    boolean del = false;
    // List of mountains that the teacher tutor
    ArrayList<String> listCities = new ArrayList<>();
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    String teacherID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_tutor_profile);

        /* DISABLE landscape orientation  */
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        fAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //delImage = findViewById(R.id.deleteImage);
        PhoneNumber = findViewById(R.id.editPhoneNumber);
        TextInputLayout descriptionInputLayout = findViewById(R.id.editDescription);
        description = descriptionInputLayout.getEditText();
        addSub = findViewById(R.id.addSubject);
        profile = findViewById(R.id.btnSaveProfile);
        addImage = findViewById(R.id.btnUpdateImage);
        img = findViewById(R.id.imageView);
        citySpinner = findViewById(R.id.txtCities);
        subjectList = (ListView) findViewById(R.id.subList);
        ArrayAdapter a = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        subjectList.setAdapter(a);
        a.notifyDataSetChanged();


        String[] cities = getResources().getStringArray(R.array.Cities);
        boolean[] selectCities = new boolean[cities.length];
        ArrayList<Integer> listCitiesNum = new ArrayList<>();
        setSpinnerCity(citySpinner, selectCities, listCitiesNum, cities);


//        PhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) PhoneNumber.setHint("");
//                else PhoneNumber.setHint("Enter Phone Number");
//            }
//        });


        PhoneNumber.setHint("Enter Phone Number");
        PhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) PhoneNumber.setHint("");
                else PhoneNumber.setHint("Enter Phone Number");
            }
        });

//        /* Select City Spinner Code () */
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item) {
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                View v = super.getView(position, convertView, parent);
//                if (position == 0) { // Hint
//                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
//                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(0)); }
//                return v; }
//            @Override
//            public int getCount() { return super.getCount(); }
//            @Override /* Disable selection of the Hint (first selection) */
//            public boolean isEnabled(int position) { return ((position == 0) ? false : true); }
//            @Override /* Set the color of the Hint (first selection) to Grey */
//            public View getDropDownView(int position, View convertView, ViewGroup parent) {
//                View view = super.getDropDownView(position, convertView, parent);
//                TextView tv = (TextView)view;
//                if (position == 0) tv.setTextColor(Color.GRAY); else tv.setTextColor(Color.BLACK);
//                return view; }
//        };
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        String[] cities = getResources().getStringArray(R.array.Cities);
//        adapter.add("Choose City");
//        adapter.addAll(cities);
//        citySpinner.setAdapter(adapter);
//        citySpinner.setSelection(0); //display hint
//        /* END Select City Spinner Code () */

//        delImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                    StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(imgURL);
//                    photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            // File deleted successfully
//                            Log.d("Picture", "onSuccess: deleted file");
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception exception) {
//                            // Uh-oh, an error occurred!
//                            Log.d("Picture", "onFailure: did not delete file");
//                        }
//                    });
//                }
//        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (del == true) {
                    final Dialog d = new Dialog(SetTutorProfile.this);
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
                            del = true;
                            d.dismiss();
                        }
                    });

                    deleteImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            img.setImageDrawable(null);
                            img.setBackgroundResource(R.mipmap.ic_launcher_round);
                            del = false;
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
                    del = true;
                }
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
                String userID = fAuth.getCurrentUser().getUid();
                String pNum = PhoneNumber.getText().toString().trim();
                String descrip = description.getText().toString().trim();

                if (TextUtils.isEmpty(pNum)) {
                    PhoneNumber.setError("PhoneNumber is required.");
                    return;
                }

                if (pNum.charAt(0) != '0' || pNum.charAt(1) != '5' || pNum.length() != 10) {
                    System.out.println(pNum);
                    PhoneNumber.setError("Invalid phoneNumber.");
                    return;
                }
                if (list.isEmpty()) {
                    Toast.makeText(SetTutorProfile.this, "You must choose at least one subject",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                int count = 0;
                boolean flage = false;
                for (subjectObj sub : list) {
                    count++;
                    if(sub.getType().equals("frontal")  ||  sub.getType().equals("both"))
                        flage = true;
                    if (listCities.isEmpty() && flage) {
                        Toast.makeText(SetTutorProfile.this, "You must choose at least one service city",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(listCities.size() != 0 && count == list.size() && flage == false){
                        Toast.makeText(SetTutorProfile.this, "You have chosen to transfer" +
                                        " private lessons only online, do not select service cities",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                FireBaseTeacher t = new FireBaseTeacher();
                /* set isTeacher to teacher status (1=teacher,0=customer) */
                mDatabase.child("users").child(userID).child("isTeacher").setValue(1);
                /* add the teacher to database */
                /* img=null because there is no need to store url before the image was successfully uploaded */
                String teacherID = t.addTeacherToDB(pNum, descrip, userID, listCities, list, null); // imgURL
                /* upload the image and ON SUCCESS store url on the teacher database */
                /* if no image to upload */
                if (imageData==null) {
                    goToTutorMain();
                } else {
                    uploadImageAndGoToMain(teacherID);
                }
            }
        });
    }

    /* Creating a dialogue for choosing cities where the teacher tutor */
    private void setSpinnerCity(TextView citySpinner, boolean[] selectCities,
                                ArrayList<Integer> listCitiesNum, String[] cities) {
        ArrayList<String> addTempCities = new ArrayList<>();
        ArrayList<String> removeTempCities = new ArrayList<>();
        citySpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SetTutorProfile.this);
                builder.setTitle("Select service cities");
                builder.setCancelable(false);
                builder.setMultiChoiceItems(cities, selectCities, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            listCitiesNum.add(which);
                            listCities.add(cities[which]);
                            // addTempCities.add(cities[which]);
                            Collections.sort(listCitiesNum);
                        } else {
                            listCitiesNum.remove((Integer) which);
                            listCities.remove(cities[which]);
                            //removeTempCities.add(cities[which]);
                        }
                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(listCities.isEmpty()){
                            citySpinner.setTextColor(Color.GRAY);
                            citySpinner.setText("Select service cities");
                            //removeServiceCities(removeTempCities);
                        }
                        else{
                            citySpinner.setTextColor(Color.BLACK);
                            Collections.sort(listCities);
                            citySpinner.setText(printList(listCities));
                            //addServiceCities(addTempCities);
                            //removeServiceCities(removeTempCities);
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < selectCities.length; i++)
                            selectCities[i] = false;
//                        for(String rCity : listCities)
//                            removeTempCities.add(rCity);
                        listCitiesNum.clear();
                        listCities.clear();
                        citySpinner.setTextColor(Color.GRAY);
                        citySpinner.setText("Select service cities");
                        //removeServiceCities(removeTempCities);
                    }
                });
                builder.show();
            }
        });
    }

    private void goToTutorMain() {
        /* were logging in as tutor (tutor status value = 1).
         * therefore, pass 'Status' value (1) to MainActivity. */
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

    public void createDialog(ArrayAdapter a) {
        final Dialog d = new Dialog(this);
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
                (this, android.R.layout.simple_spinner_item, subjectObj.Prices.values()));
        nameSpinner.setAdapter(new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item, subjectObj.SubName.values()));
        typeSpinner = d.findViewById(R.id.spinnerLType);
        typeSpinner.setAdapter(new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item, subjectObj.Type.values()));
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String price = priceEdit.getText().toString().trim();
                String price = priceEdit.getSelectedItem().toString().trim();
                String exp = expEdit.getText().toString().trim();
                String nameSub = nameSpinner.getSelectedItem().toString().trim();
                String type = typeSpinner.getSelectedItem().toString().trim();
                if (price == "Select Price") {
                    Toast.makeText(SetTutorProfile.this, "Please select a price.", Toast.LENGTH_SHORT).show();
                    return; }
                if (listSub.contains(nameSub)) {
                    Toast.makeText(SetTutorProfile.this, "You already have selected this subject.", Toast.LENGTH_SHORT).show();
                    return; }
                if (nameSub == "Select subject") {
                    Toast.makeText(SetTutorProfile.this, "Subject is required.", Toast.LENGTH_SHORT).show();
                    return; }
                if (type == "Select learning type") {
                    Toast.makeText(SetTutorProfile.this, "Learning type is required.", Toast.LENGTH_SHORT).show();
                    return; }
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
                (this, android.R.layout.simple_spinner_item, subjectObj.Prices.values()));

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
        //priceEdit.setText(Integer.toString((currSub.getPrice())));
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
                    Toast.makeText(SetTutorProfile.this, "Please select a price.", Toast.LENGTH_SHORT).show();
                    return; }
                if (nameSub.isEmpty() || nameSub.equals(subjectObj.SubName.HINT)) {
                    Toast.makeText(SetTutorProfile.this, "Subject is required.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (type.isEmpty() || type.equals(subjectObj.Type.HINT)) {
                    Toast.makeText(SetTutorProfile.this, "Learning type is required.", Toast.LENGTH_SHORT).show();
                    return;
                }
                /* if the subject already exists BUT IN THE ENTRY THAT CURRENTLY EDITING - IT'S OK!  */
                if (listSub.contains(nameSub) && !nameSub.equals(currSub.getsName())) {
                    Toast.makeText(SetTutorProfile.this, "You already have selected this subject.", Toast.LENGTH_SHORT).show();
                    return; }
                subjectObj s = new subjectObj(nameSub, type, Integer.parseInt(price), exp);
                /* remove the last entry (before the edit) */
                list.remove(currSub);
                /*  */
                list.add(s);
                listSub.add(nameSub);
                a.notifyDataSetChanged();
                addNotification(fAuth.getCurrentUser().getUid());
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
        if (requestCode == GALLERY_REQUEST_COD && resultCode == Activity.RESULT_OK && data != null) {
            try {
                imageData = data.getData();
                /* crop the image bmp to square */
                InputStream imageStream = getContentResolver().openInputStream(imageData);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                selectedImage = getResizedBitmap(selectedImage, 200);// 400 is for example, replace with desired size
                /* show the new image on screen */
                img.setImageBitmap(selectedImage);


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


                /* convert the new bmp to Uri & assign the new Uri to 'imageData' */
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), selectedImage, "IMG_" + Calendar.getInstance().getTime(), null);
                if (path!=null) imageData = Uri.parse(path);
                else {
                    imageData = null;
                    Toast.makeText(getApplicationContext(), "Upload image Failed", Toast.LENGTH_LONG).show();
                }
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

    private String getExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void uploadImageAndGoToMain(String teacherID) {
//        /* if no image to upload */
//        if (imageData==null) {
//            goToTutorMain();
//            return;
//        }
//        /* else - upload the image and go to main */
        imgURL = System.currentTimeMillis()+"."+getExtension(imageData);
        StorageReference Ref= FirebaseStorage.getInstance().getReference().child(imgURL);
        Ref.putFile(imageData)
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override // onProgress show loading screen
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        /* loading screen section (showing loading screen until data received from FireBase) */
                        Intent intent = new Intent(SetTutorProfile.this, LoadingScreen.class);
                        /* prevent going back to this loading screen (from the next screen) */
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        finishAffinity();
                        startActivity(intent);
                        /* END loading screen section */
                        finish();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override // on success set image URL on tutor database & go to main
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        /* Set the image URL AFTER After the image has been successfully uploaded */
                        mDatabase.child("teachers").child(teacherID).child("imgUrl").setValue(imgURL);
                        /* remove the cropped image from gallery */
                        if (imageData!=null) getApplicationContext().getContentResolver().delete(imageData, null, null);
                        goToTutorMain();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), "Upload imageFailed", Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void addNotification(String userID) {
        HashMap<String, Object> map = new HashMap<>();
        String key = FirebaseDatabase.getInstance().getReference().child("notifications").child(userID).push().getKey();
        map.put("TeacherEmail","");
        map.put("TeacherName","");
        map.put("UserEmail","");
        map.put("Subject","");
        map.put("FormOfLearning","");
        map.put("Remarks","Congratulations! You are a teacher!");
        map.put("RequestStatus","");
        map.put("PhoneNumber","");
        map.put("sendTo","");
        map.put("sentFrom",fAuth.getCurrentUser().getUid());
        map.put("NotificationKey",key);
        if (key != null)
            FirebaseDatabase.getInstance().getReference().child("notifications").child(userID).child(key).setValue(map);
    }

    // Print the List of cities that the teacher tutor
    private String printList (ArrayList < String > list) {
        String s =list.toString();
        s = s.replace("[","");
        s = s.replace("]","");
        return s;
    }

//    /* Delete cities and subjects frome firebase in the tree search */
//    public ArrayList<String> removeServiceCities(ArrayList < String > removeList) {
//        Collections.sort(removeList);
//        Map<String, Object> childUpdates = new HashMap<>();
//        new FireBaseUser().getUserRef().addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                teacherID = dataSnapshot.child("teacherID").getValue(String.class);
//                for(subjectObj sList : list) {
//                    for (String rCity : removeList) {
//                        if(sList.getType().equals("frontal") || sList.getType().equals("both")) {
//                            childUpdates.put("search/" + sList.getType() + "/" + sList.getsName()
//                                    + "/" + rCity + "/" + sList.getPrice() + "/teacherID", null);
//                        }
//                    }
//                }
//                myRef.updateChildren(childUpdates);
//                removeList.clear();
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
//        return removeList;
//    }
//
//    /* Add cities and subjects to firebase in the tree search */
//    public ArrayList<String> addServiceCities(ArrayList < String > addList) {
//        Collections.sort(addList);
//        Map<String, Object> childUpdates = new HashMap<>();
//        new FireBaseUser().getUserRef().addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                teacherID = dataSnapshot.child("teacherID").getValue(String.class);
//                for(subjectObj sList : list) {
//                    for (String aCity : addList) {
//                        if(sList.getType().equals("frontal") || sList.getType().equals("both")) {
//                            childUpdates.put("search/" + sList.getType() + "/" + sList.getsName()
//                                    + "/" + aCity + "/" + sList.getPrice() + "/teacherID", teacherID);
//                        }
//                    }
//                }
//                myRef.updateChildren(childUpdates);
//                addList.clear();
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
//        return addList;
//    }
}