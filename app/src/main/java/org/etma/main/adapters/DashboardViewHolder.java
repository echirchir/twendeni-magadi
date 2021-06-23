package org.etma.main.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.etma.main.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DashboardViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.icon)
    public ImageView icon;

    @BindView(R.id.icon_arrow)
    public ImageView iconArrow;

    @BindView(R.id.title)
    public TextView title;

    @BindView(R.id.amount)
    public TextView amount;

    @BindView(R.id.item_background)
    public LinearLayout background;


    public DashboardViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }
}
