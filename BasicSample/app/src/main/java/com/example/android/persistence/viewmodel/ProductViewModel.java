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
import android.arch.lifecycle.ViewModelProvider;
import android.databinding.ObservableField;

import com.example.android.persistence.db.DatabaseCreator;
import com.example.android.persistence.model.Comment;
import com.example.android.persistence.model.Product;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

import static com.example.android.persistence.db.util.RealmUtil.commentDao;
import static com.example.android.persistence.db.util.RealmUtil.productDao;

public class ProductViewModel extends ViewModel {

    private Realm database;

    private static final MutableLiveData ABSENT = new MutableLiveData();

    static {
        //noinspection unchecked
        ABSENT.setValue(null);
    }

    private final LiveData<? extends Product> mObservableProduct;

    public ObservableField<Product> product = new ObservableField<>();

    private final String mProductId;

    private final LiveData<List<? extends Comment>> mObservableComments;

    public ProductViewModel(final String productId) {

        database = Realm.getDefaultInstance();

        mProductId = productId;

        final DatabaseCreator databaseCreator = DatabaseCreator.getInstance();

        mObservableComments = Transformations.switchMap(databaseCreator.isDatabaseCreated(),
                isDbCreated -> {
                    if (!isDbCreated) {
                        //noinspection unchecked
                        return ABSENT;

                    } else {
                        //noinspection ConstantConditions
                        return Transformations.map(
                                commentDao(database).loadComments(mProductId), ArrayList::new);
                    }
        });

        mObservableProduct = Transformations.switchMap(databaseCreator.isDatabaseCreated(),
                isDbCreated -> {
                    if (!isDbCreated) {
                        //noinspection unchecked
                        return ABSENT;
                    } else {
                        //noinspection ConstantConditions
                        return productDao(database).loadProduct(mProductId);
                    }
        });

        databaseCreator.createDb();

    }
    /**
     * Expose the LiveData Comments query so the UI can observe it.
     */
    public LiveData<List<? extends Comment>> getComments() {
        return mObservableComments;
    }

    public LiveData<? extends Product> getObservableProduct() {
        return mObservableProduct;
    }

    public void setProduct(Product product) {
        this.product.set(product);
    }

    /**
     * A creator is used to inject the product ID into the ViewModel
     * <p>
     * This creator is to showcase how to inject dependencies into ViewModels. It's not
     * actually necessary in this case, as the product ID can be passed in a public method.
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        private final String mProductId;

        public Factory(String productId) {
            mProductId = productId;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new ProductViewModel(mProductId);
        }
    }
}
