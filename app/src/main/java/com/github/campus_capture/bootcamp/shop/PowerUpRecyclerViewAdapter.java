package com.github.campus_capture.bootcamp.shop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.campus_capture.bootcamp.R;

import java.util.List;
import java.util.Locale;

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
        int value = mValues.get(position).getValue();
        int fund = mValues.get(position).getFund();

        holder.mItem = mValues.get(position);
        holder.powerUpName.setText(mValues.get(position).getName());
        holder.powerUpValue.setText(String.format(Locale.ENGLISH, "Value: %d", value));
        holder.powerUpFund.setText(String.format(Locale.ENGLISH, "Teams fund: %d", fund));
        holder.powerUpProgressBar.setProgress(100*fund/value);

        //TODO: Change the max to be the money the user have
        holder.powerUpSeekBar.setMax(value);

        holder.powerUpSeekBar.setProgress(0);

        addSeekBarChangeListener(holder);
    }

    private void addSeekBarChangeListener(ViewHolder holder){
        holder.powerUpSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                String format;
                if(i>0) {
                    format = String.format(Locale.ENGLISH, "I spend %d coins for my team.", i);

                }
                else
                {
                    format = "I spend literally nothing for my team.";
                }
                holder.powerUpSpendText.setText(format);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView powerUpName;
        public final TextView powerUpValue;
        public final TextView powerUpFund;
        public final Button powerUpButton;
        public final ProgressBar powerUpProgressBar;
        public PowerUp mItem;
        public final SeekBar powerUpSeekBar;
        public final TextView powerUpSpendText;

        public ViewHolder(View view) {
            super(view);
            powerUpName = view.findViewById(R.id.power_up_name);
            powerUpValue = view.findViewById(R.id.power_up_value);
            powerUpFund = view.findViewById(R.id.power_up_fund);
            powerUpProgressBar = view.findViewById(R.id.power_up_progress_bar);
            powerUpButton = view.findViewById(R.id.power_up_button);
            powerUpSeekBar = view.findViewById(R.id.power_up_seek_bar);
            powerUpSpendText = view.findViewById(R.id.power_up_spend_text);
        }
    }
}
