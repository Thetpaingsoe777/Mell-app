package com.xavey.android;

import android.app.Application;

import java.util.Properties;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * Created by hnin on 5/25/15.
 */
public class XaveyApp extends Application {

    @Override
    public void onCreate(){
        super.onCreate();

        Fabric.with(this,new Crashlytics());
    }
}
