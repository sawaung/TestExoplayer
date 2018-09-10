package saa.com.testexoplayer;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import saa.com.testexoplayer.Model.video;

/**
 * Created by admin on 9/8/2018.
 */

public class VideoRecyclerViewAdapter extends RecyclerView.Adapter<VideoRecyclerViewAdapter.MyViewHolder> {
    private List<video> moviesList;
    Context mContext;
    String tag = "VideoRecyclerViewAdapter";



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_video_row_item,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        video objVideo = this.moviesList.get(position);
        holder.title.setText(objVideo.title);
        holder.genre.setText(objVideo.genre);
        holder.year.setText(objVideo.year);
       /* Glide.with(holder.itemView)
                .load(Uri.parse(objVideo.imageLink))
                .into(holder.imageView);*/

       Log.i(tag , "uir -> " + Uri.parse(objVideo.imageLink));

        Glide.with(mContext).load(Uri.parse(objVideo.imageLink)).crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return this.moviesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;
        public ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            genre = (TextView) view.findViewById(R.id.genre);
            year = (TextView) view.findViewById(R.id.year);
            imageView = (ImageView)view.findViewById(R.id.video_image);
        }
    }

    public void setMoviesList(List<video> moviesList) {
        this.moviesList = moviesList;
        this.notifyDataSetChanged();
    }
}
