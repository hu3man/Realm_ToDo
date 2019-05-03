package com.comp3617.assignment2.data;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by houstonkrohman on 2017-11-07.
 */

public class Task extends RealmObject{
    @PrimaryKey
    private int     id;
    private String  taskData;
    private Date    dueDate;
    private boolean isReminderON;
    private int     priority = 0;

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTaskData() { return taskData; }
    public void setTaskData(String taskData) {
        this.taskData = taskData;
    }
    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
    public boolean isReminderON() {
        return isReminderON;
    }
    public void setReminderON(boolean reminderON) {
        isReminderON = reminderON;
    }

    static void delete(Realm realm, int id) {
        Task item = realm.where(Task.class).equalTo("id", id).findFirst();
        if (item != null) {
            item.deleteFromRealm();
        }
    }

}
