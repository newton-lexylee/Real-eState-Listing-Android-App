package com.marcoscg.dialogsheet;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import tmedia.ir.melkeurmia.R;


/**
 * Created by @MarcosCGdev on 01/12/2017.
 */

public class DialogSheet {

    private Context context;
    private Dialog dialog;
    private int buttonColor = -1, backgroundColor = -1;
    private boolean showButtons = false;

    private TextView titleTextView, messageTextView;
    private ImageView iconImageView;
    private Button positiveButton, negativeButton;
    private RelativeLayout textContainer;
    private LinearLayout messageContainer;

    private View inflatedView;

    public interface OnPositiveClickListener {
        public void onClick(View v);
    }

    public interface OnNegativeClickListener {
        public void onClick(View v);
    }

    public DialogSheet(Context context) {
        this.context = context;
        init(context);
    }

    public DialogSheet setTitle(CharSequence title) {
        titleTextView.setVisibility(View.VISIBLE);
        titleTextView.setText(title);
        return this;
    }

    public DialogSheet setTitle(@StringRes int titleRes) {
        setTitle(context.getResources().getString(titleRes));
        return this;
    }

    public DialogSheet setMessage(CharSequence message) {
        messageTextView.setVisibility(View.VISIBLE);
        messageTextView.setText(message);
        return this;
    }

    public DialogSheet setMessage(@StringRes int messageRes) {
        setMessage(context.getResources().getString(messageRes));
        return this;
    }

    public DialogSheet setIcon(Drawable icon) {
        showIcon();
        iconImageView.setImageDrawable(icon);
        return this;
    }

    public DialogSheet setIcon(Bitmap icon) {
        showIcon();
        iconImageView.setImageBitmap(icon);
        return this;
    }

    public DialogSheet setIcon(@DrawableRes int iconRes) {
        showIcon();
        iconImageView.setImageResource(iconRes);
        return this;
    }

    public DialogSheet setPositiveButton(CharSequence text, final OnPositiveClickListener onPositiveClickListener) {
        positiveButton.setVisibility(View.VISIBLE);
        positiveButton.setText(text);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (onPositiveClickListener!=null)
                    onPositiveClickListener.onClick(view);
            }
        });
        showButtons = true;
        return this;
    }

    public DialogSheet setNegativeButton(CharSequence text, final OnNegativeClickListener onNegativeClickListener) {
        negativeButton.setVisibility(View.VISIBLE);
        negativeButton.setText(text);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (onNegativeClickListener!=null)
                    onNegativeClickListener.onClick(view);
            }
        });
        showButtons = true;
        return this;
    }

    public DialogSheet setPositiveButton(@StringRes int textRes, OnPositiveClickListener onPositiveClickListener) {
        setPositiveButton(context.getResources().getString(textRes), onPositiveClickListener);
        return this;
    }

    public DialogSheet setNegativeButton(@StringRes int textRes, OnNegativeClickListener onNegativeClickListener) {
        setNegativeButton(context.getResources().getString(textRes), onNegativeClickListener);
        return this;
    }

    public DialogSheet setButtonsColor(@ColorInt int buttonsColor) {
        this.buttonColor = buttonsColor;
        return this;
    }

    public DialogSheet setButtonsColorRes(@ColorRes int buttonsColorRes) {
        this.buttonColor = ContextCompat.getColor(context, buttonsColorRes);
        return this;
    }

    public DialogSheet setBackgroundColor(@ColorInt int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public DialogSheet setBackgroundColorRes(@ColorRes int backgroundColorRes) {
        this.backgroundColor = ContextCompat.getColor(context, backgroundColorRes);
        return this;
    }

    public DialogSheet setCancelable(boolean cancelable) {
        dialog.setCancelable(cancelable);
        return this;
    }

    public DialogSheet setView(View view) {
        messageContainer.addView(view);
        if (inflatedView==null)
            inflatedView = view;
        return this;
    }

    public DialogSheet setView(@LayoutRes int layoutRes) {
        inflatedView = View.inflate(context, layoutRes, null);
        setView(inflatedView);
        return this;
    }

    public View getInflatedView() {
        return inflatedView;
    }

    public void show() {
        if (backgroundColor==-1)
            backgroundColor = Utils.getThemeBgColor(context);
        if (backgroundColor!=-1) {
            dialog.findViewById(R.id.mainDialogContainer).setBackgroundColor(backgroundColor);
            titleTextView.setTextColor(Utils.getTextColor(backgroundColor));
            messageTextView.setTextColor(Utils.getTextColorSec(backgroundColor));
        }

        if (!showButtons)
            textContainer.setPadding(0,0,0,0);
        else {
            int color;
            if (buttonColor!=-1)
                color = buttonColor;
            else color = Utils.getThemeAccentColor(context);
            negativeButton.setTextColor(color);
            Utils.setButton(backgroundColor, color, positiveButton, true);
            Utils.setButton(backgroundColor, color, negativeButton, false);
            positiveButton.setTextColor(Utils.buttonTextColor(color));
        }

        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    private void init(Context context) {
        dialog = new Dialog(context, R.style.BottomDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_bottomdialog);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        titleTextView = (TextView) dialog.findViewById(R.id.dialogTitle);
        messageTextView = (TextView) dialog.findViewById(R.id.dialogMessage);
        iconImageView = (ImageView) dialog.findViewById(R.id.dialogIcon);
        positiveButton = (Button) dialog.findViewById(R.id.buttonPositive);
        negativeButton = (Button) dialog.findViewById(R.id.buttonNegative);
        textContainer = (RelativeLayout) dialog.findViewById(R.id.textContainer);
        messageContainer = (LinearLayout) dialog.findViewById(R.id.messageContainer);
    }

    private void showIcon() {
        iconImageView.setVisibility(View.VISIBLE);
    }

}
