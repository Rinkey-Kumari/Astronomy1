package com.rk.myapplication.Fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.rk.myapplication.Common.Common;
import com.rk.myapplication.Model.APODModel;
import com.rk.myapplication.R;
import com.rk.myapplication.Retrofilt.INasa;
import com.rk.myapplication.Retrofilt.RetrofitClient;

import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class FragmentPicOfToday extends Fragment {

    private static String TAG = "FragmentPicOfToday";
    static FragmentPicOfToday instance;
    CompositeDisposable compositeDisposable;
    INasa iNasa;
    Common common;
   // private String MY_SHARED_PREF = "MyPref";
    private TextView Title, date, Description, Copyright;
    private ImageView AstronomicPicture;
    private ProgressBar progressBar;
    private NestedScrollView nestedScrollView;
    private String titleText;
    private String desText;
    private String toDate;
    private SharedPreferences mSharedPrefs;


    public FragmentPicOfToday() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        iNasa = retrofit.create(INasa.class);
        common = new Common();
    }

    public static FragmentPicOfToday getInstance() {
        if (instance == null) {
            instance = new FragmentPicOfToday();
        }
        return instance;
    }

    @Override
    public void onPause() {
      /*  Bitmap bitmap = getScreenimageFromView(AstronomicPicture);
        Log.d(TAG, "on pause" + bitmap);
        // if bitmap is not null then
        // save it to gallery
        if (bitmap != null) {
            saveMediaToStorage(bitmap);
        }
        SharedPreferences.Editor store = mSharedPrefs.edit();

        // write all the data entered by the user in SharedPreference and apply
        store.putString(titleText, Title.getText().toString());
        store.putString(toDate, getDate());
        store.putString(desText, Description.getText().toString());
        Log.d(TAG, "title = " + titleText + " toDate = " + toDate + " descrip = " + desText);
        store.apply();*/
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pic_of_theday, container, false);
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        common.setDate(dateFormate.format(calendar.getTime()));
        Title = view.findViewById(R.id.tvTitle);
        date = view.findViewById(R.id.tvDate);
        Description = view.findViewById(R.id.tvDescription);
        AstronomicPicture = view.findViewById(R.id.pic_of_day);
        Copyright = view.findViewById(R.id.tvCopyRight);
        progressBar = view.findViewById(R.id.progressCircular);
        nestedScrollView = view.findViewById(R.id.nestedScrollView);
        //mSharedPrefs = getContext().getSharedPreferences(MY_SHARED_PREF, Context.MODE_PRIVATE);
        getAstronomicData();
        return view;

    }

 /*   private void saveMediaToStorage(Bitmap bitmap) {
        FileOutputStream fos;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentResolver resolver = getContext().getContentResolver();
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, "Image" + ".jpg");
                values.put(MediaStore.MediaColumns.MIME_TYPE, "image/*");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                fos = (FileOutputStream) resolver.openOutputStream(Objects.requireNonNull(imageUri));
                //bitmap.compress(Bitmap.CompressFormat.JPEG,)
                Objects.requireNonNull(fos);
                Toast.makeText(getContext(), "saving file to location ", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "not able to save ", Toast.LENGTH_LONG).show();

        }
        //BitmapDrawable img = (BitmapDrawable) AstronomicPicture.getDrawable();
        //img.getBitmap()
    }

    private Bitmap getScreenimageFromView(ImageView astronomicPicture) {
        astronomicPicture.buildDrawingCache();
        Bitmap bmp = astronomicPicture.getDrawingCache();
        Log.i("rinkey", " getScreenimageFromView = bmp =" + bmp);
        return bmp;
    }

    private String getDate() {
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormate.format(calendar.getTime());
    }*/

    private void getAstronomicData() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        compositeDisposable.add(iNasa.getAPOD(common.getDate(), common.isHD(), Common.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<APODModel>() {
                               @Override
                               public void accept(final APODModel apodModel) throws Exception {
                                   progressBar.setVisibility(View.GONE);
                                   nestedScrollView.setVisibility(View.VISIBLE);
                                   if (apodModel.getMedia_type().equals("video")) {
                                       AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                       builder.setCancelable(false);
                                       builder.setTitle("Want To See Video?");
                                       builder.setMessage("Not able to load Image right now. \n If You Want To See It Click Ok");
                                       builder.setPositiveButton("OK !!", new DialogInterface.OnClickListener() {
                                           @Override
                                           public void onClick(DialogInterface dialog, int which) {
                                               Intent iWebIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(apodModel.getUrl()));
                                               startActivity(iWebIntent);
                                           }
                                       }).setNegativeButton("Cancel !!", new DialogInterface.OnClickListener() {
                                           @Override
                                           public void onClick(DialogInterface dialog, int which) {
                                               dialog.dismiss();
                                           }
                                       });
                                       builder.setIcon(getResources().getDrawable(R.drawable.ic_loading_error));
                                       builder.create().show();
                                   }
                                   if (apodModel.getMedia_type().equals("image")) {
                                       Glide.with(getContext()).load(apodModel.getHdurl()).into(AstronomicPicture);
                                   }
                                   Title.setText(new StringBuilder("Title:-").append(apodModel.getTitle()));
                                   date.setText(new StringBuilder("Date:-").append(apodModel.getDate()));
                                   Description.setText(apodModel.getExplanation());
                                   if (apodModel.getCopyright() != null) {
                                       Copyright.setText(new StringBuilder("©Copyright To ").append(apodModel.getCopyright()));
                                   }
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Toast.makeText(getActivity(), throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                               }
                           }
                ));
    }

}