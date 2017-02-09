package com.maddog05.maddogdialogs;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/*
 * Created by maddog05 on 06/04/2016.
 */
public class MaddogProgressDialog extends View{

    private static final int NO_COLOR = -1;

    private AlertDialog dialog;
    private Typeface typefaceTitle, typefaceMessage;
    private Context ctx;
    private String title, message;
    private int color = NO_COLOR;

    public MaddogProgressDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(ctx);
    }

    public MaddogProgressDialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(ctx);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MaddogProgressDialog(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(ctx);
    }

    public MaddogProgressDialog(Context ctx) {
        super(ctx);
        init(ctx);
    }

    private void init(Context ctx) {
        this.ctx = ctx;
        this.dialog = new AlertDialog.Builder(ctx).setView(R.layout.maddog_progress_dialog).create();
    }

    public void setTheme(int rTheme) {
        this.dialog = new AlertDialog.Builder(ctx, rTheme).setView(R.layout.maddog_progress_dialog).create();
    }

    public void setTitle(int resTitle)
    {
        this.title = ctx.getString(resTitle);
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setMessage(int resMessage)
    {
        this.message = ctx.getString(resMessage);
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public void setTypefaceTitle(Typeface typeface)
    {
        this.typefaceTitle = typeface;
    }

    public void setTypefaceMessage(Typeface typeface)
    {
        this.typefaceMessage = typeface;
    }

    public void setCancelable(boolean isCancelable)
    {
        dialog.setCancelable(isCancelable);
    }

    public void setAccentColor(int color){
        this.color = color;
    }

    public void setAccentColorResource(int resColor){
        this.color = ContextCompat.getColor(this.ctx, resColor);
    }

    public void show()
    {
        dialog.show();
        TextView lblTitle = (TextView) dialog.getWindow().findViewById(R.id.maddogProgressDialogTitle);
        if(lblTitle != null) {
            if(title != null && !title.isEmpty()) {
                lblTitle.setText(title);
                if(typefaceTitle != null)
                    lblTitle.setTypeface(typefaceTitle);
            }
            else {
                lblTitle.setVisibility(GONE);
            }
        }
        TextView lblMessage = (TextView) dialog.getWindow().findViewById(R.id.maddogProgressDialogMessage);
        if(lblMessage != null)
        {
            if(message != null && !message.isEmpty()) {
                lblMessage.setText(message);
                if(typefaceMessage != null)
                    lblMessage.setTypeface(typefaceMessage);
            }
            else {
                lblMessage.setVisibility(GONE);
            }
        }
        if(color != NO_COLOR) {
            MaterialProgressBar progressBar = (MaterialProgressBar) dialog.getWindow().findViewById(R.id.maddogProgressDialogIndicator);
            progressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }

    }

    public void dismiss()
    {
        if(dialog != null)
            dialog.dismiss();
    }
}
