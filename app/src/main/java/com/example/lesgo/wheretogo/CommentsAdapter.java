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

    private List<Comment> comment_list;
    public CommentsAdapter(List<Comment> list){comment_list = list;}

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        public TextView txtcomment,txtname,txtrating;
        public ImageView imgView;

        public CustomViewHolder(View itemView){
            super(itemView);
            txtcomment = (TextView)itemView.findViewById(R.id.comment);
            txtname = (TextView)itemView.findViewById(R.id.username);
            txtrating = (TextView)itemView.findViewById(R.id.rating);
        }
    }


    @Override
    public CommentsAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflator = LayoutInflater.from(context);
        View v = inflator.inflate(R.layout.comment_view,parent,false);
        return new CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Comment c = comment_list.get(position);
        holder.txtcomment.setText("\""+c.getComment() + "\"");
        holder.txtname.setText("by user "+c.getUsername());
        holder.txtrating.setText(c.getRating());

    }

    @Override
    public int getItemCount() {
        if(comment_list!=null){
            return comment_list.size();
        }
        else
        {
            return 0;
        }

    }
}
