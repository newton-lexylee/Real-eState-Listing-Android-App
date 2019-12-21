package tmedia.ir.melkeurmia.otto;

import android.app.Activity;
import android.net.Uri;

import com.stepstone.stepper.StepperLayout;

import java.util.ArrayList;
import java.util.Objects;

import tmedia.ir.melkeurmia.model.CategoryItem;

/**
 * Created by tmedia on 8/20/2017.
 */

public class AppEvents {
    public static class NavigateHome {
    }


    public static class sendOrderID {

        private int id;

        public sendOrderID(int id) {
            this.id = id;
        }

        public int getId() {
            return this.id;
        }
    }

    public static class BackStep {
        private int order_id;

        public BackStep(int id) {
            order_id = id;
        }

        public int getOrderID() {
            return order_id;
        }
    }

    public static class DialogCategoryChose {
        private String _name;
        private int _id;
        private Objects obj;

        public DialogCategoryChose(String name, int id) {
            _id = id;
            _name = name;
        }

        public int getTargetChosseID() {
            return _id;
        }

        public String getTargetChosseName() {
            return _name;
        }
    }

    public static class UpdateLocation {
        private int city_id;

        public UpdateLocation(int id) {
            city_id = id;
        }

        public int getCityID() {
            return city_id;
        }
    }


    public static class CloseActivity {
        private Activity _activity;

        public CloseActivity(Activity activity) {
            _activity = activity;
        }

        public Activity closeActivity() {
            return _activity;
        }
    }

    public static class SpaceNavClick {
        private int _id;

        public SpaceNavClick(int id) {
            _id = id;
        }

        public int onSpaceClick() {
            return _id;
        }
    }

    public static class ChangePager {
        private int _id;
        private ArrayList<CategoryItem> _list;

        public ChangePager(int id, ArrayList<CategoryItem> list) {
            _list = list;
            _id = id;
        }

        public ArrayList<CategoryItem> getChangeList() {
            return _list;
        }

        public int getID() {
            return _id;
        }
    }

    public static class ChangeCategoryPagerLVL2{
        private int _id;
        private String _name;
        private boolean _canResolveOrder;

        public ChangeCategoryPagerLVL2(int id, boolean carReolveOrder, String label) {
            _id = id;
            _name = label;
            _canResolveOrder = carReolveOrder;
        }
        public int getID() {
            return _id;
        }
        public String getName() {
            return _name;
        }
        public boolean getCarReolveOrder() {
            return _canResolveOrder;
        }
    }

    public static class ChangeCategoryPagerLVL3{
        private int _id;

        public ChangeCategoryPagerLVL3(int id) {
            _id = id;
        }
        public int getID() {
            return _id;
        }
    }

    //for update bottom toolabr to bring front
    public static class ChangeToolbarOrder {
        public ChangeToolbarOrder() {
        }
    }
    //for update bottom toolabr to bring front
    public static class startSwipeRefresh {
        public startSwipeRefresh() {
        }
    }
    //for update bottom toolabr to bring front
    public static class stopSwipeRefresh {
        public stopSwipeRefresh() {
        }
    }

    //for update bottom toolabr to bring front
    public static class stopProgressBar {
        public stopProgressBar() {
        }
    }

    //for update bottom toolabr to bring front
    public static class PayOrder {
        private int _id;
        public PayOrder(int id) {
            _id = id;
        }

        public int getOrderID(){
            return  _id;
        }
    }



    public static class ChangePagerToID_L2 {
        private int _page;
        private int _id;
        private ArrayList<CategoryItem> _lists;


        public ChangePagerToID_L2(int id, ArrayList<CategoryItem> lists) {
            _id = id;
            _lists = lists;
        }

        public int getID() {
            return _id;
        }
        public ArrayList<CategoryItem> getLists() {
            return _lists;
        }
    }

    public static class ChangePagerToID_L3 {
        private int _id;
        private ArrayList<CategoryItem> _lists;


        public ChangePagerToID_L3(int id, ArrayList<CategoryItem> lists) {
            _id = id;
            _lists = lists;
        }

        public int getID() {
            return _id;
        }
        public ArrayList<CategoryItem> getLists() {
            return _lists;
        }
    }

    public static class openImagePicker{
        public openImagePicker(){

        }
    }
    public static class onPickCrop{
        private String _url;
        public onPickCrop(String url){
            _url = url;
        }
        public String getURL(){
            return _url;
        }
    }

    public static class ChangeCategoryPager{
        private int id;
        private String name;
        private boolean is_root;
        public ChangeCategoryPager(int id, String _name, boolean is_root){
            this.id = id;
            this.name = _name;
            this.is_root = is_root;
        }

        public int getID(){
            return  this.id;
        }
        public String getName(){
            return  this.name;
        }
        public boolean isRoot(){
            return  this.is_root;
        }
    }



    public static class InsertIAB{
        StepperLayout.OnNextClickedCallback _callback;
        public InsertIAB(StepperLayout.OnNextClickedCallback callback){
            this._callback = callback;
        }

        public StepperLayout.OnNextClickedCallback getCallback(){
            return  this._callback;
        }
    }

    public static class ReturnInsertIAB{
        StepperLayout.OnNextClickedCallback _callback;
        public ReturnInsertIAB(StepperLayout.OnNextClickedCallback callback){
            this._callback = callback;
        }

        public StepperLayout.OnNextClickedCallback getCallback(){
            return  this._callback;
        }
    }



    public static class PagerAdapterEvent{
        private int pos;
        public PagerAdapterEvent(int pos){
            this.pos = pos;
        }
        public int getPos(){
            return this.pos;
        }
    }
}
