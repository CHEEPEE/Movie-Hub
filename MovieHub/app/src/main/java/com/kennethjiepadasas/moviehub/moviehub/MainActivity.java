package com.kennethjiepadasas.moviehub.moviehub;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.NetworkOnMainThreadException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kennethjiepadasas.moviehub.moviehub.helper.DatabaseHelper;
import com.kennethjiepadasas.moviehub.moviehub.model.MoviesModel;
import com.kennethjiepadasas.moviehub.moviehub.views.MovieRecyclerViewAdapter;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements InternetConnectivityListener {
    OkHttpClient client = new OkHttpClient();
    ArrayList<MoviesModel> moviesModelArrayList = new ArrayList<>();
    MovieRecyclerViewAdapter movieRecyclerViewAdapter;
    RecyclerView rvMovieList;
    Context context;
    TextView btnReload;
    TextView connectionLabel;
    String url = "https://api.themoviedb.org/3/discover/movie?api_key=38c21cee709b82cfe7ea0ab324d2f88c&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false&page=1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvMovieList = (RecyclerView) findViewById(R.id.rv_movielist);
        btnReload = (TextView) findViewById(R.id.btnReload);
        connectionLabel = (TextView) findViewById(R.id.connectionLabel);
        context = MainActivity.this;
        DatabaseHelper.getInstance(context,"moviehub.db");
        InternetAvailabilityChecker.init(this);
        client.newBuilder();
        movieRecyclerViewAdapter = new MovieRecyclerViewAdapter(context,moviesModelArrayList);
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        rvMovieList.setLayoutManager(layoutManager);
        rvMovieList.setAdapter(movieRecyclerViewAdapter);

        getMovieList();
        movieRecyclerViewAdapter.setOnItemClickListener(new MovieRecyclerViewAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position, MoviesModel moviesModel) {
                movieDetailsDialog(moviesModel);
            }
        });
        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMovieList();
            }
        });
    }
    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {
        //do something based on connectivity
        if (isConnected){
                try {
                    moviesModelArrayList.clear();
                    getMovies();
                    System.out.print("Connected");
                }catch (IOException e){
                    System.out.print("failed " + e);
                }
        }
    }
    void getMovies() throws IOException {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String jsonResponse = response.body().string();
                String jsonValidate = jsonResponse.toString().replace("'","#23d5kjh");
                String insertRespose = "Insert Into movies (movie_list_json) values(\""+jsonValidate.replace("\"","'")+"\");";
                DatabaseHelper.execute(insertRespose);

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(jsonResponse);
                        try {
                            JSONObject json = new JSONObject(jsonResponse);
                            JSONArray jsonArray = json.getJSONArray("results");
                            System.out.println(json.getJSONArray("results"));
                            for (int i = 0; i<jsonArray.length();i++){
                                MoviesModel moviesModel = new MoviesModel();
                                moviesModel.setTitle(jsonArray.getJSONObject(i).getString("title"));
                                moviesModel.setId(jsonArray.getJSONObject(i).getInt("id"));
                                moviesModel.setPoster_path(jsonArray.getJSONObject(i).getString("poster_path"));
                                moviesModel.setVote_average(jsonArray.getJSONObject(i).getDouble("vote_average"));
                                moviesModel.setOverview(jsonArray.getJSONObject(i).getString("overview"));

                                moviesModel.setPopularity(jsonArray.getJSONObject(i).getLong("popularity"));
                                moviesModel.setVoteCount(jsonArray.getJSONObject(i).getDouble("vote_count"));
                                moviesModel.setRelease_date(jsonArray.getJSONObject(i).getString("release_date"));

                                moviesModelArrayList.add(moviesModel);
                                System.out.println(jsonArray.getJSONObject(i).getString("title"));
                            }
                            movieRecyclerViewAdapter.notifyDataSetChanged();
                            btnReload.setVisibility(View.GONE);
                            connectionLabel.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
    }

    public boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }


    void loadfromDB(){
        String movieListJson="";
        Cursor cursonJsonLoader = DatabaseHelper.rawQuery("Select movie_list_json from movies;");
        cursonJsonLoader.moveToFirst();
        if (cursonJsonLoader!=null && cursonJsonLoader.getCount()!= 0){
            if (cursonJsonLoader.moveToFirst()){
                do {
                    movieListJson = cursonJsonLoader.getString(cursonJsonLoader.getColumnIndex("movie_list_json"));
                }while (cursonJsonLoader.moveToNext());
            }
        }
        try {
            String aux =movieListJson.replace("'","\"");
            JSONObject json = new JSONObject(aux.replace("#23d5kjh","'"));
            JSONArray jsonArray = json.getJSONArray("results");
            System.out.println(json.getJSONArray("results"));
            moviesModelArrayList.clear();
            for (int i = 0; i<jsonArray.length();i++){
                MoviesModel moviesModel = new MoviesModel();
                moviesModel.setTitle(jsonArray.getJSONObject(i).getString("title"));
                moviesModel.setId(jsonArray.getJSONObject(i).getInt("id"));
                moviesModel.setPoster_path(jsonArray.getJSONObject(i).getString("poster_path"));
                moviesModel.setVote_average(jsonArray.getJSONObject(i).getDouble("vote_average"));
                moviesModel.setOverview(jsonArray.getJSONObject(i).getString("overview"));

                moviesModel.setPopularity(jsonArray.getJSONObject(i).getLong("popularity"));
                moviesModel.setVoteCount(jsonArray.getJSONObject(i).getDouble("vote_count"));
                moviesModel.setRelease_date(jsonArray.getJSONObject(i).getString("release_date"));

                moviesModelArrayList.add(moviesModel);
                System.out.println(jsonArray.getJSONObject(i).getString("title"));
            }
            movieRecyclerViewAdapter.notifyDataSetChanged();
            if (moviesModelArrayList.size()>0){
                btnReload.setVisibility(View.GONE);
                connectionLabel.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getMovieList(){
        loadfromDB();
        if(isNetworkAvailable(context)){
            try {
                getMovies();
            }catch (IOException e) {
                System.out.print("failed " + e);
            }
        }else {
            connectionLabel.setVisibility(View.VISIBLE);
            btnReload.setVisibility(View.VISIBLE);
            loadfromDB();


        }
    }

    private void movieDetailsDialog(MoviesModel moviesModel){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
       dialog.setContentView(R.layout.movie_details_dialog);
        TextView title = (TextView) dialog.findViewById(R.id.dTitle);
        TextView buttonClose = (TextView) dialog.findViewById(R.id.buttonClose);
        TextView overview = (TextView) dialog.findViewById(R.id.dOverview);
        TextView release_date = (TextView) dialog.findViewById(R.id.release_date);
        release_date.setText("Release Date: "+moviesModel.getRelease_date());
        ImageView dPosterImage = (ImageView) dialog.findViewById(R.id.dPosterImage);
        TextView popularity = (TextView) dialog.findViewById(R.id.popularity);
        TextView vote = (TextView) dialog.findViewById(R.id.vote);
        vote.setText("Average Vote: "+moviesModel.getVote_average()+"/10");
        title.setText(moviesModel.getTitle());
        overview.setText(moviesModel.getOverview());
        popularity.setText("Popularity: "+moviesModel.getPopularity());

        GlideApp.with(context).load(Utils.posterPrePath+moviesModel.getPoster_path()).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(dPosterImage);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
}
