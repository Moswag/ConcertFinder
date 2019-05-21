package cytex.co.zw.concertfinder.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.view.View;


import cytex.co.zw.concertfinder.R;
import dmax.dialog.SpotsDialog;

public class CustomProgressDialog {

    public static void showDialog(Context context, String title, View view, final String message, final int resource){
        final CustomAlertDialog dialog=new CustomAlertDialog(view,context);

        final AlertDialog alertDialog = new SpotsDialog.Builder()
                .setContext(context)
                .setMessage(title)
                .setTheme(R.style.Custom)
                .setCancelable(false)
                .build();

        alertDialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                alertDialog.dismiss();
                dialog.showAlertDialog(message,resource);
            }
        }, 3000);
    }
}
