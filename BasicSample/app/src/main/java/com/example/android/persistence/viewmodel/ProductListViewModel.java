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

package com.example.android.persistence.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.example.android.persistence.db.DatabaseCreator;
import com.example.android.persistence.db.entity.ProductEntity;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

import static com.example.android.persistence.db.util.RealmUtil.productDao;

public class ProductListViewModel extends ViewModel {

    private Realm database;

    private static final MutableLiveData ABSENT = new MutableLiveData();

    static {
        //noinspection unchecked
        ABSENT.setValue(null);
    }

    private final LiveData<List<ProductEntity>> mObservableProducts;

    public ProductListViewModel() {
        database = Realm.getDefaultInstance();

        final DatabaseCreator databaseCreator = DatabaseCreator.getInstance();

        LiveData<Boolean> databaseCreated = databaseCreator.isDatabaseCreated();

        mObservableProducts = Transformations.switchMap(databaseCreated,
                isDbCreated -> {
                    // Not needed here, but watch out for null
                    if (!Boolean.TRUE.equals(isDbCreated)) {
                        //noinspection unchecked
                        return ABSENT;
                    } else {
                        return Transformations.map(
                                productDao(database).loadAllProducts(), ArrayList::new);
                    }
                });

        databaseCreator.createDb();
    }

    /**
     * Expose the LiveData Products query so the UI can observe it.
     */
    public LiveData<List<ProductEntity>> getProducts() {
        return mObservableProducts;
    }
}
