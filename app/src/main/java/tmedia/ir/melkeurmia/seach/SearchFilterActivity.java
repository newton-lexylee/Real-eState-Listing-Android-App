package tmedia.ir.melkeurmia.seach;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.sevenheaven.segmentcontrol.SegmentControl;

import tmedia.ir.melkeurmia.R;
import tmedia.ir.melkeurmia.insertorders.categoryselector.NewCategorySelector;
import tmedia.ir.melkeurmia.otto.GlobalBus;
import tmedia.ir.melkeurmia.tools.AppSharedPref;
import tmedia.ir.melkeurmia.tools.CONST;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class SearchFilterActivity extends AppCompatActivity {

    private static final int OPEN_CATEGORY_MENU = 21312;
    private SegmentControl mSegmentHorzontal;
    private Switch thumb_switch;
    private TextView switchBtn_txtView;


    private Switch feature_image_switch;
    private Button apply_search_btn, category_selector_btn,city_selector_btn;
    private RadioGroup general_type_group;
    private RadioButton general_type_1, general_type_2, general_type_3;
    private RelativeLayout thumb_selector_container, category_selector_container, general_type_container;
    private LinearLayout  general_filter_container;



    private Integer segment_price_send = 0;
    private String feature_image_send = "no";
    private Integer category_send = 0;
    private Integer general_type_send = 2;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_filter);

        context = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("اعمال فیلتر");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initLayout();

        mSegmentHorzontal = (SegmentControl) findViewById(R.id.segment_control);
        switchBtn_txtView = (TextView) findViewById(R.id.switchBtn_txtView);
        city_selector_btn = (Button) findViewById(R.id.city_selector_btn);

        mSegmentHorzontal.setOnSegmentControlClickListener(new SegmentControl.OnSegmentControlClickListener() {
            @Override
            public void onSegmentControlClick(int index) {
                segment_price_send = index;
            }
        });

        feature_image_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switchColor(isChecked);
                if (isChecked) {
                    switchBtn_txtView.setText("بله");
                    feature_image_send = "yes";
                } else {
                    switchBtn_txtView.setText("خیر");
                    feature_image_send = "no";
                }
            }
        });


        city_selector_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CONST.showInsertProvinceCitySelector(SearchFilterActivity.this, context, new CONST.onCitySelect() {
                    @Override
                    public void onCitySelect() {
                        if (AppSharedPref.read("CITY_NAME", "") != null) {
                            city_selector_btn.setText(AppSharedPref.read("CITY_NAME", ""));
                        }
                    }
                });
            }
        });

        category_selector_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SearchFilterActivity.this, NewCategorySelector.class);
                startActivityForResult(i, SearchFilterActivity.OPEN_CATEGORY_MENU);
            }
        });

        general_type_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.general_type_1) {
                    general_type_send = 1;
                } else if(checkedId == R.id.general_type_2){
                    general_type_send = 2;
                }else if(checkedId == R.id.general_type_3){
                    general_type_send = 3;
                }
            }
        });



        apply_search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFilterParams();
            }
        });

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.fontpath))
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SearchFilterActivity.OPEN_CATEGORY_MENU) {
            if (resultCode == Activity.RESULT_OK) {
                String label = data.getStringExtra("label");
                int id = data.getIntExtra("id", 0);
                if (label.length() > 0) {
                    category_selector_btn.setText(label);
                    category_selector_btn.setSelected(true);
                    category_send = id;
                }
            }
        }
    }

    private void switchColor(boolean checked) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                feature_image_switch.getThumbDrawable().setColorFilter(checked ? getColor(R.color.red): getColor(R.color.red_100), PorterDuff.Mode.MULTIPLY);
                feature_image_switch.getTrackDrawable().setColorFilter(!checked ?getColor(R.color.red): getColor(R.color.red_100), PorterDuff.Mode.MULTIPLY);
            }

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


    private void initLayout() {
        feature_image_switch = (Switch) findViewById(R.id.feature_image_switch);
        apply_search_btn = (Button) findViewById(R.id.apply_seach_btn);
        category_selector_btn = (Button) findViewById(R.id.category_selector_btn);
        general_type_group = (RadioGroup) findViewById(R.id.general_type_group);
        general_type_1 = (RadioButton) findViewById(R.id.general_type_1);
        general_type_2 = (RadioButton) findViewById(R.id.general_type_2);
        general_type_3 = (RadioButton) findViewById(R.id.general_type_3);
        thumb_selector_container = (RelativeLayout) findViewById(R.id.thumb_selector_container);
        category_selector_container = (RelativeLayout) findViewById(R.id.category_selector_container);
        general_type_container = (RelativeLayout) findViewById(R.id.general_type_container);
        general_filter_container = (LinearLayout) findViewById(R.id.general_filter_container);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_filter, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.clear_filter) {

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    private void initsearchform(int id) {

        calcGeneral(id);
    }

    private void calcGeneral(int id) {
        general_filter_container.setVisibility(View.VISIBLE);
        general_type_container.setVisibility(View.VISIBLE);
    }

    private void sendFilterParams() {

        boolean can_finish = true;
        Intent resultIntent = new Intent();
        resultIntent.putExtra("segment_price_send", segment_price_send);
        resultIntent.putExtra("feature_image_send", feature_image_send);


        if (general_type_container.isShown()) {
            resultIntent.putExtra("general_type_send", general_type_send);
        }
        if (category_send != 0) {
            resultIntent.putExtra("category_send", category_send);
        }

        if(!AppSharedPref.read("CITY_ID", "").equals("")){
            resultIntent.putExtra("province_id", AppSharedPref.read("CITY_ID", ""));
        }



        setResult(RESULT_OK, resultIntent);
        if(can_finish){
            finish();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
