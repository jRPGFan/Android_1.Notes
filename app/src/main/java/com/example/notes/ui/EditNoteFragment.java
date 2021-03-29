package com.example.notes.ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.notes.Data.Note;
import com.example.notes.MainActivity;
import com.example.notes.R;
import com.example.notes.observe.Publisher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class EditNoteFragment extends Fragment {
    static final String SELECTED_NOTE = "Selected_Note";
//    private static boolean edit;
    private Note note;
    private Publisher publisher;

    private EditText etEditNoteTitle;
    private EditText etEditNoteContents;
    private TextView tvNoteDate;
    private TextView tvNoteTime;
    private SimpleDateFormat simpleDateFormat;

    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;
    private String headerColor;

    private Button ibButtonP200;
    private Button ibButtonP500;
    private Button ibButtonP700;
    private Button ibButtonWhite;
    private Button selectedHeaderColorIB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            note = getArguments().getParcelable(SELECTED_NOTE);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        MainActivity mainActivity = (MainActivity)context;
        publisher = mainActivity.getPublisher();
    }

    @Override
    public void onDetach() {
        publisher = null;
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_note_fragment_layout, container, false);
        initView(view);
        initCalendar();
        if (note != null) populateView();
            else setDateTime();
        return view;
    }

    @SuppressLint({"ResourceAsColor", "ResourceType"})
    private void initView(View view) {
        etEditNoteTitle = view.findViewById(R.id.edit_note_title);
        etEditNoteContents = view.findViewById(R.id.edit_note_contents);
        tvNoteDate = view.findViewById(R.id.note_date);
        tvNoteTime = view.findViewById(R.id.note_time);
        ImageButton ibDatePicker = view.findViewById(R.id.date_picker);
        ImageButton ibTimePicker = view.findViewById(R.id.time_picker);

        ibButtonP200 = view.findViewById(R.id.button_p200);
        ibButtonP500 = view.findViewById(R.id.button_p500);
        ibButtonP700 = view.findViewById(R.id.button_p700);
        ibButtonWhite = view.findViewById(R.id.button_white);

        Button saveNoteEdit = view.findViewById(R.id.save_note_edit);
        Button cancelNoteEdit = view.findViewById(R.id.cancel_note_edit);

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        tvNoteDate.setOnClickListener(v -> pickDate());
        ibDatePicker.setOnClickListener(v -> pickDate());

        tvNoteTime.setOnClickListener(v -> pickTime());
        ibTimePicker.setOnClickListener(v -> pickTime());

        //selectDefaultHeaderColorIBElevation();

        ibButtonP200.setOnClickListener(v -> {
            headerColor = getResources().getString(R.color.purple_200);
            setHeaderColorIBElevation(ibButtonP200);
        });
        ibButtonP500.setOnClickListener(v -> {
            headerColor = getResources().getString(R.color.purple_500);
            setHeaderColorIBElevation(ibButtonP500);
        });
        ibButtonP700.setOnClickListener(v -> {
            headerColor = getResources().getString(R.color.purple_700);
            setHeaderColorIBElevation(ibButtonP700);
        });
        ibButtonWhite.setOnClickListener(v -> {
            headerColor = getResources().getString(R.color.white);
            setHeaderColorIBElevation(ibButtonWhite);
        });

        saveNoteEdit.setOnClickListener(v -> saveNoteEdit());
        cancelNoteEdit.setOnClickListener(v -> {
            assert getFragmentManager() != null;
            getFragmentManager().popBackStackImmediate();
        });
    }

    @SuppressLint("ResourceType")
    private void populateView() {
        etEditNoteTitle.setText(note.getTitle());
        headerColor = note.getHeaderColor();
        etEditNoteTitle.setBackgroundColor(Color.parseColor(headerColor));
        etEditNoteContents.setText(note.getContents());
        String noteDateTime = simpleDateFormat.format(note.getCreationDate().getTime());
        tvNoteDate.setText(String.format("%s", noteDateTime.substring(0, noteDateTime.indexOf(" "))));
        tvNoteTime.setText(String.format("%s", noteDateTime.substring(noteDateTime.indexOf(" ") + 1)));
    }

    @SuppressLint("ResourceType")
    private void setDateTime() {
        String currentDateTime = simpleDateFormat.format(new Date());
        tvNoteDate.setText(currentDateTime.substring(0, currentDateTime.indexOf(" ")));
        tvNoteTime.setText(currentDateTime.substring(currentDateTime.indexOf(" ") + 1));
        headerColor = getResources().getString(R.color.white);
    }

    public static EditNoteFragment newInstance(Note note) {
//        edit = true;
        EditNoteFragment editNoteFragment = new EditNoteFragment();
        Bundle argsBundle = new Bundle();
        argsBundle.putParcelable(SELECTED_NOTE, note);
        editNoteFragment.setArguments(argsBundle);
        return editNoteFragment;
    }

    public static EditNoteFragment newInstance() {
//        edit = false;
        return new EditNoteFragment();
    }

//    @SuppressLint({"NonConstantResourceId", "ResourceType"})
//    private void selectDefaultHeaderColorIBElevation(){
//        switch (headerColor){
//            case R.color.purple_200:
//                selectedHeaderColorIB = ibButtonP200;
//                ibButtonP200.setElevation(5);
//                break;
//            case R.color.purple_500:
//                selectedHeaderColorIB = ibButtonP500;
//                ibButtonP500.setElevation(5);
//                break;
//            case R.color.purple_700:
//                selectedHeaderColorIB = ibButtonP700;
//                ibButtonP700.setElevation(5);
//                break;
//            case R.color.white:
//                selectedHeaderColorIB = ibButtonWhite;
//                ibButtonWhite.setElevation(5);
//                break;
//            default:
//        }
//    }

    private void setHeaderColorIBElevation(Button button) {
        if(selectedHeaderColorIB != null) selectedHeaderColorIB.setElevation(0);
        button.setElevation(3);
        etEditNoteTitle.setBackgroundColor(Color.parseColor(headerColor));
        selectedHeaderColorIB = button;
    }

    private void initCalendar() {
        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);
    }

    private void pickDate(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, month, day) ->
                        tvNoteDate.setText(String.format("%s-%s-%s",
                                String.valueOf(year).length() == 1 ? "0" + year : year,
                                String.valueOf(month++).length() == 1 ? "0" + month++ : month++,
                                String.valueOf(day).length() == 1 ? "0" + day : day))
                , mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void pickTime() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view, hour, minute) ->
                        tvNoteTime.setText(String.format("%s:%s:00",
                                String.valueOf(hour).length() == 1 ? "0" + hour : hour,
                                String.valueOf(minute).length() == 1 ? "0" + minute : minute)),
                mHour, mMinute, true);
        timePickerDialog.show();
    }

    private void saveNoteEdit() {
        if (note == null) note = new Note();
        note.setTitle(etEditNoteTitle.getText().toString());
        note.setContents(etEditNoteContents.getText().toString());
        try {
            note.setCreationDate(simpleDateFormat.parse(String.format("%s %s",
                    tvNoteDate.getText().toString(), tvNoteTime.getText().toString())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        note.setHeaderColor(headerColor);
        publisher.notify(note);

        Toast.makeText(getContext(), "Note Saved", Toast.LENGTH_SHORT).show();
//        Objects.requireNonNull(getActivity()).onBackPressed();
        getFragmentManager().popBackStackImmediate();
    }
}
