package tmedia.ir.melkeurmia.model;

import com.google.gson.JsonArray;

/**
 * Created by tmedia on 9/20/2017.
 */

public class OrderItem {
    private int id;
    private String title;
    private String desc;
    private String url;
    private String date;
    private String cat_name;
    private JsonArray attachments;
    private boolean own_mode;
    private String status;
    private String general_mode;
    private String price_mode;
    private String price;
    private String ejare;
    private String rahan;
    private int type;
    private int code;
    private int is_lux;

    public OrderItem() {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.url = url;
        this.date = date;

    }


    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public JsonArray getAttachments() {
        return attachments;
    }

    public void setAttachments(JsonArray attachments) {
        this.attachments = attachments;
    }

    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }

    public boolean isOwn_mode() {
        return own_mode;
    }

    public void setOwn_mode(boolean own_mode) {
        this.own_mode = own_mode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getEjare() {
        return ejare;
    }

    public void setEjare(String ejare) {
        this.ejare = ejare;
    }

    public String getRahan() {
        return rahan;
    }

    public void setRahan(String rahan) {
        this.rahan = rahan;
    }

    public String getGeneral_mode() {
        return general_mode;
    }

    public void setGeneral_mode(String general_mode) {
        this.general_mode = general_mode;
    }

    public String getPrice_mode() {
        return price_mode;
    }

    public void setPrice_mode(String price_mode) {
        this.price_mode = price_mode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getIs_lux() {
        return is_lux;
    }

    public void setIs_lux(int is_lux) {
        this.is_lux = is_lux;
    }
}
