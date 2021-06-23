package org.etma.main.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.etma.main.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class EventViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.event_image)
    public CircleImageView event_image;

    @BindView(R.id.event_title)
    public TextView event_title;

    @BindView(R.id.event_date)
    public TextView event_date;

    public EventViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }
}
