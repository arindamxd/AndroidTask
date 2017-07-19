package com.arindam.androidtask;

/**
 * Created by Arindam Karmakar on 19/07/2017.
 */

public class Category {

    private String name;

    public Category(String s, String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
