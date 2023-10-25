package com.rjdev.meterinstallapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rjdev.meterinstallapp.activity.MeterDetailActivity;
import com.rjdev.meterinstallapp.Model.InstallDetailModel;
import com.rjdev.meterinstallapp.R;

import java.util.ArrayList;

public class OfflineDetailAdapter extends RecyclerView.Adapter<OfflineDetailAdapter.ViewHolder> {
    Context context;
    private ArrayList<InstallDetailModel> data_list;


    public OfflineDetailAdapter(ArrayList<InstallDetailModel> data_list,Context context) {

        this.data_list = data_list;
        this.context = context;
    }

    @NonNull
    @Override
    public OfflineDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_offline_data, parent, false);
        return new OfflineDetailAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OfflineDetailAdapter.ViewHolder holder, int position) {
        holder.tv_consumerid.setText(data_list.get(position).getconsumer_no());
        holder.tv_loc.setText(data_list.get(position).getAddress());
        holder.tv_meterno.setText(data_list.get(position).getmeter_no());
        holder.ll_main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MeterDetailActivity.class);
                intent.putExtra("customer",holder.tv_consumerid.getText().toString());
                context.startActivity(intent);


            }
        });

    }

    @Override
    public int getItemCount() {
        return data_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_consumerid,tv_loc,tv_meterno;
        LinearLayout ll_main_layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_consumerid = itemView.findViewById(R.id.tv_consumerid);
            tv_loc = itemView.findViewById(R.id.tv_loc);
            tv_meterno = itemView.findViewById(R.id.tv_meterno);
            ll_main_layout = itemView.findViewById(R.id.ll_main_layout);


        }
    }
}
