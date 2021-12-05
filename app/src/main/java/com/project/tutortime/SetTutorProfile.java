package com.project.tutortime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
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
import com.project.tutortime.firebase.subjectObj;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class SetTutorProfile extends AppCompatActivity {
    TextView delImage;
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
        //citySpinner = findViewById(R.id.spinnerCity);
        subjectList = (ListView) findViewById(R.id.subList);
        ArrayAdapter a = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        subjectList.setAdapter(a);
        a.notifyDataSetChanged();


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
                String userID = fAuth.getCurrentUser().getUid();
                String pNum = PhoneNumber.getText().toString().trim();
                String descrip = description.getText().toString().trim();
                if (TextUtils.isEmpty(pNum)) {
                    PhoneNumber.setError("PhoneNumber is required.");
                    return;
                }

                if (pNum.length() != 10 && pNum.charAt(0) != 0 &&  pNum.charAt(1) != 5) {
                    PhoneNumber.setError("Invalid phoneNumber.");
                    return;
                }
//                if (citySpinner.getSelectedItemPosition()==0) {
//                    TextView errorText = (TextView)citySpinner.getSelectedView();
//                    errorText.setError("City is required.");
//                    Toast.makeText(SetTutorProfile.this, "City is required.",
//                            Toast.LENGTH_SHORT).show();
//                    return;
//                }
                if (list.isEmpty()) {
                    Toast.makeText(SetTutorProfile.this, "You must choose at least one subject",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                FireBaseTeacher t = new FireBaseTeacher();
                /* set isTeacher to teacher status (1=teacher,0=customer) */
                mDatabase.child("users").child(userID).child("isTeacher").setValue(1);
                /* add the teacher to database */
                /* img=null because there is no need to store url before the image was successfully uploaded */
                String teacherID = t.addTeacherToDB(pNum, descrip, userID, list, null); // imgURL
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
                String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), selectedImage, "Title", null);
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
            } catch (IOException e) {
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
}