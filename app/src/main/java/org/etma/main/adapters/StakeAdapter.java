package org.etma.main.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.etma.main.R;
import org.etma.main.ui.StakeObject;

import java.util.ArrayList;
import java.util.List;

public class StakeAdapter extends RecyclerView.Adapter<MemberRelationshipViewHolder> {

    private List<StakeObject> stakes;

    public StakeAdapter(List<StakeObject> stakes) {
        this.stakes = stakes;
    }

    @NonNull
    @Override
    public MemberRelationshipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_relationship_layout, parent, false);

        return new MemberRelationshipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberRelationshipViewHolder holder, int position) {
        holder.name.setText(stakes.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return stakes.size();
    }

    public StakeObject getItem(int position){
        return stakes.get(position);
    }

    public void setModels(List<StakeObject> orders){
        stakes = new ArrayList<>(orders);
    }

    public void animateTo(List<StakeObject> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<StakeObject> newModels) {
        for (int i = stakes.size() - 1; i >= 0; i--) {
            final StakeObject model = stakes.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<StakeObject> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final StakeObject model = newModels.get(i);
            if (!stakes.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<StakeObject> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final StakeObject model = newModels.get(toPosition);
            final int fromPosition = stakes.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private StakeObject removeItem(int position) {
        final StakeObject model = stakes.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    private void addItem(int position, StakeObject model) {
        stakes.add(position, model);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        final StakeObject model = stakes.remove(fromPosition);
        stakes.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
}
