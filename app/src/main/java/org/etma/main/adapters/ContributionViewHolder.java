package org.etma.main.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.etma.main.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContributionViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.contributed_by)
    public TextView contributedBy;

    @BindView(R.id.contribution_amount)
    public TextView contributionAmount;

    @BindView(R.id.contribution_date)
    public TextView contributionDate;

    @BindView(R.id.contribution_icon)
    public ImageView verified;

    @BindView(R.id.pledge_name)
    public TextView pledgeName;

    @BindView(R.id.payment_mode)
    public TextView paymentMode;

    public ContributionViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }
}
