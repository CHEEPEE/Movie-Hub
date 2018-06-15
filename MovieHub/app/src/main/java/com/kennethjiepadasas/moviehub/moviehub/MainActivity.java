package com.kennethjiepadasas.moviehub.moviehub;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.kennethjiepadasas.moviehub.moviehub.model.MoviesModel;
import com.kennethjiepadasas.moviehub.moviehub.views.MovieRecyclerViewAdapter;

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

public class MainActivity extends AppCompatActivity {
    OkHttpClient client = new OkHttpClient();
    ArrayList<MoviesModel> moviesModelArrayList = new ArrayList<>();
    MovieRecyclerViewAdapter movieRecyclerViewAdapter;
    RecyclerView rvMovieList;
    String url = "https://api.themoviedb.org/3/discover/movie?api_key=38c21cee709b82cfe7ea0ab324d2f88c&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false&page=1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvMovieList = (RecyclerView) findViewById(R.id.rv_movielist);
        try {
            getMovies();
        }catch (IOException e){
            System.out.print("failed " + e);
        }
        client.newBuilder();
        movieRecyclerViewAdapter = new MovieRecyclerViewAdapter(MainActivity.this,moviesModelArrayList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager( MainActivity.this,2);
        rvMovieList.setLayoutManager(layoutManager);
        rvMovieList.setAdapter(movieRecyclerViewAdapter);
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

                final String myResponse = response.body().string();
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(myResponse);
                        try {

                            JSONObject json = new JSONObject(myResponse);
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
    public boolean isInternetAvailable() {
        try {
            final InetAddress address = InetAddress.getByName("www.google.com");
            return !address.equals("");
        } catch (UnknownHostException e) {
            // Log error
        }
        return false;
    }

}
