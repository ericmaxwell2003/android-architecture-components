package com.example.android.persistence.db.util;


import android.arch.lifecycle.LiveData;

import javax.annotation.Nonnull;

import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import io.realm.RealmObject;

public class RealmModelLiveData <T extends RealmModel> extends LiveData<T> {

    private T result;

    private final RealmChangeListener<T> listener = new RealmChangeListener<T>() {

        @Override
        public void onChange(@Nonnull T result) {
            setValue(result);
        }

    };

    public RealmModelLiveData(T realmResult) {
        result = realmResult;
    }

    @Override
    protected void onActive() {
        RealmObject.addChangeListener(result, listener);
    }

    @Override
    protected void onInactive() {
        RealmObject.removeChangeListener(result, listener);
    }

}