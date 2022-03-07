package com.example.androidproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import in.akshit.horizontalcalendar.HorizontalCalendarView;
import in.akshit.horizontalcalendar.Tools;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private MedicineListAdapter medicineListAdapter;

    ArrayList<Medicine> medicineArrayList = new ArrayList<>();

    HashMap<String,ArrayList<Medicine>> map = new HashMap<>();
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setMyCalendar(findViewById(R.id.calendar));

        fab = findViewById(R.id.fab);

        recyclerView = findViewById(R.id.MyList);

        ArrayList<Medicine> list =new ArrayList<>();
        map.put("l1",list);
        
        recyclerView = findViewById(R.id.MyList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        medicineListAdapter = new MedicineListAdapter(this,medicineArrayList);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(medicineListAdapter);




    }

   public void addMedicine(View view){

//       Medicine medicine = new Medicine();
//       medicine.setName("name ");
//       medicineArrayList.add(medicine);
//       medicineListAdapter.notifyDataSetChanged();
//
//       Toast.makeText(this, "new Med Added ", Toast.LENGTH_SHORT).show();
       // Intent out = new Intent(getApplicationContext(),MedicineReasonRecurrencyActivity.class);
      // startActivity(out);


    }





    void setMyCalendar(View myCalendar){

        HorizontalCalendarView calendarView = (HorizontalCalendarView) myCalendar;

        Calendar starttime = Calendar.getInstance();
        starttime.add(Calendar.MONTH,-6);

        Calendar endtime = Calendar.getInstance();
        endtime.add(Calendar.MONTH,6);

        ArrayList datesToBeColored = new ArrayList();
        datesToBeColored.add(Tools.getFormattedDateToday());

        calendarView.setUpCalendar(starttime.getTimeInMillis(),
                endtime.getTimeInMillis(),
                datesToBeColored,
                new HorizontalCalendarView.OnCalendarListener() {
                    @Override
                    public void onDateSelected(String date) {
                        dateSelected(date);
                    }
                });
    }


    void dateSelected(String date){
        Toast.makeText(this, ""+date, Toast.LENGTH_SHORT).show();
        medicineListAdapter.notifyDataSetChanged();

    }


}