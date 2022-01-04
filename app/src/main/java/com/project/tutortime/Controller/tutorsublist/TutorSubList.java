package com.project.tutortime.Controller.tutorsublist;

import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.tutortime.Model.firebase.FireBaseTutor;
import com.project.tutortime.R;


import com.project.tutortime.databinding.FragmentMySubListBinding;
import com.project.tutortime.Model.firebase.FireBaseUser;
import com.project.tutortime.Model.firebase.subjectObj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TutorSubList extends Fragment {

    //private MySubListViewModel mViewModel;
    private TutorSubListViewModel TutorSubListViewModel;
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
    SubListAdapter sLstAd;
    FireBaseTutor fbTutor = new FireBaseTutor();


    public static TutorSubList newInstance() {
        return new TutorSubList();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        TutorSubListViewModel = new ViewModelProvider(this).get(TutorSubListViewModel.class);
        binding = FragmentMySubListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        subjectList = binding.ListViewSubList;
        addSub = binding.btnAddSubject;
        serviceCitiesSpinner = binding.txtServiceCities;

//        ArrayAdapter a = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, list);
//        subjectList.setAdapter(a);
//        a.notifyDataSetChanged();

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
                createDialog();
            }
        });

        subjectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                subjectObj s = (subjectObj) subjectList.getItemAtPosition(i);
                createEditDialog(s);
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

                /*Updates the buttons in Dialog Cities for Selected Cities*/
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
//                ArrayAdapter a = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, list);
//                subjectList.setAdapter(a);
//                a.notifyDataSetChanged();
                sLstAd = new SubListAdapter(TutorSubList.this.getActivity(), R.layout.single_sub_row, list, teacherID);
                subjectList.setAdapter(sLstAd);
                sLstAd.notifyDataSetChanged();

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

    public void createDialog() {
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
                                    fbTutor.updateList(null,newSub,listCities);
                                    listSub.add(nameSub);
                                    sLstAd.notifyDataSetChanged();

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
                fbTutor.updateList(null,newSub,listCities);
                listSub.add(nameSub);
                sLstAd.notifyDataSetChanged();

                d.dismiss();
            }
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { d.dismiss(); }
        });
        d.show();
    }

    public void createEditDialog(subjectObj currSub) {
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
        sLstAd = new SubListAdapter(TutorSubList.this.getActivity(), R.layout.single_sub_row, list, teacherID);
        subjectList.setAdapter(sLstAd);
        sLstAd.notifyDataSetChanged();
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
                                    /* remove the last entry (before the edit) */
                                    list.remove(currSub);
                                    /*  */
                                    list.add(newSub);
                                    fbTutor.updateList(currSub,newSub,listCities);
                                    listSub.remove(currSub.getsName());
                                    listSub.add(nameSub);
                                    sLstAd.notifyDataSetChanged();
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
                                    fbTutor.removeServiceCities(removeTempCities, listCities,list);

                                    subjectObj newSub = new subjectObj(nameSub, type, Integer.parseInt(price), exp);
                                    /* remove the last entry (before the edit) */
                                    list.remove(currSub);
                                    /*  */
                                    list.add(newSub);
                                    fbTutor.updateList(currSub,newSub,oldCities);
                                    listSub.remove(currSub.getsName());
                                    listSub.add(nameSub);
                                    sLstAd.notifyDataSetChanged();

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
                fbTutor.updateList(currSub,newSub,listCities);
                sLstAd.notifyDataSetChanged();
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
                                    fbTutor.removeServiceCities(removeTempCities, listCities,list);
                                    list.remove(currSub);
                                    listSub.remove(currSub.getsName());
                                    fbTutor.updateList(currSub,null,oldServiceCities);
                                    sLstAd.notifyDataSetChanged();
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
                fbTutor.updateList(currSub,null,listCities);
                sLstAd.notifyDataSetChanged();
                d.dismiss();
            }
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sLstAd.notifyDataSetChanged();
                d.dismiss();
            }
        });
        d.show();
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
                        fbTutor.removeServiceCities(removeTempCities, listCities,list);
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
                        fbTutor.removeServiceCities(removeTempCities, listCities,list);
                    } else {
                        serviceCitiesSpinner.setTextColor(Color.BLACK);
                        serviceCitiesSpinner.setText(printList(listCities));
                        fbTutor.addServiceCities(addTempCities, listCities,list);
                        fbTutor.removeServiceCities(removeTempCities, listCities,list);
                    }
                }
                else{
                    builder.show();
                }

            }
        });



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
                    fbTutor.removeServiceCities(removeTempCities, listCities,list);
                }
                else{
                    builder.show();
                }
            }
        });

        builder.show();
    }
    public class SubListAdapter extends BaseAdapter {
        Context context;
        int layoutResourceId;
        ArrayList<subjectObj> subs;
        String teacherID;


        public SubListAdapter(Context context, int layoutResourceId, ArrayList<subjectObj> subs,
                              String teacherID) {
            super();
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.subs = subs;
            this.teacherID = teacherID;

        }

        @Override
        public int getCount() {
            return subs.size();
        }

        @Override
        public subjectObj getItem(int position) {
            int c = 0;
            for (subjectObj sub : subs) {
                if (c == position) return sub;
                c++;
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            TutorSubList.AppInfoHolder holder = null;

            if (row == null) {

                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new TutorSubList.AppInfoHolder();

                holder.nameText = (TextView) row.findViewById(R.id.singleSubName);
                holder.typeText = (TextView) row.findViewById(R.id.singleSubType);
                holder.priceText = (TextView) row.findViewById(R.id.singleSubPrice);
                holder.expText = (TextView) row.findViewById(R.id.singleSubEx);
                row.setTag(holder);

            } else {
                holder = (TutorSubList.AppInfoHolder) row.getTag();
            }
            TutorSubList.AppInfoHolder finalHolder = holder;
            subjectObj s = getItem(position);
            System.out.println(s);
            finalHolder.nameText.setText(s.getsName());
            finalHolder.typeText.setText("Type: "+s.getType());
            finalHolder.priceText.setText("Price: " +String.valueOf(s.getPrice()));
            finalHolder.expText.setText("Experience: " +s.getExperience());


            row.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    createEditDialog(s);
                }
            });
            return row;
        }



    }
    static class AppInfoHolder {
        TextView nameText, typeText, priceText, expText;
    }
}