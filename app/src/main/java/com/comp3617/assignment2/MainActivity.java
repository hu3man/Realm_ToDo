package com.comp3617.assignment2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.comp3617.assignment2.data.Task;

import io.realm.Realm;
import io.realm.RealmResults;

import static android.widget.AdapterView.OnItemClickListener;
import static android.widget.AdapterView.OnItemLongClickListener;

public class MainActivity extends AppCompatActivity {
    public static final int ADD_ITEM_RC = 33;
    public static final int SETTINGS_RC = 77;

    RealmResults<Task> taskList;
    ListView           taskListView;
    TaskAdapter        adapter;
    Realm              realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        realm = Realm.getDefaultInstance();
        taskList = realm.where(Task.class).findAll();
        adapter = new TaskAdapter(taskList);

        taskListView = (ListView) findViewById(R.id.listView);
        taskListView.setAdapter(adapter);

        //Click on item to edit
        taskListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Task task = adapter.getItem(i);
                if (task == null) {
                    return;
                }

                Intent editIntent = new Intent(getApplicationContext(), TaskDetailActivity.class);
                editIntent.putExtra("id", task.getId());
                startActivity(editIntent);
            }
        });

        //Long click on item to delete
        taskListView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Task task = adapter.getItem(i);
                if (task == null) {
                    return true;
                }

                //Show alert dialog
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setTitle("Confirm Delete");
                alertDialogBuilder
                        .setMessage("Click delete to remove task!")
                        .setCancelable(false)
                        .setPositiveButton("Delete",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                final int pk = task.getId();
                                realm.executeTransactionAsync(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        realm.where(Task.class).equalTo("id", pk).findAll().deleteAllFromRealm();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        return true;
    }

    private void getAllTasks(){ }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SETTINGS_RC) {
            Toast.makeText(this, "Settings updated", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.addItem:
                intent = new Intent(this, TaskDetailActivity.class);
                startActivityForResult(intent, ADD_ITEM_RC);
                break;
            case R.id.settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}
