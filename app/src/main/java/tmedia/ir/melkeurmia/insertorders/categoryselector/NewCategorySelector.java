package tmedia.ir.melkeurmia.insertorders.categoryselector;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.squareup.otto.Subscribe;

import tmedia.ir.melkeurmia.R;
import tmedia.ir.melkeurmia.all_category.CategoryLevel1Fragment;
import tmedia.ir.melkeurmia.all_category.CategoryLevel2Fragment;
import tmedia.ir.melkeurmia.all_category.CategoryLevel3Fragment;
import tmedia.ir.melkeurmia.all_category.CategoryLevel4Fragment;
import tmedia.ir.melkeurmia.all_category.CategoryPagerAdapter;
import tmedia.ir.melkeurmia.otto.AppEvents;
import tmedia.ir.melkeurmia.otto.GlobalBus;
import tmedia.ir.melkeurmia.tools.CONST;
import tmedia.ir.melkeurmia.tools.RTLNonSwipeableViewPagerBase;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class NewCategorySelector extends AppCompatActivity {

    private Context context;
    private View rootView;


    private RTLNonSwipeableViewPagerBase viewPager;
    CategoryLevel2Fragment level2;
    CategoryLevel3Fragment level3;
    CategoryLevel4Fragment level4;

    private ProgressDialog dialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_category_selector_dialog);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("انتخاب دسته بندی");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("در حال بارگذاری");

        viewPager = (RTLNonSwipeableViewPagerBase) findViewById(R.id.category_pager);
        viewPager.setOffscreenPageLimit(1);
        CategoryPagerAdapter viewPagerAdapter = new CategoryPagerAdapter(getSupportFragmentManager());

        level2 = new CategoryLevel2Fragment();
        level3 = new CategoryLevel3Fragment();
        level4 = new CategoryLevel4Fragment();

        viewPagerAdapter.addFragment(new CategoryLevel1Fragment(), "First");
        viewPagerAdapter.addFragment(level2, "Secound");
        viewPagerAdapter.addFragment(level3, "Login");
        viewPagerAdapter.addFragment(level4, "Login");

        viewPager.setAdapter(viewPagerAdapter);
    }

    @Override
    public void onBackPressed() {
        boolean can_exit = false;
        if(viewPager.getCurrentItem() == 0){
            super.onBackPressed();
        }else{
            if(viewPager.getCurrentItem() > 0){
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                can_exit = true;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        GlobalBus.getBus().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalBus.getBus().unregister(this);
    }


    @Subscribe
    public void ChangeCategoryPager(final AppEvents.ChangeCategoryPager events) {
        dialog.show();
        viewPager.setCurrentItem(1);

        if(events.isRoot()){
            Intent returnIntent = new Intent();
            returnIntent.putExtra("label",events.getName());
            returnIntent.putExtra("id",events.getID());
            setResult(Activity.RESULT_OK,returnIntent);

            
            finish();
        }
    }

    @Subscribe
    public void ChangeCategoryPagerLVL2(AppEvents.ChangeCategoryPagerLVL2 event){
        //viewPager.setCurrentItem(2);
        Intent returnIntent = new Intent();
        returnIntent.putExtra("label",event.getName());
        returnIntent.putExtra("id",event.getID());
        setResult(Activity.RESULT_OK,returnIntent);
        finish();

    }


    @Subscribe
    public void ChangeCategoryPagerLVL3(AppEvents.ChangeCategoryPagerLVL3 event){
        viewPager.setCurrentItem(3);
    }

    private void hideCategoryShown() {
        /*Fragment fragment = getSupportFragmentManager().findFragmentByTag("categoryshow");
        if (fragment != null)
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();*/
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

    @Subscribe
    public void startSwipeRefresh(final AppEvents.stopProgressBar events) {
        dialog.dismiss();
    }

}
