package tmedia.ir.melkeurmia;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.squareup.otto.Subscribe;

import tmedia.ir.melkeurmia.myorders.AboutUSActivity;
import tmedia.ir.melkeurmia.myorders.ContactUSActivity;
import tmedia.ir.melkeurmia.myorders.FavoritsActivity;
import tmedia.ir.melkeurmia.myorders.MyOrdersActivity;
import tmedia.ir.melkeurmia.otto.AppEvents;
import tmedia.ir.melkeurmia.otto.GlobalBus;
import tmedia.ir.melkeurmia.tools.AppSharedPref;
import tmedia.ir.melkeurmia.tools.CONST;

public class Profile_Fragment extends Fragment {
    private View rootView;
    private Button my_orders_btn;
    private Button favorit_orders_btn;

    private Button about_btn;
    private Button agency_login;
    private Button contact_btn;

    private ProgressDialog progressDialog;


    public static Profile_Fragment newInstance() {
        Profile_Fragment fragment = new Profile_Fragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rootView = view;

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("در حال بارگذاری");
        my_orders_btn = rootView.findViewById(R.id.my_orders_btn);
        about_btn = rootView.findViewById(R.id.about_btn);
        agency_login = rootView.findViewById(R.id.agency_login);
        contact_btn = rootView.findViewById(R.id.contact_btn);
        favorit_orders_btn = rootView.findViewById(R.id.favorit_orders_btn);

        my_orders_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MyOrdersActivity.class);
                startActivity(i);
            }
        });

        favorit_orders_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), FavoritsActivity.class);
                startActivity(i);
            }
        });


        if(AppSharedPref.read("IS_MODIR",false)){
            agency_login.setText("خروج" +  "(" + AppSharedPref.read("MODIR_NAME", "") +")");

            agency_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.show();
                    progressDialog.setMessage("در حال خروج");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    AppSharedPref.init(getContext());
                                    AppSharedPref.write("ID", "");
                                    AppSharedPref.write("TOKEN", "");
                                    AppSharedPref.write("IS_MODIR", false);
                                    AppSharedPref.write("MODIR_NAME", "");
                                    AppSharedPref.write("online",0);
                                    agency_login.setText("ورود بعنوان بنگاه مسکن");
                                    CONST.restartApp(getActivity());
                                }
                            }, 1152);
                        }
                    });
                }
            });

        }else{
            agency_login.setText("ورود بعنوان بنگاه مسکن");

            agency_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), AgencyLogin.class);
                    startActivity(i);
                }
            });

        }


        ((MainActivity) getActivity()).setOnBackClickListener(new MainActivity.OnBackClickListener() {
            @Override
            public boolean onBackClick() {
                return false;
            }
        });

        about_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(getContext(), AboutUSActivity.class);
                startActivity(i);
            }
        });

        contact_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(getContext(), ContactUSActivity.class);
                startActivity(i);
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
                    AppEvents.NavigateHome home = new AppEvents.NavigateHome();
                    GlobalBus.getBus().post(home);
                }
                return false;
            }
        });
    }

}