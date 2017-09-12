package com.example.android.persistence.db.util;

import com.example.android.persistence.db.dao.CommentDao;
import com.example.android.persistence.db.dao.ProductDao;

import io.realm.Realm;

public class RealmUtil {

    public static ProductDao productDao(Realm realm) {
        return new ProductDao(realm);
    }

    public static CommentDao commentDao(Realm realm) {
        return new CommentDao(realm);
    }

}
