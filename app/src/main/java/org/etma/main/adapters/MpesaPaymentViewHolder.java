package org.etma.main.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.etma.main.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MpesaPaymentViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.mpesa_status)
    public ImageView status;
    @BindView(R.id.payment_mode)
    public TextView paymentMode;

    @BindView(R.id.payment_date)
    public TextView paymentDate;

    @BindView(R.id.payment_amount)
    public TextView paymentAmount;

    @BindView(R.id.payment_status)
    public TextView paymentStatus;

    public MpesaPaymentViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }
}
