package com.example.androidproject.add_tracker.view;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidproject.R;
import com.example.androidproject.model.RequestModel;
import com.example.androidproject.remote_data.AddTracker;
import com.example.androidproject.remote_data.AddTrackerInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class RequestList extends AppCompatActivity  {
   // ArrayList<RequestModel> persons  ;
    AddTrackerInterface addTracker = new AddTracker();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ArrayAdapter<String> adapter;
    ListView lstPersons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        Intent intent = getIntent();
        ArrayList<RequestModel> persons = (ArrayList<RequestModel>)getIntent().getSerializableExtra("ARRAYLIST");
        lstPersons = findViewById(R.id.listview);
        for(int i=0;i<persons.size();i++) {
            ReqListAdapter adapter = new ReqListAdapter(this, R.layout.reqlist_item, R.id.senderEmail, persons);
            lstPersons.setAdapter(adapter);
            lstPersons.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Toast.makeText(RequestList.this, adapterView.getItemAtPosition(i).toString(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
    }
}