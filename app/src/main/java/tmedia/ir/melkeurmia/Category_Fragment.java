package tmedia.ir.melkeurmia;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;

import tmedia.ir.melkeurmia.all_category.CategoryLevel1Fragment;
import tmedia.ir.melkeurmia.all_category.CategoryLevel2Fragment;
import tmedia.ir.melkeurmia.all_category.CategoryLevel3Fragment;
import tmedia.ir.melkeurmia.all_category.CategoryLevel4Fragment;
import tmedia.ir.melkeurmia.all_category.CategoryPagerAdapter;
import tmedia.ir.melkeurmia.otto.AppEvents;
import tmedia.ir.melkeurmia.otto.GlobalBus;
import tmedia.ir.melkeurmia.tools.RTLNonSwipeableViewPagerBase;

public class Category_Fragment extends Fragment {
    private RTLNonSwipeableViewPagerBase viewPager;
    CategoryLevel2Fragment level2;
    CategoryLevel3Fragment level3;
    CategoryLevel4Fragment level4;

    private View rootView;
    public static Category_Fragment newInstance() {
        Category_Fragment fragment = new Category_Fragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.category_fragment_layout, container, false);
        // ButterKnife.bind(this);
        return rootView;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = (RTLNonSwipeableViewPagerBase) view.findViewById(R.id.category_pager);
        viewPager.setOffscreenPageLimit(1);
        CategoryPagerAdapter viewPagerAdapter = new CategoryPagerAdapter(getChildFragmentManager());

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
    public void onResume() {
        super.onResume();

        if(getView() == null){
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    boolean can_exit = false;
                    if(viewPager.getCurrentItem() == 0){
                        AppEvents.NavigateHome home = new AppEvents.NavigateHome();
                        GlobalBus.getBus().post(home);
                    }else{
                        if(viewPager.getCurrentItem() > 0){
                            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                            can_exit = true;
                        }
                    }
                    return false;
                }
                return false;
            }
        });
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

    @Subscribe
    public void ChangeCategoryPager(final AppEvents.ChangeCategoryPager events) {
        AppEvents.startSwipeRefresh startSwipeRefresh = new AppEvents.startSwipeRefresh();
        GlobalBus.getBus().post(startSwipeRefresh);
        viewPager.setCurrentItem(1);
    }

    @Subscribe
    public void ChangeCategoryPagerLVL2(AppEvents.ChangeCategoryPagerLVL2 event){
        viewPager.setCurrentItem(2);
    }


    @Subscribe
    public void ChangeCategoryPagerLVL3(AppEvents.ChangeCategoryPagerLVL3 event){
        viewPager.setCurrentItem(3);
    }

    private void hideCategoryShown() {
        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag("categoryshow");
        if (fragment != null)
            getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }

}
