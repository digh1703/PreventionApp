package com.example.preventionapp;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//전반적인 앱의 정보를 저장
public class AppInfo {
    private  Context context;
    private  FirebaseAuth mAuth;
    private  FirebaseUser user;
    private  User userData;

    private static AppInfo appInfo = null;
    private AppInfo() { }

    public static AppInfo getAppInfo(){
        if (appInfo == null) {
            appInfo = new AppInfo();
        }
        return appInfo;
    }
    public void setContext(Context context){
        this.context = context;
    }

    public Context getAppInfoContext() {
        return this.context;
    }

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public void setmAuth(FirebaseAuth mAuth) {
        this.mAuth = mAuth;
    }

    public boolean isAuthorized() {
        if(mAuth.getCurrentUser() == null){
            return false;
        }
        else{
            return true;
        }
    }

    public FirebaseUser getUser() {
        return user;
    }

    public void setUser(FirebaseUser user) {
        this.user = user;
    }

    public User getUserData() {
        return userData;
    }

    public void setUserData(User userData) {
        this.userData = userData;
    }

    public void clear(){
        this.context = null;
        this.user = null;
        this.userData = null;
    }
}
