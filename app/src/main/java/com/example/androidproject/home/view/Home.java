package com.example.androidproject.home.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.androidproject.add_medicine.add_medicine_view.AddMedicine;
import com.example.androidproject.add_tracker.view.RequestList;
import com.example.androidproject.add_tracker.view.AddTracker_Screen;
import com.example.androidproject.alarm_dialog.presenter.MyPeriodicWorker;
import com.example.androidproject.friend_list.FriendList;
import com.example.androidproject.local_data.LocalDataBase;
import com.example.androidproject.R;
import com.example.androidproject.home.presenter.HomePresenter;
import com.example.androidproject.home.presenter.HomePresenterInterface;
import com.example.androidproject.login.loginView.LoginScreen;
import com.example.androidproject.model.MedicineDose;
import com.example.androidproject.model.MedicineList;
import com.example.androidproject.model.RequestModel;
import com.example.androidproject.remote_data.AddTracker;
import com.example.androidproject.remote_data.AddTrackerInterface;
import com.example.androidproject.remote_data.MedicineDAO;
import com.example.androidproject.remote_data.RemoteSource;
import com.example.androidproject.repo.ListRepository;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import in.akshit.horizontalcalendar.HorizontalCalendarView;
import in.akshit.horizontalcalendar.Tools;

public class Home extends AppCompatActivity implements HomeInterface , NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private MedicineListAdapter medicineListAdapter;
    private ArrayList<MedicineDose> medicineArrayList = new ArrayList<>();
    private Handler handler;
    private HomePresenterInterface presenterInterface;
    private Intent intent;
    RemoteSource remote = new MedicineDAO();
    Calendar currentDate;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    AddTrackerInterface addTracker = new AddTracker();
    LoginScreen login = new LoginScreen();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String myEmail ;
    ArrayList<RequestModel> persons  ;
    ArrayList<RequestModel> friends = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkOverlayPermission();
        checkStoragePermission();

        if(user!=null){
            myEmail = user.getEmail();
        }
        currentDate = Calendar.getInstance();
        intent = getIntent();
        persons = new ArrayList<>();
        persons = addTracker.returnRequestArr();
        friends = addTracker.returnFriendsArr();
        setMyCalendar(findViewById(R.id.calendar));


        setMedicineListAdapter(findViewById(R.id.MyList));

        setDrawer(findViewById(R.id.toolbar), findViewById(R.id.drawer_layout));

        NavigationView navigationView= (NavigationView) findViewById(R.id.nvView);
        navigationView.setNavigationItemSelectedListener(this);

        presenterInterface = new HomePresenter(this, ListRepository.getInstance(this , LocalDataBase.getInstance(this)));

        handler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                medicineListAdapter.setMedicineList(medicineArrayList);
                medicineListAdapter.notifyDataSetChanged();
            }
        };
        Calendar today = Calendar.getInstance();
        presenterInterface.getMedicineList(new SimpleDateFormat("dd-MM-yyyy").format(today.getTime()));

        Log.i("TAG", "onCreate: Finished ");


        PeriodicWorkRequest periodicWork =
                new PeriodicWorkRequest.Builder(MyPeriodicWorker.class,50, TimeUnit.MINUTES )
                        .addTag("periodicWork")
                        .build();
        WorkManager workManager = WorkManager.getInstance();
        workManager.cancelAllWorkByTag("periodicWork");
        workManager.enqueue(periodicWork);

        addTracker.reqList(myEmail);
        addTracker.friendList(user.getUid());
        presenterInterface.updateTimes();

    }
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            Intent sendIntent = new Intent(this, LoginScreen.class);
            startActivity(sendIntent);

        }
    }

    @Override
    public void updateList(MedicineList medicineList) {
        if (medicineList != null) {
            medicineArrayList = medicineList.getMedicineDoseArrayList();

        }else{
            medicineArrayList = new ArrayList<>();
        }

        handler.sendEmptyMessage(0);
    }

    private void setMedicineListAdapter(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        this.recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        medicineListAdapter = new MedicineListAdapter(medicineArrayList);
        this.recyclerView.setLayoutManager(linearLayoutManager);
        this.recyclerView.setAdapter(medicineListAdapter);

    }

    private void setDrawer(Toolbar bar, DrawerLayout drawer) {


        Toolbar toolbar = bar;
        DrawerLayout drawerLayout = drawer;
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            public void onDrawerClosed(View view) {
                supportInvalidateOptionsMenu();
                //drawerOpened = false;
            }

            public void onDrawerOpened(View drawerView) {
                supportInvalidateOptionsMenu();
                //drawerOpened = true;
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerToggle.syncState();

    }

    public void addMedicine(View view) {
        Intent i = new Intent(this, AddMedicine.class);
        Toast.makeText(this, "new Med Added ", Toast.LENGTH_SHORT).show();
        startActivity(i);

    }

    private void setMyCalendar(View myCalendar) {

        HorizontalCalendarView calendarView = (HorizontalCalendarView) myCalendar;

        Calendar startTime = Calendar.getInstance();
        startTime.add(Calendar.MONTH, -6);

        Calendar endTime = Calendar.getInstance();
        endTime.add(Calendar.MONTH, 6);

        ArrayList datesToBeColored = new ArrayList();
        datesToBeColored.add(Tools.getFormattedDateToday());

        calendarView.setUpCalendar(startTime.getTimeInMillis(),
                endTime.getTimeInMillis(),
                datesToBeColored,
                new HorizontalCalendarView.OnCalendarListener() {
                    @Override
                    public void onDateSelected(String date) {
                        dateSelected(date);
                    }
                });

    }

    private void dateSelected(String date) {
        Toast.makeText(this, "" + date, Toast.LENGTH_SHORT).show();
        presenterInterface.getMedicineList(date);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    public void checkOverlayPermission(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!Settings.canDrawOverlays(this)) {
                // send user to the device settings
                Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivity(myIntent);
            }
        }
    }

    public void checkStoragePermission(){

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED|| checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},5);

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant that should be quite unique

            return;
        }
    }
    String toString(Calendar calendar) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        return sdf.format(calendar.getTime());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.addTracker: {
                Intent sendIntent = new Intent(this, AddTracker_Screen.class);
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra("SenderEmail", user.getEmail());
                sendIntent.putExtra("SenderID",user.getUid());
                startActivity(sendIntent);
                break;
            }
            case  R.id.reqList:{
                Intent reqIntent = new Intent(this, RequestList.class);
                reqIntent.putExtra("ARRAYLIST", persons);
                startActivity(reqIntent);
                break;
            }
            case  R.id.trackersList:{
                Intent friendIntent = new Intent(this, FriendList.class);
                friendIntent.putExtra("ARRAYLISTFRIENDS", friends);
                startActivity(friendIntent);
                break;
            }
            case R.id.logout:{
                SharedPreferences data = getSharedPreferences("LoginStatus", MODE_PRIVATE);

                data.edit().putBoolean("LoggedIn", false).commit();
                FirebaseAuth.getInstance().signOut();
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(this,"hi ",Toast.LENGTH_SHORT).show();
                finishAffinity();
                break;
            }
        }
        return true;
    }
}