package com.project.tutortime.Controller.search;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.tutortime.View.LoadingDialog;
import com.project.tutortime.R;
import com.project.tutortime.databinding.FragmentSearchBinding;
import com.project.tutortime.Model.firebase.subjectObj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class Search extends Fragment {
    FirebaseAuth fAuth;
    private DatabaseReference mDatabase;
    Button addBtn;
    Spinner subjectSpin;
    TextView typeSpinner;
    TextView citySpinner;
    EditText minPrice, maxPrice;
    DialogInterface t;
    LoadingDialog loadingDialog;
    private FragmentSearchBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        /* show loading dialog until all fragment resources ready */
        loadingDialog = new LoadingDialog(getContext());
        loadingDialog.show();
        addBtn = binding.buttonSearch;
        String[] type = {getResources().getString(R.string.Online), getResources().getString(R.string.Frontal)};
        typeSpinner = binding.learn;
        boolean[] selectType = new boolean[type.length];
        ArrayList<Integer> listType = new ArrayList<>();
        setSpinner(typeSpinner, selectType, listType, type, getResources().getString(R.string.Online_Frontal));

        subjectSpin = binding.selectSubSpinner;
        subjectSpin.setAdapter(new ArrayAdapter<>
                (this.getActivity(), android.R.layout.simple_spinner_item, subjectObj.SubName.values()));

        citySpinner = binding.selectCity;
        String[] cities = getResources().getStringArray(R.array.Cities);
        String[] Cities = new String[cities.length+1];
        Cities[0] =  getResources().getString(R.string.All); /////////////////////////
        System.arraycopy(cities, 0, Cities, 1, cities.length);

        boolean[] selectCity = new boolean[Cities.length];
        ArrayList<Integer> listCity = new ArrayList<>();
        setSpinnerForCity(citySpinner,selectCity,listCity, Cities, getResources().getString(R.string.choose_city));

        maxPrice = binding.max;
        minPrice = binding.min;
        closeLoadingDialog();
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String typeResult = typeSpinner.getText().toString().trim();
                String cityResult = citySpinner.getText().toString().trim();
                String subjectResult = subjectSpin.getSelectedItem().toString();
                String minResult = minPrice.getText().toString().trim();
                String maxResult = maxPrice.getText().toString().trim();
                int min=0, max=300;

                ///////// start check error in search ////////

                if (subjectSpin.getSelectedItemPosition() == 0) {
                    TextView errorText = (TextView) subjectSpin.getSelectedView();
                    errorText.setError("Subject is required.");
                    Toast.makeText(getActivity(), "Subject is required.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (typeResult.equals("")){
                    Toast.makeText(getActivity(), "Learning type is required.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
//                if (citySpinner.getText().toString().equals("")){
//                    Toast.makeText(getActivity(), "City is required.",
//                            Toast.LENGTH_SHORT).show();
//                    return;
//                }
                if (typeResult.equals("Frontal") && cityResult.equals("")){
                    Toast.makeText(getActivity(), "City is required.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    if (!maxResult.equals(""))max = Integer.parseInt(maxResult);
                } catch (NumberFormatException ignored) {
                    maxPrice.setError("Type a number.");
                    Toast.makeText(getActivity(), "Type a number.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    if (!minResult.equals(""))min = Integer.parseInt(minResult);
                } catch (NumberFormatException ignored) {
                    minPrice.setError("Type a number.");
                    Toast.makeText(getActivity(), "Type a number.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

//                if (minPrice.getText().toString().equals("")) {
//                    Toast.makeText(getActivity(), "Type a minimum price number.",
//                            Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (maxPrice.getText().toString().equals("")) {
//                    Toast.makeText(getActivity(), "Type a maximum price number.",
//                            Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (Integer.parseInt(minResult)>Integer.parseInt(maxResult)) {
//                    Toast.makeText(getActivity(), "Price range is illegal.",
//                            Toast.LENGTH_SHORT).show();
//                    return;
//                }

                /////////  end check error in search ////////

                Bundle bundle = new Bundle();
                bundle.putString("typeResult", typeResult);
                bundle.putString("cityResult", cityResult);
                bundle.putString("subjectResult", subjectResult);
                bundle.putInt("min", min);
                bundle.putInt("max", max);
                bundle.putInt("sort_by", 0);

                SearchResults fragment2 = new SearchResults();
                fragment2.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(((ViewGroup)getView().getParent()).getId(), fragment2).addToBackStack(null).commit();

            }
        });
        return root;

    }

    private void setSpinner(TextView typeSpinner, boolean[] selectType, ArrayList<Integer> list, String[] type, String title) {
        typeSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(title);
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
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < list.size(); i++) {
                            stringBuilder.append(type[list.get(i)]);
                            if (i != list.size() - 1) {
                                stringBuilder.append(", ");
                            }
                        }
                        typeSpinner.setText(stringBuilder.toString());
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
                        for (int i = 0; i < selectType.length; i++) {
                            selectType[i] = false;
                            list.clear();
                            typeSpinner.setText("");
                        }
                    }
                });
                builder.show();
            }
        });
    }


    private void setSpinnerForCity(TextView typeSpinner, boolean[] selectType, ArrayList<Integer> list, String[] type, String title) {
        typeSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(title);
                builder.setCancelable(false);
                builder.setMultiChoiceItems(type, selectType, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (which == 0){
                            if (isChecked){
                                list.clear();
                                for (int i = 0; i < type.length; i++) {
                                    list.add(i);
                                    selectType[i] = true;
                                }
                            }
                            else {
                                list.clear();
                                Arrays.fill(selectType, false);
                            }
                        }
                        else if (isChecked) {
                            list.remove((Integer) 0);
                            selectType[0] = false;
                            list.add(which);
                            Collections.sort(list);
                        } else {
                            selectType[0] = false;
                            list.remove((Integer) 0);
                            list.remove((Integer) which);
                        }
                    }
                });
                builder.setPositiveButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i) != 0){
                                stringBuilder.append(type[list.get(i)]);
                                if (i != list.size() - 1) {
                                    stringBuilder.append(", ");
                                }
                            }
                        }
                        typeSpinner.setText(stringBuilder.toString());
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
                        for (int i = 0; i < selectType.length; i++) {
                            selectType[i] = false;
                            list.clear();
                            typeSpinner.setText("");
                        }
                    }
                });
                builder.show();
            }
        });
    }

    private void almog() {
        fAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String userID = fAuth.getCurrentUser().getUid();
        mDatabase.child("users").child(userID).child("isTeacher").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    if (dataSnapshot.getValue(Integer.class) == 0) {
                        Toast.makeText(getActivity(), "Your a Student.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Your a Teacher.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void closeLoadingDialog() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                /* hide loading dialog (fragment resources ready) */
                loadingDialog.cancel();
            }
        });
    }
}