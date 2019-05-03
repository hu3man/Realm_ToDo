package com.comp3617.assignment2;

import android.app.Application;

import com.comp3617.assignment2.data.Task;

import java.util.concurrent.atomic.AtomicInteger;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by houstonkrohman on 2017-11-09.
 */

public class MyApplication extends Application {
    public static AtomicInteger realmPKValue;

    @Override
    public void onCreate() {
        super.onCreate();

        //Initialize Realm
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        Realm realm = Realm.getDefaultInstance();

        //Set realmPKValue to the value of the highest id+1 - Realm does not support auto-increment PK
        try {
            realmPKValue = new AtomicInteger(realm.where(Task.class).max("id").intValue());
        }
        catch (Exception e){
            realmPKValue = new AtomicInteger(0);
        }
        realm.close();
    }

}
