package com.high_technology_software.concept.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;
import java.util.Date;

import javax.xml.validation.TypeInfoProvider;

public class ChooserPickerFragment extends DialogFragment {

    public static final String EXTRA_CRIME_DATE = "com.high_technology_software.concept.criminalintent.crime_date";

    private static final String DIALOG_DATE = "date";
    private static final String DIALOG_TIME = "time";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;

    private Date mDate;

    public static ChooserPickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_DATE, date);

        ChooserPickerFragment fragment = new ChooserPickerFragment();
        fragment.setArguments(args);

        return fragment;
    }

    private void sendResult(int resultCode) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent i = new Intent();
        i.putExtra(EXTRA_CRIME_DATE, mDate);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }

    private void showDialog(String dialog, int request) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        DialogFragment dialogFragment = null;

        switch (request) {
            case REQUEST_DATE:
                dialogFragment = DatePickerFragment.newInstance(mDate);
                break;
            case REQUEST_TIME:
                dialogFragment = TimePickerFragment.newInstance(mDate);
                break;
        }

        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(ChooserPickerFragment.this, request);
            dialogFragment.show(fm, dialog);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mDate = (Date) getArguments().getSerializable(EXTRA_CRIME_DATE);

        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_chooser, null);

        Button dateButton = (Button) v.findViewById(R.id.dialog_chooser_dateButton);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_DATE, REQUEST_DATE);
            }
        });

        Button timeButton = (Button) v.findViewById(R.id.dialog_chooser_timeButton);
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_TIME, REQUEST_TIME);
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.chooser_title)
                .setPositiveButton(
                        android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendResult(Activity.RESULT_OK);
                            }
                        })
                .setNegativeButton(
                        android.R.string.no,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendResult(Activity.RESULT_CANCELED);
                            }
                        })
                .create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;

        Date date = null;

        switch (requestCode) {
            case REQUEST_DATE:
                date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);

                Calendar beforeCalendar = Calendar.getInstance();
                beforeCalendar.setTime(mDate);
                Calendar afterCalendar = Calendar.getInstance();
                afterCalendar.setTime(date);
                afterCalendar.set(Calendar.HOUR_OF_DAY, beforeCalendar.get(Calendar.HOUR_OF_DAY));
                afterCalendar.set(Calendar.MINUTE, beforeCalendar.get(Calendar.MINUTE));

                date = afterCalendar.getTime();
                break;
            case REQUEST_TIME:
                date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
                break;
        }

        if (date != null) {
            mDate = date;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
