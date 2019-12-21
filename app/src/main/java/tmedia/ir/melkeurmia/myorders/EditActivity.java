package tmedia.ir.melkeurmia.myorders;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.BasicNameValuePair;
import com.koushikdutta.async.http.NameValuePair;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;
import com.linchaolong.android.imagepicker.ImagePicker;
import com.linchaolong.android.imagepicker.cropper.CropImage;
import com.linchaolong.android.imagepicker.cropper.CropImageView;
import com.marcoscg.dialogsheet.DialogSheet;
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo;
import com.miguelbcr.ui.rx_paparazzo2.entities.FileData;
import com.miguelbcr.ui.rx_paparazzo2.entities.Response;
import com.miguelbcr.ui.rx_paparazzo2.entities.size.ScreenSize;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.model.AspectRatio;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import th.in.lordgift.widget.EditTextIntegerComma;
import tmedia.ir.melkeurmia.InsertOrderActivity;
import tmedia.ir.melkeurmia.MapLocationActivity;
import tmedia.ir.melkeurmia.R;
import tmedia.ir.melkeurmia.otto.AppEvents;
import tmedia.ir.melkeurmia.otto.GlobalBus;
import tmedia.ir.melkeurmia.tools.AppSharedPref;
import tmedia.ir.melkeurmia.tools.CONST;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class EditActivity extends AppCompatActivity {
    private static final int LOCATION_ACTIVITY_RESULT_CODE = 21321;
    private Context context = this;

    private Button category_selector_btn;

    private LinearLayout category_selector, form_container, general_container, general_price_con, general_type_con,general_metraj_con, general_select_image_con, general_otagh_con, general_tel_con, general_title_con, general_desc_con,general_price_form_con, ejareh_con, vadie_con;

    private EditTextIntegerComma general_price,ejareh, vadie;
    private EditText general_otagh, general_tel, general_title, general_desc,general_metraj;

    private Button general_price_type_btn, general_type_btn, general_select_image,city_selector_btn,location_selector_btn;

    private ArrayList<String> pathList = new ArrayList<>();
    private ArrayList<String> web_pathList = new ArrayList<>();
    private ArrayList<String> final_path = new ArrayList<>();
    private View viewItemSelected;
    private ImageView btnDelete;
    private HorizontalScrollView horizontalScrollView;
    private LinearLayout root;
    private LinearLayout layoutListItemSelect;
    private BitmapFactory.Options options;
    private FrameLayout.LayoutParams imageView_lp;


    private Integer selected_cat_value = 0;
    private Integer price_type_send;
    private String price_send;
    private Integer general_type_send;
    private String general_otagh_send;
    private String general_tel_send;
    private String general_title_send;
    private String general_desc_send;
    private String ejareh_send;
    private String vadieh_send;
    private List<NameValuePair> send_paramas = new ArrayList<NameValuePair>();
    private boolean allow_next = false;
    private boolean is_edit = false;
    private int edit_order_id = 0;
    private ProgressDialog progressDialog;
    private int select_id;
    private boolean can_send = false;
    private ImagePicker imagePicker = new ImagePicker();
    private String general_metraj_send = "";

    private String selected_location;
    private String location_map_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("ویرایش آگهی");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        getLayouts();
        category_selector_btn = (Button) findViewById(R.id.category_selector_btn);


        //for image picker
        options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        imageView_lp = new FrameLayout.LayoutParams(280, 280);
        imageView_lp.setMargins(10, 15, 10, 15);

        select_id = getIntent().getExtras().getInt("id");
        edit_order_id = select_id;
        loadOrderInfo(select_id);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.fontpath))
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        city_selector_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CONST.showInsertProvinceCitySelector(EditActivity.this, EditActivity.this, new CONST.onCitySelect() {
                    @Override
                    public void onCitySelect() {
                        if (AppSharedPref.read("CITY_NAME", "") != null) {
                            city_selector_btn.setText(AppSharedPref.read("CITY_NAME", ""));
                        }
                    }
                });
            }
        });
    }


    private void loadOrderInfo(int select_id) {
        progressDialog.show();
        Ion.with(context)
                .load(CONST.GET_ORDER_EDIT)
                .setBodyParameter("order_id", String.valueOf(select_id))
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {


                        progressDialog.dismiss();
                        if (e == null) {
                            JsonParser parser = new JsonParser();
                            JsonObject obj = parser.parse(result).getAsJsonObject();
                            if (obj.has("status")) {
                                String status = obj.get("status").getAsString();
                                if (status.equals("found")) {
                                    fillFields(obj);

                                }
                            }
                        } else {
                            finish();
                            Toasty.error(context, "خطا در برقراری ارتباط با سرور", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void getLayouts() {
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
        category_selector = (LinearLayout) findViewById(R.id.category_selector);
        general_price_type_btn = (Button) findViewById(R.id.general_price_type_btn);
        general_type_btn = (Button) findViewById(R.id.general_type_btn);
        general_select_image = findViewById(R.id.general_select_image);
        city_selector_btn = findViewById(R.id.city_selector_btn);
        location_selector_btn = findViewById(R.id.location_selector_btn);
        form_container = findViewById(R.id.form_container);
        general_container = findViewById(R.id.general_container);
        general_price = findViewById(R.id.general_price);
        general_tel = findViewById(R.id.general_tel);
        general_title = findViewById(R.id.general_title);
        general_desc = findViewById(R.id.general_desc);
        general_metraj = findViewById(R.id.general_metraj);
        general_otagh= findViewById(R.id.general_otagh);
        general_price_con = findViewById(R.id.general_price_con);
        general_price_form_con = findViewById(R.id.general_price_form_con);
        general_type_con = findViewById(R.id.general_type_con);
        general_metraj_con = findViewById(R.id.general_metraj_con);
        general_select_image_con = (LinearLayout) findViewById(R.id.general_select_image_con);
        general_otagh_con = (LinearLayout) findViewById(R.id.general_otagh_con);
        general_tel_con = (LinearLayout) findViewById(R.id.general_tel_con);
        general_title_con = (LinearLayout) findViewById(R.id.general_title_con);
        general_desc_con = (LinearLayout) findViewById(R.id.general_desc_con);
        layoutListItemSelect = (LinearLayout) findViewById(R.id.layoutListItemSelect);
        vadie = (EditTextIntegerComma) findViewById(R.id.vadie);
        ejareh = (EditTextIntegerComma) findViewById(R.id.ejareh);
        ejareh_con = (LinearLayout) findViewById(R.id.ejareh_con);
        vadie_con = (LinearLayout) findViewById(R.id.vadie_con);


        progressDialog = new ProgressDialog(context);
        horizontalScrollView.setVisibility(View.GONE);
        form_container.setVisibility(View.GONE);

    }

    private void showProgress(String msg) {
        progressDialog.setMessage(msg);
        progressDialog.show();
        progressDialog.setCancelable(false);
    }

    private void showTargetForm(int id) {
        showGeneralForm(id);
    }


    private void showGeneralForm(int id) {
        initilizeFields();
        form_container.setVisibility(View.VISIBLE);
        general_container.setVisibility(View.VISIBLE);
        general_type_con.setVisibility(View.VISIBLE);
        general_price_con.setVisibility(View.VISIBLE);
    }

    private void checkForm() {

        if (!category_selector_btn.getText().equals("انتخاب")) {
            if (checkGeneralField()) {
                sendMainform();
            }
        } else {
            showErrorCon(category_selector);
            Toasty.error(context, "لطفا دسته بندی مورد نظر را انتخاب کنید", Toast.LENGTH_LONG, true).show();
        }
    }


    private boolean checkEjare() {
        if (ejareh_con.getVisibility() == View.VISIBLE) {
            if (ejareh.getText().toString().trim().equals("")) {
                showErrorCon(ejareh_con);
                Toasty.error(context, "لطفا ودیعه مورد نظر را وارد کنید.", Toast.LENGTH_LONG, true).show();
                return false;
            } else {
                ejareh_send = ejareh.getValue().toString();
                price_send = "";
                return true;
            }
        }else{
            return  true;
        }
    }

    private boolean checkVadieh() {
        if (vadie_con.getVisibility() == View.VISIBLE) {
            if (vadie.getText().toString().trim().equals("")) {
                showErrorCon(vadie_con);
                Toasty.error(context, "لطفا اجاره مورد نظر را وارد کنید.", Toast.LENGTH_LONG, true).show();
                return false;
            } else {
                vadieh_send = vadie.getValue().toString();
                price_send = "";
                return true;
            }
        } else {
            return true;
        }
    }


    private boolean checkDescTitle() {
        if (general_desc.getText().toString().trim().length() < 1) {
            showErrorCon(general_desc_con);
            Toasty.error(context, "لطفا توضیحات آگهی را وارد نمایید.", Toast.LENGTH_LONG, true).show();
            return false;
        } else {
            general_desc_send = general_desc.getText().toString().trim();
            return true;
        }
    }

    private boolean checkTitleField() {
        if (general_title.getText().toString().length() < 1) {
            showErrorCon(general_title_con);
            Toasty.error(context, "لطفا عنوان آگهی را وارد نمایید.", Toast.LENGTH_LONG, true).show();
            return false;
        } else {
            general_title_send = general_title.getText().toString().trim();
            return true;
        }
    }

    private boolean checkTelField() {
        String mobile_str = general_tel.getText().toString();
        boolean is_mobile_ok = Pattern.matches("09(0(\\d)|1(\\d)|2(\\d)|3(\\d)|(9(\\d)))\\d{7}$", mobile_str);
        if (is_mobile_ok) {
            general_tel_send = mobile_str;
            return true;
        } else {
            showErrorCon(general_tel_con);
            Toasty.error(context, "لطفا موبایل خود رو بصورت صحیح وارد کنید.", Toast.LENGTH_LONG, true).show();
            return false;
        }
    }

    private boolean checkOtagh() {
        if (general_otagh.getText().toString().length() < 1) {
            showErrorCon(general_otagh_con);
            Toasty.error(EditActivity.this, "لطفا تعداد اتاق را وارد نمایید.", Toast.LENGTH_LONG, true).show();
            return false;
        } else {
            general_otagh_send = general_otagh.getText().toString().trim();
            return true;
        }
    }


    private boolean checkMetraj() {
        if (general_metraj.getText().toString().length() < 1) {
            showErrorCon(general_metraj_con);
            Toasty.error(EditActivity.this, "لطفا متراژ مورد نظر را وارد نمایید.", Toast.LENGTH_LONG, true).show();
            return false;
        } else {
            general_metraj_send = general_metraj.getText().toString().trim();
            return true;
        }
    }

    private boolean checkOrderType() {
        if (general_type_send == null) {
            showErrorCon(general_type_con);
            Toasty.error(context, "لطفا نوع آگهی را انتخاب کنید.", Toast.LENGTH_LONG, true).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkPriceField() {
        if (price_type_send != null) {
            if (price_type_send == 0) {
                if (general_price.getText().toString().trim().equals("")) {
                    showErrorCon(general_price_con);
                    Toasty.error(context, "لطفا قیمت مورد نظر را وارد کنید.", Toast.LENGTH_LONG, true).show();
                    return false;
                } else {
                    price_send = general_price.getValue().toString();
                    return true;
                }
            } else {
                price_send = general_price.getValue().toString();
                return true;
            }
        } else {
            showErrorCon(general_price_con);
            Toasty.error(context, "لطفا نوع قیمت را انتخاب کنید.", Toast.LENGTH_LONG, true).show();
            return false;
        }
    }

    private void showErrorCon(final LinearLayout view) {
        int colorFrom = getResources().getColor(R.color.white);
        int colorTo = getResources().getColor(R.color.error_container_color);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(500); // milliseconds
        colorAnimation.setRepeatCount(1);
        colorAnimation.setRepeatMode(ValueAnimator.REVERSE);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                view.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });
        colorAnimation.start();
    }

    private boolean checkGeneralField() {
        if (checkOrderType()) {
            if (checkPriceField()) {
                if (checkVadieh()) {
                    if (checkEjare()) {
                        if (checkOtagh()) {
                            if (checkMetraj()) {
                                if (checkTelField()) {
                                    if (checkTitleField()) {
                                        if (checkDescTitle()) {
                                            addGeneralField();
                                            allow_next = true;
                                            return true;
                                        } else {
                                            return false;
                                        }
                                    } else {
                                        return false;
                                    }
                                } else {
                                    return false;
                                }
                            } else {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    private void initilizeFields() {
        initilizePriceField();
        initilizeTypeField();
        initilizeImagePickerField();
    }

    private void initilizeImagePickerField() {
        general_select_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePickerIntent();
            }
        });
    }

    private void initilizePriceField() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        general_price_form_con.setVisibility(View.GONE);
        TextView title = new TextView(context);
        title.setText("نوع قیمت");
        title.setPadding(20, 20, 20, 20);
        title.setGravity(Gravity.RIGHT);
        title.setTextSize(18);
        alert.setCustomTitle(title);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_item);
        arrayAdapter.add("مقطوع");
        arrayAdapter.add("معاوضه");
        arrayAdapter.add("توافقی");

        alert.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                price_type_send = which+1;
                general_price_type_btn.setText(arrayAdapter.getItem(which).toString());
                if (which != 0) {
                    general_price_form_con.setVisibility(View.GONE);
                } else {
                    general_price_form_con.setVisibility(View.VISIBLE);
                }
            }
        });


        final AlertDialog alertd = alert.create();

        general_price_type_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertd.show();
            }
        });
    }

    private void initilizeTypeField() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);

        TextView title = new TextView(context);
        title.setText("نوع آگهی");
        title.setPadding(20, 20, 20, 20);
        title.setGravity(Gravity.RIGHT);
        title.setTextSize(18);
        alert.setCustomTitle(title);


        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_item);
        arrayAdapter.add("فروشی");
        arrayAdapter.add("درخواستی");
        arrayAdapter.add("اجاره");

        alert.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                general_type_btn.setText(arrayAdapter.getItem(which).toString());
                general_type_send = which+1;
                switch (which) {
                    case 0:
                        vadie_con.setVisibility(View.GONE);
                        ejareh_con.setVisibility(View.GONE);
                        general_price_con.setVisibility(View.VISIBLE);
                        vadieh_send = "";
                        ejareh_send = "";
                        break;
                    case 1:
                        vadie_con.setVisibility(View.GONE);
                        ejareh_con.setVisibility(View.GONE);
                        general_price_con.setVisibility(View.GONE);
                        vadieh_send = "";
                        ejareh_send = "";
                        price_send = "";
                        break;
                    case 2:
                        vadie_con.setVisibility(View.VISIBLE);
                        ejareh_con.setVisibility(View.VISIBLE);
                        general_price_con.setVisibility(View.GONE);
                        price_send = "";
                        break;
                }
            }
        });


        final AlertDialog alertd = alert.create();

        general_type_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertd.show();
            }
        });
    }


    private void openImagePickerIntent() {

        UCrop.Options options = new UCrop.Options();
        options.setToolbarTitle("ویرایش تصویر");
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setFreeStyleCropEnabled(false);
        options.setAspectRatioOptions(0, new AspectRatio("1:1", 1f, 1f));

        new DialogSheet(this)
                .setTitle("انتخاب تصویر")
                .setMessage("لطفا توجه داشته باشید در صورتی که تصویر شما شرایط لازم جهت انتشار را نداشته باشد حذف خواهد شد.")
                .setCancelable(true)
                .setPositiveButton("گالری", new DialogSheet.OnPositiveClickListener() {
                    @Override
                    public void onClick(View v) {
                        //imagePicker.startGallery(InsertOrderActivity.this, callback);



                        RxPaparazzo.single(EditActivity.this)
                                .crop(options)
                                .size(new ScreenSize())
                                .usingGallery()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<Response<EditActivity, FileData>>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(Response<EditActivity, FileData> editActivityFileDataResponse) {
                                        if(editActivityFileDataResponse.data() != null){
                                            finilizeImage(editActivityFileDataResponse.data().getFile().toString());
                                        }
                                    }


                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });

                    }
                })
                .setNegativeButton("دوربین", new DialogSheet.OnNegativeClickListener() {
                    @Override
                    public void onClick(View v) {
                        RxPaparazzo.single(EditActivity.this)
                                .crop(options)
                                .size(new ScreenSize())
                                .usingCamera()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<Response<EditActivity, FileData>>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(Response<EditActivity, FileData> editActivityFileDataResponse) {
                                        if(editActivityFileDataResponse.data() != null){
                                            finilizeImage(editActivityFileDataResponse.data().getFile().toString());
                                        }
                                    }


                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });

                    }
                })
                .setButtonsColorRes(R.color.colorPrimary)
                .show();
    }

    private void finilizeImage(String imageUri) {
        horizontalScrollView.setVisibility(View.VISIBLE);
        viewItemSelected = getLayoutInflater().inflate(R.layout.piclist_item_selected, layoutListItemSelect, false);
        viewItemSelected.setTag(imageUri);
        btnDelete = (ImageView) viewItemSelected.findViewById(R.id.btnDelete);
        pathList.add(imageUri);
        calcbtnDelete();
        viewItemSelected.setId(0);
        ImageView imageItem = (ImageView) viewItemSelected.findViewById(R.id.imageItem);
        imageItem.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageItem.setLayoutParams(imageView_lp);

        Uri img_uri = Uri.parse(imageUri);
        imageItem.setImageURI(img_uri);
        layoutListItemSelect.addView(viewItemSelected);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == LOCATION_ACTIVITY_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                location_selector_btn.setSelected(true);
                JSONObject obj = new JSONObject();
                try {
                    obj.put("lat", bundle.getDouble("lat"));
                    obj.put("lng", bundle.getDouble("lng"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                location_map_send = obj.toString();
                location_selector_btn.setText("انتخاب شده");


            }
        }

        super.onActivityResult(requestCode, resultCode, data);
        imagePicker.onActivityResult(this, requestCode, resultCode, data);
    }

    private void addGeneralField() {
        String user_id = AppSharedPref.read("ID", "0");
        Date curDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String DateToStr = format.format(curDate);



        send_paramas.add(new BasicNameValuePair("cat_id", String.valueOf(selected_cat_value)));
        send_paramas.add(new BasicNameValuePair("province_id", AppSharedPref.read("CITY_ID", "")));
        send_paramas.add(new BasicNameValuePair("title", general_title_send));
        send_paramas.add(new BasicNameValuePair("desc", general_desc_send));
        send_paramas.add(new BasicNameValuePair("metraj", general_metraj_send));
        send_paramas.add(new BasicNameValuePair("tel", general_tel_send));
        send_paramas.add(new BasicNameValuePair("title", general_title_send));
        send_paramas.add(new BasicNameValuePair("otagh", general_otagh_send));
        send_paramas.add(new BasicNameValuePair("rahn_price", vadieh_send));
        send_paramas.add(new BasicNameValuePair("ejareh_price", ejareh_send));
        send_paramas.add(new BasicNameValuePair("price", price_send));
        send_paramas.add(new BasicNameValuePair("selected_client", user_id));
        send_paramas.add(new BasicNameValuePair("status", "disabled"));
        send_paramas.add(new BasicNameValuePair("location", location_map_send));
        send_paramas.add(new BasicNameValuePair("price_type", String.valueOf(price_type_send)));
        send_paramas.add(new BasicNameValuePair("general_type", String.valueOf(general_type_send)));


    }

    private void sendMainform() {
        final_path.clear();
        int total = layoutListItemSelect.getChildCount() - web_pathList.size();
        int _temp = 0;

        uploaddata();
        /*if(checkhavelocalimage()){
            for (int i = 0; i < layoutListItemSelect.getChildCount(); i++) {
                View item = layoutListItemSelect.getChildAt(i);
                String tag = (String) item.getTag();
                if (!tag.substring(0, 6).equals("images")) {
                    _temp++;
                    DecodeFileAsync task = new DecodeFileAsync(tag, total);
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, _temp);
                }
            }
        }else{

        }*/


    }

    private void uploaddata() {
        String app_token = AppSharedPref.read("TOKEN", "");
        byte[] data = Base64.decode(app_token, Base64.DEFAULT);

        progressDialog.dismiss();
        progressDialog.setMessage("در حال ارسال آگهی");
        progressDialog.show();
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
                                    Builders.Any.B ionBuilder;

                                    ionBuilder = Ion.with(context).load("POST", CONST.EDIT_ORDER_V2);
                                    ionBuilder.setMultipartParameter("order_id", String.valueOf(edit_order_id));

                                    for (int i = 0; i < send_paramas.size(); i++) {
                                        ionBuilder.setMultipartParameter(send_paramas.get(i).getName(), send_paramas.get(i).getValue());
                                    }




                                    ionBuilder.setHeader("Authorization", "Bearer " + token);
                                    for (int j = 0; j < pathList.size(); j++) {
                                        File file = new File(pathList.get(j));
                                        String name = "img_" + j;
                                        ionBuilder.setMultipartFile(name, "image//*", file);
                                    }

                                    for (int  m = 0; m < web_pathList.size(); m++) {
                                        String name = web_pathList.get(m);
                                        ionBuilder.setMultipartParameter("keepimg[]",name);

                                    }


                                    ionBuilder.asString().withResponse().setCallback(new FutureCallback<com.koushikdutta.ion.Response<String>>() {
                                        @Override
                                        public void onCompleted(Exception e, com.koushikdutta.ion.Response<String> result) {
                                            progressDialog.dismiss();

                                            if (e == null) {
                                                JsonParser parser = new JsonParser();
                                                JsonObject json_obj = parser.parse(result.getResult()).getAsJsonObject();
                                                if (json_obj.has("status")) {
                                                    if (json_obj.get("status").getAsString().equals("ok")) {
                                                        finish();
                                                    } else {
                                                        Toasty.error(context, "خطا در ویرایش آگهی لطفا، دوباره تلاش کنید", Toast.LENGTH_LONG).show();
                                                    }
                                                } else {
                                                    Toasty.error(context, "خطا در ویرایش آگهی لطفا، دوباره تلاش کنید", Toast.LENGTH_LONG).show();
                                                }
                                            }else{
                                                Toasty.error(context, "خطا در برفراری ارتباط با سرور", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                                }
                            } else {
                                progressDialog.dismiss();
                                Toasty.error(context, "خطا در برفراری ارتباط با سرور", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }


    private void fillFields(JsonObject obj) {
        JsonObject order = obj.get("order").getAsJsonObject();

        showTargetForm(order.get("cat_id").getAsInt());
        selected_cat_value = order.get("cat_id").getAsInt();
        String cat_name = obj.get("cat_name").getAsString();
        category_selector_btn.setText(cat_name);
        category_selector_btn.setEnabled(false);
        category_selector.setEnabled(false);

        city_selector_btn.setText(order.get("province_name").getAsString());
        city_selector_btn.setSelected(true);


        location_selector_btn.setOnClickListener(v -> {
            Intent i = new Intent(EditActivity.this, MapLocationActivity.class);
            if(!order.get("location").isJsonNull()){
                i.putExtra("location", selected_location.toString());
            }
            startActivityForResult(i, LOCATION_ACTIVITY_RESULT_CODE);
        });

        if(!order.get("location").isJsonNull()){
            location_selector_btn.setText("آخرین بازدیدها");
            location_selector_btn.setSelected(true);
            selected_location = order.get("location").getAsString();
        }

        int general_type_value = order.get("general_type").isJsonNull() ? 0 : order.get("general_type").getAsInt();
        int price_type_value = order.get("price_type").isJsonNull() ? 0 : order.get("price_type").getAsInt();
        int price_value = order.get("price").isJsonNull() ? 0 : order.get("price").getAsInt();
        String tel_value = order.get("tel").isJsonNull() ? "" : order.get("tel").getAsString();
        String title_value = order.get("title").isJsonNull() ? "" : order.get("title").getAsString();
        String desc_value = order.get("desc").isJsonNull() ? "" : order.get("desc").getAsString();
        String otagh_value = order.get("otagh").isJsonNull() ? "" : order.get("otagh").getAsString();

        if(!order.get("metraj").isJsonNull()){
            general_metraj_send = order.get("metraj").getAsString();
        }else{
            general_metraj_send = "";
        }

        price_send = String.valueOf(price_value);
        price_type_send = price_type_value;
        general_type_send = general_type_value;
        general_otagh_send = otagh_value;
        general_tel_send = tel_value;
        general_title_send = title_value;
        general_desc_send = desc_value;


       //vadie.setText(String.valueOf(amlak_vadieh_value));
        //ejareh.setText(String.valueOf(amlak_ejare_value));

        general_price.setText(String.valueOf(price_value));
        general_price.setText(String.valueOf(price_value));
        general_otagh.setText(String.valueOf(otagh_value));
        general_tel.setText(String.valueOf(tel_value));
        general_title.setText(String.valueOf(title_value));
        general_desc.setText(String.valueOf(general_desc_send));
        general_metraj.setText(general_metraj_send);

        if (price_type_value == 1) {
            general_price_type_btn.setText("مقطوع");
            general_price_form_con.setVisibility(View.VISIBLE);
        } else if (price_type_value == 2) {
            general_price_type_btn.setText("معاوضه");
            general_price_form_con.setVisibility(View.GONE);
        } else if (price_type_value == 3) {
            general_price_type_btn.setText("توافقی");
            general_price_form_con.setVisibility(View.GONE);
        }
        if (general_type_value == 1) {
            general_type_btn.setText("فروشی");
            vadie_con.setVisibility(View.GONE);
            ejareh_con.setVisibility(View.GONE);
            general_price_con.setVisibility(View.VISIBLE);
        } else if (general_type_value == 2) {
            general_type_btn.setText("درخواستی");
            vadie_con.setVisibility(View.GONE);
            ejareh_con.setVisibility(View.GONE);
            general_price_con.setVisibility(View.GONE);
            vadieh_send = "";
            ejareh_send = "";
            price_send = "";
        }else if (general_type_value == 3) {
            general_type_btn.setText("اجاره");
            vadie_con.setVisibility(View.VISIBLE);
            ejareh_con.setVisibility(View.VISIBLE);
            general_price_con.setVisibility(View.GONE);
            price_send = "";

            vadieh_send = order.get("rahn_price").getAsString();
            ejareh_send = order.get("ejareh_price").getAsString();

            vadie.setText(vadieh_send);
            ejareh.setText(ejareh_send);
        }

        fillAttachments(order.get("attachments").getAsJsonArray());
    }

    private void fillAttachments(JsonArray attachments) {
        //http://192.168.115.1/ahmadian/storage/app/
        //http://192.168.115.1/ahmadian/storage/app/users/user1/img_##.jpg

        GradientDrawable border = new GradientDrawable();
        border.setColor(0xFFFFFFFF); //white background
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            border.setStroke(3, getColor(R.color.primary)); //black border with full opacity
        } else {
            border.setStroke(3, getResources().getColor(R.color.primary)); //black border with full opacity
        }

        if (attachments.size() > 0) {
            horizontalScrollView.setVisibility(View.VISIBLE);
            for (int i = 0; i < attachments.size(); i++) {
                viewItemSelected = getLayoutInflater().inflate(R.layout.piclist_item_selected, layoutListItemSelect, false);
                btnDelete = (ImageView) viewItemSelected.findViewById(R.id.btnDelete);
                calcbtnDelete();
                viewItemSelected.setId(i);

                String name = attachments.get(i).getAsString().replace("\"", "");
                viewItemSelected.setTag(name);
                ImageView imageItem = (ImageView) viewItemSelected.findViewById(R.id.imageItem);
                web_pathList.add(name);
                Glide.with(context)
                        .load(CONST.STORAGE + attachments.get(i).getAsString().replace("\"", ""))
                        .skipMemoryCache(true)
                        .into(imageItem);
                imageItem.setScaleType(ImageView.ScaleType.FIT_CENTER);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    imageItem.setBackgroundDrawable(border);
                } else {
                    imageItem.setBackground(border);
                }
                imageItem.setLayoutParams(imageView_lp);
                imageItem.getLayoutParams().width = 100;
                imageItem.getLayoutParams().height = 100;

                layoutListItemSelect.addView(viewItemSelected);
            }
        }
    }

    private void calcbtnDelete() {
        if (btnDelete != null) {
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ViewGroup parent = (ViewGroup) v.getParent().getParent().getParent();
                    View child = (View) v.getParent().getParent();
                    String tag = (String) child.getTag();
                    parent.removeView(child);

                    if (tag.substring(0,6).equals("orders")) {
                        web_pathList.remove(tag);
                    } else {
                        pathList.remove(tag);
                    }

                    if (parent.getChildCount() < 1) {
                        horizontalScrollView.setVisibility(View.GONE);
                    }

                }
            });
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
        getMenuInflater().inflate(R.menu.activity_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save_edit) {
            checkForm();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
