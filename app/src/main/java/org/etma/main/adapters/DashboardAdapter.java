package org.etma.main.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.etma.main.R;

import java.util.List;

public class DashboardAdapter extends  RecyclerView.Adapter<DashboardViewHolder>{

    private List<DashboardItem> items;
    private Context context;

    public DashboardAdapter(List<DashboardItem> items, Context ctx) {
        this.items = items;
        this.context = ctx;
    }

    @Override
    public DashboardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dash_item_card, parent, false);

        return new DashboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardViewHolder holder, int position) {

        holder.title.setText(items.get(position).getTitle());
        holder.amount.setText(items.get(position).getAmount());

        switch (position){
            case 0:
                holder.background.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                holder.iconArrow.setImageResource(R.drawable.arrow_right);
                holder.icon.setImageResource(R.drawable.members);
                break;
            case 1:
                holder.background.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
                holder.iconArrow.setImageResource(R.drawable.arrow_right);
                holder.icon.setImageResource(R.drawable.money);
                break;
            case 2:
                holder.background.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
                holder.iconArrow.setImageResource(R.drawable.arrow_right);
                holder.icon.setImageResource(R.drawable.contribute);
                break;
            case 3:
                holder.background.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                holder.title.setTextColor(ContextCompat.getColor(context, R.color.black));
                holder.amount.setTextColor(ContextCompat.getColor(context, R.color.black));
                holder.iconArrow.setImageResource(R.drawable.arrow_black);
                holder.icon.setImageResource(R.drawable.balance);
                break;
            case 4:
                holder.background.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                holder.title.setTextColor(ContextCompat.getColor(context, R.color.black));
                holder.amount.setTextColor(ContextCompat.getColor(context, R.color.black));
                holder.iconArrow.setImageResource(R.drawable.arrow_black);
                holder.icon.setImageResource(R.drawable.statements);
                break;
            case 5:
                holder.background.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                holder.title.setTextColor(ContextCompat.getColor(context, R.color.black));
                holder.amount.setTextColor(ContextCompat.getColor(context, R.color.black));
                holder.iconArrow.setImageResource(R.drawable.arrow_black);
                holder.icon.setImageResource(R.drawable.extra_campaigns);

                break;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public DashboardItem getItem(int position){
        return items.get(position);
    }
}
