package com.rk.myapplication.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.rk.myapplication.Common.Common;
import com.rk.myapplication.Model.APODModel;
import com.rk.myapplication.R;
import com.rk.myapplication.Retrofilt.INasa;
import com.rk.myapplication.Retrofilt.RetrofitClient;


import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class SearchFragment extends Fragment {

    private LinearLayout searchLayout;
    private TextView Title,date,Description ,Copyright;
    private ImageView AstronomicPicture;
    private ProgressBar progressBar;
    private NestedScrollView nestedScrollView;
    private EditText searchView;
    private ImageButton searchbutton;
    CompositeDisposable compositeDisposable;
    INasa iNasa;
    Common common;

    public SearchFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        iNasa = retrofit.create(INasa.class);
        common = new Common();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        searchLayout = view.findViewById(R.id.lSearch);
        Title = view.findViewById(R.id.tvTitleSearch);
        date = view.findViewById(R.id.tvDateSearch);
        Description = view.findViewById(R.id.tvDescriptionSearch);
        AstronomicPicture = view.findViewById(R.id.pic_of_Search);
        progressBar = view.findViewById(R.id.progressCircularSearch);
        nestedScrollView = view.findViewById(R.id.nestedScrollViewSearch);
        searchView = view.findViewById(R.id.search_View);
        Copyright = view.findViewById(R.id.tvCopyRight);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                progressBar.setVisibility(View.GONE);
            }
        });
        searchbutton = view.findViewById(R.id.SearchButton);
        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Date  =  searchView.getText().toString();
                common.setDate(Date);
                getAstronomicData();
            }
        });
        return view;
    }

    private void getAstronomicData(){
        compositeDisposable.add(iNasa.getAPOD(common.getDate(),common.isHD(), Common.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<APODModel>() {
                               @Override
                               public void accept(final APODModel apodModel) throws Exception {
                                   progressBar.setVisibility(View.GONE);
                                   nestedScrollView.setVisibility(View.VISIBLE);
                                   searchLayout.setVisibility(View.GONE);
                                   if(apodModel.getMedia_type().equals("video")) {
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
                                   }if(apodModel.getMedia_type().equals("image")){
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