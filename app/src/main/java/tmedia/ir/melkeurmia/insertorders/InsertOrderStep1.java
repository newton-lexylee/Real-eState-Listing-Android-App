package tmedia.ir.melkeurmia.insertorders;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.BasicNameValuePair;
import com.koushikdutta.async.http.NameValuePair;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;
import com.squareup.otto.Subscribe;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;
import th.in.lordgift.widget.EditTextIntegerComma;
import tmedia.ir.melkeurmia.MapLocationActivity;
import tmedia.ir.melkeurmia.R;
import tmedia.ir.melkeurmia.insertorders.categoryselector.NewCategorySelector;
import tmedia.ir.melkeurmia.interfaces.CategoryCallback;
import tmedia.ir.melkeurmia.otto.AppEvents;
import tmedia.ir.melkeurmia.otto.GlobalBus;
import tmedia.ir.melkeurmia.tools.AppSharedPref;
import tmedia.ir.melkeurmia.tools.CONST;


public class InsertOrderStep1 extends Fragment implements BlockingStep, CategoryCallback {


    private static final int LOCATION_ACTIVITY_RESULT_CODE = 12213;
    private View rootView;
    private Button category_selector_btn;
    private int TAP_THRESHOLD = 2;
    private LinearLayout city_selector, category_selector, form_container, general_container, general_price_con, general_type_con, general_otagh_con, general_select_image_con, general_metraj_con, general_tel_con, general_title_con, general_desc_con, general_price_form_con, ejareh_con, vadie_con;
    private EditTextIntegerComma general_price, ejareh, vadie;
    private EditText general_metraj, general_otagh, general_tel, general_title, general_desc;
    private AlertDialog.Builder prict_alert;
    private Button city_selector_btn,location_selector_btn, general_price_type_btn, general_type_btn, general_select_image;
    private int form_catehory_id, form_price_type, form_price;
    private static final int READ_STORAGE_CODE = 1001;
    private static final int WRITE_STORAGE_CODE = 1002;
    private ArrayList<String> pathList = new ArrayList<>();
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
    private String general_otagh_send;
    private Integer general_type_send;
    private String general_metraj_send;
    private String general_tel_send;
    private String general_title_send;
    private String general_desc_send;
    private String ejareh_send;
    private String vadieh_send;
    private List<NameValuePair> send_paramas = new ArrayList<NameValuePair>();
    private boolean allow_next = false;
    private boolean is_edit = false;
    private int edit_order_id = 0;
    private static int OPEN_CATEGORY_MENU = 25841;
    private ProgressDialog progressDialog;
    private RadioGroup general_type_rg;
    private RadioGroup general_price_rg;
    private RadioButton general_price_maghtoo;

    private String location_map_send;
    private boolean hide_otagh = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.insert_order_step_1, container, false);

        layoutListItemSelect = rootView.findViewById(R.id.layoutListItemSelect);
        getLayouts();
        category_selector_btn = rootView.findViewById(R.id.category_selector_btn);
        city_selector_btn = rootView.findViewById(R.id.city_selector_btn);
        location_selector_btn = rootView.findViewById(R.id.location_selector_btn);


        category_selector_btn.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), NewCategorySelector.class);
            startActivityForResult(i, InsertOrderStep1.OPEN_CATEGORY_MENU);
        });

        String city_name = AppSharedPref.read("CITY_NAME", "");
        if (!city_name.equals("")) {
            city_selector_btn.setText(city_name);
            city_selector_btn.setSelected(true);
        }

        city_selector_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CONST.showInsertProvinceCitySelector(getActivity(), getContext(), new CONST.onCitySelect() {
                    @Override
                    public void onCitySelect() {
                        if (AppSharedPref.read("CITY_NAME", "") != null) {
                            city_selector_btn.setText(AppSharedPref.read("CITY_NAME", ""));
                        }
                    }
                });
            }
        });


        location_selector_btn.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), MapLocationActivity.class);
            startActivityForResult(i, LOCATION_ACTIVITY_RESULT_CODE);
        });

        //for image picker
        options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        imageView_lp = new FrameLayout.LayoutParams(280, 280);
        imageView_lp.setMargins(10, 0, 10, 0);

        return rootView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == LOCATION_ACTIVITY_RESULT_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
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

        for (Fragment fragment : getChildFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == InsertOrderStep1.OPEN_CATEGORY_MENU) {
            if (resultCode == Activity.RESULT_OK) {
                String label = data.getStringExtra("label");
                int id = data.getIntExtra("id", 0);
                //returnIntent.putExtra("result",result);
                nullAllParams();
                selected_cat_value = id;
                if (label.length() > 0) {
                    category_selector_btn.setText(label);
                    category_selector_btn.setSelected(true);
                    showGeneralForm();
                }

                switch (id){
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
                    default:
                        hide_otagh = false;
                        break;
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private void getLayouts() {
        horizontalScrollView = (HorizontalScrollView) rootView.findViewById(R.id.horizontalScrollView);
        category_selector = (LinearLayout) rootView.findViewById(R.id.category_selector);
        general_price_type_btn = (Button) rootView.findViewById(R.id.general_price_type_btn);
        general_price_type_btn.setVisibility(View.GONE);
        general_type_btn = (Button) rootView.findViewById(R.id.general_type_btn);
        general_type_btn.setVisibility(View.GONE);
        general_select_image = (Button) rootView.findViewById(R.id.general_select_image);
        form_container = (LinearLayout) rootView.findViewById(R.id.form_container);
        general_container = (LinearLayout) rootView.findViewById(R.id.general_container);
        general_price = (EditTextIntegerComma) rootView.findViewById(R.id.general_price);
        general_tel = (EditText) rootView.findViewById(R.id.general_tel);
        general_title = (EditText) rootView.findViewById(R.id.general_title);
        general_desc = (EditText) rootView.findViewById(R.id.general_desc);
        general_metraj = (EditText) rootView.findViewById(R.id.general_metraj);
        general_otagh = (EditText) rootView.findViewById(R.id.general_otagh);
        general_price_con = (LinearLayout) rootView.findViewById(R.id.general_price_con);
        general_price_form_con = (LinearLayout) rootView.findViewById(R.id.general_price_form_con);
        ejareh_con = (LinearLayout) rootView.findViewById(R.id.ejareh_con);
        vadie_con = (LinearLayout) rootView.findViewById(R.id.vadie_con);
        general_type_con = (LinearLayout) rootView.findViewById(R.id.general_type_con);
        general_otagh_con = (LinearLayout) rootView.findViewById(R.id.general_otagh_con);
        general_select_image_con = (LinearLayout) rootView.findViewById(R.id.general_select_image_con);
        general_metraj_con = (LinearLayout) rootView.findViewById(R.id.general_metraj_con);
        general_tel_con = (LinearLayout) rootView.findViewById(R.id.general_tel_con);
        general_title_con = (LinearLayout) rootView.findViewById(R.id.general_title_con);
        general_desc_con = (LinearLayout) rootView.findViewById(R.id.general_desc_con);
        vadie = (EditTextIntegerComma) rootView.findViewById(R.id.vadie);
        ejareh = (EditTextIntegerComma) rootView.findViewById(R.id.ejareh);
        general_type_rg = rootView.findViewById(R.id.general_type_rg);
        general_price_rg = rootView.findViewById(R.id.general_price_rg);
        progressDialog = new ProgressDialog(getContext());

        general_price_maghtoo = rootView.findViewById(R.id.general_price_maghtoo);
        ejareh_con.setVisibility(View.GONE);
        vadie_con.setVisibility(View.GONE);



        general_price_con.setVisibility(View.GONE);
    }

    private void showProgress(String msg) {
        progressDialog.setMessage(msg);
        progressDialog.show();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        horizontalScrollView.setVisibility(View.GONE);
        form_container.setVisibility(View.GONE);
    }


    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {
        //handle error inside of the fragment, e.g. show error on EditText
    }

    @Override
    public void onCategoryChoose(int id, String value) {
        nullAllParams();
        selected_cat_value = id;
        if (value.length() > 0) {
            category_selector_btn.setText(value);
            category_selector_btn.setSelected(true);
            showGeneralForm();
        }
    }

    private void showGeneralForm() {
        initilizeFields();
        form_container.setVisibility(View.VISIBLE);
        general_container.setVisibility(View.VISIBLE);
        general_type_con.setVisibility(View.VISIBLE);

    }

    private void initilizeFields() {
        initilizeTypeField();
        initilizePriceField();
        initilizeImagePickerField();
    }

    private void initilizeImagePickerField() {
        general_select_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppEvents.openImagePicker id_event = new AppEvents.openImagePicker();
                GlobalBus.getBus().post(id_event);
            }
        });
    }

    private void initilizeTypeField() {

        general_type_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {



                switch (checkedId) {
                    case -1:
                        break;
                    case R.id.general_type_sell:
                        general_type_send = 1;
                        vadie_con.setVisibility(View.GONE);
                        ejareh_con.setVisibility(View.GONE);
                        general_price_con.setVisibility(View.VISIBLE);
                        vadieh_send = "";
                        ejareh_send = "";
                        break;
                    case R.id.general_type_request:
                        general_type_send = 2;
                        vadie_con.setVisibility(View.GONE);
                        ejareh_con.setVisibility(View.GONE);
                        general_price_con.setVisibility(View.GONE);
                        vadieh_send = "";
                        ejareh_send = "";
                        price_send = "";
                        break;
                    case R.id.general_type_ejare:


                        general_type_send = 3;
                        vadie_con.setVisibility(View.VISIBLE);
                        ejareh_con.setVisibility(View.VISIBLE);
                        general_price_con.setVisibility(View.GONE);
                        price_send = "";
                        break;
                }
            }
        });
    }

    private void hideOtagh() {
        hide_otagh = true;
        general_otagh_con.setVisibility(View.GONE);
        general_otagh_send = "0";
    }

    private void initilizePriceField() {

        general_price_maghtoo.setChecked(true);
        price_type_send = 1;

        general_price_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                switch (checkedId) {
                    case -1:
                        break;
                    case R.id.general_price_maghtoo:
                        general_price_form_con.setVisibility(View.VISIBLE);
                        price_type_send = 1;
                        break;
                    case R.id.general_price_moaveze:
                        general_price_form_con.setVisibility(View.GONE);
                        price_type_send = 2;
                        break;
                    case R.id.general_price_tavafoghi:
                        general_price_form_con.setVisibility(View.GONE);
                        price_type_send = 3;
                        break;
                }
            }
        });

    }


    private void checkForm(StepperLayout.OnNextClickedCallback callback) {
        if (!category_selector_btn.getText().equals("انتخاب")) {
            if (checkGeneralField()) {
                progressDialog.setMessage("در حال ارسال آگهی...");
                progressDialog.show();
                progressDialog.setCancelable(false);

                if(AppSharedPref.read("IS_MODIR",false)){
                    sendModirOrder(callback);
                }else{
                    sendOrder(callback);
                }

            }
        } else {
            showErrorCon(category_selector);
            Toasty.error(getContext(), "لطفا دسته بندی مورد نظر را انتخاب کنید", Toast.LENGTH_LONG, true).show();
        }
    }


    private boolean checkGeneralField() {
        if (checkOrderType()) {
            if (checkPriceField()) {
                if (checkVadieh()) {
                    if (checkEjare()) {
                        if (checkImagePicker()) {
                            if (checkTitleField()) {
                                if (checkTelField()) {
                                    if (checkMetraj()) {
                                        if (checkOtagh()) {
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
        } else {
            return false;
        }
    }


    private boolean checkEjare() {
        if (ejareh_con.getVisibility() == View.VISIBLE) {
            if (ejareh.getText().toString().trim().equals("")) {
                showErrorCon(ejareh_con);
                Toasty.error(getContext(), "لطفا ودیعه مورد نظر را وارد کنید.", Toast.LENGTH_LONG, true).show();
                return false;
            } else {
                ejareh_send = ejareh.getValue().toString();
                return true;
            }
        } else {
            return true;
        }
    }

    private boolean checkVadieh() {
        if (vadie_con.getVisibility() == View.VISIBLE) {
            if (vadie.getText().toString().trim().equals("")) {
                showErrorCon(vadie_con);
                Toasty.error(getContext(), "لطفا اجاره مورد نظر را وارد کنید.", Toast.LENGTH_LONG, true).show();
                return false;
            } else {
                vadieh_send = vadie.getValue().toString();
                return true;
            }
        } else {
            return true;
        }
    }

    private boolean checkDescTitle() {
        if (general_desc.getText().toString().trim().length() < 1) {
            showErrorCon(general_desc_con);
            Toasty.error(getContext(), "لطفا توضیحات آگهی را وارد نمایید.", Toast.LENGTH_LONG, true).show();
            return false;
        } else {
            general_desc_send = general_desc.getText().toString().trim();
            return true;
        }
    }

    private boolean checkTitleField() {
        if (general_title.getText().toString().length() < 1) {
            showErrorCon(general_title_con);
            Toasty.error(getContext(), "لطفا عنوان آگهی را وارد نمایید.", Toast.LENGTH_LONG, true).show();
            return false;
        } else {
            general_title_send = general_title.getText().toString().trim();
            return true;
        }
    }

    private boolean checkTelField() {
        String mobile_str = general_tel.getText().toString();
        boolean is_mobile_ok = Pattern.matches("(0)?9[0-9]{9}$", mobile_str);
        if (is_mobile_ok) {
            general_tel_send = mobile_str;
            return true;
        } else {
            showErrorCon(general_tel_con);
            Toasty.error(getContext(), "لطفا موبایل خود رو بصورت صحیح وارد کنید.", Toast.LENGTH_LONG, true).show();
            return false;
        }
    }

    private boolean checkMetraj() {

        if (general_metraj.getText().toString().length() < 1) {
            showErrorCon(general_metraj_con);
            Toasty.error(getContext(), "لطفا متراژ مورد نظر را وارد نمایید.", Toast.LENGTH_LONG, true).show();
            return false;
        } else {
            general_metraj_send = general_metraj.getText().toString().trim();
            return true;
        }
    }

    private boolean checkOtagh() {
        if(!hide_otagh){
            if (general_otagh.getText().toString().length() < 1) {
                showErrorCon(general_otagh_con);
                Toasty.error(getContext(), "لطفا تعداد اتاق را وارد نمایید.", Toast.LENGTH_LONG, true).show();
                return false;
            } else {
                general_otagh_send = general_otagh.getText().toString().trim();
                return true;
            }
        }else{
            return  true;
        }
    }

    private boolean checkImagePicker() {
        return true;
    }

    private boolean checkOrderType() {
        if (general_type_send == null) {
            showErrorCon(general_type_con);
            Toasty.error(getContext(), "لطفا نوع آگهی را انتخاب کنید.", Toast.LENGTH_LONG, true).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkPriceField() {
        if (general_price_con.getVisibility() == View.VISIBLE) {
            if (price_type_send != null) {
                if (price_type_send == 1) {
                    if (general_price.getText().toString().trim().equals("")) {
                        showErrorCon(general_price_con);
                        Toasty.error(getContext(), "لطفا قیمت مورد نظر را وارد کنید.", Toast.LENGTH_LONG, true).show();
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
                Toasty.error(getContext(), "لطفا نوع قیمت را انتخاب کنید.", Toast.LENGTH_LONG, true).show();
                return false;
            }
        } else {
            return true;
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


    @Override
    @UiThread
    public void onCompleteClicked(final StepperLayout.OnCompleteClickedCallback callback) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.complete();
            }
        }, 2000L);
    }

    @Override
    @UiThread
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        callback.goToPrevStep();
    }


    private void nullAllParams() {
        category_selector_btn.setText("انتخاب");
        general_price_type_btn.setText("انتخاب کنید");
        general_type_btn.setText("انتخاب کنید");

        send_paramas.clear();
        selected_cat_value = null;
        price_type_send = null;
        price_send = "";
        general_type_send = null;
        general_metraj_send = null;
        general_tel_send = null;
        general_title_send = null;
        general_desc_send = null;
        vadieh_send = "";
        ejareh_send = "";
    }


    @Subscribe
    public void onPickCrop(AppEvents.onPickCrop event) {
        horizontalScrollView.setVisibility(View.VISIBLE);

        viewItemSelected = getActivity().getLayoutInflater().inflate(R.layout.piclist_item_selected, layoutListItemSelect, false);
        viewItemSelected.setTag(event.getURL());
        btnDelete = (ImageView) viewItemSelected.findViewById(R.id.btnDelete);
        pathList.add(event.getURL());
        calcbtnDelete();
        viewItemSelected.setId(0);
        ImageView imageItem = (ImageView) viewItemSelected.findViewById(R.id.imageItem);
        imageItem.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageItem.setLayoutParams(imageView_lp);

        imageItem.getLayoutParams().width = 200;
        imageItem.getLayoutParams().height = 200;

        Uri img_uri = Uri.parse(event.getURL());
        imageItem.setImageURI(img_uri);
        layoutListItemSelect.addView(viewItemSelected);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }



    private void addGeneralField() {
        String user_id = AppSharedPref.read("ID", "0");
        Date curDate = new Date();

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
        send_paramas.add(new BasicNameValuePair("location", location_map_send));
        send_paramas.add(new BasicNameValuePair("selected_client", user_id));
        send_paramas.add(new BasicNameValuePair("status", "disabled"));
        send_paramas.add(new BasicNameValuePair("price_type", String.valueOf(price_type_send)));
        send_paramas.add(new BasicNameValuePair("general_type", String.valueOf(general_type_send)));

    }

    private void calcbtnDelete() {
        if (btnDelete != null) {
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String selected_item = (String) ((View) v.getParent().getParent()).getTag();
                    layoutListItemSelect.removeView((View) v.getParent().getParent());
                    pathList.remove(selected_item);

                    if (pathList.size() == 0) {
                        horizontalScrollView.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    private boolean isUserOnline(String mobile) {
        String current_tel = AppSharedPref.read("MOBILE", "");
        if (current_tel.equals(mobile)) {
            return true;
        } else {
            return false;
        }
    }

    private void sendModirOrder(StepperLayout.OnNextClickedCallback callback) {
        if(AppSharedPref.read("IS_MODIR",false)){
            send_paramas.add(new BasicNameValuePair("is_modir","1"));
        }


        Builders.Any.B ionBuilder;


        ionBuilder = Ion.with(getContext()).load("POST", CONST.MODIR_ADD_ORDER);
        for (int i = 0; i < send_paramas.size(); i++) {
            ionBuilder.setMultipartParameter(send_paramas.get(i).getName(), send_paramas.get(i).getValue());
        }

        for (int j = 0; j < pathList.size(); j++) {
            File file = new File(pathList.get(j));
            String name = "img_" + j;
            ionBuilder.setMultipartFile(name, "image*//*", file);
        }

        ionBuilder.asString().withResponse().setCallback((e1, result1) -> {
            callback.getStepperLayout().hideProgress();
            callback.goToNextStep();

            if (e1 == null) {
                JsonParser parser1 = new JsonParser();
                JsonObject json_obj1 = parser1.parse(result1.getResult()).getAsJsonObject();
                if (json_obj1.has("status")) {
                    if (json_obj1.get("status").getAsString().equals("ok")) {
                        JsonObject order = json_obj1.get("order").getAsJsonObject();
                        allow_next = false;


                        progressDialog.dismiss();
                        getActivity().finish();


                    } else {
                        Toasty.error(getContext(), "خطا در ارسال آگهی لطفا، دوباره تلاش کنید", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toasty.error(getContext(), "خطا در ارسال آگهی لطفا، دوباره تلاش کنید", Toast.LENGTH_LONG).show();
                }
            } else {
                Toasty.error(getContext(), "خطا در برفراری ارتباط با سرور", Toast.LENGTH_LONG).show();
            }
        });
    }
    private void sendOrder(StepperLayout.OnNextClickedCallback callback) {
        String app_token = AppSharedPref.read("TOKEN", "");
        byte[] data = Base64.decode(app_token, Base64.DEFAULT);

        send_paramas.add(new BasicNameValuePair("selected_client",
                AppSharedPref.read("ID", "")));

        try {
            final String user_pass = new String(data, "UTF-8");

            Future<String> stringFuture = Ion.with(getContext())
                    .load(CONST.APP_TOKEN)
                    .setBodyParameter("username", user_pass)
                    .setBodyParameter("password", user_pass.split("_")[0])
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {

                            progressDialog.dismiss();

                            if (e == null) {
                                JsonParser parser = new JsonParser();
                                JsonObject json_obj = parser.parse(result).getAsJsonObject();
                                if (json_obj.has("token")) {

                                    String token = json_obj.get("token").getAsString();
                                    Builders.Any.B ionBuilder;


                                    ionBuilder = Ion.with(getContext()).load("POST", CONST.ADD_ORDER);
                                    for (int i = 0; i < send_paramas.size(); i++) {
                                        ionBuilder.setMultipartParameter(send_paramas.get(i).getName(), send_paramas.get(i).getValue());
                                    }

                                    ionBuilder.setHeader("Authorization", "Bearer " + token);
                                    for (int j = 0; j < pathList.size(); j++) {
                                        File file = new File(pathList.get(j));
                                        String name = "img_" + j;
                                        ionBuilder.setMultipartFile(name, "image*//*", file);
                                    }

                                    ionBuilder.asString().withResponse().setCallback((e1, result1) -> {
                                        callback.getStepperLayout().hideProgress();
                                        callback.goToNextStep();


                                        if (e1 == null) {
                                            JsonParser parser1 = new JsonParser();
                                            JsonObject json_obj1 = parser1.parse(result1.getResult()).getAsJsonObject();
                                            if (json_obj1.has("status")) {
                                                if (json_obj1.get("status").getAsString().equals("ok")) {
                                                    JsonObject order = json_obj1.get("order").getAsJsonObject();
                                                    allow_next = false;


                                                    Toasty.success(getContext(),"آگهی شما بعد از بررسی کارشناسان ملک ارومیه ثبت خواهد گردید.",CONST.TOAST_TIME).show();
                                                    progressDialog.hide();
                                                    getActivity().finish();


                                                } else {
                                                    Toasty.error(getContext(), "خطا در ارسال آگهی لطفا، دوباره تلاش کنید", Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                Toasty.error(getContext(), "خطا در ارسال آگهی لطفا، دوباره تلاش کنید", Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            Toasty.error(getContext(), "خطا در برفراری ارتباط با سرور", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            } else {
                                Toasty.error(getContext(), "خطا در برفراری ارتباط با سرور", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void clearForm() {
        nullAllParams();
        final_path.clear();
        pathList.clear();
        horizontalScrollView.setVisibility(View.GONE);
        form_container.setVisibility(View.GONE);
        form_container.setVisibility(View.GONE);
        general_title.setText("");
        general_metraj.setText("");
        general_desc.setText("");
        general_tel.setText("");
        general_price.setText("");
        ejareh.setText("");
        vadie.setText("");
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        GlobalBus.getBus().register(this);
    }

    @Override
    public void onDetach() {
        GlobalBus.getBus().unregister(this);
        super.onDetach();
    }

    @Override
    public VerificationError verifyStep() {
        //return null if the user can go to the next step, create a new VerificationError instance otherwise
        return null;
    }

    @Override
    @UiThread
    public void onNextClicked(final StepperLayout.OnNextClickedCallback callback) {

        if (selected_cat_value != null) {
            checkForm(callback);
        } else {
            showErrorCon(category_selector);
            Toasty.error(getContext(), "لطفا دسته بندی مورد نظر را انتخاب کنید", Toast.LENGTH_LONG, true).show();
        }
    }


    @Subscribe
    public void getOrderID(AppEvents.BackStep events) {
        edit_order_id = events.getOrderID();
        is_edit = true;
        final_path.clear();
        editUI();


        calcbtnDelete();
    }

    private void editUI() {
        category_selector.setEnabled(false);
        category_selector_btn.setEnabled(false);
    }


}
