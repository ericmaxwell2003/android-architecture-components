package com.example.android.persistence.db.util;

import android.arch.lifecycle.LiveData;

import javax.annotation.Nonnull;

import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import io.realm.RealmResults;

public class RealmResultsLiveData<T extends RealmModel> extends LiveData<RealmResults<T>> {

    private RealmResults<T> results;

    private final RealmChangeListener<RealmResults<T>> listener = new RealmChangeListener<RealmResults<T>>() {

        @Override
        public void onChange(@Nonnull RealmResults<T> results) {
            setValue(results);
        }

    };

    public RealmResultsLiveData(RealmResults<T> realmResults) {
        results = realmResults;
    }

    @Override
    protected void onActive() {
        results.addChangeListener(listener);
    }

    @Override
    protected void onInactive() {
        results.removeChangeListener(listener);
    }


}
