package org.etma.main.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.etma.main.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StatementViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.statement_icon) public ImageView mStatementType;
    @BindView(R.id.statement_ref_number) public TextView mStatementRefNumber;
    @BindView(R.id.statement_date) public TextView mStatementDate;
    @BindView(R.id.statement_description) public TextView mStatementDescription;
    @BindView(R.id.statement_amount) public TextView mStatementAmount;

    public StatementViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }
}
