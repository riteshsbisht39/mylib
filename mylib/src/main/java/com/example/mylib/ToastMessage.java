package com.example.mylib;

import android.content.Context;
import android.widget.Toast;

public class ToastMessage {

    public static void toastMessage(Context c, String message) {

        Toast.makeText(c, message, Toast.LENGTH_LONG).show();

    }
}
