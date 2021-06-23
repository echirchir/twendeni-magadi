package org.etma.main.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.etma.main.R;
import org.etma.main.db.PaymentMode;
import org.etma.main.helpers.Util;
import org.etma.main.ui.ContributionObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class ContributionsAdapter extends RecyclerView.Adapter<ContributionViewHolder>{

    private List<ContributionObject> contributions;

    public ContributionsAdapter(List<ContributionObject> contributions) {
        this.contributions = contributions;
    }

    @NonNull
    @Override
    public ContributionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contribution_item_layout, parent, false);

        return new ContributionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContributionViewHolder holder, int position) {

        holder.contributedBy.setText(contributions.get(position).getPaidBy());
        holder.contributionAmount.setText("KES " + Util.formatMoney(Util.DECIMAL_FORMAT, Double.parseDouble(contributions.get(position).getAmount())));
        holder.contributionDate.setText(contributions.get(position).getContributionDate());
        holder.pledgeName.setText(contributions.get(position).getPledgeName());

        boolean verified = Boolean.parseBoolean(contributions.get(position).getVerified());

        if (verified){
            holder.verified.setImageResource(R.drawable.success);
        }else{
            holder.verified.setImageResource(R.drawable.cancel);
        }


        String mode = contributions.get(position).getPayMode().toUpperCase();

        String[] modes = mode.split(" ");

        if (modes.length > 1){

            if (modes[0].equals("BANK")){
                holder.paymentMode.setText(String.valueOf(modes[0].charAt(0)) + " " + modes[1]);
            }else{
                holder.paymentMode.setText(mode);
            }
        }else{
            holder.paymentMode.setText(mode);
        }

    }

    @Override
    public int getItemCount() {
        return contributions.size();
    }

    public ContributionObject getItem(int position){
        return contributions.get(position);
    }

    public void setModels(List<ContributionObject> orders){
        contributions = new ArrayList<>(orders);
    }

    public void animateTo(List<ContributionObject> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<ContributionObject> newModels) {
        for (int i = contributions.size() - 1; i >= 0; i--) {
            final ContributionObject model = contributions.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<ContributionObject> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final ContributionObject model = newModels.get(i);
            if (!contributions.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<ContributionObject> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final ContributionObject model = newModels.get(toPosition);
            final int fromPosition = contributions.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private ContributionObject removeItem(int position) {
        final ContributionObject model = contributions.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    private void addItem(int position, ContributionObject model) {
        contributions.add(position, model);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        final ContributionObject model = contributions.remove(fromPosition);
        contributions.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
}
