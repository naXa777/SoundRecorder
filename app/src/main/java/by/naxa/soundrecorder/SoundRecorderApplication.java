package by.naxa.soundrecorder;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.squareup.leakcanary.LeakCanary;

import io.fabric.sdk.android.Fabric;

public class SoundRecorderApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        final CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(!BuildConfig.USE_CRASHLYTICS).build();
        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics.Builder().core(core).build())
                .debuggable(true)
                .build();
        Fabric.with(this, new Crashlytics());
    }

}
