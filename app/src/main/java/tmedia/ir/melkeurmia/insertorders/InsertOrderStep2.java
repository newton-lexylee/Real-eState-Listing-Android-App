package tmedia.ir.melkeurmia.insertorders;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.marcoscg.dialogsheet.DialogSheet;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.util.List;

import app.dinus.com.loadingdrawable.LoadingView;
import eu.fiskur.simpleviewpager.SimpleViewPager;
import tmedia.ir.melkeurmia.R;

public class InsertOrderStep2 extends Fragment implements BlockingStep {
    private int TAP_THRESHOLD = 2;
    private Context context;

    private Button final_post_btn, order_delete_btn, order_pay_btn;
    private LoadingView gallery_loading;
    private SimpleViewPager image_pager;
    private LinearLayout order_insert_info, order_btn, slider_view, all_field_view, general_field, category_container, general_price_view, general_type_view, general_rahn_view, general_ejareh_view;
    private TextView item_title, item_desc, item_category, general_price_view_tv, general_type_view_tv, city_name_tv, general_rahn_tv, general_ejareh_tv;

    private View general_price_view_sep,
            general_type_view_sep, general_rahn_view_sep;

    private View rootView;

    private JsonObject order;
    private String title;
    private String desc;
    private String email;
    private String tel;
    private String cat_name;
    private JsonArray attachments;

    private List<String> image_urls;

    private ProgressDialog progressDialog;
    private int finilize_order_id;


    private RadioGroup pay_radioes;
    private RadioButton fori_pay_radio, nardoban_pay_radio;
    private Button pay_btn;
    private int final_pay_amount;

    private int order_purchase_fori, order_purchase_fori_nardoban, order_purchase_vizhe;
    private DialogSheet sheet;





    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.insert_order_step_2, container, false);

        context = getContext();
        return rootView;
    }

    @Override
    public VerificationError verifyStep() {
        //return null if the user can go to the next step, create a new VerificationError instance otherwise
        return null;
    }

    @Override
    public void onSelected() {
        getActivity().finish();
    }

    @Override
    public void onError(@NonNull VerificationError error) {
        //handle error inside of the fragment, e.g. show error on EditText
    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {

    }

    @Override
    public void onCompleteClicked(final StepperLayout.OnCompleteClickedCallback callback) {


    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {

    }
}