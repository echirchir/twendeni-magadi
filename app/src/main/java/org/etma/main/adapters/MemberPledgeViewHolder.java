package org.etma.main.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.etma.main.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MemberPledgeViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.member_name)
    TextView memberName;

    @BindView(R.id.pledge_amount)
    TextView pledgeAmount;

    @BindView(R.id.pledge_date)
    TextView pledgeDate;

    @BindView(R.id.progress_limit)
    ProgressBar progressBar;

    @BindView(R.id.paid)
    TextView amountPaid;

    @BindView(R.id.pledge_percentage)
    TextView pledgePercentage;

    public MemberPledgeViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }
}
