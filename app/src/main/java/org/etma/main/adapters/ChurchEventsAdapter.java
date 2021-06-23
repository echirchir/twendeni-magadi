package org.etma.main.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import org.etma.main.R;
import org.etma.main.ui.ChurchEventObject;

import java.util.ArrayList;
import java.util.List;

public class ChurchEventsAdapter extends RecyclerView.Adapter<EventViewHolder> {

    private List<ChurchEventObject> events;

    public ChurchEventsAdapter(List<ChurchEventObject> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card_layout, parent, false);

        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {

        holder.event_title.setText(events.get(position).getTitle());
        holder.event_date.setText(events.get(position).getDate());

        Picasso
                .get()
                .load(events.get(position).getImg_url())
                .placeholder(R.mipmap.logo)
                .centerCrop()
                .resize(100, 100)
                .error(R.mipmap.logo)
                .into(holder.event_image);

    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public ChurchEventObject getItem(int position){
        return events.get(position);
    }

    public void setModels(List<ChurchEventObject> orders){
        events = new ArrayList<>(orders);
    }

    public void animateTo(List<ChurchEventObject> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<ChurchEventObject> newModels) {
        for (int i = events.size() - 1; i >= 0; i--) {
            final ChurchEventObject model = events.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<ChurchEventObject> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final ChurchEventObject model = newModels.get(i);
            if (!events.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<ChurchEventObject> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final ChurchEventObject model = newModels.get(toPosition);
            final int fromPosition = events.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private ChurchEventObject removeItem(int position) {
        final ChurchEventObject model = events.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    private void addItem(int position, ChurchEventObject model) {
        events.add(position, model);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        final ChurchEventObject model = events.remove(fromPosition);
        events.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
}
