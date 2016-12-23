package net.appsynth.paymehostdemo.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import net.appsynth.paymehostdemo.R;

/**
 * Created by nutron on 12/18/15 AD.
 */
public class DialogUtils {

    public static final String KEY_INPUT_MULTILINE_TEXT = "input_text";

    public interface OnDialogClickedListener {
        int POSITIVE = 1;
        int NEUTRAL = 2;
        int NEGATIVE = 3;

        void onButtonClicked(int event, Bundle value);
    }

    public static AlertDialog showHostAppDialog(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Host App");
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setNegativeButton( context.getString(R.string.label_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();

        try {
            if (context != null) {
                alert.show();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return alert;
    }

    public static AlertDialog showOKialog(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setNegativeButton( context.getString(R.string.label_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();

        try {
            if (context != null) {
                alert.show();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return alert;
    }

    public static AlertDialog showAlertDialog(Context context, String title, String message,
                                              String positive, String neutral, String negative,
                                              final OnDialogClickedListener listener) {
        return showAlertDialog(context, title, message, positive, neutral, negative, false, listener);
    }

    public static AlertDialog showAlertDialog(Context context, String title, String message,
                                              String positive, String neutral, String negative,
                                              boolean isCancelable, final OnDialogClickedListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        if (title != null && !title.equals("")) {
            builder.setTitle(title);
        }

        builder.setMessage(message);
        builder.setCancelable(isCancelable);

        if (positive != null && !positive.equals("")) {
            builder.setPositiveButton(positive,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            if (listener != null) {
                                listener.onButtonClicked(OnDialogClickedListener.POSITIVE, new Bundle());
                            }
                        }
                    });
        }
        if (neutral != null && !neutral.equals("")) {
            builder.setNeutralButton(neutral,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            if (listener != null) {
                                listener.onButtonClicked(OnDialogClickedListener.NEUTRAL, new Bundle());
                            }
                        }
                    });
        }

        if (negative != null && !negative.equals("")) {
            builder.setNegativeButton(negative,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            if (listener != null) {
                                listener.onButtonClicked(OnDialogClickedListener.NEGATIVE, new Bundle());
                            }
                        }
                    });
        }

        AlertDialog alert = builder.create();

        try {
            if (context != null) {
                alert.show();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return alert;
    }

}
