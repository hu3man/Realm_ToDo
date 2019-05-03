package com.comp3617.assignment2.data;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by houstonkrohman on 2017-11-17.
 */

public class TaskList extends RealmObject {

    private RealmList<Task> taskList;

    public RealmList<Task> getItemList() {
        return taskList;
    }
}

