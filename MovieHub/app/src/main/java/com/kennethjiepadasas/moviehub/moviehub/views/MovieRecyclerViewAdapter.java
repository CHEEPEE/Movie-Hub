package com.kennethjiepadasas.moviehub.moviehub.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kennethjiepadasas.moviehub.moviehub.R;
import com.kennethjiepadasas.moviehub.moviehub.model.MoviesModel;

import java.util.ArrayList;

/**
 * Created by Keji's Lab on 19/01/2018.
 */

public class MovieRecyclerViewAdapter extends RecyclerView.Adapter<MovieRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<MoviesModel> moviesModels;
    private Context context;



    public class MyViewHolder extends RecyclerView.ViewHolder{

       public TextView movieTItle,movieOverview;


        public MyViewHolder(View view){
            super(view);
            movieTItle = (TextView) view.findViewById(R.id.movieTitle);
            movieOverview = (TextView) view.findViewById(R.id.movieOverview);

        }
    }

    public MovieRecyclerViewAdapter(Context c, ArrayList<MoviesModel> moviesModels){
        this.moviesModels = moviesModels;
        this.context =c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movies_list_item,parent,false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        MoviesModel moviesModel = moviesModels.get(position);
        holder.movieTItle.setText(moviesModel.getTitle());
        holder.movieOverview.setText(moviesModel.getOverview());
    }

    @Override
    public int getItemCount() {
        return moviesModels.size();
    }
}


