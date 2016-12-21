package com.ghedeon.rebro.demo.model;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass
public class Domain extends RealmObject {
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
