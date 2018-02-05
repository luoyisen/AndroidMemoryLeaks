package x.wsxf.androidmemoryleaks;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by HHX on 2018/2/5.
 */

public class MyApp extends Application {
    private static MyApp instance;
    private RefWatcher refWatcher;

    public static RefWatcher getRefWatcher() {
        return getInstance().refWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        refWatcher = LeakCanary.install(this);
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

    }

    public static MyApp getInstance() {
        return instance;
    }
}
