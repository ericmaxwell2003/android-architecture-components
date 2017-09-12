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

package com.example.android.persistence.db.dao;

import android.arch.lifecycle.LiveData;

import com.example.android.persistence.db.entity.ProductEntity;
import com.example.android.persistence.db.util.RealmModelLiveData;
import com.example.android.persistence.db.util.RealmResultsLiveData;

import java.util.List;

import javax.annotation.Nonnull;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class ProductDao {

    private Realm db;

    public ProductDao(Realm db) {
        this.db = db;
    }

    public LiveData<RealmResults<ProductEntity>> loadAllProducts() {
        return new RealmResultsLiveData<>(db.where(ProductEntity.class).findAllAsync());
    }

    public LiveData<ProductEntity> loadProduct(String productId) {
        return new RealmModelLiveData<>(byId(productId).findFirstAsync());
    }

    public ProductEntity loadProductSync(String productId) {
        return byId(productId).findFirst();
    }

    private RealmQuery<ProductEntity> byId(String id) {
        return db.where(ProductEntity.class).equalTo("id", id);
    }

    public void insertOrReplaceAll(final List<ProductEntity> products) {

        if(db.isInTransaction()) {
            db.insertOrUpdate(products);


        } else {
            db.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(@Nonnull Realm realm) {
                    realm.insertOrUpdate(products);
                }
            });
        }
    }

}
