package com.stewhouse.nproject;

import android.content.Context;
import android.os.Build;

/**
 * Created by Gomguk on 16. 7. 14..
 */
public class GUtil {

    public static int getColor(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(id);
        } else {
            return context.getResources().getColor(id);
        }
    }
}
