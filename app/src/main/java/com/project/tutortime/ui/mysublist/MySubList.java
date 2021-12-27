package com.project.tutortime.ui.mysublist;

import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project.tutortime.MainActivity;
import com.project.tutortime.R;
import com.project.tutortime.databinding.FragmentMySubListBinding;
import com.project.tutortime.firebase.FireBaseUser;
import com.project.tutortime.firebase.subjectObj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class  MySubList extends Fragment {

    //private MySubListViewModel mViewModel;
    private MySubListViewModel MySubListViewModel;
    private FragmentMySubListBinding binding;

    TextView serviceCitiesSpinner;
    ListView subjectList;
    Button addSub;
    String teacherID;
    ArrayList<subjectObj> list = new ArrayList<>();
    ArrayList<String> listSub = new ArrayList<>();
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    ArrayList<String> listCities = new ArrayList<>();
    String[] arrCities ;
    boolean[] selectCities ;
    ArrayList<Integer> listCitiesNum = new ArrayList<>();

    public static MySubList newInstance() {
        return new MySubList();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        MySubListViewModel = new ViewModelProvider(this).get(MySubListViewModel.class);
        binding = FragmentMySubListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        subjectList = binding.ListViewSubList;
        addSub = binding.btnAddSubject;
        serviceCitiesSpinner = binding.txtServiceCities;

        ArrayAdapter a = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, list);
        subjectList.setAdapter(a);
        a.notifyDataSetChanged();

        /* Disable all Buttons & Text Edit Fields - until all data received from FireBase */
        addSub.setEnabled(false);
        serviceCitiesSpinner.setEnabled(false);
        subjectList.setEnabled(false);
        /* END Disable all Buttons & Text Edit Fields */

        arrCities = getResources().getStringArray(R.array.Cities);
        selectCities = new boolean[arrCities.length];

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

        return root;
        //return inflater.inflate(R.layout.fragment_my_sub_list, container, false);
    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        MySubListViewModel = new ViewModelProvider(this).get(MySubListViewModel.class);
//        // TODO: Use the ViewModel
//    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                teacherID = dataSnapshot.child("users").child(userID).child("teacherID").getValue(String.class);

                for (DataSnapshot citySnapsot : dataSnapshot.child("teachers").child(teacherID).
                        child("serviceCities").getChildren()) {
                    String serviceCity = citySnapsot.getValue(String.class);
                    listCities.add(serviceCity);
                }
                if (listCities.isEmpty()){
                    serviceCitiesSpinner.setTextColor(Color.GRAY);
                    serviceCitiesSpinner.setText("Select service cities");
                }
                else {
                    serviceCitiesSpinner.setTextColor(Color.BLACK);
                    serviceCitiesSpinner.setText(printList(listCities));
                }

                /* Variable for teacher tutor cities */

                for( int i = 0 ; i < arrCities.length ; i++){
                    if (listCities.contains(arrCities[i])){
                        selectCities[i] = true;
                    }
                }
                setCitySpinner(serviceCitiesSpinner, selectCities, listCitiesNum, arrCities);

                for (DataSnapshot subSnapsot : dataSnapshot.child("teachers").child(teacherID).
                        child("sub").getChildren()) {
                    subjectObj sub = new subjectObj(subSnapsot.child("sName").getValue(String.class),
                            subSnapsot.child("type").getValue(String.class),
                            subSnapsot.child("price").getValue(Integer.class),
                            subSnapsot.child("experience").getValue(String.class));
                    sub.setKey(subSnapsot.getKey());
                    list.add(sub);
                    listSub.add(sub.getsName());
                }
                ArrayAdapter a = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, list);
                subjectList.setAdapter(a);
                a.notifyDataSetChanged();

                /* Enable all Buttons & Text Edit Fields - data already received from FireBase */
                addSub.setEnabled(true);
                subjectList.setEnabled(true);
                serviceCitiesSpinner.setEnabled(true);
                /* END Enable all Buttons & Text Edit Fields */
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "onCreate error. " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
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

    public void createDialog(ArrayAdapter a) {
        final Dialog d = new Dialog(getActivity());
        Spinner priceEdit;
        EditText expEdit;
        Button addBtn, closeBtn;
        Spinner nameSpinner, typeSpinner;
        d.setContentView(R.layout.subject_add_dialog);
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
                if (price.equals("Select Price")) {
                    //priceEdit.setError("Price is required.");
                    Toast.makeText(getActivity(), "Please select a price.", Toast.LENGTH_SHORT).show();
                    return; }
                if (listSub.contains(nameSub)) {
                    Toast.makeText(getActivity(), "You already have selected this subject.", Toast.LENGTH_SHORT).show();
                    return; }
                if (nameSub.equals("Select subject")) {
                    Toast.makeText(getActivity(), "Subject is required.", Toast.LENGTH_SHORT).show();
                    return; }
                if (type.equals("Select learning type")) {
                    Toast.makeText(getActivity(), "Learning type is required.", Toast.LENGTH_SHORT).show();
                    return; }
                if ((type.equals("frontal") || type.equals("both")) && listCities.isEmpty()) {

                    new AlertDialog.Builder(getContext())
                            .setTitle("Select Service City")
                            .setMessage("You must choose at least one service city!")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    buildCityDialog(true);
                                    subjectObj newSub = new subjectObj(nameSub, type, Integer.parseInt(price), exp);
                                    list.add(newSub);
                                    updateList(null,newSub,listCities);
                                    listSub.add(nameSub);
                                    d.dismiss();
                                    return;
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    d.dismiss();
                    return;
                }
                subjectObj newSub = new subjectObj(nameSub, type, Integer.parseInt(price), exp);
                list.add(newSub);
                updateList(null,newSub,listCities);
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
                if (price.equals("Select Price")) {
                    //priceEdit.setError("Price is required.");
                    Toast.makeText(getActivity(), "Please select a price.", Toast.LENGTH_SHORT).show();
                    return; }
                if (nameSub.equals(subjectObj.SubName.HINT)) {
                    Toast.makeText(getActivity(), "Subject is required.", Toast.LENGTH_SHORT).show();
                    return; }
                if (type.equals(subjectObj.Type.HINT)) {
                    Toast.makeText(getActivity(), "Learning type is required.", Toast.LENGTH_SHORT).show();
                    return; }
                /* if the subject already exists BUT IN THE ENTRY THAT CURRENTLY EDITING - IT'S OK!  */
                if (listSub.contains(nameSub) && !nameSub.equals(currSub.getsName())) {
                    Toast.makeText(getActivity(), "You already have selected this subject.", Toast.LENGTH_SHORT).show();
                    return; }

                if ((type.equals("frontal") || type.equals("both"))
                        && listCities.isEmpty()) {
//
                    new AlertDialog.Builder(getContext())
                            .setTitle("Select Service City")
                            .setMessage("You must choose at least one service city!")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    buildCityDialog(true);
                                    subjectObj newSub = new subjectObj(nameSub, type, Integer.parseInt(price), exp);
                                    /* remove the last entry (before the edit) */
                                    list.remove(currSub);
                                    /*  */
                                    list.add(newSub);
                                    updateList(currSub,newSub,listCities);
                                    listSub.remove(currSub.getsName());
                                    listSub.add(nameSub);
                                    d.dismiss();
                                    return;
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    d.dismiss();
                    return;
                }
                if (type.equals("online") && !listCities.isEmpty()) {
//                    Toast.makeText(getActivity(), "You have chosen to transfer private lessons" +
//                                    " only online, do not select service cities",
//                            Toast.LENGTH_SHORT).show();
                    new AlertDialog.Builder(getContext())
                            .setTitle("Delete All Service City")
                            .setMessage("You chose to teach only online. \n" +
                                    "Are you sure you want to delete all service cities?")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ArrayList<String> removeTempCities = new ArrayList<>();
                                    for (int i = 0; i < selectCities.length; i++)
                                        selectCities[i] = false;
                                    for (String rCity : listCities)
                                        removeTempCities.add(rCity);
                                    listCitiesNum.clear();
                                    // Copy old service cities
                                    ArrayList<String> oldCities = new ArrayList<>(listCities);
                                    listCities.clear();
                                    serviceCitiesSpinner.setTextColor(Color.GRAY);
                                    serviceCitiesSpinner.setText("Select service cities");
                                    removeServiceCities(removeTempCities, listCities);

                                    subjectObj newSub = new subjectObj(nameSub, type, Integer.parseInt(price), exp);
                                    /* remove the last entry (before the edit) */
                                    list.remove(currSub);
                                    /*  */
                                    list.add(newSub);
                                    updateList(currSub,newSub,oldCities);
                                    listSub.remove(currSub.getsName());
                                    listSub.add(nameSub);
                                    d.dismiss();
                                }
                  })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                   d.dismiss();
                    return;
                }


                subjectObj newSub = new subjectObj(nameSub, type, Integer.parseInt(price), exp);
                /* remove the last entry (before the edit) */
                list.remove(currSub);
                /*  */
                list.add(newSub);
                updateList(currSub,newSub,listCities);
                listSub.remove(currSub.getsName());
                listSub.add(nameSub);
                d.dismiss();
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listSub.size()==1) {
                    Toast.makeText(getActivity(), "You can not delete the only subject " +
                                    "defined for you. You must define at least one subject.",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                boolean hasFrontal = false;
                for (subjectObj sub : list) {
                    if (!sub.equals(currSub) && (sub.getType().equals("frontal") || sub.getType().equals("both"))) {
                        hasFrontal = true;
                        break;
                    }
                }

                if (!listCities.isEmpty() && !hasFrontal) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Delete All Service City")
                            .setMessage("You chose to teach only online. \n" +
                                    "Are you sure you want to delete all service cities?")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ArrayList<String> removeTempCities = new ArrayList<>();
                                    for (int i = 0; i < selectCities.length; i++)
                                        selectCities[i] = false;
                                    for (String rCity : listCities)
                                        removeTempCities.add(rCity);
                                    listCitiesNum.clear();
                                    ArrayList<String> oldServiceCities = new ArrayList<>(listCities);
                                    listCities.clear();
                                    serviceCitiesSpinner.setTextColor(Color.GRAY);
                                    serviceCitiesSpinner.setText("Select service cities");
                                    removeServiceCities(removeTempCities, listCities);
                                    list.remove(currSub);
                                    listSub.remove(currSub.getsName());
                                    updateList(currSub,null,oldServiceCities);
                                    a.notifyDataSetChanged();
                                    d.dismiss();
                                    return;
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    d.dismiss();
                    return;
                }
                list.remove(currSub);
                listSub.remove(currSub.getsName());
                updateList(currSub,null,listCities);
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

    /* Subject Update - update a subject according to the previous subject and a new subject.
     * Subject Addition - addition of a new subject ('prevSub'=null when calling the method)
     * Subject Deletion - deletion of an old subject ('newSub'=null when calling the method) */
    public void updateList(subjectObj prevSub,subjectObj newSub, ArrayList<String> oldListCities) {
        /* Make a list of all the RealTime DataBase commands to execute
         * (for the purpose of executing all the commands at once) */
        Map<String, Object> childUpdates = new HashMap<>();
        new FireBaseUser().getUserRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                teacherID = dataSnapshot.child("teacherID").getValue(String.class);
                if (prevSub!=null) {
                    /* (add a command) delete the subject from the Search Tree */
                    if(prevSub.getType().equals("online")) {
                        childUpdates.put("search/" + prevSub.getType() + "/" + prevSub.getsName()
                                + "/" + prevSub.getPrice() + "/" + teacherID, null);
                    }
                    else{
                        for (String sCity : oldListCities) {
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
                //new FireBaseTeacher().setSubList(teacherID, list);
                //myRef.child("teachers").child(teacherID).child("sub").setValue(listSub);
                ArrayAdapter a = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, list);
                subjectList.setAdapter(a);
                a.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void setCitySpinner(TextView citySpinner, boolean[] selectCities, ArrayList<Integer> listCitiesNum,
                                String[] arrCities) {


        citySpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            buildCityDialog(false);


            }
        });
    }

    /* Print the List of cities that the teacher tutor */
    private String printList (ArrayList < String > list) {
        String s =list.toString();
        s = s.replace("[","");
        s = s.replace("]","");
        return s;
    }

    /* Delete cities and subjects from firebase in the tree search */
    public ArrayList<String> removeServiceCities(ArrayList < String > removeList, ArrayList < String > currentList) {
        Collections.sort(removeList);
        Map<String, Object> childUpdates = new HashMap<>();
        new FireBaseUser().getUserRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                teacherID = dataSnapshot.child("teacherID").getValue(String.class);
                for(subjectObj sList : list) {
                    for (String rCity : removeList) {
                        if(sList.getType().equals("frontal") || sList.getType().equals("both")) {
                            childUpdates.put("search/" + sList.getType() + "/" + sList.getsName()
                                    + "/" + rCity + "/" + sList.getPrice() + "/" + teacherID, null);
                        }
                    }
                }
                childUpdates.put("teachers/" + teacherID + "/serviceCities", currentList);
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
    public ArrayList<String> addServiceCities(ArrayList < String > addList, ArrayList < String > currentList) {
        Collections.sort(addList);
        Map<String, Object> childUpdates = new HashMap<>();
        new FireBaseUser().getUserRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                teacherID = dataSnapshot.child("teacherID").getValue(String.class);
                for(subjectObj sList : list) {
                    for (String aCity : addList) {
                        if(sList.getType().equals("frontal") || sList.getType().equals("both")) {
                            childUpdates.put("search/" + sList.getType() + "/" + sList.getsName()
                                    + "/" + aCity + "/" + sList.getPrice() + "/" + teacherID, teacherID);
                        }
                    }
                }

                childUpdates.put("teachers/" + teacherID + "/serviceCities", currentList);
                myRef.updateChildren(childUpdates);
                addList.clear();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return addList;
    }
    public void buildCityDialog(boolean fromEditSub){
        ArrayList<String> addTempCities = new ArrayList<>();
        ArrayList<String> removeTempCities = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select service cities");
        builder.setCancelable(false);


        builder.setMultiChoiceItems(arrCities, selectCities, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    listCitiesNum.add(which);
                    listCities.add(arrCities[which]);
                    addTempCities.add(arrCities[which]);
                    Collections.sort(listCitiesNum);
                } else {
                    listCitiesNum.remove((Integer) which);
                    listCities.remove(arrCities[which]);
                    removeTempCities.add(arrCities[which]);
                }
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                boolean hasFrontal = false;
                boolean error = false;
                for (subjectObj sub : list) {
                    if (sub.getType().equals("frontal") || sub.getType().equals("both"))
                        hasFrontal = true;
                    if (listCities.isEmpty() && hasFrontal) {
                        Toast.makeText(getActivity(), "You must choose at least one service city",
                                Toast.LENGTH_SHORT).show();
                        error =true;
                        removeServiceCities(removeTempCities, listCities);
                    }
                }

                if (!listCities.isEmpty() && !hasFrontal && !fromEditSub) {
                    Toast.makeText(getActivity(), "You have" +
                                    " only online lessons, do not select service cities",
                            Toast.LENGTH_SHORT).show();
                    error = true ;
                }
                if (!error) {
                    if (listCities.isEmpty()) {
                        serviceCitiesSpinner.setTextColor(Color.GRAY);
                        serviceCitiesSpinner.setText("Select service cities");
                        removeServiceCities(removeTempCities, listCities);
                    } else {
                        serviceCitiesSpinner.setTextColor(Color.BLACK);
                        serviceCitiesSpinner.setText(printList(listCities));
                        addServiceCities(addTempCities, listCities);
                        removeServiceCities(removeTempCities, listCities);
                    }
                }
                else{
                    builder.show();
                }

            }
        });

//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });

        builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                boolean hasFrontal = false;
                for (subjectObj sub : list) {
                    if (sub.getType().equals("frontal") || sub.getType().equals("both")){
                        Toast.makeText(getActivity(), "You must choose at least one service city",
                                Toast.LENGTH_SHORT).show();
                        hasFrontal = true;
                        break;
                    }
                }
                if(!hasFrontal) {
                    for (int i = 0; i < selectCities.length; i++)
                        selectCities[i] = false;
                    for (String rCity : listCities)
                        removeTempCities.add(rCity);
                    listCitiesNum.clear();
                    listCities.clear();
                    serviceCitiesSpinner.setTextColor(Color.GRAY);
                    serviceCitiesSpinner.setText("Select service cities");
                    removeServiceCities(removeTempCities, listCities);
                }
                else{
                    builder.show();
                }
            }
        });

        builder.show();
    }
}