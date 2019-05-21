package cytex.co.zw.concertfinder.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import cytex.co.zw.concertfinder.R;


public class CustomAlertDialog {
    View view;
    Context context;

    public CustomAlertDialog(View view, Context context) {
        this.view = view;
        this.context = context;
    }

    public  void showAlertDialog( String msg, int resource){
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = view.findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(context).inflate(resource, viewGroup, false);



        //Now we need an AlertDialog.Builder object
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);
        TextView message=dialogView.findViewById(R.id.message);
        Button okButton=dialogView.findViewById(R.id.ok);
        message.setText(msg);



        //finally creating the alert dialog and displaying it
        final android.support.v7.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

            }
        });


    }
}
