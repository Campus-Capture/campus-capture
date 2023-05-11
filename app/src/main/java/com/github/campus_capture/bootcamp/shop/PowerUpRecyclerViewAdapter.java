package com.github.campus_capture.bootcamp.shop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.campus_capture.bootcamp.R;
import com.github.campus_capture.bootcamp.databinding.FragmentVotePowerUpBinding;

import java.util.List;

public class PowerUpRecyclerViewAdapter extends RecyclerView.Adapter<PowerUpRecyclerViewAdapter.ViewHolder>{

    private final List<PowerUp> mValues;

    public PowerUpRecyclerViewAdapter(List<PowerUp> items) {
        mValues = items;
    }

    @NonNull
    @Override
    public PowerUpRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //return new PowerUpRecyclerViewAdapter.ViewHolder(FragmentVotePowerUpBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_power_up_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PowerUpRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.powerUpName.setText(mValues.get(position).getName());
        holder.powerUpValue.setText(Integer.toString(mValues.get(position).getValue()));
        holder.powerUpFund.setText(Integer.toString(mValues.get(position).getFund()));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView powerUpName;
        public final TextView powerUpValue;
        public final TextView powerUpFund;
        public PowerUp mItem;

        public ViewHolder(View view) {
            super(view);
            powerUpName = view.findViewById(R.id.power_up_name);
            powerUpValue = view.findViewById(R.id.power_up_value);
            powerUpFund = view.findViewById(R.id.power_up_fund);
        }
    }
}
