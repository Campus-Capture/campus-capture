package com.github.campus_capture.bootcamp.shop;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.campus_capture.bootcamp.R;
import com.github.campus_capture.bootcamp.firebase.BackendInterface;

import java.util.List;
import java.util.Locale;

public class PowerUpRecyclerViewAdapter extends RecyclerView.Adapter<PowerUpRecyclerViewAdapter.ViewHolder>{

    private final List<PowerUp> mValues;

    private int userMoney;

    private final BackendInterface backendInterface;
    private final TextView powerUpMoney;

    private int fund;
    private int value;

    public PowerUpRecyclerViewAdapter(List<PowerUp> items, int userMoney, BackendInterface backendInterface, TextView moneyView) {
        mValues = items;
        this.backendInterface = backendInterface;
        this.userMoney = userMoney;
        powerUpMoney = moneyView;
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
        value = mValues.get(position).getValue();
        fund = mValues.get(position).getFund();

        holder.mItem = mValues.get(position);
        holder.powerUpName.setText(mValues.get(position).getName());
        holder.powerUpValue.setText(String.format(Locale.ENGLISH, "Value: %d", value));
        holder.powerUpFund.setText(String.format(Locale.ENGLISH, "Teams fund: %d", fund));
        holder.powerUpProgressBar.setProgress(100*fund/value);

        holder.powerUpSeekBar.setMax(userMoney);

        holder.powerUpSeekBar.setProgress(0);

        addSeekBarChangeListener(holder);

        addSpendButtonListener(holder, mValues.get(position).getName());
    }

    private void addSpendButtonListener(ViewHolder holder, String powerUpName){
        holder.powerUpButton.setOnClickListener(view -> {
            int spendValue = holder.powerUpSeekBar.getProgress();
            if(spendValue > userMoney){
                Toast.makeText(powerUpMoney.getContext(), "Well... you are too broke.", Toast.LENGTH_LONG).show();
            } else {
                backendInterface.sendMoney(powerUpName, spendValue).thenAccept(b -> {
                        if(b)
                        {
                            userMoney -= spendValue;
                            powerUpMoney.setText(String.format(Locale.ENGLISH, "Money: %d", userMoney));

                            fund += spendValue;
                            holder.powerUpFund.setText(String.format(Locale.ENGLISH, "Teams fund: %d", fund));

                            holder.powerUpProgressBar.setProgress(100*fund/value);

                            holder.powerUpSeekBar.setMax(userMoney);
                        }
                        else
                        {
                            Toast.makeText(powerUpMoney.getContext(), "Server refused", Toast.LENGTH_LONG).show();
                        }
                }).exceptionally(e -> {
                    Toast.makeText(powerUpMoney.getContext(), "Failed to buy item", Toast.LENGTH_LONG).show();
                    Log.e("Shop_Screen", "Failed to buy item: " + e.getMessage());
                    return null;
                });
            }
        });
    }

    private void addSeekBarChangeListener(ViewHolder holder){
        holder.powerUpSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                String format;
                if(i>0) {
                    format = String.format(Locale.ENGLISH, "I spend %d coins for my team.", i);
                    holder.powerUpButton.setEnabled(true);
                }
                else
                {
                    format = "I spend literally nothing for my team.";
                    holder.powerUpButton.setEnabled(false);
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
