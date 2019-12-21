package tmedia.ir.melkeurmia.all_category;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.santalu.emptyview.EmptyView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import tmedia.ir.melkeurmia.R;
import tmedia.ir.melkeurmia.adapters.AllItemAdapter;
import tmedia.ir.melkeurmia.adapters.CategoryListAdapter;
import tmedia.ir.melkeurmia.itemView.ShowOrder;
import tmedia.ir.melkeurmia.model.CategoryItem;
import tmedia.ir.melkeurmia.model.OrderItem;
import tmedia.ir.melkeurmia.otto.AppEvents;
import tmedia.ir.melkeurmia.otto.GlobalBus;
import tmedia.ir.melkeurmia.tools.ApiCallTools;
import tmedia.ir.melkeurmia.tools.AppSharedPref;
import tmedia.ir.melkeurmia.tools.CONST;
import tmedia.ir.melkeurmia.tools.NonScrollListView;


public class CategoryLevel3Fragment extends Fragment {
    private View rootView;

    private static final int OPEN_ORDER_CODE = 101;
    private ListView category_btn_list;
    private XRecyclerView category_item_lv;
    private ArrayList<OrderItem> listData = new ArrayList<OrderItem>();
    private Drawable dividerDrawable;
    private AllItemAdapter mAdapter;
    private ApiCallTools apiCall = new ApiCallTools();
    private ProgressDialog progressDialog;
    private int target_city_id = 0;
    private int current_page = 1;
    private EmptyView error_view ;
    private NonScrollListView cats_list_view;
    private  ArrayList<CategoryItem> category_items = new ArrayList<>();
    private CategoryListAdapter category_adapter;
    private XRecyclerView mRecyclerView;
    private int target_cat_id = 0;

    private boolean canReolveOrder = false;

    public static CategoryLevel3Fragment newInstance(String title) {
        CategoryLevel3Fragment fragmentFirst = new CategoryLevel3Fragment();
        return fragmentFirst;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.category_level3_layout, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(category_items.size()> 0){
            category_items.clear();
            category_adapter.notifyDataSetChanged();
        }

        mAdapter = new AllItemAdapter(getContext(), listData);

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
    public void ChangeCategoryPagerLVL2(final AppEvents.ChangeCategoryPagerLVL2 events) {
        canReolveOrder = events.getCarReolveOrder();


        AppEvents.startSwipeRefresh startSwipeRefresh = new AppEvents.startSwipeRefresh();
        GlobalBus.getBus().post(startSwipeRefresh);
        target_cat_id = events.getID();
        Ion.with(getContext())
                .load(CONST.LOAD_CAT_ORDERS)
                .setBodyParameter("cat_id", String.valueOf(events.getID()))
                .setBodyParameter("city_id", AppSharedPref.read("CITY_ID", "0"))
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        AppEvents.stopSwipeRefresh stopSwipeRefresh = new AppEvents.stopSwipeRefresh();
                        GlobalBus.getBus().post(stopSwipeRefresh);
                        if(e == null){
                            JsonParser parser = new JsonParser();
                            if(!parser.parse(result).isJsonNull()){
                                JsonObject root = parser.parse(result).getAsJsonObject();

                                JsonArray root_cats = root.get("sub_cats").getAsJsonArray();
                                JsonObject root_orders = root.get("orders").getAsJsonObject();
                                if(canReolveOrder){
                                    resolveOrders(root_orders);
                                }else{
                                    listData.clear();
                                    mAdapter.notifyDataSetChanged();
                                }

                                resolveCats(root_cats);
                            }else{
                                CONST.errorConectionToast(getContext());
                            }
                        }else{
                            CONST.errorConectionToast(getContext());
                        }
                    }
                });
    }


    private void resolveOrders(JsonObject root_orders) {

        JsonArray items = root_orders.get("data").getAsJsonArray();

        if(listData.size() > 0){
            listData.clear();
            mAdapter.notifyDataSetChanged();
            error_view.showEmpty();
        }

        for (int i = 0; i < items.size(); i++) {
            JsonObject item = items.get(i).getAsJsonObject();
            OrderItem orderItem = new OrderItem();
            orderItem.setId(item.get("id").getAsInt());
            orderItem.setCat_name(item.get("cat_name").getAsString());
            orderItem.setTitle(item.get("title").getAsString());
            orderItem.setDesc(item.get("desc").getAsString());
            orderItem.setOwn_mode(false);
            orderItem.setStatus(item.get("status").getAsString());
            orderItem.setAttachments(item.get("attachments").getAsJsonArray());
            orderItem.setDate(item.get("created_at").getAsString());
            if(!item.get("order_type").isJsonNull()){
                orderItem.setType(item.get("order_type").getAsInt());
            }else{
                orderItem.setType(-1);
            }
            listData.add(orderItem);

            orderItem.setGeneral_mode(item.get("general_type").getAsString());

            switch (item.get("general_type").getAsString()){
                case "1":
                    switch (item.get("price_type").getAsString()){
                        case "1":
                            orderItem.setPrice_mode("1");
                            orderItem.setPrice(item.get("price").getAsString());
                            break;
                        case "2":
                            orderItem.setPrice_mode("2");
                            orderItem.setPrice("معاوضه");
                            break;
                        case "3":
                            orderItem.setPrice_mode("3");
                            orderItem.setPrice("توافقی");
                            break;
                    }
                    break;
                case "2":
                    orderItem.setPrice("درخواستی");
                    break;
                case "3":
                    orderItem.setEjare(item.get("ejareh_price").getAsString());
                    orderItem.setRahan(item.get("rahn_price").getAsString());
                    break;
            }

        }

        error_view = (EmptyView) rootView.findViewById(R.id.error_view);
        mRecyclerView = (XRecyclerView) rootView.findViewById(R.id.orders_recyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setAutoMeasureEnabled(true);
        //GridLayoutManager layoutManager = new GridLayoutManager(context,2);

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dividerDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.item_row_drawable, null);
        } else {
            dividerDrawable = getResources().getDrawable(R.drawable.item_row_drawable);
        }
        mRecyclerView.setItemViewCacheSize(999999999);

        mRecyclerView.addItemDecoration(mRecyclerView.new DividerItemDecoration(dividerDrawable));

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallBeat);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallBeat);

        mAdapter = new AllItemAdapter(getContext(), listData);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.refresh();
        mAdapter.notifyDataSetChanged();

        mAdapter.setOnItemClickListener(new AllItemAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, OrderItem data) {
                Intent i =new Intent(getActivity(), ShowOrder.class);
                i.putExtra("id",data.getId());
                startActivityForResult(i, OPEN_ORDER_CODE);
            }
        });


        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                current_page = 1;
                int city_id = Integer.parseInt(AppSharedPref.read("CITY_ID", "0"));
                Ion.with(getContext())
                        .load(CONST.LOAD_CAT_ORDERS)
                        .setBodyParameter("cat_id", String.valueOf(target_cat_id))
                        .setBodyParameter("city_id", String.valueOf(city_id))
                        .asString()
                        .setCallback((e, result) -> {
                            AppEvents.stopSwipeRefresh stopSwipeRefresh = new AppEvents.stopSwipeRefresh();
                            GlobalBus.getBus().post(stopSwipeRefresh);
                            if(e == null){
                                JsonParser parser = new JsonParser();
                                if(!parser.parse(result).isJsonNull()){

                                    if(listData.size() > 0){
                                        listData.clear();
                                        mAdapter.notifyDataSetChanged();
                                    }

                                    JsonObject root = parser.parse(result).getAsJsonObject();

                                    JsonObject root_orders1 = root.get("orders").getAsJsonObject();

                                    JsonArray items1 = root_orders1.get("data").getAsJsonArray();
                                    for (int i = 0; i < items1.size(); i++) {
                                        JsonObject item = items1.get(i).getAsJsonObject();
                                        OrderItem orderItem = new OrderItem();
                                        orderItem.setId(item.get("id").getAsInt());
                                        orderItem.setCat_name(item.get("cat_name").getAsString());
                                        orderItem.setTitle(item.get("title").getAsString());
                                        orderItem.setDesc(item.get("desc").getAsString());
                                        orderItem.setOwn_mode(false);
                                        orderItem.setStatus(item.get("status").getAsString());
                                        orderItem.setAttachments(item.get("attachments").getAsJsonArray());
                                        orderItem.setDate(item.get("created_at").getAsString());

                                        orderItem.setGeneral_mode(item.get("general_type").getAsString());

                                        switch (item.get("general_type").getAsString()){
                                            case "1":
                                                switch (item.get("price_type").getAsString()){
                                                    case "1":
                                                        orderItem.setPrice_mode("1");
                                                        orderItem.setPrice(item.get("price").getAsString());
                                                        break;
                                                    case "2":
                                                        orderItem.setPrice_mode("2");
                                                        orderItem.setPrice("معاوضه");
                                                        break;
                                                    case "3":
                                                        orderItem.setPrice_mode("3");
                                                        orderItem.setPrice("توافقی");
                                                        break;
                                                }
                                                break;
                                            case "2":
                                                orderItem.setPrice("درخواستی");
                                                break;
                                            case "3":
                                                orderItem.setEjare(item.get("ejareh_price").getAsString());
                                                orderItem.setRahan(item.get("rahn_price").getAsString());
                                                break;
                                        }

                                        listData.add(orderItem);
                                    }
                                    mAdapter.notifyDataSetChanged();
                                    mRecyclerView.refreshComplete();


                                }else{
                                    CONST.errorConectionToast(getContext());
                                }
                            }else{
                                CONST.errorConectionToast(getContext());
                            }
                        });
            }

            @Override
            public void onLoadMore() {
                current_page++;
                Ion.with(getContext())
                        .load(CONST.LOAD_CAT_ORDERS)
                        .setBodyParameter("cat_id", String.valueOf(target_cat_id))
                        .setBodyParameter("city_id", AppSharedPref.read("CITY_ID", "0"))
                        .setBodyParameter("page", String.valueOf(current_page))
                        .asString()
                        .setCallback(new FutureCallback<String>() {
                            @Override
                            public void onCompleted(Exception e, String result) {
                                AppEvents.stopSwipeRefresh stopSwipeRefresh = new AppEvents.stopSwipeRefresh();
                                GlobalBus.getBus().post(stopSwipeRefresh);
                                if(e == null){
                                    JsonParser parser = new JsonParser();
                                    if(!parser.parse(result).isJsonNull()){

                                        if(listData.size() > 0){
                                            listData.clear();
                                            mAdapter.notifyDataSetChanged();
                                        }

                                        JsonObject root = parser.parse(result).getAsJsonObject();

                                        JsonObject root_orders = root.get("orders").getAsJsonObject();

                                        JsonArray items = root_orders.get("data").getAsJsonArray();
                                        for (int i = 0; i < items.size(); i++) {
                                            JsonObject item = items.get(i).getAsJsonObject();
                                            OrderItem orderItem = new OrderItem();
                                            orderItem.setId(item.get("id").getAsInt());
                                            orderItem.setCat_name(item.get("cat_name").getAsString());
                                            orderItem.setTitle(item.get("title").getAsString());
                                            orderItem.setDesc(item.get("desc").getAsString());
                                            orderItem.setOwn_mode(false);
                                            orderItem.setStatus(item.get("status").getAsString());
                                            orderItem.setAttachments(item.get("attachments").getAsJsonArray());
                                            orderItem.setDate(item.get("created_at").getAsString());

                                            orderItem.setGeneral_mode(item.get("general_type").getAsString());

                                            switch (item.get("general_type").getAsString()){
                                                case "1":
                                                    switch (item.get("price_type").getAsString()){
                                                        case "1":
                                                            orderItem.setPrice_mode("1");
                                                            orderItem.setPrice(item.get("price").getAsString());
                                                            break;
                                                        case "2":
                                                            orderItem.setPrice_mode("2");
                                                            orderItem.setPrice("معاوضه");
                                                            break;
                                                        case "3":
                                                            orderItem.setPrice_mode("3");
                                                            orderItem.setPrice("توافقی");
                                                            break;
                                                    }
                                                    break;
                                                case "2":
                                                    orderItem.setPrice("درخواستی");
                                                    break;
                                                case "3":
                                                    orderItem.setEjare(item.get("ejareh_price").getAsString());
                                                    orderItem.setRahan(item.get("rahn_price").getAsString());
                                                    break;
                                            }

                                            listData.add(orderItem);
                                        }
                                        mAdapter.notifyDataSetChanged();
                                        mRecyclerView.refreshComplete();


                                    }else{
                                        CONST.errorConectionToast(getContext());
                                    }
                                }else{
                                    CONST.errorConectionToast(getContext());
                                }
                            }
                        });

            }
        });


    }

    private  void resolveCats(JsonArray root_cats) {

        if(category_items.size() > 0){
            category_items.clear();
            category_adapter.notifyDataSetChanged();

        }
        for (int i = 0; i<root_cats.size();i++){
            JsonObject item = root_cats.get(i).getAsJsonObject();
            CategoryItem cat = new CategoryItem();
            cat.setName(item.get("name").getAsString());
            cat.setId(item.get("id").getAsInt());
            cat.setLeft(item.get("lft").getAsInt());
            cat.setRight(item.get("rgt").getAsInt());
            category_items.add(cat);
        }
        cats_list_view = rootView.findViewById(R.id.sub_cats_list);
        category_adapter = new CategoryListAdapter(getContext(),category_items);
        cats_list_view.setAdapter(category_adapter);
        category_adapter.notifyDataSetChanged();
        cats_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CategoryItem item = (CategoryItem) parent.getAdapter().getItem(position);
                AppEvents.ChangeCategoryPagerLVL3 lvl3_item_click = new AppEvents.ChangeCategoryPagerLVL3(item.getId());
                GlobalBus.getBus().post(lvl3_item_click);

            }
        });
    }

}
