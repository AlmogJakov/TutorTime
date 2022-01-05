package com.project.tutortime.Model.firebase;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.tutortime.Controller.tutorprofile.TutorProfile;
import com.project.tutortime.Controller.tutorsublist.TutorSubList;
import com.project.tutortime.R;
import com.project.tutortime.View.LoadingScreen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FireBaseTutor extends FirebaseManager {
    FireBaseUser u = new FireBaseUser();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();

    public String addTeacherToDB(String phoneNum, String description, List<String> serviceCities,
                                 List<subjectObj> sub, String imgUrl, rankObj rank){
        String userid = fAuth.getCurrentUser().getUid();
        //writeNewTeacher(phoneNum, description, userid, sub,  imgUrl);
        tutorObj teacher = new tutorObj(phoneNum, description, userid, serviceCities, imgUrl, rank);
        String teacherId = myRef.push().getKey();
        /* set user 'teacherID' variable */
        u.getUserRef().child("teacherID").setValue(teacherId);
        /* set the teacher object */
        myRef.child("teachers").child(teacherId).setValue(teacher);
        /* ADD ALL TEACHER SUBJECTS IN ONE COMMAND */
        new FireBaseUser().getUserRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /* Make a list of all the RealTime DataBase commands to execute
                 * (for the purpose of executing all the commands at once) */
                Map<String, Object> childUpdates = new HashMap<>();
                /* (add a command) add the subject to the Search Tree */
                for(subjectObj sList : sub) {
                    for (String aCity : serviceCities) {
                        if(sList.getType().equals("frontal") || sList.getType().equals("both")) {
                            childUpdates.put("search/" + sList.getType() + "/" + sList.getsName()
                                    + "/" + aCity + "/" + sList.getPrice() + "/"+teacherId, teacherId);
                        }
                    }
                    if(sList.getType().equals("online")){
                        childUpdates.put("search/" + sList.getType() + "/" + sList.getsName()
                                + "/" + sList.getPrice() + "/"+teacherId, teacherId);
                    }
                    /* (add a command) add the subject to the current teacher object */
                    childUpdates.put("teachers/" + teacherId + "/sub/" + sList.getsName(), sList);
                }
//                for (int i = 0; i < sub.size(); i++) {
//                    /* (add a command) add the subject to the Search Tree */
//                    childUpdates.put("search/" + sub.get(i).getType() + "/" + sub.get(i).getsName() +
//                            "/" + City + "/" + sub.get(i).getPrice() + "/" + teacherId, teacherId);
//                    /* (add a command) add the subject to the current teacher object */
//                    childUpdates.put("teachers/" + teacherId + "/sub/" + sub.get(i).getsName(), sub.get(i));
//                }
                /* Finally, execute all RealTime DataBase commands in one command (safely). */
                myRef.updateChildren(childUpdates);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        return teacherId;
    }

    public void setServiceCities(String teacherId, ArrayList<subjectObj> serviceCities){
        myRef.child("teachers").child(teacherId).child("serviceCities").setValue(serviceCities);
    }

    public void setSubList(String teacherId, ArrayList<subjectObj> listSub){
        myRef.child("teachers").child(teacherId).child("sub").setValue(listSub);
    }

    public void setPhoneNum(String teacherId, String pNum){
        myRef.child("teachers").child(teacherId).child("phoneNum").setValue(pNum);
    }

    public void setDescription(String teacherId, String des){
        myRef.child("teachers").child(teacherId).child("description").setValue(des);
    }

    public void setImgUrl(String teacherId, String imgUrl){
        myRef.child("teachers").child(teacherId).child("imgUrl").setValue(imgUrl);
    }



    public void updateTutorDetails(Activity context, Boolean delImg, final String imgURL, final String pNum, final String descrip){
        System.out.println( "img="+imgURL);
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
      myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                /* get teacher ID */
                String teacherID = dataSnapshot.child("users").child(userID).child("teacherID").getValue(String.class);
                        /* Make a list of all the RealTime DataBase commands to execute
                            (for the purpose of executing all the commands at once) */
                Map<String, Object> childUpdates = new HashMap<>();
                if (imgURL != null && !delImg)
                    childUpdates.put("teachers/" + teacherID + "/imgUrl", imgURL);
                childUpdates.put("teachers/" + teacherID + "/phoneNum", pNum);
                childUpdates.put("teachers/" + teacherID + "/description", descrip);
                        /* If the user deleted the image - delete it from the storage and add
                            a delete command to childUpdates (to delete it URL from the RealTime DataBase) */
                if (delImg) {
                    if (imgURL != null)
                        childUpdates.put("teachers/" + teacherID + "/imgUrl", null);

                    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                    StorageReference storageReference = firebaseStorage.getReference(imgURL);
                    storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e("Picture", "#deleted");
                        }
                    });
                }
                /* Finally, execute all RealTime DataBase commands in one command (safely). */
                myRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("Profile", "#updated");
                        TutorProfile.goToTutorMain(context);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

    }
    public void DeleteTutorProfile(Activity context,ArrayList <String> listCities, ArrayList <subjectObj> listSub) {
        Map<String, Object> childUpdates = new HashMap<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String teacherID = dataSnapshot.child("users").child(userID).child("teacherID").getValue(String.class);
                /* Delete subjects from the search tree*/
                for(subjectObj sList : listSub) {
                    for (String rCity : listCities) {
                        if(sList.getType().equals("frontal") || sList.getType().equals("both")) {
                            childUpdates.put("search/" + sList.getType() + "/" + sList.getsName()
                                    + "/" + rCity + "/" + sList.getPrice() + "/" + teacherID, null);
                        }
                    }
                    if(sList.getType().equals("online")){
                        childUpdates.put("search/" + sList.getType() + "/" + sList.getsName()
                                + "/" + sList.getPrice() + "/" + teacherID, null);
                    }
                }
                childUpdates.put("teachers/" + teacherID , null);
                childUpdates.put("users/" + userID + "/teacherID/", null);
                childUpdates.put("users/" + userID + "/isTeacher/", 0);
                /* Delete all chats where the teacher is appeared */
                for (DataSnapshot userChat : dataSnapshot.child("chats").child(userID).getChildren()) {
                    if (userChat.child("teacherID").getValue(String.class).equals(userID)) {
                        childUpdates.put("chats/" + userID + "/" + userChat.getKey(), null);
                        childUpdates.put("chats/" + userChat.child("studentID").getValue(String.class)
                                + "/" + userChat.getKey(), null);
                        childUpdates.put("messages/" + userChat.getKey(), null);
                    }
                }

                /* Delete all notifications related to the teacher */
                for (DataSnapshot userNotifications : dataSnapshot.child("notifications").
                        child(userID).getChildren()) {
                    String title = userNotifications.child("title").getValue(String.class);
                    if (title.equals("rating received!") || title.equals("Congratulations!")) {
                        System.out.println(userNotifications.getKey());
                        childUpdates.put("notifications/" + userID + "/" + userNotifications.getKey(), null);

                    }
                }
                myRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("Profile", "#deleted");
                        TutorProfile.goToUserMain(context);
                    }
                });

            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

    }

    /* Subject Update - update a subject according to the previous subject and a new subject.
     * Subject Addition - addition of a new subject ('prevSub'=null when calling the method)
     * Subject Deletion - deletion of an old subject ('newSub'=null when calling the method) */
    public void updateList(subjectObj prevSub,subjectObj newSub, ArrayList<String> listCities) {
        /* Make a list of all the RealTime DataBase commands to execute
         * (for the purpose of executing all the commands at once) */
        Map<String, Object> childUpdates = new HashMap<>();
       myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String teacherID = dataSnapshot.child("users").child(userID).child("teacherID").getValue(String.class);
                if (prevSub!=null) {
                    /* (add a command) delete the subject from the Search Tree */
                    if(prevSub.getType().equals("online")) {
                        childUpdates.put("search/" + prevSub.getType() + "/" + prevSub.getsName()
                                + "/" + prevSub.getPrice() + "/" + teacherID, null);
                    }
                    else{
                        for (String sCity : listCities) {
                            childUpdates.put("search/" + prevSub.getType() + "/" + prevSub.getsName()
                                    + "/" + sCity + "/" + prevSub.getPrice() + "/" + teacherID, null);
                        }
                    }
                    /* (add a command) delete the subject from the current teacher object */
                    childUpdates.put("teachers/" + teacherID + "/sub/" + prevSub.getsName(), null);
                }
                if (newSub!=null) {
                    /* (add a command) add the subject to the Search Tree */
                    if(newSub.getType().equals("online")){
                        childUpdates.put("search/" + newSub.getType() + "/" + newSub.getsName()
                                + "/" + newSub.getPrice() + "/" + teacherID, teacherID);
                    }
                    else{
                        for(String sCity : listCities) {
                            childUpdates.put("search/" + newSub.getType() + "/" + newSub.getsName()
                                    + "/" + sCity + "/" + newSub.getPrice() + "/" + teacherID, teacherID);
                        }
                    }

                    /* (add a command) add the subject to the current teacher object */
                    childUpdates.put("teachers/" + teacherID + "/sub/" + newSub.getsName(), newSub);
                }
                /* Finally, execute all RealTime DataBase commands in one command (safely). */
                myRef.updateChildren(childUpdates);





            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /* Delete cities and subjects from firebase in the tree search */
    public ArrayList<String> removeServiceCities(ArrayList < String > removeList, ArrayList < String > currentList,ArrayList <subjectObj > list) {
        Collections.sort(removeList);
        Map<String, Object> childUpdates = new HashMap<>();
       myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String teacherID = dataSnapshot.child("users").child(userID).child("teacherID").getValue(String.class);
                for(subjectObj sList : list) {
                    for (String rCity : removeList) {
                        if(sList.getType().equals("frontal") || sList.getType().equals("both")) {
                            childUpdates.put("search/" + sList.getType() + "/" + sList.getsName()
                                    + "/" + rCity + "/" + sList.getPrice() + "/" + teacherID, null);
                        }
                    }
                }
                childUpdates.put("teachers/" + teacherID + "/serviceCities/", currentList);
                myRef.updateChildren(childUpdates);
                removeList.clear();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return removeList;
    }

    /* Add cities and subjects to firebase in the tree search */
    public ArrayList<String> addServiceCities(ArrayList < String > addList, ArrayList < String > currentList,ArrayList < subjectObj > list) {
        Collections.sort(addList);
        Map<String, Object> childUpdates = new HashMap<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String teacherID = dataSnapshot.child("users").child(userID).child("teacherID").getValue(String.class);
                for(subjectObj sList : list) {
                    for (String aCity : addList) {
                        if(sList.getType().equals("frontal") || sList.getType().equals("both")) {
                            childUpdates.put("search/" + sList.getType() + "/" + sList.getsName()
                                    + "/" + aCity + "/" + sList.getPrice() + "/" + teacherID, teacherID);
                        }
                    }
                }

                childUpdates.put("teachers/" + teacherID + "/serviceCities/", currentList);
                myRef.updateChildren(childUpdates);
                addList.clear();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return addList;
    }



}