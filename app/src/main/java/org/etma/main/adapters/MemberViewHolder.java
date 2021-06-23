package org.etma.main.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.etma.main.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MemberViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.icon)
    public ImageView icon;

    @BindView(R.id.fullname)
    public TextView fullName;

    @BindView(R.id.email)
    public TextView email;

    @BindView(R.id.amount)
    public TextView amount;

    public MemberViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }
}
