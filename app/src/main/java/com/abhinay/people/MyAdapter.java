package com.abhinay.people;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter <MyAdapter.ViewHolder>{

    RecyclerView recyclerView;
    Context context;
    ArrayList<String> items = new ArrayList<>();
    ArrayList<String> urls = new ArrayList<>();

    public void update(String name, String url){
        items.add(name);
        urls.add(url);
        notifyDataSetChanged(); //refreshes the recycler view automatically
    }

    public MyAdapter(RecyclerView recyclerView, Context context, ArrayList<String> items, ArrayList<String> urls) {
        this.recyclerView = recyclerView;
        this.context = context;
        this.items = items;
        this.urls = urls;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.document, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        //initialize the elements of indiv, items..
        viewHolder.nameOfFile.setText(items.get(i));
    }

    @Override
    public int getItemCount() {
        //return the no of items...
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView nameOfFile;

        public ViewHolder(View itemView){
            super(itemView);
            nameOfFile= itemView.findViewById(R.id.nameOfFile);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    int position = recyclerView.getChildLayoutPosition(view);
                    //intent to open the file in browser. change here.
                    Intent intent = new Intent();
//                    intent.setType(Intent.ACTION_VIEW); //denotes that we are going to view something
//                    intent.setData(Uri.parse(urls.get(position)));
                    intent.setDataAndType(Uri.parse(urls.get(position)), Intent.ACTION_VIEW );
                    context.startActivity(intent);
                }
            });
        }
    }
}
