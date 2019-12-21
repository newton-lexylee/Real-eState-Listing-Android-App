package tmedia.ir.melkeurmia;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.koushikdutta.async.http.BasicNameValuePair;
import com.koushikdutta.async.http.NameValuePair;
import com.marcoscg.dialogsheet.DialogSheet;
import com.santalu.emptyview.EmptyView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import th.in.lordgift.widget.EditTextIntegerComma;
import tmedia.ir.melkeurmia.adapters.AllItemAdapter;
import tmedia.ir.melkeurmia.insertorders.categoryselector.NewCategorySelector;
import tmedia.ir.melkeurmia.itemView.ShowOrder;
import tmedia.ir.melkeurmia.model.OrderItem;
import tmedia.ir.melkeurmia.otto.AppEvents;
import tmedia.ir.melkeurmia.otto.GlobalBus;
import tmedia.ir.melkeurmia.tools.ApiCallTools;
import tmedia.ir.melkeurmia.tools.AppSharedPref;
import tmedia.ir.melkeurmia.tools.CONST;

import static tmedia.ir.melkeurmia.R.id.general_price_con;
import static tmedia.ir.melkeurmia.tools.AppSharedPref.read;


/**
 * Created by tmedia on 9/14/2017.
 */

public class All_Item_Fragment extends Fragment {

    private Context context;
    private ViewGroup rootView;
    private int current_page = 1;

    private XRecyclerView mRecyclerView;
    private AllItemAdapter mAdapter;
    private ArrayList<OrderItem> listData;
    private ApiCallTools apiCall = new ApiCallTools();
    private boolean hasNetWork = true;
    private Drawable dividerDrawable;
    private ProgressDialog progressDialog;
    private View header;
    private LinearLayout ads_view_con;

    private int target_city_id = 0;
    private static final int OPEN_CATEGORY_MENU = 12213;

    private Button topbar_filter_area_btn,
            topbar_filter_category_btn,
            topbar_filter_price_btn;
    private ImageButton remove_filter_btn;

    private int selected_filter_id;

    private String filter_city_id;
    private String filter_cat_id;
    private String filter_vadie_min;
    private String filter_vadie_max;
    private String filter_ejareh_min;
    private String filter_ejareh_max;
    private String filter_general_price_min;
    private String filter_general_price_max;
    private int total_page = 0;
    private int page = 1;
    private boolean status = false;
    private EmptyView emptyView ;
    private View filter_dialog_view;
    private LinearLayout filter_view_general_price_con;
    private LinearLayout filter_view_ejareh_con_con;
    private LinearLayout filter_view_vadie_con_con;

    public static All_Item_Fragment newInstance() {
        All_Item_Fragment fragment = new All_Item_Fragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        rootView = (ViewGroup) inflater.inflate(R.layout.all_item_fragment_layout, container, false);
        return rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emptyView = (EmptyView) rootView.findViewById(R.id.error_view);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("در حال بارگذاری");
        progressDialog.show();

        target_city_id = Integer.valueOf(read("CITY_ID", "0"));
        if (target_city_id != 0) {
            loadItem(Integer.valueOf(target_city_id));
        } else {
            loadItem(1);
        }

        ((MainActivity) getActivity()).setOnBackClickListener(new MainActivity.OnBackClickListener() {
            @Override
            public boolean onBackClick() {
                return false;
            }
        });

        topbar_filter_area_btn = rootView.findViewById(R.id.topbar_filter_area_btn);
        topbar_filter_category_btn = rootView.findViewById(R.id.topbar_filter_category_btn);
        topbar_filter_price_btn = rootView.findViewById(R.id.topbar_filter_price_btn);
        remove_filter_btn = rootView.findViewById(R.id.remove_filter_btn);

        topbar_filter_area_btn.setOnClickListener(v -> CONST.showInsertProvinceCitySelector(getActivity(), getContext(), () -> {
            if (AppSharedPref.read("CITY_NAME", "") != null) {
                topbar_filter_area_btn.setText(read("CITY_NAME", ""));
                filter_city_id = AppSharedPref.read("CITY_ID", "");
                topbar_filter_area_btn.setBackgroundResource(R.drawable.topbar_filter_btn_selected);
                remove_filter_btn.setVisibility(View.VISIBLE);
                startToSearch();
            }
        }));

        topbar_filter_category_btn.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), NewCategorySelector.class);
            startActivityForResult(i, All_Item_Fragment.OPEN_CATEGORY_MENU);
            topbar_filter_category_btn.setBackgroundResource(R.drawable.topbar_filter_btn_selected);
        });

        filter_dialog_view = getActivity().getLayoutInflater().inflate(R.layout.filter_price_layout, null);
        filter_view_general_price_con = filter_dialog_view.findViewById(general_price_con);
        filter_view_ejareh_con_con = filter_dialog_view.findViewById(R.id.ejareh_con);
        filter_view_vadie_con_con = filter_dialog_view.findViewById(R.id.vadie_con);

        topbar_filter_price_btn.setOnClickListener(v -> {



            EditTextIntegerComma general_price_min = filter_dialog_view.findViewById(R.id.general_price_min);
            EditTextIntegerComma general_price_max = filter_dialog_view.findViewById(R.id.general_price_max);
            EditTextIntegerComma vadie_min = filter_dialog_view.findViewById(R.id.vadie_min);
            EditTextIntegerComma vadie_max = filter_dialog_view.findViewById(R.id.vadie_max);
            EditTextIntegerComma ejareh_min = filter_dialog_view.findViewById(R.id.ejareh_min);
            EditTextIntegerComma ejareh_max = filter_dialog_view.findViewById(R.id.ejareh_max);

            switch (selected_filter_id) {
                case 5:
                case 6:
                case 7:
                case 12:
                case 13:
                case 14:
                case 15:
                    filter_view_general_price_con.setVisibility(View.GONE);
                    filter_view_ejareh_con_con.setVisibility(View.VISIBLE);
                    filter_view_vadie_con_con.setVisibility(View.VISIBLE);
                    break;
                default:
                    filter_view_general_price_con.setVisibility(View.VISIBLE);
                    filter_view_ejareh_con_con.setVisibility(View.GONE);
                    filter_view_vadie_con_con.setVisibility(View.GONE);
                    break;
            }

            new DialogSheet(getContext())
                    .setTitle("انتخاب محدوده قیمت")
                    .setMessage("کمترین و بیشترین قیمت را وارد نمایید.")
                    .setView(filter_dialog_view)
                    .setCancelable(true)
                    .setPositiveButton("تایید", new DialogSheet.OnPositiveClickListener() {
                        @Override
                        public void onClick(View v) {

                            switch (selected_filter_id) {
                                case 5:
                                case 6:
                                case 7:
                                case 12:
                                case 13:
                                case 14:
                                case 15:
                                    filter_vadie_min = vadie_min.getValue().equals("") ? null : String.valueOf(vadie_min.getValue());
                                    filter_vadie_max = vadie_max.getValue().equals("") ? null : String.valueOf(vadie_max.getValue());
                                    filter_ejareh_min = ejareh_min.getValue().equals("") ? null : String.valueOf(ejareh_min.getValue());
                                    filter_ejareh_max = ejareh_max.getValue().equals("") ?  null : String.valueOf(ejareh_max.getValue());
                                    //filter_ejareh_max = String.valueOf(ejareh_max.getValue());
                                    break;
                                default:
                                    filter_general_price_min = general_price_min.getValue().equals("") ? null : String.valueOf(general_price_min.getValue());
                                    filter_general_price_max = general_price_max.getValue().equals("") ? null : String.valueOf(general_price_max.getValue());
                                    break;
                            }
                            topbar_filter_price_btn.setBackgroundResource(R.drawable.topbar_filter_btn_selected);
                            startToSearch();

                            remove_filter_btn.setVisibility(View.VISIBLE);
                        }
                    })
                    .setButtonsColorRes(R.color.colorPrimary)
                    .show();
        });

        remove_filter_btn.setVisibility(View.GONE);
        remove_filter_btn.setOnClickListener(v -> {
            remove_filter_btn.setVisibility(View.GONE);
            topbar_filter_area_btn.setBackgroundResource(R.drawable.topbar_filter_btn);
            topbar_filter_category_btn.setBackgroundResource(R.drawable.topbar_filter_btn);
            topbar_filter_price_btn.setBackgroundResource(R.drawable.topbar_filter_btn);
            topbar_filter_area_btn.setText("انتخاب منطقه");
            topbar_filter_category_btn.setText("دسته بندی");
            topbar_filter_price_btn.setText("محدوده قیمت");
            mAdapter.notifyDataSetChanged();
            emptyView.showContent();

            filter_view_general_price_con.setVisibility(View.VISIBLE);
            filter_view_ejareh_con_con.setVisibility(View.GONE);
            filter_view_vadie_con_con.setVisibility(View.GONE);

            loadItem(0);

        });

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    getActivity().finish();
                }
                return false;
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == All_Item_Fragment.OPEN_CATEGORY_MENU) {
            if (resultCode == Activity.RESULT_OK) {
                String label = data.getStringExtra("label");
                selected_filter_id = data.getIntExtra("id", 0);
                topbar_filter_category_btn.setText(label);
                filter_cat_id = String.valueOf(selected_filter_id);
                remove_filter_btn.setVisibility(View.VISIBLE);

                startToSearch();

            }
        }
    }

    private void startToSearch() {
        Log.d(CONST.APP_LOG,"start search");


        List<NameValuePair> send_paramas  = new ArrayList<NameValuePair>();
        send_paramas.add(new BasicNameValuePair("province_id",filter_city_id));
        send_paramas.add(new BasicNameValuePair("category_send",filter_cat_id));
        send_paramas.add(new BasicNameValuePair("ejareh_min",filter_ejareh_min));
        send_paramas.add(new BasicNameValuePair("ejareh_max",filter_ejareh_max));
        send_paramas.add(new BasicNameValuePair("vadie_min",filter_vadie_min));
        send_paramas.add(new BasicNameValuePair("vadie_max",filter_vadie_max));
        send_paramas.add(new BasicNameValuePair("price_min",filter_general_price_min));
        send_paramas.add(new BasicNameValuePair("price_max",filter_general_price_max));

        apiCall.getOrderByFilters(getContext(), CONST.SEARCH_FILTER, current_page, send_paramas, (items, status) -> {
            progressDialog.dismiss();
            if (items.size() > 0) {
                emptyView.showContent();
                listData.clear();
                listData.addAll(items);
                mAdapter.notifyDataSetChanged();
            } else {
                emptyView.showEmpty("هیچ آگهی در این مورد وجود ندارد.");
            }
        });


        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                current_page = 1;
                apiCall.getOrderByFilters(getContext(), CONST.SEARCH_FILTER, current_page, send_paramas, new ApiCallTools.onOrderLoad() {
                    @Override
                    public void onOrdersLoad(ArrayList<OrderItem> items, boolean status) {

                        listData.clear();
                        listData.addAll(items);
                        mAdapter.notifyDataSetChanged();
                        mRecyclerView.refreshComplete();
                    }
                });
            }

            @Override
            public void onLoadMore() {
                current_page++;

                apiCall.getOrderByFilters(getContext(), CONST.SEARCH_FILTER, current_page, send_paramas, new ApiCallTools.onOrderLoad() {
                    @Override
                    public void onOrdersLoad(ArrayList<OrderItem> items, boolean status) {
                        if (status) {
                            mRecyclerView.setNoMore(true);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            listData.addAll(items);
                            mAdapter.notifyDataSetChanged();
                            mRecyclerView.refreshComplete();
                        }
                    }
                });


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
    public void getCityID(final AppEvents.UpdateLocation events) {
        listData.clear();
        mAdapter.notifyDataSetChanged();
    }

    private void loadItem(final int id) {
        progressDialog.dismiss();
        listData = new ArrayList<OrderItem>();

        mRecyclerView = (XRecyclerView) rootView.findViewById(R.id.recyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        //GridLayoutManager layoutManager = new GridLayoutManager(context,2);
        mRecyclerView.setNestedScrollingEnabled(true);
        layoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setHasFixedSize(true);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dividerDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.item_row_drawable, null);
        } else {
            dividerDrawable = getResources().getDrawable(R.drawable.item_row_drawable);
        }

        mRecyclerView.addItemDecoration(mRecyclerView.new DividerItemDecoration(dividerDrawable));

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallBeat);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallBeat);

        mAdapter = new AllItemAdapter(context, listData);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.refresh();
        mRecyclerView.setItemViewCacheSize(999999999);


        mAdapter.setOnItemClickListener(new AllItemAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, OrderItem data) {
                Intent i = new Intent(getContext(), ShowOrder.class);
                i.putExtra("id", data.getId());
                i.putExtra("mode", false);
                startActivity(i);
            }
        });


        AppEvents.startSwipeRefresh startSwipeRefresh = new AppEvents.startSwipeRefresh();
        GlobalBus.getBus().post(startSwipeRefresh);
        apiCall.getOrders(context, CONST.GET_ORDERS_ADS, id, current_page, new ApiCallTools.onOrderLoad() {
            @Override
            public void onOrdersLoad(ArrayList<OrderItem> items, boolean status) {
                AppEvents.stopSwipeRefresh stopSwipeRefresh = new AppEvents.stopSwipeRefresh();
                GlobalBus.getBus().post(stopSwipeRefresh);

                listData.addAll(items);

                mRecyclerView.getRecycledViewPool().clear();
                mAdapter.notifyDataSetChanged();
                // mRecyclerView.refreshComplete();
            }
        });

        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                current_page = 1;
                apiCall.getOrders(context, CONST.GET_ORDERS, id, current_page, new ApiCallTools.onOrderLoad() {
                    @Override
                    public void onOrdersLoad(ArrayList<OrderItem> items, boolean status) {
                        listData.clear();
                        listData.addAll(items);

                        mAdapter.notifyDataSetChanged();
                        mRecyclerView.refreshComplete();
                    }
                });
            }

            @Override
            public void onLoadMore() {
                current_page++;
                apiCall.getOrders(context, CONST.GET_ORDERS, id, current_page, new ApiCallTools.onOrderLoad() {
                    @Override
                    public void onOrdersLoad(ArrayList<OrderItem> items, boolean status) {
                        if (status) {
                            mRecyclerView.setNoMore(true);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            listData.addAll(items);
                            mAdapter.notifyDataSetChanged();
                            mRecyclerView.refreshComplete();
                        }
                    }
                });
            }
        });
    }

}