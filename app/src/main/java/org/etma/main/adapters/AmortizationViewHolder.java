package org.etma.main.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.etma.main.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AmortizationViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.icon)
    public ImageView icon;

    @BindView(R.id.due_date)
    public TextView date;

    @BindView(R.id.amount)
    public TextView amount;

    @BindView(R.id.pledge)
    public TextView pledge;

    public AmortizationViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }
}
