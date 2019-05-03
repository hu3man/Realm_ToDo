package com.comp3617.assignment2.data;

import io.realm.Realm;

/**
 * Created by houstonkrohman on 2017-11-17.
 */

public class DbRepository {

    public static void addItemAsync(Realm realm, final Task task) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(task);
            }
        });
    }

    public static Task getTaskById(Realm realm, final int id){
        Task task;
        realm.beginTransaction();
        task = realm.where(Task.class).equalTo("id", id).findFirst();
        realm.commitTransaction();
        return task;
    }

    public static void deleteItemAsync(Realm realm, final int id) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Task.delete(realm, id);
            }
        });
    }
}
