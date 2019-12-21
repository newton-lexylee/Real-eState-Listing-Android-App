package tmedia.ir.melkeurmia.tools;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gohn.nativedialog.ButtonType;
import com.gohn.nativedialog.NDialog;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import ir.hamsaa.RtlMaterialSpinner;
import tmedia.ir.melkeurmia.R;
import tmedia.ir.melkeurmia.SplashScreen;
import tmedia.ir.melkeurmia.adapters.SpinnerAdapter;
import tmedia.ir.melkeurmia.adapters.SpinnerCityAdapter;
import tmedia.ir.melkeurmia.model.Brand;
import tmedia.ir.melkeurmia.model.CityModel;
import tmedia.ir.melkeurmia.model.ProviceModel;
import tmedia.ir.melkeurmia.otto.AppEvents;
import tmedia.ir.melkeurmia.otto.GlobalBus;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by tmedia on 9/14/2017.
 */

public class CONST {
    //public static final String BASE_URL = "http://melkeurmia.com";
    //public static final String BASE_URL = "http://10.0.3.2:8000";
    public static final String BASE_URL = "http://melkeurmia.com";
    public static final String STORAGE = BASE_URL + "/uploads/";
    public static final int SPLASH_TIME = 3000;
    public static final int TOAST_TIME = 5000;
    public static boolean IS_HELP = false;
    public static String APP_LOG = "APP_LOG";
    public static String GET_ORDERS_ADS = BASE_URL + "/api/vr1/get_orders";
    public static String GET_ORDERS = BASE_URL + "/api/vr1/get_orders";
    public static String GET_ORDERS_WITH_ID = BASE_URL + "/api/vr1/getOrdersByCat";
    public static String GET_ORDER_EDIT = BASE_URL + "/api/vr1/get_order_edit";
    public static final String APP_TOKEN = BASE_URL + "/api/vr1/get_token";
    public static final String MODIR_APP_TOKEN = BASE_URL + "/api/vr1/get_modir_token";
    public static final String USER_REGISTER = BASE_URL + "/api/vr1/register_user";
    public static final String USER_LOGIN = BASE_URL + "/api/vr1/login_user";
    public static final String AGENCY_LOGIN = BASE_URL + "/api/vr1/login_agency";
    public static final String ACTIVE_USER = BASE_URL + "/api/vr1/active_user";
    public static final String CATEGORIES_URL = BASE_URL + "/api/vr1/get_categories";
    public static final String GET_ORDER = BASE_URL + "/api/vr1/get_order";
    public static final String GET_ORDER_WITH_STATE = BASE_URL + "/api/vr1/get_order_states";
    public static final String ADD_STATE = BASE_URL + "/api/vr1/add_state";
    public static final String GET_CITIES = BASE_URL + "/api/vr1/get_location";
    public static final String ADD_ORDER = BASE_URL + "/api/vr1/add_order";
    public static final String MODIR_ADD_ORDER = BASE_URL + "/api/vr1/add_modir_order";
    public static final String EDIT_ORDER = BASE_URL + "/api/vr1/edit_order";
    public static final String EDIT_ORDER_V2 = BASE_URL + "/api/vr1/edit_order_2";
    public static final String GET_SETS = BASE_URL + "/api/vr1/get_settings";
    public static final String GET_CONDITION = BASE_URL + "/api/vr1/load_condition";
    public static final String RENEW_ORDER = BASE_URL + "/api/vr1/renewOrder";
    public static final String UPGRADE_ORDER = BASE_URL + "/api/vr1/upgradeOrder";
    public static final String ROOT_CATS = BASE_URL + "/api/vr1/get_parent_cats";
    public static final String GET_LOCATIONS = BASE_URL + "/api/vr1/get_locations";
    public static final String LOAD_CAT_ORDERS = BASE_URL + "/api/vr1/get_cat_orders";
    public static final String SEND_CONTACT = BASE_URL + "/api/vr1/send_contact";
    public static final String LOAD_AGENCY_INFO = BASE_URL + "/api/vr1/get_agency_info";


    public static final String REMOVE_ORDER = BASE_URL + "/api/vr1/del_order";
    public static final String GET_LOCATION_ID = BASE_URL + "/api/vr1/getLocationID";
    public static final String FINALIZE_ORDER = BASE_URL + "/api/vr1/finalize_order";
    public static final String SEARCH_API = BASE_URL + "/api/vr1/search";
    public static final String SEARCH_FILTER = BASE_URL + "/api/vr1/search_filter";
    public static final String SEARCHSTR = BASE_URL + "/api/vr1/search_str";
    public static final String MY_ORDERS = BASE_URL + "/api/vr1/getuser_orders";
    public static final String MODIR_ORDERS = BASE_URL + "/api/vr1/get_modir_orders";
    public static final String SEND_CODE = BASE_URL + "/api/vr1/send_code";;
    public static final String GET_NEW_ORDER = BASE_URL + "/api/vr1/get_new_order";;

    public static final String BAZAR_KEY = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwDPEFBgBbmy6zcNdFQV9KlBBU79jhUyeisYdN4kYMAW3oM35YEsIPLIfI9w00TK7wF/hEch6ol3bgseWPqDWnD9uS97stDq9EMZq+5wj+cTqm6a0ih+MVlt3hGTi8Y1jpxrergqde/c8hedCiEO1XgnW5w8RuL41mKOkatUFRFifPBAO17xTSqSvT/u5sTjM46qG1u3Eak5lT99LOZHynT/KwUb1t3YHDbm01QSI38CAwEAAQ==1";

    public static ArrayList<Brand> NAGHLIYE_BRANDS_LIST = new ArrayList<>();

    public static boolean isSpinnerTouched = false;

    public static void writeFile(String data) {
        final File path =
                Environment.getExternalStoragePublicDirectory
                        (
                                //Environment.DIRECTORY_PICTURES
                                Environment.DIRECTORY_DOCUMENTS + "/melkeurmia/"
                        );

        // Make sure the path directory exists.
        if (!path.exists()) {
            // Make it, if it doesn't exit
            path.mkdirs();
        }


        final File file = new File(path, "output.html");

        // Save your stream, don't forget to flush() it before closing it.

        try {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);

            myOutWriter.close();

            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


    public static String formatInt(String number) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("fa", "IR"));
        String str = "";
        if (number != null){
            double num = Double.parseDouble(number);
            str = nf.format(num);
        }else{
            str = "0";
        }
        return str;
    }

    public static String formatPersianInt(String input) {



        String newValue = (((((((((((input+"")
                .replaceAll("1", "١")).replaceAll("2", "٢"))
                .replaceAll("3", "٣")).replaceAll("4", "٤"))
                .replaceAll("5", "٥")).replaceAll("6", "٦"))
                .replaceAll("7", "٧")).replaceAll("8", "٨"))
                .replaceAll("9", "٩")).replaceAll("0", "٠"));


        return newValue;

    }

    public static String formatPriceInt(int number) {
        //String price_str = th.in.lordgift.widget.Utils.insertNumberComma(String.valueOf(number));
        NumberFormat nf = NumberFormat.getInstance(new Locale("fa", "IR"));
        String str = nf.format(Long.valueOf(number));
        return str;
    }

    public static String formatPriceDouble(double number) {
        //String price_str = th.in.lordgift.widget.Utils.insertNumberComma(String.valueOf(number));
        NumberFormat nf = NumberFormat.getInstance(new Locale("fa", "IR"));
        String str = nf.format(Double.valueOf(number));
        return str;
    }

    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return manager.getActiveNetworkInfo();
    }

    public static boolean isConnected(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return info != null && info.isConnected();
    }


    public static void showProvinceCitySelector(final Activity activity, final Context context) {

        NDialog nDialog = new NDialog(context, ButtonType.NO_BUTTON);

        //DialogSheet sheet = new DialogSheet(context);
        View view = View.inflate(context, R.layout.province_city_selector, null);
        //sheet.setView(view);
        //sheet.setCancelable(false);
        RtlMaterialSpinner province_sp = (RtlMaterialSpinner) view.findViewById(R.id.province_sp);
        province_sp.setPopupBackgroundResource(R.drawable.white_shape);

        ProgressBar pb_root = (ProgressBar) view.findViewById(R.id.pb_root);

        //sheet.show();
        nDialog.setCustomView(view);
        nDialog.show();
        nDialog.isCancelable(false);

        pb_root.setVisibility(View.VISIBLE);
        Ion.with(context)
                .load(CONST.GET_LOCATIONS)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e == null) {
                            pb_root.setVisibility(View.GONE);

                            JsonParser parser = new JsonParser();
                            List<ProviceModel> provinces_list = new ArrayList<ProviceModel>();
                            if(!parser.parse(result).isJsonNull()){
                                JsonArray provice_items = parser.parse(result).getAsJsonObject().get("locations").getAsJsonArray();
                                for (int i = 0; i<provice_items.size();i++){
                                    JsonObject province = provice_items.get(i).getAsJsonObject();
                                    ProviceModel model = new ProviceModel();
                                    model.setName(province.get("name").getAsString());
                                    model.setId(province.get("id").getAsInt());
                                    JsonArray city_items = province.get("cities").getAsJsonArray();

                                    List<CityModel> cities_list = new ArrayList<CityModel>();
                                    for (int j=0;j<city_items.size();j++){
                                        JsonObject city_item = city_items.get(j).getAsJsonObject();
                                        CityModel cityModel = new CityModel();
                                        cityModel.setName(city_item.get("name").getAsString());
                                        cityModel.setId(city_item.get("id").getAsInt());
                                        cities_list.add(cityModel);
                                    }
                                    model.setCities(cities_list);
                                    provinces_list.add(model);
                                }


                                SpinnerAdapter adapter = new SpinnerAdapter(context,
                                        R.layout.custom_spinner_items,
                                        provinces_list);

                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                List<CityModel> temp_list = new ArrayList<CityModel>();

                                SpinnerCityAdapter city_adapter = new SpinnerCityAdapter(context,
                                        android.R.layout.simple_spinner_item,
                                        temp_list);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                province_sp.setAdapter(adapter); // Set the custom adapter to the spinner


                                province_sp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                        if(position> 0){
                                            ProviceModel user = adapter.getItem(position-1);

                                            // Here you can do the action you want to...
                                            temp_list.clear();
                                            temp_list.addAll(user.getCities());

                                            AppSharedPref.write("CITY_ID", String.valueOf(user.getId()));
                                            AppSharedPref.write("CITY_NAME", user.getName());

                                            city_adapter.notifyDataSetChanged();

                                            AppEvents.UpdateLocation id_event = new AppEvents.UpdateLocation(user.getId());
                                            GlobalBus.getBus().post(id_event);
                                            nDialog.dismiss();
                                            CONST.restartApp(activity);
                                        }
                                    }
                                });



                            }
                        } else {
                            Toasty.error(context, context.getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }

    public static void restartApp(Activity activity) {
        Intent mStartActivity = new Intent(activity, SplashScreen.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(activity, mPendingIntentId, mStartActivity,
                PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
        activity.finish();
    }

    public static void showInsertProvinceCitySelector(final Activity activity, final Context context, onCitySelect event) {

        NDialog nDialog = new NDialog(context, ButtonType.NO_BUTTON);
        View view = View.inflate(context, R.layout.province_city_selector, null);
        RtlMaterialSpinner province_sp = (RtlMaterialSpinner) view.findViewById(R.id.province_sp);
        ProgressBar pb_root = (ProgressBar) view.findViewById(R.id.pb_root);

        province_sp.setPopupBackgroundResource(R.drawable.white_shape);

        nDialog.setCustomView(view);
        nDialog.show();
        nDialog.isCancelable(false);

        pb_root.setVisibility(View.VISIBLE);
        Ion.with(context)
                .load(CONST.GET_LOCATIONS)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e == null) {
                            pb_root.setVisibility(View.GONE);

                            JsonParser parser = new JsonParser();
                            List<ProviceModel> provinces_list = new ArrayList<ProviceModel>();
                            if(!parser.parse(result).isJsonNull()){
                                JsonArray provice_items = parser.parse(result).getAsJsonObject().get("locations").getAsJsonArray();
                                for (int i = 0; i<provice_items.size();i++){
                                    JsonObject province = provice_items.get(i).getAsJsonObject();
                                    ProviceModel model = new ProviceModel();
                                    model.setName(province.get("name").getAsString());
                                    model.setId(province.get("id").getAsInt());
                                    JsonArray city_items = province.get("cities").getAsJsonArray();

                                    List<CityModel> cities_list = new ArrayList<CityModel>();
                                    for (int j=0;j<city_items.size();j++){
                                        JsonObject city_item = city_items.get(j).getAsJsonObject();
                                        CityModel cityModel = new CityModel();
                                        cityModel.setName(city_item.get("name").getAsString());
                                        cityModel.setId(city_item.get("id").getAsInt());
                                        cities_list.add(cityModel);
                                    }
                                    model.setCities(cities_list);
                                    provinces_list.add(model);
                                }


                                SpinnerAdapter adapter = new SpinnerAdapter(context,
                                        R.layout.custom_spinner_items,
                                        provinces_list);

                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                List<CityModel> temp_list = new ArrayList<CityModel>();

                                SpinnerCityAdapter city_adapter = new SpinnerCityAdapter(context,
                                        android.R.layout.simple_spinner_item,
                                        temp_list);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                province_sp.setAdapter(adapter); // Set the custom adapter to the spinner


                                province_sp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                        if(position>0){
                                            ProviceModel user = adapter.getItem(position-1);
                                            AppSharedPref.write("CITY_ID", String.valueOf(user.getId()));
                                            AppSharedPref.write("CITY_NAME", user.getName());
                                            event.onCitySelect();
                                            nDialog.dismiss();
                                        }
                                    }
                                });

                            }
                        } else {
                            Toasty.error(context, context.getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }

    public static void showNewOrderBadge(Context context) {
        java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        Ion.with(context)
                .load(CONST.GET_NEW_ORDER)
                .setBodyParameter("date",dateFormat.format(date))
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if(e == null){
                        }
                    }
                });
    }


    public interface onCitySelect {
        void onCitySelect();
    }

    public static void showCitySelector(final Activity activity, final Context context, final ProgressDialog progressDialog) {
        final List<String> citiesList = new ArrayList<>();
        final List<Integer> citiesList_number = new ArrayList<>();


        progressDialog.show();
        citiesList.clear();
        Ion.with(context)
                .load(CONST.GET_CITIES)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        progressDialog.dismiss();
                        if (e == null) {
                            JsonParser jsonParser = new JsonParser();
                            JsonObject root = jsonParser.parse(result).getAsJsonObject();
                            JsonArray cities = root.get("cities").getAsJsonArray();
                            for (int i = 0; i < cities.size(); i++) {
                                citiesList_number.add(cities.get(i).getAsJsonObject().get("id").getAsInt());
                                citiesList.add(cities.get(i).getAsJsonObject().get("name").getAsString());
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.list_title, citiesList);
                            final AlertDialog.Builder builder = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT);
                            LayoutInflater inflater = activity.getLayoutInflater();
                            View view = inflater.inflate(R.layout.titlebar, null);
                            TextView title_tv = (TextView) view.findViewById(R.id.title_tv);
                            title_tv.setText("شهر مورد نظر را از انتخاب کنید.");
                            builder.setCancelable(false);
                            builder.setCustomTitle(view);
                            builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int selected_id = citiesList_number.get(which);
                                    AppSharedPref.write("CITY_ID", String.valueOf(selected_id));
                                    AppSharedPref.write("CITY_NAME", citiesList.get(which));
                                    AppEvents.UpdateLocation id_event = new AppEvents.UpdateLocation(selected_id);
                                    GlobalBus.getBus().post(id_event);
                                    dialog.dismiss();
                                    Intent mStartActivity = new Intent(activity, SplashScreen.class);
                                    int mPendingIntentId = 123456;
                                    PendingIntent mPendingIntent = PendingIntent.getActivity(activity, mPendingIntentId, mStartActivity,
                                            PendingIntent.FLAG_CANCEL_CURRENT);
                                    AlarmManager mgr = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
                                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                                    System.exit(0);
                                }
                            });
                            builder.show();
                        } else {
                            Toasty.error(context, context.getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public static void showRatingBar(Context context) {
        SharedPreferences pref = context.getSharedPreferences("PELAK", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        if (!pref.getBoolean("is_show", false)) {
            Intent intent = new Intent(Intent.ACTION_EDIT);
            intent.setData(Uri.parse("bazaar://details?id=tmedia.ir.melkeurmia"));
            intent.setPackage("com.farsitel.bazaar");
            context.startActivity(intent);
            editor.putBoolean("is_show", true);
            editor.commit();
        }
    }

    public static void errorConectionToast(Context context) {
        Toasty.error(context, "خطا در پردازش درخواست شما", 2000).show();
    }
}


