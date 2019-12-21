package tmedia.ir.melkeurmia;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.linchaolong.android.imagepicker.ImagePicker;
import com.linchaolong.android.imagepicker.cropper.CropImage;
import com.linchaolong.android.imagepicker.cropper.CropImageView;
import com.marcoscg.dialogsheet.DialogSheet;
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo;
import com.miguelbcr.ui.rx_paparazzo2.entities.FileData;
import com.miguelbcr.ui.rx_paparazzo2.entities.Response;
import com.miguelbcr.ui.rx_paparazzo2.entities.size.ScreenSize;
import com.squareup.otto.Subscribe;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.model.AspectRatio;

import java.net.MalformedURLException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import tmedia.ir.melkeurmia.adapters.InserOrderAdapter;
import tmedia.ir.melkeurmia.otto.AppEvents;
import tmedia.ir.melkeurmia.otto.GlobalBus;
import tmedia.ir.melkeurmia.tools.AppSharedPref;
import tmedia.ir.melkeurmia.tools.CONST;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class InsertOrderActivity extends AppCompatActivity implements StepperLayout.StepperListener {

    private static final String SKU_INSERT = "56zfdS";
    private Context context;
    private StepperLayout stepperLayout;
    private ProgressDialog progressDialog;
    private ImagePicker imagePicker = new ImagePicker();

    private boolean mBazaarInstalled;
    private static final String SKU_PREMIUM = "premium";
    boolean mIsPremium = false;
    static final int RC_REQUEST = 1;
    private static final String TAG = CONST.APP_LOG;
    private StepperLayout.OnNextClickedCallback _event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            setContentView(R.layout.activity_insert_order_above);
        } else {
            setContentView(R.layout.activity_insert_order);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.insert_order_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            toolbar.setTitleTextColor(getResources().getColor(R.color.black, null));
        } else {
            toolbar.setTitleTextColor(getResources().getColor(R.color.black));
        }

        AppSharedPref.init(this);

        stepperLayout = (StepperLayout) findViewById(R.id.stepperLayout);
        stepperLayout.setTabNavigationEnabled(true);
        stepperLayout.setAdapter(new InserOrderAdapter(getSupportFragmentManager(), this), 0);
        stepperLayout.setShowErrorStateEnabled(true);
        stepperLayout.setShowErrorStateOnBackEnabled(true);
        stepperLayout.setListener(this);


        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.fontpath))
                .setFontAttrId(R.attr.fontPath)
                .build()
        );


    }


    @Subscribe
    public void getBack(AppEvents.BackStep events) {
        stepperLayout.setCurrentStepPosition(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.insert_ad_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

       /* if(id == R.id.own_ad_menu){

        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    @Override
    public void onCompleted(View completeButton) {

    }

    @Override
    public void onError(VerificationError verificationError) {

    }

    @Override
    public void onStepSelected(int newStepPosition) {
        if (newStepPosition == 0) {
            stepperLayout.showBottomNavgiation();

        } else {

            stepperLayout.hideBottomNavgiation();
            stepperLayout.setTabNavigationEnabled(false);

        }

    }

    @Override
    public void onReturn() {

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
    public void onBackPressed() {
        int currentStepPosition = stepperLayout.getCurrentStepPosition();
        if (currentStepPosition > 0) {
            stepperLayout.onBackClicked();
        } else {
            finish();
        }
    }





    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    public void showAlert() {
        new SweetAlertDialog(InsertOrderActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("پرداخت موفق")
                .setContentText("آگهی شما با موفقیت ثبت شد.")
                .setConfirmText("برگشت")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        ActivityCompat.finishAffinity(InsertOrderActivity.this);
                        Intent i = new Intent(InsertOrderActivity.this, MainActivity.class);
                        startActivity(i);
                    }
                })
                .show();
    }


    @Subscribe
    public void openImagePicker(AppEvents.openImagePicker event) {
        openImagePickerIntent();
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



                        RxPaparazzo.single(InsertOrderActivity.this)
                                .crop(options)
                                .size(new ScreenSize())
                                .usingGallery()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<Response<InsertOrderActivity, FileData>>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(Response<InsertOrderActivity, FileData> insertOrderActivityFileDataResponse) {



                                        if(insertOrderActivityFileDataResponse.data() != null){
                                            AppEvents.onPickCrop id_event = new AppEvents.onPickCrop(insertOrderActivityFileDataResponse.data().getFile().toString());
                                            GlobalBus.getBus().post(id_event);
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
                        RxPaparazzo.single(InsertOrderActivity.this)
                                .crop(options)
                                .size(new ScreenSize())
                                .usingCamera()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<Response<InsertOrderActivity, FileData>>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(Response<InsertOrderActivity, FileData> insertOrderActivityFileDataResponse) {

                                        if(insertOrderActivityFileDataResponse.data() != null){
                                            AppEvents.onPickCrop id_event = new AppEvents.onPickCrop(insertOrderActivityFileDataResponse.data().getFile().toString());
                                            GlobalBus.getBus().post(id_event);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}