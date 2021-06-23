package org.etma.main.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.etma.main.R;
import org.etma.main.ui.MemberRelationshipObject;

import java.util.ArrayList;
import java.util.List;

public class MemberRelationshipAdapter extends RecyclerView.Adapter<MemberRelationshipViewHolder> {

    private List<MemberRelationshipObject> relationships;

    public MemberRelationshipAdapter(List<MemberRelationshipObject> relationships) {
        this.relationships = relationships;
    }

    @NonNull
    @Override
    public MemberRelationshipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_relationship_layout, parent, false);

        return new MemberRelationshipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberRelationshipViewHolder holder, int position) {

        holder.name.setText(relationships.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return relationships.size();
    }

    public MemberRelationshipObject getItem(int position){
        return relationships.get(position);
    }

    public void setModels(List<MemberRelationshipObject> orders){
        relationships = new ArrayList<>(orders);
    }

    public void animateTo(List<MemberRelationshipObject> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<MemberRelationshipObject> newModels) {
        for (int i = relationships.size() - 1; i >= 0; i--) {
            final MemberRelationshipObject model = relationships.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<MemberRelationshipObject> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final MemberRelationshipObject model = newModels.get(i);
            if (!relationships.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<MemberRelationshipObject> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final MemberRelationshipObject model = newModels.get(toPosition);
            final int fromPosition = relationships.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private MemberRelationshipObject removeItem(int position) {
        final MemberRelationshipObject model = relationships.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    private void addItem(int position, MemberRelationshipObject model) {
        relationships.add(position, model);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        final MemberRelationshipObject model = relationships.remove(fromPosition);
        relationships.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
}
