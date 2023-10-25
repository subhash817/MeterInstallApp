package com.rjdev.meterinstallapp.retrofit;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rjdev.meterinstallapp.R;


//import com.example.redemo.R;


/**
 * Created by mobulous2 on 16/3/17.
 */

public class ServiceProgressDialog
{
    public boolean isShowing=false;
    Activity activity;
    public CustomDialog customDialog;
    public ProgressDialog progressDialog;
    public String text;
    public ServiceProgressDialog(Activity activity) {
        this.activity = activity;
    }
    public void showProgressDialog(boolean isloader)
    {
        if (isloader)
        {
            progressDialog = new ProgressDialog(activity);
       //     progressDialog.setMessage(activity.getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.show();
            isShowing=true;
            if (!progressDialog.isShowing()) {
                progressDialog.show();
                isShowing = true;
            }
        }
    }
    public void showProgressDialog(String text)
    {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(text);
        progressDialog.setCancelable(false);
        progressDialog.show();
        isShowing=true;
        if (!progressDialog.isShowing()) {
            progressDialog.show();
            isShowing=true;
        }
    }

    public void showCustomProgressDialog(String text)
    {
        customDialog = CustomDialog.show(activity, text, true);
        customDialog.show();
        isShowing=true;
    }
    public void showCustomProgressDialog()
    {
       // customDialog = CustomDialog.show(activity,activity.getString(R.string.loading), true);
        customDialog.show();
        isShowing=true;
    }


    public void hideProgressDialog()
    {
        isShowing=false;
        if (progressDialog!=null&&progressDialog.isShowing())
        {   progressDialog.dismiss();   }
        if (customDialog!=null && customDialog.isShowing())
        {   customDialog.dismiss(); }
    }

//    CustomDialog
    public static class CustomDialog extends Dialog {
        public CustomDialog(Context context) {
            super(context);
        }

        public CustomDialog(Context context, int themeResId) {
            super(context, themeResId);
        }

        public static CustomDialog show(Context context, String text, boolean cancelable) {
            CustomDialog dialog = new CustomDialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setTitle(context.getResources().getString(R.string.loading));
          /*  LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            DataBindingUtil.inflate(inflater,R.layout.loader, null, false);
            dialog.setContentView(R.layout.loader);*/
            Rect displayRectangle = new Rect();
            Window window = dialog.getWindow();
            window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

            LayoutInflater inflater = (LayoutInflater)context. getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.loader, null);
            layout.setMinimumWidth((int)(displayRectangle.width() * 0.3f));

            dialog.setContentView(layout);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;

            dialog.getWindow().setAttributes(lp);

            ProgressBar progressWithoutBg = (ProgressBar) dialog.findViewById(R.id.progressbar);
            TextView textView=(TextView)dialog.findViewById(R.id.loader_text);
          //  Converters.setFont(textView,"regular");
            textView.setText(text);
            progressWithoutBg.setIndeterminate(true);
            progressWithoutBg.getIndeterminateDrawable().setColorFilter(0xFF00B1B2, android.graphics.PorterDuff.Mode.MULTIPLY);
            dialog.setCancelable(false);
            /*dialog.getWindow().getAttributes().gravity= Gravity.CENTER;
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.dimAmount=0.4f;
            dialog.getWindow().setAttributes(lp);*/
            //dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
            dialog.show();
            return dialog;
        }
    }

}
