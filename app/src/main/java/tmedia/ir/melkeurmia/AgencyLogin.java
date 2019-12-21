package tmedia.ir.melkeurmia;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.ion.Ion;

import java.io.UnsupportedEncodingException;

import es.dmoral.toasty.Toasty;
import tmedia.ir.melkeurmia.tools.AppSharedPref;
import tmedia.ir.melkeurmia.tools.CONST;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class AgencyLogin extends AppCompatActivity {

    private RelativeLayout gavanin_box;
    private LinearLayout form_container;
    private Context context;
    private ProgressDialog progressDialog;
    private Button btnLogin, active_btn;
    private EditText agency_email_txt, agency_pass_txt;
    private String mobile_str, codemmeli_str = "";
    private LinearLayout rootView;
    private String android_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agency_login);
        AppSharedPref.init(context);

        context = this;
        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("در حال بارگذاری");
        progressDialog.setCancelable(false);
        gavanin_box = (RelativeLayout) findViewById(R.id.gavanin_box);
        rootView = (LinearLayout) findViewById(R.id.rootView);
        form_container = (LinearLayout) findViewById(R.id.form_container);
        active_btn = (Button) findViewById(R.id.active_btn);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        agency_email_txt = (EditText) findViewById(R.id.agency_email_txt);
        agency_pass_txt = (EditText) findViewById(R.id.agency_pass_txt);


        form_container.setVisibility(View.VISIBLE);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkForm();
            }
        });

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.fontpath))
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    private void checkForm() {
        if (checkEmail()) {
            if (checkPassword()) {
                loginUser();
            }
        }
    }

    private boolean checkPassword() {
        String target = agency_pass_txt.getText().toString().trim();
        if (target.length() >= 3) {
            return true;
        } else {
            agency_pass_txt.setError("لطفا رمز عبور خود را وارد کنید");
            return  false;
        }
    }

    private boolean checkEmail() {
        String target = agency_email_txt.getText().toString().trim();
        if(android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()){
            return true;
        }else{
            agency_email_txt.setError("لطفا ایمیل خود را بطور صحیح وارد کنید.");
            return false;
        }
    }


    private void loginUser() {

        
        progressDialog.show();
        Ion.with(context)
                .load(CONST.AGENCY_LOGIN)
                .setBodyParameter("email", agency_email_txt.getText().toString().trim())
                .setBodyParameter("password", agency_pass_txt.getText().toString().trim())
                .asString()
                .setCallback((e, result) -> {
                    progressDialog.dismiss();

                    if (e == null) {
                        JsonParser parser = new JsonParser();
                        JsonObject json_obj = parser.parse(result).getAsJsonObject();

                        if(json_obj.has("info")){
                            if(json_obj.get("info").getAsString().equals("ok")){
                                byte[] username_bytes = new byte[0];
                                try {
                                    username_bytes = json_obj.get("modir").getAsJsonObject().get("email").getAsString().getBytes("UTF-8");
                                    int user_id = json_obj.get("modir").getAsJsonObject().get("id").getAsInt();
                                    AppSharedPref.write("ID", String.valueOf(user_id));
                                    AppSharedPref.write("TOKEN", Base64.encodeToString(username_bytes, Base64.DEFAULT));
                                    AppSharedPref.write("IS_MODIR", true);
                                    AppSharedPref.write("MODIR_NAME", json_obj.get("modir").getAsJsonObject().get("client_name").getAsString());
                                    AppSharedPref.write("MODIR_PASS", agency_pass_txt.getText().toString().trim());
                                    AppSharedPref.write("online",1);



                                    CONST.restartApp(AgencyLogin.this);

                                    finish();
                                } catch (UnsupportedEncodingException e1) {
                                    e1.printStackTrace();
                                }





                            }else{
                                if(json_obj.get("cause").getAsString().equals("password_incorrect")){
                                    Toasty.error(context, "رمز عبور وارد شده صحبح نمی باشد.", Toast.LENGTH_LONG, false).show();
                                }else{
                                    Toasty.error(context, "کاربری با این مشخصات وجود ندارد.", Toast.LENGTH_LONG, false).show();
                                }
                            }
                        }else{
                            Toasty.error(context, "خطا در برقراری ارتباط با سرور", Toast.LENGTH_LONG, false).show();
                        }
                    } else {
                        Toasty.error(context, "خطا در برقراری ارتباط با سرور", Toast.LENGTH_LONG, false).show();
                    }
                });

    }


}
