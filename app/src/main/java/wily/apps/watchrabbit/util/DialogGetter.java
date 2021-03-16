package wily.apps.watchrabbit.util;

import android.app.AlertDialog;
import android.content.Context;

public class DialogGetter {

    public static AlertDialog getProgressDialog(Context context, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        AlertDialog alertDialog = builder.create();

        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);

        return alertDialog;
    }
}
