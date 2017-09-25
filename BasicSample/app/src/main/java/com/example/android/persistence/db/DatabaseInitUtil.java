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

import com.example.android.persistence.db.entity.CommentEntity;
import com.example.android.persistence.db.entity.ProductEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;

import static com.example.android.persistence.db.util.RealmUtil.commentDao;
import static com.example.android.persistence.db.util.RealmUtil.productDao;

/** Generates dummy data and inserts them into the database */
class DatabaseInitUtil {

    private static final String[] FIRST = new String[]{
            "Special edition", "New", "Cheap", "Quality", "Used"};
    private static final String[] SECOND = new String[]{
            "Three-headed Monkey", "Rubber Chicken", "Pint of Grog", "Monocle"};
    private static final String[] DESCRIPTION = new String[]{
            "is finally here", "is recommended by Stan S. Stanman",
            "is the best sold product on Mêlée Island", "is \uD83D\uDCAF", "is ❤️", "is fine"};
    private static final String[] COMMENTS = new String[]{
            "Comment 1", "Comment 2", "Comment 3", "Comment 4", "Comment 5", "Comment 6",
    };

    static void initializeDb(Realm db) {
        List<ProductEntity> products = new ArrayList<>(FIRST.length * SECOND.length);
        List<CommentEntity> comments = new ArrayList<>();

        generateData(products, comments);

        insertData(db, products, comments);
    }

    private static void generateData(List<ProductEntity> products, List<CommentEntity> comments) {
        Random rnd = new Random();
        for (int i = 0; i < FIRST.length; i++) {
            for (int j = 0; j < SECOND.length; j++) {
                ProductEntity product = new ProductEntity();
                product.setName(FIRST[i] + " " + SECOND[j]);
                product.setDescription(product.getName() + " " + DESCRIPTION[j]);
                product.setPrice(rnd.nextInt(240));
                product.setId(UUID.randomUUID().toString());
                products.add(product);
            }
        }

        for (ProductEntity product : products) {
            int commentsNumber = rnd.nextInt(5) + 1;
            for (int i = 0; i < commentsNumber; i++) {
                CommentEntity comment = new CommentEntity();
                comment.setId(UUID.randomUUID().toString());
                comment.setProduct(product);
                comment.setText(COMMENTS[i] + " for " + product.getName());
                comment.setPostedAt(new Date(System.currentTimeMillis()
                        - TimeUnit.DAYS.toMillis(commentsNumber - i) + TimeUnit.HOURS.toMillis(i)));
                comments.add(comment);
            }
        }
    }

    private static void insertData(Realm db, List<ProductEntity> products, List<CommentEntity> comments) {
        db.beginTransaction();
        try {
            productDao(db).insertOrReplaceAll(products);
            commentDao(db).insertOrReplaceAll(comments);
            db.commitTransaction();
        } catch (Throwable t){
            db.cancelTransaction();
        }
    }
}
