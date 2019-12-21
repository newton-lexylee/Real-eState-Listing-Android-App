package tmedia.ir.melkeurmia.myorders;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.santalu.emptyview.EmptyView;

import java.util.ArrayList;

import tmedia.ir.melkeurmia.R;
import tmedia.ir.melkeurmia.adapters.FavoritListAdapter;
import tmedia.ir.melkeurmia.itemView.ShowOrder;
import tmedia.ir.melkeurmia.model.Favorit;
import tmedia.ir.melkeurmia.tools.CONST;
import tmedia.ir.melkeurmia.tools.FavoritUtils;

public class FavoritsActivity extends AppCompatActivity {

    private ListView lvl1_list;
    private ArrayList<Favorit> items = new ArrayList<>();
    private FavoritListAdapter adapter;
    private FavoritUtils favoritUtils;
    private boolean is_permission_grant = false;
    private EmptyView empty_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorits);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("نشان شده ها");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        empty_view = findViewById(R.id.empty_view);

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

        lvl1_list = findViewById(R.id.lvl1_list);
        items.clear();

        adapter = new FavoritListAdapter(FavoritsActivity.this, items, new FavoritListAdapter.onItemInnerClick() {
            @Override
            public void onRemoveClick(String name, String path, String id) {
                Log.d(CONST.APP_LOG,name + " -- " + path);
                Favorit favorit = new Favorit(name, path,id);
                favoritUtils.removeFavorit(favorit);
                items.clear();
                items.addAll(favoritUtils.getFavorits());
                adapter.notifyDataSetChanged();

                if(items.size() > 0){
                    Log.d(CONST.APP_LOG,"found");
                }else{
                    Log.d(CONST.APP_LOG,"not found");
                }
            }

            @Override
            public void onViewClick(String title, String path, String id) {

                Intent i = new Intent(FavoritsActivity.this, ShowOrder.class);
                i.putExtra("id", Integer.valueOf(id));
                i.putExtra("mode", false);
                startActivity(i);
            }
        });
        lvl1_list.setAdapter(adapter);
        lvl1_list.setDivider(null);


        if (favoritUtils != null) {
            if(favoritUtils.getFavorits().size() > 0){
                empty_view.showContent();
            }
            else{
                empty_view.showEmpty();

            }
            items.addAll(favoritUtils.getFavorits());
        }


        adapter.notifyDataSetChanged();

        lvl1_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        forceRTLIfSupported();

    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            is_permission_grant = true;
            favoritUtils = new FavoritUtils(FavoritsActivity.this);
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {

        }
    };

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void forceRTLIfSupported() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
