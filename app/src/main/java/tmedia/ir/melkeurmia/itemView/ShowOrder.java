package tmedia.ir.melkeurmia.itemView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mapbox.mapboxsdk.Mapbox;
import com.marcoscg.dialogsheet.DialogSheet;
import com.mohan.ribbonview.RibbonView;
import com.squareup.otto.Subscribe;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import app.dinus.com.loadingdrawable.LoadingView;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import eu.fiskur.simpleviewpager.ImageURLLoader;
import eu.fiskur.simpleviewpager.SimpleViewPager;
import tmedia.ir.melkeurmia.MapLocationActivity;
import tmedia.ir.melkeurmia.R;
import tmedia.ir.melkeurmia.model.Favorit;
import tmedia.ir.melkeurmia.myorders.EditActivity;
import tmedia.ir.melkeurmia.otto.AppEvents;
import tmedia.ir.melkeurmia.otto.GlobalBus;
import tmedia.ir.melkeurmia.tools.AppSharedPref;
import tmedia.ir.melkeurmia.tools.CONST;
import tmedia.ir.melkeurmia.tools.FavoritUtils;
import tmedia.ir.melkeurmia.tools.PersianDate;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static tmedia.ir.melkeurmia.tools.AppSharedPref.read;

public class ShowOrder extends AppCompatActivity {

    private static final int OPEN_EDIT_ORDER = 4578;
    private Context context;
    private ProgressDialog progressDialog;
    private ScrollView scrollView;
    private Button upgrade_order_btn, order_edit_btn, order_delete_btn;
    private LoadingView gallery_loading;
    private SimpleViewPager image_pager;
    private LinearLayout otagh_view,order_insert_info, order_btn, slider_view, all_field_view, general_field, general_rahn_view, general_ejareh_view, city_name_view, category_container, general_price_view, general_type_view, item_address_con;
    private TextView status_txt, item_title,otagh_view_tv,metraj_view_tv, item_desc, item_category, city_name_tv, general_rahn_tv, general_ejareh_tv, general_price_view_tv, general_type_view_tv, state_info_tv, item_address_tv;
    private View general_price_view_sep, general_type_view_sep, city_name_view_sep, general_rahn_view_sep, otagh_view_sep;
    private View rootView;
    private JsonObject order;
    private String title;
    private String desc;
    private String email;
    private String tel;
    private String cat_name;
    private JsonArray attachments;
    private String status;
    private int general_type;
    private FloatingActionButton contact_info;
    private boolean is_expired = false;
    private ArrayList<String> image_urls;
    private int finilize_order_id;
    private boolean mode;
    private int id;
    protected BarChart state_chart;
    private TextView tvX, tvY;
    private LinearLayout chart_con;
    private String contact_email;
    private String contact_tel;
    private String contact_mobile;
    private String contact_sms;
    private String price_type;
    private LinearLayout ads_view_con;
    private DialogSheet sheet;
    private RadioGroup pay_radioes;
    private RadioButton fori_pay_radio, renew_pay_radio;
    private Button pay_btn;
    private int order_purchase_fori, order_purchase, order_purchase_renew;
    private boolean mBazaarInstalled;
    boolean mIsPremium = false;
    static final int RC_REQUEST = 1;
    private static final String TAG = CONST.APP_LOG;

    private MenuItem favorit_menu_object;

    private int current_image_slider_item = 0;

    private boolean is_agency;
    private int agency_id;
    private Toolbar toolbar;

    private RibbonView ribbonView;
    private boolean is_permission_grant;
    private FavoritUtils favoritUtils;
    private Favorit favorit;
    private boolean is_exist = false;

    private String share_text = "";
    private View map_fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(getApplicationContext(), getString(R.string.map_token));

        setContentView(R.layout.activity_show_order);


        mode = getIntent().getExtras().getBoolean("mode");
        id = getIntent().getExtras().getInt("id");


        context = this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mode) {
            toolbar.setTitle("مدیریت آگهی");
        } else {

            toolbar.setTitle("نمایش آگهی");
        }



        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("در حال بارگذاری");

        initViews();

        order_delete_btn.setOnClickListener(v -> removeOrder(id));
        sheet = new DialogSheet(this);
        View view = View.inflate(this, R.layout.pay_layout, null);
        sheet.setView(view);
        pay_radioes = (RadioGroup) view.findViewById(R.id.pay_radioes);
        fori_pay_radio = view.findViewById(R.id.fori_pay_radio);
        renew_pay_radio = view.findViewById(R.id.renew_pay_radio);
        pay_btn = (Button) view.findViewById(R.id.pay_btn);
        ribbonView.setVisibility(View.GONE);
        switchmode(mode);
        loadorder(id);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.fontpath))
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            is_permission_grant = false;
            TedPermission.with(this)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("جهت کارکرد صحیح نرم افزار لطفا مجوزهای درخواست شده را ارائه دهید.")
                    .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .check();
        } else {
            is_permission_grant = true;
            favoritUtils = new FavoritUtils(this);
        }
    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            is_permission_grant = true;
            favoritUtils = new FavoritUtils(ShowOrder.this);
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {

        }
    };

    @Subscribe
    public void shoppingCardBooksChanged (AppEvents.PagerAdapterEvent event) {
        Intent i = new Intent(ShowOrder.this, FullScreenGallery.class);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("imgs", image_urls);
        bundle.putInt("pos", event.getPos());
        i.putExtras(bundle);
        startActivity(i);
    }



    private void switchmode(boolean mode) {
        if (mode) {
            order_insert_info.setVisibility(View.VISIBLE);
            order_btn.setVisibility(View.VISIBLE);
            chart_con.setVisibility(View.VISIBLE);

        } else {
            order_insert_info.setVisibility(View.GONE);
            order_btn.setVisibility(View.GONE);
            chart_con.setVisibility(View.GONE);
            final Handler handler = new Handler();
            handler.postDelayed(() -> Ion.with(context)
                    .load(CONST.ADD_STATE)
                    .setBodyParameter("order_id", String.valueOf(id))
                    .asString()
                    .setCallback((e, result) -> {
                    }), 3000);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        GlobalBus.getBus().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GlobalBus.getBus().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.show_order_menu, menu);
        return true;
    }



    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        favorit_menu_object = menu.findItem(R.id.action_favorit);
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorit:
                if (is_permission_grant) {
                    if (favoritUtils.isFavoritExist(favorit)) {
                        favoritUtils.removeFavorit(favorit);
                        item.setIcon(getResources().getDrawable(R.drawable.favorit_icon));
                        Toast.makeText(ShowOrder.this, "این بخش از علاقه مندی ها حذف شد", Toast.LENGTH_LONG).show();
                    } else {
                        item.setIcon(getResources().getDrawable(R.drawable.favorit_selected_icon));
                        favoritUtils.putFavorit(favorit);
                        Toast.makeText(ShowOrder.this, "این بخش به علاقه مندی ها اضافه شد", Toast.LENGTH_LONG).show();
                    }

                }
                return true;
            case R.id.action_share:
                loadAgencyInfo(null,false);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    private void initViews() {
        scrollView =  findViewById(R.id.scrollView);
        order_insert_info =  findViewById(R.id.order_insert_info);
        otagh_view =  findViewById(R.id.otagh_view);
        otagh_view_sep =  findViewById(R.id.otagh_view_sep);
        order_btn =  findViewById(R.id.order_btn);
        slider_view =  findViewById(R.id.slider_view);
        all_field_view =  findViewById(R.id.all_field_view);
        general_field =  findViewById(R.id.general_field);
        city_name_view =  findViewById(R.id.city_name_view);
        category_container =  findViewById(R.id.category_container);
        general_price_view =  findViewById(R.id.general_price_view);
        general_type_view =  findViewById(R.id.general_type_view);
        item_address_con =  findViewById(R.id.item_address_con);
        general_rahn_view =  findViewById(R.id.general_rahn_view);
        general_ejareh_view =  findViewById(R.id.general_ejareh_view);
        status_txt =  findViewById(R.id.status_txt);
        item_title =  findViewById(R.id.item_title);
        item_desc =  findViewById(R.id.item_desc);
        otagh_view_tv =  findViewById(R.id.otagh_view_tv);
        metraj_view_tv =  findViewById(R.id.metraj_view_tv);
        item_category =  findViewById(R.id.item_category);
        general_price_view_tv =  findViewById(R.id.general_price_view_tv);
        city_name_tv =  findViewById(R.id.city_name_tv);
        general_rahn_tv =  findViewById(R.id.general_rahn_tv);
        general_ejareh_tv =  findViewById(R.id.general_ejareh_tv);
        general_type_view_tv =  findViewById(R.id.general_type_view_tv);
        state_info_tv =  findViewById(R.id.state_info_tv);
        item_address_tv =  findViewById(R.id.item_address);
        order_edit_btn =  findViewById(R.id.order_edit_btn);
        order_delete_btn =  findViewById(R.id.order_delete_btn);
        general_price_view_sep =  findViewById(R.id.general_price_view_sep);
        general_type_view_sep =  findViewById(R.id.general_type_view_sep);
        city_name_view_sep =  findViewById(R.id.city_name_view_sep);
        general_rahn_view_sep =  findViewById(R.id.general_rahn_view_sep);
        chart_con =  findViewById(R.id.chart_con);
        gallery_loading = findViewById(R.id.gallery_loading);
        image_pager = findViewById(R.id.image_pager);
        contact_info = findViewById(R.id.fab);
        map_fab = findViewById(R.id.mapFab);
        ribbonView = findViewById(R.id.ribbonView);
        state_chart = findViewById(R.id.state_chart);
        state_chart.setVisibility(View.GONE);

        //getItemOrder();

        if(contact_info != null){
            if (mode) {
                contact_info.setVisibility(View.GONE);
            } else {
                contact_info.setVisibility(View.VISIBLE);
            }
        }


        final Rect scrollBounds = new Rect();
        scrollView.getHitRect(scrollBounds);
        final boolean[] is_visible_chart = {false};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                    if (scrollView.getLocalVisibleRect(scrollBounds)) {

                        if (state_chart.getGlobalVisibleRect(scrollBounds)
                                && state_chart.getHeight() == scrollBounds.height()
                                && state_chart.getWidth() == scrollBounds.width()) {
                            is_visible_chart[0] = true;
                        } else {
                            is_visible_chart[0] = false;
                        }
                        if (is_visible_chart[0]) {

                            state_chart.animateY(1000);
                            state_chart.fitScreen();
                        }
                    }
                }
            });
        }
    }

    private void loadorder(final int id) {
        order_edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i_edit = new Intent(ShowOrder.this, EditActivity.class);
                i_edit.putExtra("id", id);
                startActivityForResult(i_edit, OPEN_EDIT_ORDER);
            }
        });

        progressDialog.setMessage("در حال بارگذاری آگهی");
        progressDialog.setCancelable(false);
        progressDialog.show();
        //removeOrder(events.getId());

        Ion.with(context)
                .load(CONST.GET_ORDER_WITH_STATE)
                .setBodyParameter("city_id", AppSharedPref.read("CITY_ID", "0"))
                .setBodyParameter("order_id", String.valueOf(id))
                .asString()
                .setCallback((e, result) -> {
                    progressDialog.dismiss();
                    if (e == null) {
                        JsonParser parser = new JsonParser();
                        JsonObject obj = parser.parse(result).getAsJsonObject();
                        if (obj.has("status")) {
                            String status1 = obj.get("status").getAsString();
                            if (status1.equals("found")) {
                                int cat_id = obj.get("order").getAsJsonObject().get("cat_id").getAsInt();

                                if(!obj.get("order").getAsJsonObject().get("is_agency").isJsonNull()){
                                    is_agency = ( obj.get("order").getAsJsonObject().get("is_agency").getAsInt() == 1) ? true : false;
                                    if(is_agency){
                                        agency_id = obj.get("order").getAsJsonObject().get("modir_id").getAsInt();
                                    }
                                }
                                showOrderView(cat_id, obj);
                            }
                        }
                    } else {
                        Toasty.error(context, "خطا در برقراری ارتباط با سرور", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showstate(JsonElement datas) {

        BarData data = new BarData();
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        int all_view = 0;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        int data_count = 0;

        if (datas.isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : datas.getAsJsonObject().entrySet()) {
                JsonArray array = entry.getValue().getAsJsonArray();
                try {
                    Date d = dateFormat.parse(entry.getKey());
                    PersianDate pdate = new PersianDate(d);
                    String data_fa = pdate.getShMonth() + "-" + pdate.getShDay();
                    BarEntry B_entry = new BarEntry(data_count, array.size());
                    entries.add(B_entry);
                    labels.add(data_fa);
                    data_count++;
                    all_view += array.size();

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        state_info_tv.setText("کل بازدید صورت گرفته : " + all_view + " بار نمایش");

        List<IBarDataSet> dataSets = new ArrayList<>();
        BarDataSet bds = new BarDataSet(entries, "Bazdid");
        int[] colors = {R.color.primary_dark};
        bds.setColors(ColorTemplate.createColors(colors));
        String[] xAxisLabels = labels.toArray(new String[0]);

        bds.setStackLabels(xAxisLabels);
        dataSets.add(bds);
        data.addDataSet(bds);
        data.setDrawValues(true);
        data.setBarWidth(.8f);

        XAxis xaxis = state_chart.getXAxis();
        xaxis.setDrawGridLines(false);
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xaxis.setGranularityEnabled(true);
        xaxis.setGranularity(1);
        xaxis.setDrawLabels(true);
        xaxis.setCenterAxisLabels(true);
        xaxis.setLabelCount(data_count + 1);
        xaxis.setXOffset(-120f);
        xaxis.setDrawAxisLine(false);
        xaxis.setTextSize(12f);
        xaxis.setTextColor(Color.DKGRAY);
        CategoryBarChartXaxisFormatter xaxisFormatter = new CategoryBarChartXaxisFormatter(xAxisLabels);
        xaxis.setValueFormatter(xaxisFormatter);

        YAxis yAxisRight = state_chart.getAxisRight();
        yAxisRight.setDrawGridLines(false);
        yAxisRight.setDrawAxisLine(false);
        yAxisRight.setEnabled(false);

        Legend legend = state_chart.getLegend();
        legend.setEnabled(false);

        state_chart.setFitBars(true);
        state_chart.setPadding(0, 50, 0, 0);
        state_chart.setData(data);
        state_chart.setDescription(null);
        state_chart.animateXY(1000, 1000);
        state_chart.invalidate();
        /*state_chart.setScaleEnabled(false);
        state_chart.setDoubleTapToZoomEnabled(false);
        state_chart.setDragEnabled(false);*/

    }

    private void initstatus(String status) {
        if (status.equals("enabled")) {

            status_txt.setText("آگهی شما منتشر شده و در حال نمایش بصورت عمومی می باشد.");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                status_txt.setTextColor(getColor(R.color.green_700));

            } else {
                status_txt.setTextColor(getResources().getColor(R.color.green_700));
            }
            if (mode) {
                chart_con.setVisibility(View.VISIBLE);
            } else {
                chart_con.setVisibility(View.GONE);
            }
        }
        if (status.equals("disabled")) {

            chart_con.setVisibility(View.GONE);
            status_txt.setText("آگهی شما در صف انتشار می باشد و بعد از تایید توسط تیم ملک ارومیه منتشر خواهد شد.");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                status_txt.setTextColor(getColor(R.color.deep_orange_A700));
            } else {
                status_txt.setTextColor(getResources().getColor(R.color.deep_orange_A700));
            }
        }

    }

    private void showSliderView(JsonArray images) {
        slider_view.setVisibility(View.VISIBLE);



        image_pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                current_image_slider_item = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        slider_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        if (image_urls != null) {
            image_urls.clear();
            image_pager.clearIndicator();
        }

        if (images.size() > 0) {
            image_urls = new ArrayList<>();
            for (int i = 0; i < images.size(); i++) {
                image_urls.add(CONST.STORAGE + images.get(i).getAsString());
            }

            gallery_loading.setVisibility(View.GONE);
            image_pager.setVisibility(View.VISIBLE);


            image_pager.setImageUrls(image_urls, new ImageURLLoader() {
                @Override
                public void loadImage(ImageView view, String url) {
                    Glide.with(context).load(url).listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String s, Target<GlideDrawable> target, boolean b) {
                            return true;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable glideDrawable, String s, Target<GlideDrawable> target, boolean b, boolean b1) {

                            return false;
                        }
                    }).skipMemoryCache(true).into(view);
                }
            });

            int indicatorColor = Color.parseColor("#ffffff");
            int selectedIndicatorColor = Color.parseColor("#c1c1c1");
            image_pager.showIndicator(indicatorColor, selectedIndicatorColor);
        } else {
            slider_view.setVisibility(View.GONE);
        }
    }

    private void removeOrder(final int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("حذف آگهی");
        builder.setMessage("آیا مطمئن هستید ؟");


        builder.setPositiveButton("بله", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String app_token = read("TOKEN", "");
                byte[] data = Base64.decode(app_token, Base64.DEFAULT);
                try {
                    String user_pass = new String(data, "UTF-8");
                    Ion.with(context)
                            .load(CONST.APP_TOKEN)
                            .setBodyParameter("username", user_pass)
                            .setBodyParameter("password", user_pass.split("_")[0])
                            .asString()
                            .setCallback(new FutureCallback<String>() {
                                @Override
                                public void onCompleted(Exception e, String result) {
                                    if (e == null) {
                                        JsonParser parser = new JsonParser();
                                        JsonObject json_obj = parser.parse(result).getAsJsonObject();
                                        if (json_obj.has("token")) {
                                            String token = json_obj.get("token").getAsString();
                                            Ion.with(context)
                                                    .load(CONST.REMOVE_ORDER)
                                                    .setHeader("Authorization", "Bearer " + token)
                                                    .setBodyParameter("order_id", String.valueOf(id))
                                                    .asString()
                                                    .setCallback(new FutureCallback<String>() {
                                                        @Override
                                                        public void onCompleted(Exception e, String result) {
                                                            Toasty.success(context, "آگهی با موفقیت حذف شد.", Toast.LENGTH_LONG).show();

                                                            Intent resultIntent = new Intent();
                                                            resultIntent.putExtra("update", true);
                                                            finish();
                                                        }
                                                    });
                                        }
                                    } else {
                                        Toasty.error(context, "خطا در برفراری ارتباط با سرور", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("نه", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OPEN_EDIT_ORDER) {
            finish();
        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void showOrderView(int cat_id, JsonObject obj) {
        order = obj.get("order").getAsJsonObject();

        title = order.get("title").getAsString();
        desc = order.get("desc").getAsString();
        email = order.get("email").isJsonNull() ? "" : order.get("email").getAsString();
        tel = order.get("tel").getAsString();
        cat_name = obj.get("cat_name").getAsString();
        attachments = obj.get("order").getAsJsonObject().get("attachments").getAsJsonArray();
        status = obj.get("order").getAsJsonObject().get("status").getAsString();
        int expire_val = obj.get("order").getAsJsonObject().get("is_expired").getAsInt();

        switch (cat_id){
            case 7:
            case 10:
            case 11:
            case 13:
            case 14:
            case 17:
            case 18:
            case 21:
            case 23:
            case 24:
            case 25:
                hideOtagh();
                break;
        }


        share_text += title + "\n";
        share_text += "کد آگهی: " + order.get("unique_id").getAsString() + "\n\n";

        toolbar.setTitle("کد آگهی: " + CONST.formatPersianInt(order.get("unique_id").getAsString()));

        favorit = new Favorit(title, order.get("unique_id").getAsString(), order.get("id").getAsString());
        // set your desired icon here based on a flag if you like
        if (is_permission_grant) {
            if (favoritUtils.isFavoritExist(favorit)) {
                is_exist = true;
                favorit_menu_object.setIcon(getResources().getDrawable(R.drawable.favorit_selected_icon));
            } else {
                is_exist = false;
                favorit_menu_object.setIcon(getResources().getDrawable(R.drawable.favorit_icon));
            }
        }

        if(order.get("is_lux").getAsInt() == 1){
            ribbonView.setVisibility(View.VISIBLE);
        }

        city_name_tv.setText(order.get("city_name").getAsString());


        if (!order.get("address").isJsonNull()) {
            item_address_con.setVisibility(View.VISIBLE);
            item_address_tv.setText("آدرس: " + order.get("address").getAsString());
        } else {
            item_address_con.setVisibility(View.GONE);
        }

        general_type = order.get("general_type").getAsInt();


        switch (general_type) {
            case 1:
                general_type_view_tv.setText("فروشی");
                general_rahn_view.setVisibility(View.GONE);
                general_rahn_view_sep.setVisibility(View.GONE);
                general_ejareh_view.setVisibility(View.GONE);
                general_price_view.setVisibility(View.VISIBLE);
                general_price_view_sep.setVisibility(View.GONE);

                switch (order.get("price_type").getAsInt()){
                    case 1:

                        if(!order.get("price").isJsonNull()){
                            share_text +=  "\uD83D\uDCB0 " + CONST.formatInt(order.get("price").getAsString()) + " تومان " +  "\n\n";
                            general_price_view_tv.setText(CONST.formatInt(order.get("price").getAsString()) + " تومان ");
                        }else{
                            share_text +=  "\uD83D\uDCB0 " +  "0 تومان" +  "\n\n";
                            general_price_view_tv.setText(  " 0 تومان ");
                        }
                        break;
                    case 2:
                        share_text +=  "\uD83D\uDCB0 " + "معاوضه" +  "\n\n";
                        general_price_view_tv.setText("معاوضه");
                        break;
                    case 3:
                        share_text +=  "\uD83D\uDCB0 " + "توافقی" +  "\n\n";
                        general_price_view_tv.setText("توافقی");
                        break;
                }

                break;
            case 2:
                share_text +=  "\uD83D\uDCB0 " + "درخواستی" +  "\n\n";
                general_type_view_tv.setText("درخواستی");
                general_rahn_view.setVisibility(View.GONE);
                general_rahn_view_sep.setVisibility(View.GONE);
                general_ejareh_view.setVisibility(View.GONE);
                general_price_view.setVisibility(View.GONE);
                general_price_view_sep.setVisibility(View.GONE);
                break;
            case 3:
                general_type_view_tv.setText("اجاره");
                general_rahn_view.setVisibility(View.VISIBLE);
                general_rahn_view_sep.setVisibility(View.VISIBLE);
                general_ejareh_view.setVisibility(View.VISIBLE);
                general_price_view.setVisibility(View.GONE);
                general_price_view_sep.setVisibility(View.GONE);

                share_text +=  "\uD83D\uDCB0 " + " رهن: " + CONST.formatInt(String.valueOf(order.get("rahn_price").getAsInt())) + " تومان " +  "\n\n";
                share_text +=  "\uD83D\uDCB0 " + " اجاره: " + CONST.formatInt(String.valueOf(order.get("ejareh_price").getAsInt())) + " تومان " +  "\n\n";

                general_rahn_tv.setText(CONST.formatInt(String.valueOf(order.get("rahn_price").getAsInt())) + " تومان ");
                general_ejareh_tv.setText(CONST.formatInt(String.valueOf(order.get("ejareh_price").getAsInt())) + " تومان ");
                break;
        }


        if (mode) {
            state_chart.setVisibility(View.VISIBLE);
            showstate(obj.get("states"));
        }


        //share_text +=  "\uD83D\uDD0E " + " لینک آگهی: " + ;

        share_text +=  "# " + cat_name  +"\n" ;
        share_text +=  "-------------------\n\n" ;


        if(!is_agency){
            share_text +=  "☎️ " + "تلفن دفتر : " + "09144398200 - 04433481507 " +"\n" ;
            share_text +=  "☎️ " + "مدیر : " + "09143436657" +"\n" ;
            share_text +=  "☎️ " + "کارشناس فروش : " + "09220635705" +"\n" ;
            share_text +=  "-------------------\n\n" ;
            share_text +=  "لینک دانلود اپلیکیشن: " + "https://cafebazaar.ir/app/tmedia.ir.melkeurmia" ;
        }

        initstatus(status);
        item_title.setText(CONST.formatPersianInt(title));
        item_desc.setText(CONST.formatPersianInt(desc));

        item_category.setText(cat_name);
        otagh_view_tv.setText(CONST.formatInt(order.get("otagh").getAsString()));
        metraj_view_tv.setText(CONST.formatInt(order.get("metraj").getAsString()));
        showSliderView(attachments);

        if(order.get("location").isJsonNull()){
            map_fab.setVisibility(View.GONE);
        }

        map_fab.setOnClickListener(view -> {
            Intent i_map = new Intent(ShowOrder.this, MapLocationActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("location",order.get("location").getAsString());
            bundle.putString("mode","view");
            i_map.putExtras(bundle);
            startActivity(i_map);
        });


        contact_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_agency){
                    View view = View.inflate(context, R.layout.agency_contact_layout, null);
                    progressDialog.setMessage("در حال بارگذاری اطلاعات");
                    progressDialog.show();
                    loadAgencyInfo(view,true);
                }else{
                    View view = View.inflate(context, R.layout.show_order_contact_layout, null);
                    TextView tel_tv = (TextView) view.findViewById(R.id.tel_tv);
                    TextView sms_tv = (TextView) view.findViewById(R.id.sms_tv);
                    TextView fix_tel_tv = (TextView) view.findViewById(R.id.fix_tel_tv);
                    TextView email_tv = (TextView) view.findViewById(R.id.email_tv);
                    View sep1 = (View) view.findViewById(R.id.sep1);
                    View sep2 = (View) view.findViewById(R.id.sep2);
                    contact_email = " ایمیل : " + email;
                    contact_tel = " تلفن تماس : " + tel;
                    contact_sms = " ارسال پیامک : " + tel;

                    email_tv.setText(contact_email);

                    fix_tel_tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           Intent call = new Intent(Intent.ACTION_DIAL);
                           call.setData(Uri.parse("tel:04433481507"));
                           startActivity(call);
                        }
                    });

                    tel_tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent call = new Intent(Intent.ACTION_DIAL);
                            call.setData(Uri.parse("tel:09220635705"));
                            startActivity(call);
                        }
                    });

                    sms_tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", "09220635705", null)));
                        }
                    });


                    if (email.equals("")) {
                        sep1.setVisibility(View.GONE);
                        email_tv.setVisibility(View.GONE);
                    }
                    if (tel.equals("")) {
                        sep1.setVisibility(View.GONE);
                        email_tv.setVisibility(View.GONE);
                    }
                    DialogSheet dialogSheet = null;
                    dialogSheet = new DialogSheet(context)
                            .setView(view)
                            .setNegativeButton("بستن", new DialogSheet.OnNegativeClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                    dialogSheet.show();
                }


            }
        });


    }

    private void loadAgencyInfo(View view, boolean is_dialog) {
        if(is_agency){
            Ion.with(context)
                    .load(CONST.LOAD_AGENCY_INFO)
                    .setBodyParameter("id", String.valueOf(agency_id))
                    .asString()
                    .setCallback((e, result) -> {
                        progressDialog.dismiss();
                        if(e == null){
                            progressDialog.dismiss();
                            if(is_dialog){
                                showAgencyInfo(view, result);
                            }else{
                                fillAgencyData(result);
                            }
                        }else{
                            CONST.errorConectionToast(context);
                        }
                    });
        }else{
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, share_text);
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "ملک ارومیه");
            startActivity(Intent.createChooser(sharingIntent, "Share using"));
        }
    }

    private void fillAgencyData(String result) {
        JsonParser parser = new JsonParser();
        JsonObject object = parser.parse(result).getAsJsonObject();
        JsonObject agency = object.get("modir").getAsJsonObject();

        contact_email = " ایمیل : " + agency.get("email").getAsString();
        contact_tel = " تلفن تماس : " +  agency.get("tel").getAsString();
        contact_mobile = " موبایل: " +  agency.get("mobile").getAsString();
        contact_sms = " ارسال پیامک : " + agency.get("mobile").getAsString();

        share_text +=  "\uD83C\uDFE3️ " + "بنگاه املاک: " + agency.get("client_name").getAsString() +"\n" ;
        share_text +=  "☎️ " + "تلفن تماس : " + agency.get("tel").getAsString() +"\n" ;
        share_text +=  "☎️ " + "موبایل : " + agency.get("mobile").getAsString() +"\n" ;
        share_text +=  "-------------------\n\n" ;
        share_text +=  "لینک دانلود اپلیکیشن: " + "https://cafebazaar.ir/app/tmedia.ir.melkeurmia" ;


        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, share_text);
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "ملک ارومیه");
        startActivity(Intent.createChooser(sharingIntent, "Share using"));
    }

    private void hideOtagh() {
        otagh_view.setVisibility(View.GONE);
        otagh_view_sep.setVisibility(View.GONE);
    }

    private void showAgencyInfo(View view, String result) {
        JsonParser parser = new JsonParser();
        JsonObject object = parser.parse(result).getAsJsonObject();
        JsonObject agency = object.get("modir").getAsJsonObject();
        CircleImageView agency_avatar = view.findViewById(R.id.agency_avatar);
        TextView agency_name = view.findViewById(R.id.agency_name);
        TextView agency_flname = view.findViewById(R.id.agency_flname);
        TextView mobile_tv = view.findViewById(R.id.mobile_tv);
        TextView tel_tv = view.findViewById(R.id.tel_tv);
        TextView sms_tv = view.findViewById(R.id.sms_tv);
        TextView email_tv = view.findViewById(R.id.email_tv);
        View sep1 = view.findViewById(R.id.sep1);
        View sep2 = view.findViewById(R.id.sep2);

        Glide.with(context).load(CONST.BASE_URL +  agency.get("profile").getAsString()).into(agency_avatar);

        contact_email = " ایمیل : " + agency.get("email").getAsString();
        contact_tel = " تلفن تماس : " +  agency.get("tel").getAsString();
        contact_mobile = " موبایل: " +  agency.get("mobile").getAsString();
        contact_sms = " ارسال پیامک : " + agency.get("mobile").getAsString();

        share_text +=  "\uD83C\uDFE3️ " + "بنگاه املاک: " + agency.get("client_name").getAsString() +"\n" ;
        share_text +=  "☎️ " + "تلفن تماس : " + agency.get("tel").getAsString() +"\n" ;
        share_text +=  "☎️ " + "موبایل : " + agency.get("mobile").getAsString() +"\n" ;
        share_text +=  "-------------------\n\n" ;
        share_text +=  "لینک دانلود اپلیکیشن: " + "https://cafebazaar.ir/app/tmedia.ir.melkeurmia" ;


        agency_name.setText(agency.get("client_name").getAsString());
        agency_flname.setText(agency.get("fl_name").getAsString());
        mobile_tv.setText(contact_mobile);
        tel_tv.setText(contact_tel);
        email_tv.setText(contact_email);
        sms_tv.setText(contact_sms);

        email_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto" , contact_email, null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, ""));
            }
        });
        tel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", contact_tel, null)));
            }
        });
        mobile_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", contact_mobile, null)));
            }
        });

        sms_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + contact_mobile));
                intent.putExtra("sms_body", "");
                startActivity(intent);
            }
        });

        DialogSheet dialogSheet = null;
        dialogSheet = new DialogSheet(context)
                .setView(view)
                .setNegativeButton("بستن", v -> {

                });
        dialogSheet.show();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
