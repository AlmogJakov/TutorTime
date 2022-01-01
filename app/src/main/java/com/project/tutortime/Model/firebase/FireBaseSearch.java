package com.project.tutortime.Model.firebase;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.tutortime.Model.adapter.TutorAdapterItem;
import com.project.tutortime.R;
import com.project.tutortime.View.LoadingDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class FireBaseSearch {
    public static List<TutorAdapterItem> setRating(ArrayList<Integer> listRating, List<TutorAdapterItem> teachersToShow, List<TutorAdapterItem> resultOfTeachers) {
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
            return teachersToShow;
        }
        else return resultOfTeachers;
    }

    public static void setSpinnerForSearch(Context c, TextView typeSpinner, boolean[] selectType, ArrayList<Integer> list, String[] type, String title) {
        typeSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(c);
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
                builder.setPositiveButton(c.getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
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
                builder.setNegativeButton(c.getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setNeutralButton(c.getResources().getString(R.string.Clear), new DialogInterface.OnClickListener() {
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

    public static void setSpinnerForCityForSearch(Context c, TextView typeSpinner, boolean[] selectType, ArrayList<Integer> list, String[] type, String title) {
        typeSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(c);
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
                builder.setPositiveButton(c.getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
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
                builder.setNegativeButton(c.getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setNeutralButton(c.getResources().getString(R.string.Clear), new DialogInterface.OnClickListener() {
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

    public static void closeLoadingDialogForSearch(LoadingDialog loadingDialog) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                /* hide loading dialog (fragment resources ready) */
                loadingDialog.cancel();
            }
        });
    }
}
