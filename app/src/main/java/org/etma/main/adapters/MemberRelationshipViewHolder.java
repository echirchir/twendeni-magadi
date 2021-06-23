package org.etma.main.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.etma.main.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MemberRelationshipViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.name)
    TextView name;

    public MemberRelationshipViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }
}
