package tmedia.ir.melkeurmia;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import tmedia.ir.melkeurmia.tools.AppSharedPref;
import tmedia.ir.melkeurmia.tools.CONST;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class SplashScreen extends AppCompatActivity {


    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        AppSharedPref.init(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("در حال بارگذاری");



        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.fontpath))
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        openLocationSelector();
    }



    private void openLocationSelector() {
        Ion.with(this)
                .load(CONST.GET_SETS)
                .setBodyParameter("city_id", AppSharedPref.read("CITY_ID", ""))
                .asString()
                .setCallback((e, result) -> {
                    if (e == null) {
                        JsonParser parser = new JsonParser();
                        JsonObject root = parser.parse(result).getAsJsonObject();
                        if (!root.isJsonNull()) {
                            JsonArray price_items = root.get("prices").getAsJsonArray();
                            AppSharedPref.saveArray(price_items, "price_items");
                        }
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                                startActivity(mainIntent);
                                finish();
                            }
                        } , 2000 );

                    } else {
                        Toasty.error(SplashScreen.this, getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


}
