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

import com.example.android.persistence.db.entity.CommentEntity;
import com.example.android.persistence.db.util.RealmResultsLiveData;

import java.util.List;

import javax.annotation.Nonnull;

import io.realm.Realm;
import io.realm.RealmResults;

public class CommentDao {

    private Realm db;

    public CommentDao(Realm db) {
        this.db = db;
    }

    public LiveData<RealmResults<CommentEntity>> loadComments(String productId) {
        return new RealmResultsLiveData<>(db
                .where(CommentEntity.class)
                .equalTo("product.id", productId)
                .findAllAsync());
    }
//
//    @Query("SELECT * FROM comments where productId = :productId")
//    List<CommentEntity> loadCommentsSync(int productId);

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    void insertAll(List<CommentEntity> products);

    public void insertOrReplaceAll(final List<CommentEntity> comments) {

        if(db.isInTransaction()) {
            db.insertOrUpdate(comments);


        } else {
            db.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(@Nonnull Realm realm) {
                    realm.insertOrUpdate(comments);
                }
            });
        }
    }

}
