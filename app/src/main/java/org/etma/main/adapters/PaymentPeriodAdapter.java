package org.etma.main.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.etma.main.R;
import org.etma.main.ui.PaymentPeriodObject;

import java.util.ArrayList;
import java.util.List;

public class PaymentPeriodAdapter extends RecyclerView.Adapter<MemberRelationshipViewHolder> {

    private List<PaymentPeriodObject> periods;

    public PaymentPeriodAdapter(List<PaymentPeriodObject> periods) {
        this.periods = periods;
    }

    @NonNull
    @Override
    public MemberRelationshipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_relationship_layout, parent, false);

        return new MemberRelationshipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberRelationshipViewHolder holder, int position) {
        holder.name.setText(periods.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return periods.size();
    }

    public PaymentPeriodObject getItem(int position){
        return periods.get(position);
    }

    public void setModels(List<PaymentPeriodObject> orders){
        periods = new ArrayList<>(orders);
    }

    public void animateTo(List<PaymentPeriodObject> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<PaymentPeriodObject> newModels) {
        for (int i = periods.size() - 1; i >= 0; i--) {
            final PaymentPeriodObject model = periods.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<PaymentPeriodObject> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final PaymentPeriodObject model = newModels.get(i);
            if (!periods.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<PaymentPeriodObject> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final PaymentPeriodObject model = newModels.get(toPosition);
            final int fromPosition = periods.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private PaymentPeriodObject removeItem(int position) {
        final PaymentPeriodObject model = periods.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    private void addItem(int position, PaymentPeriodObject model) {
        periods.add(position, model);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        final PaymentPeriodObject model = periods.remove(fromPosition);
        periods.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
}
