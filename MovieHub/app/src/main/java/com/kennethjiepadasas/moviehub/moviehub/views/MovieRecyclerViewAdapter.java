package com.kennethjiepadasas.moviehub.moviehub.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.module.AppGlideModule;
import com.kennethjiepadasas.moviehub.moviehub.GlideApp;
import com.kennethjiepadasas.moviehub.moviehub.GlideAppModule;
import com.kennethjiepadasas.moviehub.moviehub.R;
import com.kennethjiepadasas.moviehub.moviehub.model.MoviesModel;

import java.util.ArrayList;

/**
 * Created by Keji's Lab on 19/01/2018.
 */

public class MovieRecyclerViewAdapter extends RecyclerView.Adapter<MovieRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<MoviesModel> moviesModels;
    private Context context;
    String posterPrePath = "https://image.tmdb.org/t/p/w500/";

    public class MyViewHolder extends RecyclerView.ViewHolder{

       public TextView movieTItle,movieOverview,averageVote,readmore;
       public ImageView posterImage;


        public MyViewHolder(View view){
            super(view);
            movieTItle = (TextView) view.findViewById(R.id.movieTitle);
            readmore = (TextView) view.findViewById(R.id.readmore);
            movieOverview = (TextView) view.findViewById(R.id.movieOverview);
            averageVote = (TextView) view.findViewById(R.id.averageVote);
            posterImage =(ImageView) view.findViewById(R.id.posterImage);

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
        final MoviesModel moviesModel = moviesModels.get(position);
        holder.movieTItle.setText(moviesModel.getTitle());
        holder.movieOverview.setText(moviesModel.getOverview());
        holder.averageVote.setText(moviesModel.getVote_average()+"");
        holder.readmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickLitener.onItemClick(v,position,moviesModel);
            }
        });
        GlideApp.with(context).load(posterPrePath+moviesModel.getPoster_path()).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(holder.posterImage);
    }

    @Override
    public int getItemCount() {
        return moviesModels.size();
    }
    public interface OnItemClickLitener {
        void onItemClick(View view, int position,MoviesModel moviesModel);

    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickListener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
}


