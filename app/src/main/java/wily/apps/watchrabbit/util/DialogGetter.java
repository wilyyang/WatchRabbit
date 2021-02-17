package wily.apps.watchrabbit.util;

import android.app.AlertDialog;
import android.content.Context;

public class DialogGetter {

    public static AlertDialog getProgressDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Loading ...");
        AlertDialog alertDialog = builder.create();

        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);

        return alertDialog;
    }
}
