package tmedia.ir.melkeurmia.tools;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.NameValuePair;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.koushikdutta.ion.builder.Builders;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import tmedia.ir.melkeurmia.model.OrderItem;

/**
 * Created by tmedia on 9/21/2017.
 */

public class ApiCallTools {
    private static String android_id = "";
    private onOrderLoad orderLoad;
    private onOrderLoadAds orderLoad_ads;
    int total_page = 0;
    private ArrayList<OrderItem> listData = new ArrayList<OrderItem>();
    private boolean status = false;
    private JsonObject ads ;


    public interface completeEvent {
        void onCompleted(Exception e, JsonObject response);
    }

    public static void getTK(Context context, String app_token, final completeEvent callBack) {
        android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        Ion.with(context)
                .load(CONST.APP_TOKEN)
                .setBodyParameter("username", app_token)
                .setBodyParameter("password", android_id)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (callBack != null) {
                            callBack.onCompleted(e, result);
                        }
                    }
                });
    }

    public static void getTKWithPassword(Context context, String app_token, String password, final completeEvent callBack) {
        android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        Ion.with(context)
                .load(CONST.APP_TOKEN)
                .setBodyParameter("username", app_token)
                .setBodyParameter("password", password)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (callBack != null) {
                            callBack.onCompleted(e, result);
                        }
                    }
                });
    }


    public void getOrderwithAds(final Context context, String url, final int city, final int page, final onOrderLoadAds orderLoad) {
        this.orderLoad_ads = orderLoad;
        Ion.with(context)
                .load(url)
                .setBodyParameter("city_id", String.valueOf(city))
                .setBodyParameter("page", String.valueOf(page))
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        if (e != null) {
                            CONST.errorConectionToast(context);
                        } else {
                            listData.clear();
                            JsonArray items = result.get("items").getAsJsonObject().get("data").getAsJsonArray();

                            if(!result.get("ads").isJsonNull()){
                                ads = result.get("ads").getAsJsonObject();
                            }else{

                            }
                            total_page = result.get("items").getAsJsonObject().get("last_page").getAsInt();
                            if (page > total_page) {
                                status = true;
                            } else {
                                status = false;
                            }

                            for (int i = 0; i < items.size(); i++) {
                                JsonObject item = items.get(i).getAsJsonObject();
                                OrderItem orderItem = new OrderItem();
                                orderItem.setId(item.get("id").getAsInt());
                                orderItem.setCat_name(item.get("cat_name").getAsString());
                                orderItem.setTitle(item.get("title").getAsString());
                                orderItem.setDesc(item.get("desc").getAsString());
                                orderItem.setAttachments(item.get("attachments").getAsJsonArray());
                                orderItem.setDate(item.get("created_at").getAsString());
                                orderItem.setGeneral_mode(item.get("general_type").getAsString());
                                orderItem.setCode(item.get("unique_id").getAsInt());
                                orderItem.setIs_lux(item.get("is_lux").getAsInt());

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

                            if (orderLoad != null) {
                                orderLoad.onOrdersLoad(listData, status,ads );
                            }

                        }
                    }
                });
    }

    public void getOrders(final Context context, String url, final int city, final int page, final onOrderLoad orderLoad) {
        this.orderLoad = orderLoad;

        Ion.with(context)
                .load(url)
                .setBodyParameter("city_id", String.valueOf(city))
                .setBodyParameter("page", String.valueOf(page))
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        if (e != null) {
                            CONST.errorConectionToast(context);
                        } else {
                            listData.clear();
                            JsonArray items = result.get("items").getAsJsonObject().get("data").getAsJsonArray();
                            total_page = result.get("items").getAsJsonObject().get("last_page").getAsInt();
                            if (page > total_page) {
                                status = true;
                            } else {
                                status = false;
                            }

                            for (int i = 0; i < items.size(); i++) {
                                JsonObject item = items.get(i).getAsJsonObject();
                                OrderItem orderItem = new OrderItem();
                                orderItem.setId(item.get("id").getAsInt());

                                Log.d(CONST.APP_LOG,"id: " + orderItem.getId());
                                orderItem.setCat_name(item.get("cat_name").getAsString());
                                orderItem.setTitle(item.get("title").getAsString());
                                orderItem.setDesc(item.get("desc").getAsString());
                                orderItem.setAttachments(item.get("attachments").getAsJsonArray());
                                orderItem.setDate(item.get("created_at").getAsString());
                                orderItem.setGeneral_mode(item.get("general_type").getAsString());
                                orderItem.setCode(item.get("unique_id").getAsInt());
                                orderItem.setIs_lux(item.get("is_lux").getAsInt());
                                if(!item.get("order_type").isJsonNull()){
                                    orderItem.setType(item.get("order_type").getAsInt());
                                }else{
                                    orderItem.setType(-1);
                                }

                                switch (item.get("general_type").getAsString()){
                                    case "1":
                                        switch (item.get("price_type").getAsString()){
                                            case "1":
                                                orderItem.setPrice_mode("1");
                                                if(!item.get("price").isJsonNull()){
                                                    orderItem.setPrice(item.get("price").getAsString());
                                                }
                                                //orderItem.setPrice("21212");
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
                                        if(!item.get("ejareh_price").isJsonNull()){
                                            orderItem.setEjare(item.get("ejareh_price").getAsString());
                                        }else{
                                            orderItem.setEjare("۰");
                                        }
                                        if(!item.get("rahn_price").isJsonNull()){
                                            orderItem.setRahan(item.get("rahn_price").getAsString());
                                        }else{
                                            orderItem.setRahan("۰");
                                        }
                                        break;
                                }
                                listData.add(orderItem);
                            }

                            if (orderLoad != null) {
                                orderLoad.onOrdersLoad(listData, status);
                            }

                        }
                    }
                });
    }


    public void getOrdersWithID(Context context, String url, final int city, final int page, final int id, final onOrderLoad orderLoad) {
        this.orderLoad = orderLoad;
        Ion.with(context)
                .load(url)
                .setBodyParameter("city_id", String.valueOf(city))
                .setBodyParameter("page", String.valueOf(page))
                .setBodyParameter("cat_id", String.valueOf(id))
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {

                        } else {
                            listData.clear();
                            JsonArray items = result.get("items").getAsJsonObject().get("data").getAsJsonArray();
                            total_page = result.get("items").getAsJsonObject().get("last_page").getAsInt();

                            if (page > total_page) {
                                status = true;
                            } else {
                                status = false;
                            }

                            for (int i = 0; i < items.size(); i++) {
                                JsonObject item = items.get(i).getAsJsonObject();
                                OrderItem orderItem = new OrderItem();
                                orderItem.setId(item.get("id").getAsInt());
                                orderItem.setCat_name(item.get("cat_name").getAsString());
                                orderItem.setTitle(item.get("title").getAsString());
                                orderItem.setDesc(item.get("desc").getAsString());
                                orderItem.setAttachments(item.get("attachments").getAsJsonArray());
                                orderItem.setDate(item.get("created_at").getAsString());
                                orderItem.setCode(item.get("unique_id").getAsInt());
                                orderItem.setIs_lux(item.get("is_lux").getAsInt());

                                if(!item.get("order_type").isJsonNull()){
                                    orderItem.setType(item.get("order_type").getAsInt());
                                }else{
                                    orderItem.setType(-1);
                                }

                                listData.add(orderItem);
                            }

                            if (orderLoad != null) {
                                orderLoad.onOrdersLoad(listData, status);
                            }

                        }
                    }
                });
    }


    public void getOrdersByName(Context context, String url, final int city, String str, final int page, final onOrderLoad orderLoad) {
        this.orderLoad = orderLoad;
        Ion.with(context)
                .load(url)
                .setBodyParameter("city_id", String.valueOf(city))
                .setBodyParameter("page", String.valueOf(page))
                .setBodyParameter("search", str)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                        } else {


                            listData.clear();

                            JsonParser parser = new JsonParser();
                            JsonObject root = parser.parse(result).getAsJsonObject();
                            JsonArray items = root.get("items").getAsJsonObject().get("data").getAsJsonArray();
                            total_page = root.get("items").getAsJsonObject().get("last_page").getAsInt();

                            if (page > total_page) {
                                status = true;
                            } else {
                                status = false;
                            }

                            for (int i = 0; i < items.size(); i++) {
                                JsonObject item = items.get(i).getAsJsonObject();
                                OrderItem orderItem = new OrderItem();
                                orderItem.setId(item.get("id").getAsInt());
                                orderItem.setCat_name(item.get("cat_name").getAsString());
                                orderItem.setTitle(item.get("title").getAsString());
                                orderItem.setDesc(item.get("desc").getAsString());
                                orderItem.setAttachments(item.get("attachments").getAsJsonArray());
                                orderItem.setDate(item.get("created_at").getAsString());
                                orderItem.setCode(item.get("unique_id").getAsInt());
                                orderItem.setIs_lux(item.get("is_lux").getAsInt());

                                orderItem.setGeneral_mode(item.get("general_type").getAsString());
                                if(!item.get("order_type").isJsonNull()){
                                    orderItem.setType(item.get("order_type").getAsInt());
                                }else{
                                    orderItem.setType(-1);
                                }
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

                            if (orderLoad != null) {
                                orderLoad.onOrdersLoad(listData, status);
                            }

                        }
                    }
                });
    }

    public void getOrderByFilters(final Context context, String url,final int page, List<NameValuePair> send_paramas, final onOrderLoad orderLoad) {
        this.orderLoad = orderLoad;

        Builders.Any.B ionBuilder = Ion.with(context).load("POST", url);
        for (int i = 0; i < send_paramas.size(); i++) {
            ionBuilder.setBodyParameter(send_paramas.get(i).getName(), send_paramas.get(i).getValue());
        }


        ionBuilder.asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {

                if (e == null) {
                    JsonParser parser = new JsonParser();
                    JsonObject object = parser.parse(result.getResult()).getAsJsonObject();

                    JsonArray items = object.get("items").getAsJsonObject().get("data").getAsJsonArray();
                    total_page = object.get("items").getAsJsonObject().get("last_page").getAsInt();


                    if (page > total_page) {
                        status = true;
                    } else {
                        status = false;
                    }

                    listData.clear();
                    for (int i = 0; i < items.size(); i++) {
                        JsonObject item = items.get(i).getAsJsonObject();
                        OrderItem orderItem = new OrderItem();
                        orderItem.setId(item.get("id").getAsInt());
                        orderItem.setCat_name(item.get("cat_name").getAsString());
                        orderItem.setTitle(item.get("title").getAsString());
                        orderItem.setDesc(item.get("desc").getAsString());
                        orderItem.setAttachments(item.get("attachments").getAsJsonArray());
                        orderItem.setDate(item.get("created_at").getAsString());
                        orderItem.setCode(item.get("unique_id").getAsInt());
                        orderItem.setIs_lux(item.get("is_lux").getAsInt());
                        if(!item.get("order_type").isJsonNull()){
                            orderItem.setType(item.get("order_type").getAsInt());
                        }else{
                            orderItem.setType(-1);
                        }
                        orderItem.setGeneral_mode(item.get("general_type").getAsString());

                        switch (item.get("general_type").getAsString()){
                            case "1":
                                switch (item.get("price_type").getAsString()){
                                    case "1":
                                        orderItem.setPrice_mode("1");

                                        if(!item.get("price").isJsonNull()){
                                            orderItem.setPrice(item.get("price").getAsString());
                                        }else{
                                            orderItem.setPrice("0");
                                        }
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
                    if (orderLoad != null) {
                        orderLoad.onOrdersLoad(listData, status);
                    }

                } else {
                    Toasty.error(context, "خطا در برفراری ارتباط با سرور", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void getOrdersByBundle(final Context context, String url, final int city, final int page, List<NameValuePair> send_paramas, final onOrderLoad orderLoad) {
        this.orderLoad = orderLoad;

        Builders.Any.B ionBuilder = Ion.with(context).load("POST", url);
        for (int i = 0; i < send_paramas.size(); i++) {
            ionBuilder.setBodyParameter(send_paramas.get(i).getName(), send_paramas.get(i).getValue());
        }

        ionBuilder.setBodyParameter("city_id", String.valueOf(city));

        ionBuilder.asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {


                if (e == null) {
                    JsonParser parser = new JsonParser();
                    JsonObject object = parser.parse(result.getResult()).getAsJsonObject();


                    JsonArray items = object.get("items").getAsJsonObject().get("data").getAsJsonArray();
                    total_page = object.get("items").getAsJsonObject().get("last_page").getAsInt();


                    if (page > total_page) {
                        status = true;
                    } else {
                        status = false;
                    }

                    listData.clear();
                    for (int i = 0; i < items.size(); i++) {
                        JsonObject item = items.get(i).getAsJsonObject();
                        OrderItem orderItem = new OrderItem();
                        orderItem.setId(item.get("id").getAsInt());
                        orderItem.setCat_name(item.get("cat_name").getAsString());
                        orderItem.setTitle(item.get("title").getAsString());
                        orderItem.setDesc(item.get("desc").getAsString());
                        orderItem.setAttachments(item.get("attachments").getAsJsonArray());
                        orderItem.setDate(item.get("created_at").getAsString());
                        orderItem.setCode(item.get("unique_id").getAsInt());
                        orderItem.setIs_lux(item.get("is_lux").getAsInt());
                        if(!item.get("order_type").isJsonNull()){
                            orderItem.setType(item.get("order_type").getAsInt());
                        }else{
                            orderItem.setType(-1);
                        }
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
                    if (orderLoad != null) {
                        orderLoad.onOrdersLoad(listData, status);
                    }

                } else {
                    Toasty.error(context, "خطا در برفراری ارتباط با سرور", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public interface onOrderLoad {
        void onOrdersLoad(ArrayList<OrderItem> items, boolean is_end);
    }
    public interface onOrderLoadAds {
        void onOrdersLoad(ArrayList<OrderItem> items, boolean is_end , JsonObject ads);
    }
}
