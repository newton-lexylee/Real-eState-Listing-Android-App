package tmedia.ir.melkeurmia.tools;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.Date;

public class GetNewOrderBadge extends IntentService {

    private int result = Activity.RESULT_CANCELED;
    public static final String URL = "urlpath";
    public static final String FILENAME = "filename";
    public static final String FILEPATH = "filepath";
    public static final String RESULT = "result";
    public static final String NOTIFICATION = "com.vogella.android.service.receiver";

    public GetNewOrderBadge() {
        super("DownloadService");
    }

    // will be called asynchronously by Android
    @Override
    protected void onHandleIntent(Intent intent) {
        java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        Ion.with(getApplicationContext())
                .load(CONST.GET_NEW_ORDER)
                .setBodyParameter("date",dateFormat.format(date))
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if(e == null){
                            publishResults(result);
                        }
                    }
                });
    }

    private void publishResults(String result) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }
}