package com.rjdev.meterinstallapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rjdev.meterinstallapp.Model.ReportModel;
import com.rjdev.meterinstallapp.R;

import java.util.ArrayList;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {
    Context context;
    private ArrayList<ReportModel> data_list;


    public ReportAdapter(ArrayList<ReportModel> data_list, Context context) {

        this.data_list = data_list;
        this.context = context;
    }

    @NonNull
    @Override
    public ReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_offline_data, parent, false);
        return new ReportAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ReportAdapter.ViewHolder holder, int position) {
        holder.tv_consumerid.setText(data_list.get(position).getConsumer_no());
        holder.tv_loc.setText(data_list.get(position).getLocation());
        holder.tv_meterno.setText(data_list.get(position).getMeter_no());


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
