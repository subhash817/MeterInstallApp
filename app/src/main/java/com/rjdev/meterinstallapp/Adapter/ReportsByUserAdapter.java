package com.rjdev.meterinstallapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rjdev.meterinstallapp.R;
import com.rjdev.meterinstallapp.reports.InstallationCountUserdate;

import java.util.List;

public class ReportsByUserAdapter extends RecyclerView.Adapter<ReportsByUserAdapter.myViewHolder> {
    Context context;
    private List<InstallationCountUserdate> byUserList;

    public ReportsByUserAdapter(List<InstallationCountUserdate> byUserList, Context context) {
        this.context = context;
        this.byUserList = byUserList;
    }

    @NonNull
    @Override
    public ReportsByUserAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.by_user_item_report, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportsByUserAdapter.myViewHolder holder, int position) {
        InstallationCountUserdate report = byUserList.get(position);
        holder.txtSLNo.setText(String.valueOf(position + 1));
        holder.txtinstallType.setText(report.getInstallationTypeName());
        holder.txtInstallId.setText(report.getInstallationId());
        holder.txtInstallCount.setText(report.getInstallCount());
        holder.txtType.setText(report.getInstallationTypeName());
        holder.txtCounts.setText(report.getInstallCount());
    }

    @Override
    public int getItemCount() {
        return byUserList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView txtinstallType, txtInstallId, txtInstallCount, txtSLNo,txtType,txtCounts;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            txtinstallType = itemView.findViewById(R.id.txtinstallType);
            txtSLNo = itemView.findViewById(R.id.txtSLNo);
            txtInstallId = itemView.findViewById(R.id.txtInstallId);
            txtInstallCount = itemView.findViewById(R.id.txtInstallCount);
            txtCounts = itemView.findViewById(R.id.txtCounts);
            txtType = itemView.findViewById(R.id.txtType);
        }
    }
}
