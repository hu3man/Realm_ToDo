package com.comp3617.assignment2;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.comp3617.assignment2.data.DbRepository;
import com.comp3617.assignment2.data.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;

public class TaskDetailActivity extends AppCompatActivity {

    //PK counter
    private int   nextPK;
    private Realm realm;
    private int   pk;

    //Task variables
    private String  taskDescription;
    private Date dueDate;
    private boolean isReminderOn;
    private boolean isNewTask = true;
    private int     prior = 0;

    private SimpleDateFormat formatter;

    //Display elements
    EditText descriptionET;
    EditText dueDateET;
    CheckBox reminderCB;
    SeekBar prioritySB;
    TextView priorityTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        setContentView(R.layout.activity_task_detail);

        formatter  = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

        //Assign UI Element references
        descriptionET = (EditText) findViewById(R.id.taskEditText);
        reminderCB    = (CheckBox) findViewById(R.id.checkBox);
        dueDateET     = (EditText) findViewById(R.id.dateEditText);
        prioritySB    = (SeekBar)  findViewById(R.id.prioritySeekBar);
        priorityTV    = (TextView) findViewById(R.id.priorityTextView);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        isReminderOn = preferences.getBoolean("remindersOn", false);
        if(isReminderOn){
            reminderCB.setChecked(true);
        }


        dueDateET.setInputType(InputType.TYPE_NULL);
        priorityTV.setText(R.string.High_priority_msg);
        prioritySB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int priority = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                priority = i+1;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                switch (priority){
                    case 1: priorityTV.setText(R.string.High_priority_msg);
                        break;
                    case 2: priorityTV.setText(R.string.Medium_priority_msg);
                        break;
                    case 3: priorityTV.setText(R.string.Low_priority_msg);
                        break;
                }
                prior = priority;
            }
        });

        //Load values if layout changed
        if(savedInstanceState != null){
            nextPK = savedInstanceState.getInt("nextPK");

        }else {
            nextPK = MyApplication.realmPKValue.incrementAndGet();
        }

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            int id = extras.getInt("id");
            isNewTask = false;

            Task taskData = DbRepository.getTaskById(realm, id);
            if (taskData != null) {
                this.pk = taskData.getId();
                this.dueDate = taskData.getDueDate();
                this.isReminderOn = taskData.isReminderON();
                this.taskDescription = taskData.getTaskData();

                descriptionET.setText(taskDescription);
                dueDateET.setText(dueDate.toString());
                if (isReminderOn) {
                    reminderCB.setChecked(true);
                } else {
                    reminderCB.setChecked(false);
                }
                prioritySB.setProgress(0); // call these two methods before setting progress.
                prioritySB.setMax(2);
                prioritySB.setProgress(prior);
            }

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong("nextPK", nextPK);
        super.onSaveInstanceState(outState);
    }

    protected void addTask(View v){
        taskDescription = descriptionET.getText().toString();
        isReminderOn = reminderCB.isChecked();

        //Create new task
        final Task newTask = new Task();
        newTask.setId(nextPK++);

        newTask.setReminderON(isReminderOn);
        if(dueDate == null){
            newTask.setDueDate(new Date());
        }
        else {
            newTask.setDueDate(dueDate);
        }
        newTask.setTaskData(taskDescription);
        newTask.setPriority(prior);

        //Save to tasklist - delete existing item if editing task
        if(!isNewTask) {
            DbRepository.deleteItemAsync(realm, pk);
        }
        DbRepository.addItemAsync(realm, newTask);


        //Set calendar event if specified
        if(isReminderOn){
            Calendar beginTime = Calendar.getInstance();
            if(dueDate != null) {
                beginTime.setTime(dueDate);
                beginTime.set(Calendar.HOUR_OF_DAY, 8);
            }
            else {
                beginTime.setTime(new Date());
                beginTime.set(Calendar.HOUR_OF_DAY, 8);
            }
            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                    .putExtra(CalendarContract.Events.TITLE, taskDescription);
            startActivity(intent);
        }

        Intent result = new Intent();
        setResult(MainActivity.ADD_ITEM_RC, result);
        finish();
    }

    @Override
    protected void onDestroy() {
        realm.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settingsonly_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.deleteItem:
                final int num = pk;
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.where(Task.class).equalTo("id", num).findAll().deleteAllFromRealm();
                    }
                });
                finish();
                break;

            case R.id.menu_item_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, taskDescription);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;

            case R.id.settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    public void onClickDate(View v){
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        String dateStr = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                        dueDateET.setText(dateStr);
                        try {
                            dueDate = formatter.parse(dateStr);
                        } catch (ParseException e) {
                            dueDate = null;
                        }
                    }
                }, year, month, day);
        datePickerDialog.show();
    }
}
