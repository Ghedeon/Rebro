package com.ghedeon.rebro.demo.model;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass
public class Company extends RealmObject {
    private Person owner;
    private String name;
    private Domain domain;

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }
}
