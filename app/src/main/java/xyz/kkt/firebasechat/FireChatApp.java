package xyz.kkt.firebasechat;

import android.app.Application;

import xyz.kkt.firebasechat.utils.ConfigUtils;

public class FireChatApp extends Application {

    public static final String LOG = "FireChat";

    private static ConfigUtils mConfigUtils;

    @Override
    public void onCreate() {
        super.onCreate();
        mConfigUtils = new ConfigUtils(getApplicationContext());
    }

    public static ConfigUtils getConfigUtils() {
        return mConfigUtils;
    }
}
