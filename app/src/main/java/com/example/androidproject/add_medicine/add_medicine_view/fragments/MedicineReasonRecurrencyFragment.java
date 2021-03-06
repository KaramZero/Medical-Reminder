package com.example.androidproject.add_medicine.add_medicine_view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.androidproject.R;
import com.example.androidproject.SplashActivity;
import com.example.androidproject.add_medicine.add_medicine_view.AddMedicineFragmentsCommunicator;

public class MedicineReasonRecurrencyFragment extends Fragment {
    View view;
    AddMedicineFragmentsCommunicator communicator;
    Button next;
    EditText reason;

    public MedicineReasonRecurrencyFragment() {
    }

    public MedicineReasonRecurrencyFragment(AddMedicineFragmentsCommunicator communicator) {
        this.communicator = communicator;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reason_recurrency, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


//        AutoCompleteTextView medAutoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.MedicineReasonEditText);
//        ArrayAdapter<String> medArrayAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.auto_complete_list_items, SplashActivity.diseases);
//        medAutoCompleteTextView.setThreshold(1);
//        medAutoCompleteTextView.setAdapter(medArrayAdapter);
        reason = view.findViewById(R.id.MedicineReasonEditText);
        next = view.findViewById(R.id.NextReasonRecurrencyBtn);

        reason = view.findViewById(R.id.MedicineReasonEditText);

        next.setOnClickListener((view1) ->
        {
            communicator.setReasonOfTakingDrug(reason.getText().toString());
            communicator.nextFragment();
        });
    }
}
