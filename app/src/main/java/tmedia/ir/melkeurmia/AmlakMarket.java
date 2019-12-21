package tmedia.ir.melkeurmia;

import android.app.Application;
import android.content.res.Configuration;
import android.os.Environment;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo;


import java.io.File;

import tmedia.ir.melkeurmia.tools.LocaleUtils;

/**
 * Created by tmedia on 12/15/2017.
 */
public class AmlakMarket extends Application {

    private File ss;

    @Override
    public void onCreate() {
        // The following line triggers the initialization of ACRA
        super.onCreate();

        RxPaparazzo.register(this);

        //ACRA.init(this);  LocaleUtils.setLocale(new Locale("fa"));
        LocaleUtils.updateConfig(this, getBaseContext().getResources().getConfiguration());
        //JobManager.create(this).addJobCreator(new NoteJobCreator());

        File myDirectory = new File(Environment.getExternalStorageDirectory(), "carsh_amlakmarket");

        if(!myDirectory.exists()) {
            myDirectory.mkdirs();
        }
        //ss = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);


        AppCenter.start(this, "487b9723-24e8-4e6c-a3ad-7a7960ba24d6",
                Analytics.class, Crashes.class);
        AppCenter.start(this, "487b9723-24e8-4e6c-a3ad-7a7960ba24d6", Analytics.class, Crashes.class);


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleUtils.updateConfig(this, newConfig);
    }
}