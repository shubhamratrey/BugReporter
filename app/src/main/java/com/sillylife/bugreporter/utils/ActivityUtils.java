package com.sillylife.bugreporter.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Html;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

public final class ActivityUtils {
    static final String INTENT_KEY_BUG_CONTEXT = "INTENT_KEY_BUG_CONTEXT";
    static final String INTENT_KEY_ATTACHMENT = "INTENT_KEY_ATTACHMENT";

    static void setStatusBarColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    public static boolean arePermissionsGranted(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    static @NonNull
    Drawable getTintedDrawable(@NonNull Context context, @DrawableRes int id, int color) {
        Drawable drawable = context.getResources().getDrawable(id);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, color);
        return drawable;
    }

    static @NonNull CharSequence getTextWithColor(@NonNull Context context, @ColorInt int color, @StringRes int stringId) {
        String hexColor = ColorPalette.getHexColor(color);
        return Html.fromHtml("<font color='" + hexColor + "'>" + context.getString(stringId) + "</font>");
    }
}
