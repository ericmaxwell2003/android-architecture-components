/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.persistence.db;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import io.realm.Realm;

/**
 * Creates the Realm Database asynchronously, exposing a LiveData object to notify of creation.
 */
public class DatabaseCreator {

    private static DatabaseCreator sInstance;

    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

    private final AtomicBoolean mInitializing = new AtomicBoolean(true);

    // For Singleton instantiation
    private static final Object LOCK = new Object();

    public synchronized static DatabaseCreator getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {
                    sInstance = new DatabaseCreator();
                }
            }
        }
        return sInstance;
    }

    /** Used to observe when the database initialization is done */
    public LiveData<Boolean> isDatabaseCreated() {
        return mIsDatabaseCreated;
    }

    /**
     * Creates or returns a previously-created database.
     * <p>
     * Although this uses an AsyncTask which currently uses a serial executor, it's thread-safe.
     */
    public void createDb() {

        Log.d("DatabaseCreator", "Creating DB from " + Thread.currentThread().getName());

        if (!mInitializing.compareAndSet(true, false)) {
            return; // Already initializing
        }

        mIsDatabaseCreated.setValue(false);// Trigger an update to show a loading screen.
        createDatabaseTask.execute();
    }

    private static CreateDatabaseTask createDatabaseTask = new CreateDatabaseTask();

    private static class CreateDatabaseTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("DatabaseCreator",
                    "Starting bg job " + Thread.currentThread().getName());

            Realm db = Realm.getDefaultInstance();

            // Reset the database to have new data on every run.
            db.beginTransaction();
            db.deleteAll();
            db.commitTransaction();

            // Add a delay to simulate a long-running operation
            addDelay();

            // Add some data to the database
            DatabaseInitUtil.initializeDb(db);
            Log.d("DatabaseCreator",
                    "DB was populated in thread " + Thread.currentThread().getName());

            return null;
        }

        private void addDelay() {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException ignored) {}
        }

        @Override
        protected void onPostExecute(Void ignored) {
            // Now on the main thread, notify observers that the db is created and ready.
            DatabaseCreator.getInstance().mIsDatabaseCreated.setValue(true);
        }
    };
}
