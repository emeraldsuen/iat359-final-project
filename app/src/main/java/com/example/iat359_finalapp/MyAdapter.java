package com.example.iat359_finalapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static com.example.iat359_finalapp.R.layout.row;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    public ArrayList<String> list;
    Context context;

    public MyAdapter(ArrayList<String> list) {
        this.list = list;
    }

    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(row, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyAdapter.MyViewHolder holder, int position) {

        String[] results = (list.get(position).toString()).split(",");
        holder.nameTextView.setText(results[0]);
        holder.typeTextView.setText(results[1]);
        holder.distTextView.setText(results[2]);
        holder.outputTextView.setText(results[3]);
        holder.volumeTextView.setText(results[4]);
        holder.vibrateTextView.setText(results[5]);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView nameTextView;
        public TextView typeTextView;
        public TextView distTextView;
        public TextView outputTextView;
        public TextView volumeTextView;
        public TextView vibrateTextView;
        public LinearLayout myLayout;

        private String name, type, dist, output, vol, vibrate;

        Context context;

        public MyViewHolder(View itemView) {
            super(itemView);
            myLayout = (LinearLayout) itemView;

            nameTextView = (TextView) itemView.findViewById(R.id.routeNameEntry);
            typeTextView = (TextView) itemView.findViewById(R.id.routeTypeEntry);
            distTextView = (TextView) itemView.findViewById(R.id.routeDistEntry);
            outputTextView = (TextView) itemView.findViewById(R.id.routeOutputEntry);
            volumeTextView = (TextView) itemView.findViewById(R.id.routeVolumeEntry);
            vibrateTextView = (TextView) itemView.findViewById(R.id.routeVibrateEntry);



            itemView.setOnClickListener(this);
            context = itemView.getContext();

        }

        @Override
        public void onClick(View view) {
            Toast.makeText(context,
                    "You have clicked " + ((TextView) view.findViewById(R.id.routeNameEntry)).getText().toString(),
                    Toast.LENGTH_SHORT).show();

            Intent i = new Intent(context, SetupActivity.class);

            name = (String) nameTextView.getText();
            type = (String) typeTextView.getText();
            dist = (String) distTextView.getText();
            output = (String) outputTextView.getText();
            vol = (String) volumeTextView.getText();
            vibrate = (String) vibrateTextView.getText();

            //attach extras
            i.putExtra("name", name);
            i.putExtra("type", type);
            i.putExtra("dist", dist);
            i.putExtra("output", output);
            i.putExtra("vol", vol);
            i.putExtra("vibrate", vibrate);

            context.startActivity(i);

        }
    }
}