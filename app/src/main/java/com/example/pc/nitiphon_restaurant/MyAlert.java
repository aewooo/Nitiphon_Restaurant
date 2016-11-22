package com.example.pc.nitiphon_restaurant;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by Pc on 2/11/2559.
 */
public class MyAlert {
    public  void myDialog(Context context,String strTitle,String strMessage){
        AlertDialog.Builder builder =new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.errorlogo);
        builder.setTitle(strTitle);
        builder.setMessage(strMessage);
        builder.setCancelable(false);
        builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
