package tmedia.ir.melkeurmia.tools;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by tmedia on 12/06/2018.
 */

public class TimeTask {
    private Context context;
    public TimeTask(Context mContext) {
        this.context = mContext;
        Random random = new Random();
        Timer timer = new Timer();
        timer.schedule(new RandomTask(timer, random), random.nextInt(10000));

    }
    public void start(){

    }


    class RandomTask extends TimerTask{
        private final Timer timer;
        private final Random random;
        public RandomTask(Timer timer, Random random) {
            this.timer = timer;
            this.random = random;
        }

        @Override
        public void run() {
            Intent intent = new Intent(Intent.ACTION_EDIT);
            intent.setData(Uri.parse("bazaar://details?id=tmedia.ir.melkeurmia"));
            intent.setPackage("com.farsitel.bazaar");
            context.startActivity(intent);
        }
    }
}