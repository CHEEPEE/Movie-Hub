package com.kennethjiepadasas.moviehub.moviehub;

import android.app.Application;
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
    String url = "https://api.themoviedb.org/3/discover/movie?api_key=38c21cee709b82cfe7ea0ab324d2f88c&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false&page=1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvMovieList = (RecyclerView) findViewById(R.id.rv_movielist);
        context = MainActivity.this;
        DatabaseHelper.getInstance(context,"moviehub.db");
        InternetAvailabilityChecker.init(this);
        client.newBuilder();
        movieRecyclerViewAdapter = new MovieRecyclerViewAdapter(context,moviesModelArrayList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager( context,2);
        rvMovieList.setLayoutManager(layoutManager);
        rvMovieList.setAdapter(movieRecyclerViewAdapter);

        getMovieList();

    }
    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {
        //do something based on connectivity
        if (isConnected){
                try {
                    moviesModelArrayList.clear();
                    getMovies();
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
                                moviesModel.setVote_average(jsonArray.getJSONObject(i).getInt("vote_average"));
                                moviesModel.setOverview(jsonArray.getJSONObject(i).getString("overview"));
                                moviesModelArrayList.add(moviesModel);
                                System.out.println(jsonArray.getJSONObject(i).getString("title"));
                            }
                            movieRecyclerViewAdapter.notifyDataSetChanged();
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
            System.out.println(movieListJson.substring(1370,movieListJson.length()));
            String aux =movieListJson.replace("'","\"");
            JSONObject json = new JSONObject(aux.replace("#23d5kjh","'"));
            JSONArray jsonArray = json.getJSONArray("results");
            System.out.println(json.getJSONArray("results"));
            for (int i = 0; i<jsonArray.length();i++){
                MoviesModel moviesModel = new MoviesModel();
                moviesModel.setTitle(jsonArray.getJSONObject(i).getString("title"));
                moviesModel.setId(jsonArray.getJSONObject(i).getInt("id"));
                moviesModel.setPoster_path(jsonArray.getJSONObject(i).getString("poster_path"));
                moviesModel.setVote_average(jsonArray.getJSONObject(i).getInt("vote_average"));
                moviesModel.setOverview(jsonArray.getJSONObject(i).getString("overview"));
                moviesModelArrayList.add(moviesModel);
                System.out.println(jsonArray.getJSONObject(i).getString("title"));
            }
            movieRecyclerViewAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getMovieList(){
        if(isNetworkAvailable(context)){
            try {
                getMovies();
            }catch (IOException e){
                System.out.print("failed " + e);
            }
        }else {
            loadfromDB();
        }
    }
}
