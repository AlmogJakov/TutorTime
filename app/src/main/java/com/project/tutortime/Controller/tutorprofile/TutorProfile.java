package com.project.tutortime.Controller.tutorprofile;

import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.tutortime.Model.firebase.FireBaseTutor;
import com.project.tutortime.Model.firebase.tutorObj;
import com.project.tutortime.Model.firebase.userObj;
import com.project.tutortime.View.LoadingDialog;
import com.project.tutortime.View.LoadingScreen;
import com.project.tutortime.MainActivity;
import com.project.tutortime.R;
import com.project.tutortime.databinding.FragmentTutorProfileBinding;
import com.project.tutortime.Model.firebase.FireBaseUser;
import com.project.tutortime.Model.firebase.subjectObj;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TutorProfile extends Fragment {

    private TutorProfileViewModel TutorProfileViewModel;
    private FragmentTutorProfileBinding binding;
    EditText fname, lname, pnumber, description;
    //Button addSub;
    Button saveProfile, updateImage, deleteProfile;
    String teacherID;
    Spinner citySpinner;
    ImageView img;
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    Uri imageData;
    String imgURL;
    boolean del = false;
    LoadingDialog loadingDialog;
    ArrayList<subjectObj> list = new ArrayList<>();
    List<String> listSub = new ArrayList<>();
    ArrayList<String> listCities = new ArrayList<>();
    FireBaseUser fbUser = new FireBaseUser();
    FireBaseTutor fbTutor = new FireBaseTutor();

    private static final int GALLERY_REQUEST_COD = 1;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        TutorProfileViewModel = new ViewModelProvider(this).get(TutorProfileViewModel.class);
        binding = FragmentTutorProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        fname = binding.myFName;
        lname = binding.myLName;
        pnumber = binding.myPhoneNumber;
        description = binding.editDescription.getEditText();
        saveProfile = binding.btnSaveProfile;
        updateImage = binding.btnUpdateImage;
        img = binding.imageView;
        citySpinner = binding.spinnerCity;
        deleteProfile = binding.btnDelete;

        /* Disable all Buttons & Show loading dialog (until all fragment resources ready) */
        updateImage.setVisibility(View.GONE);
        loadingDialog = new LoadingDialog(getContext());
        loadingDialog.show();
        /* END Disable all Buttons & Show loading dialog */

        /* Select City Spinner Code () */
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (position == 0) { // Hint
                    ((TextView) v.findViewById(android.R.id.text1)).setText("");
                    ((TextView) v.findViewById(android.R.id.text1)).setHint(getItem(0));
                }
                return v;
            }

            @Override
            public int getCount() {
                return super.getCount();
            }

            @Override /* Disable selection of the Hint (first selection) */
            public boolean isEnabled(int position) {
                return (position != 0);
            }

            @Override /* Set the color of the Hint (first selection) to Grey */
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) tv.setTextColor(Color.GRAY);
                else tv.setTextColor(Color.BLACK);
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        String[] cities = getResources().getStringArray(R.array.Cities);
        adapter.add("Choose City");
        adapter.addAll(cities);
        citySpinner.setAdapter(adapter);
        citySpinner.setSelection(0); //display hint
        /* END Select City Spinner Code () */


        updateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgURL != null && !del) {
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
                            img.setBackgroundResource(R.mipmap.ic_android_round);
                            del = true;
                            d.dismiss();
                        }
                    });
                    d.show();
                } else {
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
                if (pNum.charAt(0) != '0' || pNum.charAt(1) != '5' || pNum.length() != 10) {
                    pnumber.setError("Invalid phoneNumber.");
                    return; }
                if (citySpinner.getSelectedItemPosition() == 0) {
                    TextView errorText = (TextView) citySpinner.getSelectedView();
                    errorText.setError("City is required.");
                    Toast.makeText(getActivity(), "City is required.",
                            Toast.LENGTH_SHORT).show();
                    return; }
                fbTutor.updateTutorDetails(requireActivity(),del, imgURL, pNum, descrip);
                fbUser.updateUserDetails(firstName,lastName,city,"");

                if (imageData !=  null) {
                    uploadImageAndGoToMain(teacherID); }
            }
        });

        deleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete tutor profile")
                        .setMessage("Are you sure you want to delete your tutor profile?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                dialog.cancel();
                                fbTutor.DeleteTutorProfile(getActivity(),listCities,list);
//                                goToTutorMain();
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }

        });

        return root;
    }


    /* get fragment activity (to do actions on the activity) */
    public static void goToTutorMain(Activity currentActivity) {
        //Activity currentActivity = getContext();
        /* were logging in as tutor (tutor status value = 1).
         * therefore, pass 'Status' value (1) to MainActivity. */
        final ArrayList<Integer> arr = new ArrayList<Integer>();
        arr.add(1);
        //Intent intent = new Intent(SetTutorProfile.this, MainActivity.class);
        Intent intent = new Intent(currentActivity, MainActivity.class);
        /* disable returning to SetTutorProfile class after opening main
         * activity, since we don't want the user to re-choose Profile
         * -> because the tutor profile data still exists with no use!
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
                userObj user = dataSnapshot.child("users").child(userID).getValue(userObj.class);
                fname.setText(user.getfName());
                lname.setText(user.getlName());
                teacherID = user.getTeacherID();
                String currCity =user.getCity();
                String[] cities = getResources().getStringArray(R.array.Cities);
                for (int i = 0; i < cities.length; i++) {
                    if (citySpinner.getItemAtPosition(i).equals(currCity)) {
                        citySpinner.setSelection(i);
                        break;
                    }
                }
                tutorObj tutor = dataSnapshot.child("teachers").child(teacherID).getValue(tutorObj.class);
                pnumber.setText(tutor.getPhoneNum());
                description.setText(tutor.getDescription());

                imgURL = tutor.getImgUrl();

                if (imgURL != null) { /* The image exists! */
                    StorageReference storageReference = storage.getReference().child(imgURL);
                    Glide.with(getContext()).load(storageReference).into(img);

                }
                /* Enable Buttons & Hide loading dialog - data already received from FireBase */
                updateImage.setVisibility(View.VISIBLE);
                loadingDialog.cancel();
                /* END Enable Buttons & Hide loading dialog */


                /* Get my service cities from firebase */
                listCities = new ArrayList<>(tutor.getServiceCities());


                /* Get my subject list from firebase */

                for (Map.Entry<String,subjectObj> sub : tutor.getSub().entrySet()) {
                    subjectObj s = sub.getValue();
                    s.setKey(s.getsName());
                    list.add(sub.getValue());
                    listSub.add(sub.getKey());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "onCreate error. " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        loadingDialog.dismiss();
        super.onDestroyView();

        super.onDestroy();
        binding = null;
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
                        fbTutor.setImgUrl(teacherID,imgURL);
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

    public static void goToUserMain(Activity currentActivity) {
        /* were logging in as tutor (tutor status value = 1).
         * therefore, pass 'Status' value (1) to MainActivity. */
        final ArrayList<Integer> arr = new ArrayList<Integer>();
        arr.add(0);
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
}