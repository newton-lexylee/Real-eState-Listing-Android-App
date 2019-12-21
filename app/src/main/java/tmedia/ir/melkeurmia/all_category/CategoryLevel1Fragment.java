package tmedia.ir.melkeurmia.all_category;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

import tmedia.ir.melkeurmia.R;
import tmedia.ir.melkeurmia.adapters.CategoryListAdapter;
import tmedia.ir.melkeurmia.adapters.RecyclerViewAdapter;
import tmedia.ir.melkeurmia.model.CategoryItem;
import tmedia.ir.melkeurmia.otto.AppEvents;
import tmedia.ir.melkeurmia.otto.GlobalBus;
import tmedia.ir.melkeurmia.tools.CONST;
import tmedia.ir.melkeurmia.tools.NonScrollListView;

/**
 * Created by tmedia on 12/13/2017.
 */

public class CategoryLevel1Fragment extends Fragment {

    private NonScrollListView listview;
    private RecyclerViewAdapter mAdapter;
    private ArrayList<CategoryItem> modelList = new ArrayList<>();
    private View rootView;
    private CategoryListAdapter category_adapter;

    public static CategoryLevel1Fragment newInstance(String title) {
        CategoryLevel1Fragment fragmentFirst = new CategoryLevel1Fragment();
        return fragmentFirst;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.category_level1_layout, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews();
        setAdapter();
    }

    private void findViews() {
        listview = rootView.findViewById(R.id.root_cats_list);

    }


    private void setAdapter() {

        AppEvents.startSwipeRefresh startSwipeRefresh = new AppEvents.startSwipeRefresh();
        GlobalBus.getBus().post(startSwipeRefresh);

        if(modelList.size()> 0){
            modelList.clear();
            category_adapter.notifyDataSetChanged();
        }
        Ion.with(getContext())
                .load(CONST.ROOT_CATS)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        AppEvents.stopSwipeRefresh stopSwipeRefresh = new AppEvents.stopSwipeRefresh();
                        GlobalBus.getBus().post(stopSwipeRefresh);
                        if (e == null) {
                            try {
                                JsonParser parser = new JsonParser();
                                if (!parser.parse(result).isJsonNull()) {
                                    JsonObject roots = parser.parse(result).getAsJsonObject();
                                    JsonArray items = roots.get("items").getAsJsonArray();
                                    for (int i = 0; i < items.size(); i++) {
                                        JsonObject item = items.get(i).getAsJsonObject();
                                        CategoryItem cat_item = new CategoryItem();

                                        cat_item.setName(item.get("name").getAsString());
                                        cat_item.setId(item.get("id").getAsInt());
                                        cat_item.setLeft(item.get("lft").getAsInt());
                                        cat_item.setRight(item.get("rgt").getAsInt());
                                        modelList.add(cat_item);
                                    }
                                    resolveCats(modelList);


                                }
                            } catch (JsonParseException err) {

                            }
                        } else {
                            CONST.errorConectionToast(getContext());
                        }
                    }
                });


    }

    private void resolveCats(ArrayList<CategoryItem> modelList) {
        category_adapter = new CategoryListAdapter(getContext(),modelList);
        listview.setAdapter(category_adapter);


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CategoryItem item = (CategoryItem) parent.getAdapter().getItem(position);

                boolean is_root = false;
                if(item.getRight() - item.getLeft() < 2){
                    is_root = true;
                }

                AppEvents.ChangeCategoryPager space_click_event = new AppEvents.ChangeCategoryPager(item.getId(), item.getName(), is_root);
                GlobalBus.getBus().post(space_click_event);

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
}




