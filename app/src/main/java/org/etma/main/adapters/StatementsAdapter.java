package org.etma.main.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.etma.main.R;
import org.etma.main.ui.StatementObject;

import java.util.ArrayList;
import java.util.List;

public class StatementsAdapter extends RecyclerView.Adapter<StatementViewHolder>{

    private List<StatementObject> statements;

    public StatementsAdapter(List<StatementObject> statements) {
        this.statements = statements;
    }

    @Override
    public StatementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.statement_card_layout, parent, false);

        return new StatementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StatementViewHolder holder, int position) {

        holder.mStatementRefNumber.setText(statements.get(position).getType());
        holder.mStatementDate.setText(statements.get(position).getDate());
        holder.mStatementDescription.setText(statements.get(position).getDescription());
        holder.mStatementAmount.setText(statements.get(position).getAmount());

    }

    @Override
    public int getItemCount() {
        return statements.size();
    }

    public void remove(int position){
        statements.remove(position);
        notifyDataSetChanged();
    }


    public StatementObject getItem(int position){
        return statements.get(position);
    }

    public void setModels(List<StatementObject> orders){
        statements = new ArrayList<>(orders);
    }

    public void animateTo(List<StatementObject> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<StatementObject> newModels) {
        for (int i = statements.size() - 1; i >= 0; i--) {
            final StatementObject model = statements.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<StatementObject> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final StatementObject model = newModels.get(i);
            if (!statements.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<StatementObject> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final StatementObject model = newModels.get(toPosition);
            final int fromPosition = statements.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private StatementObject removeItem(int position) {
        final StatementObject model = statements.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    private void addItem(int position, StatementObject model) {
        statements.add(position, model);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        final StatementObject model = statements.remove(fromPosition);
        statements.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
}
