package com.project.demoapiprogress.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.project.demoapiprogress.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.wasabeef.glide.transformations.BlurTransformation;


public class MyUtils {

    private static final String SHARED_STORAGE = "android-demo-api-progress";
    static ProgressDialog dialog;
    static boolean done;
    Context context;


    public MyUtils(Context context) {
        this.context = context;
    }

    //---------------------------------- SHARED PREFERENCE -----------------------------------------

    public static void saveInt(Context context, String key, int value) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInt(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_STORAGE, Context.MODE_PRIVATE);
        return sharedPref.getInt(key, 0);

    }

    public static void setStatusBarBackground(Activity activity, int drawable, int navBarColor) {
        Window window = activity.getWindow();
        @SuppressLint("UseCompatLoadingForDrawables") Drawable background = activity.getResources().getDrawable(drawable);
        background.setBounds(0, 0, 50, 50);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(activity.getColor(android.R.color.transparent));
        window.setNavigationBarColor(activity.getColor(navBarColor));
        window.setBackgroundDrawable(background);
    }

    public static void saveLong(Context context, String key, long value) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static long getLong(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_STORAGE, Context.MODE_PRIVATE);
        return sharedPref.getLong(key, 0);
    }

    public static void saveString(Context context, String key, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_STORAGE, Context.MODE_PRIVATE);
        return sharedPref.getString(key, "");

    }

    public static void saveStringList(Context context, String key, List<String> value) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Set<String> set = new HashSet<>(value);
        editor.putStringSet(key, set);
        editor.apply();
    }

    public static List<String> getStringList(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_STORAGE, Context.MODE_PRIVATE);
        Set<String> set = sharedPref.getStringSet(key, new HashSet<>());
        return new ArrayList<>(set);
    }

    public static void clearSharedPref(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
    }


    //----------------------------------------- IMAGES ---------------------------------------------


    public static void loadImage(Context context, String image, ImageView imageView, AVLoadingIndicatorView loadingImage, int placeholder) {
        if (!isValidContextForGlide(context)) return;

//        if (!image.contains("http")) {
//            image = ApiClient.MEDIA_BASE_URL + image;
//        }

        try {
            loadingImage.setVisibility(View.VISIBLE);
            Glide.with(context).applyDefaultRequestOptions(new RequestOptions().error(placeholder)).load(image).diskCacheStrategy(DiskCacheStrategy.DATA).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    loadingImage.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    loadingImage.setVisibility(View.GONE);
                    return false;
                }
            }).into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static void loadImage(Context context, String image, ImageView imageView, int placeholder) {
//        if (!isValidContextForGlide(context)) return;
//
//        if (!image.contains("http") && image.contains("/uploads/")) {
//            image = ApiClient.MEDIA_BASE_URL + image;
//        }
//
//        try {
//            Glide.with(context)
//                    .applyDefaultRequestOptions(new RequestOptions().placeholder(placeholder).error(placeholder))
//                    .load(image)
//                    .diskCacheStrategy(DiskCacheStrategy.DATA)
//                    .into(imageView);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static void loadImage(Context context, String image, ImageView imageView, int placeholder) {
        if (TextUtils.isEmpty(image)) return;

        if (!isValidContextForGlide(context)) return;


        try {
//            loadingImage.setVisibility(View.VISIBLE);
            Glide.with(context).load(image).diskCacheStrategy(DiskCacheStrategy.DATA)
//                    .placeholder(placeholder)
                    .error(placeholder).signature(new MediaStoreSignature("*/*", Calendar.DATE, 0))
//                    .listener(new RequestListener<>() {
//                        @Override
//                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                            loadingImage.setVisibility(View.GONE);
//                            return false;
//                        }
//
//                        @Override
//                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                            loadingImage.setVisibility(View.GONE);
//                            return false;
//                        }
//                    })
                    .into(imageView);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void loadImage(Context context, String image, ImageView imageView) {
        if (!isValidContextForGlide(context)) return;

        if (image.isEmpty())
            imageView.setImageResource(R.drawable.placeholder_image);

        try {
            Glide.with(context)
//                    .applyDefaultRequestOptions(new RequestOptions().error(R.drawable.placeholder_image).placeholder(R.drawable.placeholder_image))
                    .load(image).diskCacheStrategy(DiskCacheStrategy.DATA)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadImageBlurred(Context context, String image, ImageView imageView) {
        if (!isValidContextForGlide(context)) return;

        if (image.isEmpty())
            imageView.setImageResource(R.drawable.placeholder_image);


        try {
            Glide.with(context)
//                    .applyDefaultRequestOptions(new RequestOptions().error(R.drawable.placeholder_image).placeholder(R.drawable.placeholder_image))
                    .load(image).diskCacheStrategy(DiskCacheStrategy.DATA)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .apply(new RequestOptions().transform(new BlurTransformation(70, 3)))
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public static void loadImageOffline(Context context, String image, ImageView imageView) {
        if (!isValidContextForGlide(context)) return;
        try {
            Glide.with(context).applyDefaultRequestOptions(new RequestOptions().error(R.drawable.placeholder_image).placeholder(R.drawable.placeholder_image)).load(image).diskCacheStrategy(DiskCacheStrategy.DATA).into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void loadThumbnail(Context context, String videoUrl, ImageView imageView) {
        if (!isValidContextForGlide(context)) return;

        try {
            RequestOptions options = new RequestOptions().frame(0);
            Glide.with(context).load(videoUrl).apply(options).into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadThumbnail(Context context, String videoUrl, ImageView imageView, long timeInMillis) {
        if (!isValidContextForGlide(context)) return;
        try {
            imageView.setVisibility(View.GONE);
            RequestOptions options = new RequestOptions().frame(timeInMillis * 1000);
            Glide.with(context).load(videoUrl).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    imageView.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    imageView.setVisibility(View.VISIBLE);
                    return false;
                }
            }).apply(options).into(imageView);
//            imageView.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            return !activity.isDestroyed() && !activity.isFinishing();
        }
        return true;
    }




    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            Network network = connectivityManager.getActiveNetwork();

            if (network != null) {
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);

                if (networkCapabilities != null) {
                    return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
                }
            }
        }

        return false;
    }

}




