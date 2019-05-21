package cytex.co.zw.concertfinder.utils;

import android.content.Context;
import android.widget.Toast;

public class MessageToast {

    public static void show(Context context, String message){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

}
