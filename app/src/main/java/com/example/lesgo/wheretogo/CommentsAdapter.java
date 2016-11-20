package com.example.lesgo.wheretogo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by lesgo on 11/20/2016.
 */
public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CustomViewHolder>  {

    public class CustomViewHolder extends RecyclerView.ViewHolder{

        public CustomViewHolder(View itemView){
            super(itemView);
        }
    }


    @Override
    public CommentsAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflator = LayoutInflater.from(context);
        View v = inflator.inflate(,parent,false);
        return new CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }
}
