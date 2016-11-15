package com.example.lesgo.wheretogo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by lesgo on 11/13/2016.
 */
public class CustomAdapter  extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder>{

    private List<Place> place_list;
    public CustomAdapter(List<Place> list) {
        place_list = list;
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder{
        public TextView txtName, txtAddress;

        public CustomViewHolder(View itemView){
            super(itemView);

            txtName = (TextView)itemView.findViewById(R.id.name);
            txtAddress = (TextView)itemView.findViewById(R.id.address);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Place p = place_list.get(getAdapterPosition());
                    listener.OnItemClick(p);
                }
            });
        }
    }

    public OnItemClickListener listener;

    public interface OnItemClickListener{
        void OnItemClick(Place place);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {this.listener = listener;}


    @Override
    public CustomAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflator = LayoutInflater.from(context);
        View v = inflator.inflate(R.layout.row_view,parent,false);
        return new CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CustomAdapter.CustomViewHolder holder, int position) {
        Place m = place_list.get(position);

        holder.txtAddress.setText(m.getAddress());
        holder.txtName.setText(m.getName());
    }

    @Override
    public int getItemCount() {
        if(place_list!=null){
            return place_list.size();
        }
        else
        {
            return 0;
        }
    }
}
