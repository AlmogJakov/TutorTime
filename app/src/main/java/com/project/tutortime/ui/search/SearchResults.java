package com.project.tutortime.ui.search;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.tutortime.LoadingDialog;
import com.project.tutortime.Login;
import com.project.tutortime.R;
import com.project.tutortime.adapter.TutorAdapter;
import com.project.tutortime.adapter.TutorAdapterItem;
import com.project.tutortime.databinding.FragmentSearchBinding;
import com.project.tutortime.databinding.FragmentSearchResultsBinding;
import com.project.tutortime.firebase.subjectObj;
import com.project.tutortime.firebase.teacherObj;
import com.project.tutortime.firebase.userObj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class SearchResults extends Fragment {
    private String typeResult, cityResult, subjectResult;
    private int maxResult;
    private int minResult;
    private int kindOfSort;
    private final String[] TypeResult = {"both", "frontal", "online"};
    private boolean[] chooseType = {true,true,true};
    ArrayList<Integer> prices = new ArrayList<>();
    ArrayList<Integer> listRating = new ArrayList<>();
    TutorAdapter adapter;
    HashSet<String> used = new HashSet<>();
    ListView listview;
    TextView sort, rank;
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    List<TutorAdapterItem> teachersToShow = new ArrayList<>();
    boolean getTeachers = false;
    List<TutorAdapterItem> resultOfTeachers = new ArrayList<>();
    LoadingDialog loadingDialog;
    private @NonNull FragmentSearchResultsBinding binding;



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Search fragment2 = new Search();
                FragmentManager fragmentManager = getFragmentManager();
                List<Fragment> f = fragmentManager.getFragments();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(((ViewGroup)getView().getParent()).getId(), fragment2).addToBackStack(null);
                fragmentTransaction.commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        /* Prevents the fragment from destroying and hence recreating while changing language. */
        this.setRetainInstance(true);
        binding = FragmentSearchResultsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        loadingDialog = new LoadingDialog(getContext());
        loadingDialog.show();

        if (getArguments().containsKey("resultOfTeachers")) {
            resultOfTeachers = getArguments().getParcelableArrayList("resultOfTeachers");
            listRating = getArguments().getIntegerArrayList("listRating");
            getTeachers = true;
        }
        typeResult = getArguments().getString("typeResult");
        cityResult = getArguments().getString("cityResult");
        subjectResult = getArguments().getString("subjectResult");
        minResult = getArguments().getInt("min");
        maxResult = getArguments().getInt("max");
        kindOfSort = getArguments().getInt("sort_by");


        if (typeResult.equals("Frontal")){ chooseType[2] = false;}
        else if (typeResult.equals("Online")) {chooseType[1] = false;}
        String[] Cities = cityResult.split(", ");

        double t = minResult;
        minResult = ((int)Math.ceil(t/20)) * 20;
        maxResult = (maxResult/20) *20;
        for (int i = minResult; i <= maxResult; i += 20) {
            prices.add(i);
        }

        sort = binding.sort;
        String[] type = {getResources().getString(R.string.priceLow), getResources().getString(R.string.priceHigh)};
        boolean[] selectType = new boolean[type.length];
        selectType[kindOfSort] = true;
        setSpinner(sort, selectType, type);

        rank = binding.rank;
        String[] typeRank = {"⭐", "⭐⭐", "⭐⭐⭐", "⭐⭐⭐⭐","⭐⭐⭐⭐⭐"};
        boolean[] selectTypeRenk = new boolean[typeRank.length];
        if (listRating.size()>0){
            for (int i = 0; i < listRating.size(); i++) {
                selectTypeRenk[listRating.get(i)] = true;
            }
        }
        setSpinner(rank, selectTypeRenk,listRating,  typeRank);

        listview = binding.featuresList;
        switch (kindOfSort){
            case 0:sortByPriceLow();break;
            case 1:sortByPriceHigh();break;
        }
        if (getTeachers){
            setRating();

            if (kindOfSort == 0)Collections.sort(teachersToShow);
            else teachersToShow.sort(Collections.reverseOrder());

            adapter = new TutorAdapter(getActivity(), teachersToShow);
            listview.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            closeLoadingDialog();
        }
        else {
            setListview(Cities);
        }
        return root;
    }

    private void setRating() {
        if (listRating.size() != 0){
            HashSet<String> used = new HashSet<>();
            for (int i = 0; i < listRating.size(); i++) {
                for (TutorAdapterItem t: resultOfTeachers) {
                    if (t.getTeacher().getRank().getAvgRank() >= listRating.get(i)+1 && t.getTeacher().getRank().getAvgRank() < listRating.get(i)+2){
                        if (!used.contains(t.getTeacher().getUserID())){
                            used.add(t.getTeacher().getUserID());
                            teachersToShow.add(t);
                        }
                    }
                }
            }
        }
        else teachersToShow = resultOfTeachers;
    }

    /** ///////// sort functions //////// */

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sortByPriceHigh() {
        prices.sort(Collections.reverseOrder());
        teachersToShow.sort(Collections.reverseOrder());
    }

    private void sortByPriceLow() {
        Collections.sort(prices);
        Collections.sort(teachersToShow);
    }



    /** Fills all search card  */
    private void setListview(String[] Cities){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (chooseType[0])setBoth(dataSnapshot);
                if (chooseType[1])setFrontal(dataSnapshot);
                if (chooseType[2])setOnline(dataSnapshot);
                resultOfTeachers = teachersToShow;
                switch (kindOfSort){
                    case 0:sortByPriceLow();break;
                    case 1:sortByPriceHigh();break;
                }
                adapter = new TutorAdapter(getActivity(), teachersToShow);
                listview.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                closeLoadingDialog();
            }

            private void setOnline(DataSnapshot dataSnapshot) {
                System.out.println("on online");
                for (int pr: prices){
                    Iterable<DataSnapshot> idOfTeacher = dataSnapshot.child("search").child(TypeResult[2]).child(subjectResult).child(Integer.toString(pr)).getChildren();
                    for (DataSnapshot s1 : idOfTeacher) {
                        String s = s1.getKey();
                        if (s != null){
                            teacherObj teacher = dataSnapshot.child("teachers").child(s).getValue(teacherObj.class);
                            if (teacher != null){
                                if (!used.contains(teacher.getUserID())){
                                    userObj user = dataSnapshot.child("users").child(teacher.getUserID()).getValue(userObj.class);
                                    System.out.println(user.getfName());
                                    teachersToShow.add(new TutorAdapterItem(user,teacher,subjectResult));
                                    used.add(teacher.getUserID());
                                }
                            }
                        }
                    }
                }
            }

            private void setFrontal(DataSnapshot dataSnapshot) {
                System.out.println("on frontal");
                for (int pr: prices){
                    for (String c:Cities) {
                        Iterable<DataSnapshot> idOfTeacher = dataSnapshot.child("search").child(TypeResult[1]).child(subjectResult).child(c).child(Integer.toString(pr)).getChildren();
                        for (DataSnapshot s1 : idOfTeacher) {
                            String s = s1.getKey();
                            if (s != null){
                                teacherObj teacher = dataSnapshot.child("teachers").child(s).getValue(teacherObj.class);
                                if (teacher != null){
                                    if (!used.contains(teacher.getUserID())){
                                        userObj user = dataSnapshot.child("users").child(teacher.getUserID()).getValue(userObj.class);
                                        System.out.println(user.getfName());
                                        teachersToShow.add(new TutorAdapterItem(user,teacher,subjectResult));
                                        used.add(teacher.getUserID());
                                    }
                                }
                            }
                        }
                    }
                }
            }

            private void setBoth(DataSnapshot dataSnapshot) {
                System.out.println("on both");
                for (int pr: prices){
                    Iterable<DataSnapshot> citiesCard = dataSnapshot.child("search").child(TypeResult[0]).child(subjectResult).getChildren();
                    for (DataSnapshot s2 : citiesCard) {
                        Iterable<DataSnapshot> idOfCity = s2.child(Integer.toString(pr)).getChildren();
                        for (DataSnapshot s1 : idOfCity) {
                            String s = (String) s1.getValue();
                            System.out.println(s);
                            if (s != null){
                                teacherObj teacher = dataSnapshot.child("teachers").child(s).getValue(teacherObj.class);
                                if (teacher != null){
                                    if (!used.contains(teacher.getUserID())){
                                        userObj user = dataSnapshot.child("users").child(teacher.getUserID()).getValue(userObj.class);
                                        System.out.println(user.getfName());
                                        teachersToShow.add(new TutorAdapterItem(user,teacher,subjectResult));
                                        used.add(teacher.getUserID());
                                    }
                                }
                            }
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }


    private void setSpinner(TextView typeSpinner, boolean[] selectType, String[] type) {
        typeSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getResources().getString(R.string.Select));
                builder.setCancelable(false);
                builder.setSingleChoiceItems(type, kindOfSort, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < selectType.length; i++) {
                            if (i!= which){
                                selectType[i]=false;
                            }
                        }

                        selectType[which] = true;

                        // when 'which' is:
                        //                0 - sort teacher cards by price from low to high
                        //                1 - sort teacher cards by price from high to low
                        //                2 - sort teacher cards by rank from high to low
                        reload(which);

                        dialog.cancel();
                    }

                });
                builder.setPositiveButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
        });
    }

    private void setSpinner(TextView typeSpinner, boolean[] selectType, ArrayList<Integer> list, String[] type) {
        typeSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getResources().getString(R.string.filter_by));
                builder.setCancelable(false);
                builder.setMultiChoiceItems(type, selectType, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            list.add(which);
                            Collections.sort(list);
                        } else {
                            list.remove((Integer) which);
                        }
                    }
                });
                builder.setPositiveButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listRating = list;
                        reload(kindOfSort);
                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setNeutralButton(getResources().getString(R.string.Clear), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listRating.size() > 0){
                            listRating = new ArrayList<>();
                            reload(kindOfSort);
                        }
                    }
                });
                builder.show();
            }
        });
    }

    private void reload(int sortBy) {
        Bundle bundle = new Bundle();
        bundle.putString("typeResult", typeResult);
        bundle.putString("cityResult", cityResult);
        bundle.putString("subjectResult", subjectResult);
        bundle.putInt("min", minResult);
        bundle.putInt("max", maxResult);
        bundle.putInt("sort_by", sortBy);
        bundle.putParcelableArrayList("resultOfTeachers", (ArrayList<? extends Parcelable>) resultOfTeachers);
        bundle.putIntegerArrayList("listRating", listRating);

        SearchResults fragment2 = new SearchResults();
        fragment2.setArguments(bundle);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(((ViewGroup)getView().getParent()).getId(), fragment2);
        fragmentTransaction.commit();
    }



    public void closeLoadingDialog() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                /* wait until the adapter loaded all images */
                while(!adapter.isAllResourcesReady()) { }
                /* hide loading dialog (fragment resources ready) */
                loadingDialog.cancel();
            }
        });
    }
}
