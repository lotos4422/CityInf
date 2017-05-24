package com.example.cityinf.CityDataClasses;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cityinf.R;
import com.squareup.picasso.Picasso;

import java.util.List;


public class RVAdapter extends RecyclerView.Adapter<RVAdapter.SummaryHolder> {

    private List<Geoname> list;
    private static Context context;

    RVAdapter(List<Geoname> s) {
        this.list = s;
    }


    public static void link(Context c) {
        context = c;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public SummaryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        SummaryHolder pvh = new SummaryHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(SummaryHolder holder, int position) {
        holder.summary.setText(list.get(position).getSummary());
        holder.title.setText(list.get(position).getTitle());
        if(list.get(position).getThumbnailImg()==null)
        holder.Photo.setImageResource(R.mipmap.ic_city);
        else Picasso.with(context).load(list.get(position).getThumbnailImg()).into(holder.Photo);
    }

    @Override
    public int getItemCount() {
        if(list==null)
            return -1;
        return list.size();
    }

    public static class SummaryHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView summary;
        TextView title;
        ImageView Photo;

        public SummaryHolder(View itemView) {
            super(itemView);
            summary = (TextView) itemView.findViewById(R.id.summary_textView);
            title = (TextView) itemView.findViewById(R.id.title_textview);
            Photo = (ImageView) itemView.findViewById(R.id.photo);
        }
    }
}
