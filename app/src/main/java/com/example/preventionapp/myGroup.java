package com.example.preventionapp;
import java.util.ArrayList;

public class myGroup {
    private ArrayList<String> child;
    private String groupName;
    myGroup(String name){
        this.groupName = name;
        this.child = new ArrayList<String>();
    }

    public ArrayList<String> getChild() {
        return child;
    }

    public String getGroupName() {
        return groupName;
    }
}
