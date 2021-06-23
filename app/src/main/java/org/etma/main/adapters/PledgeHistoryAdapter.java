package org.etma.main.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.etma.main.R;
import org.etma.main.db.MemberPledge;
import org.etma.main.helpers.Util;
import org.etma.main.ui.MemberPledgeObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class PledgeHistoryAdapter extends RecyclerView.Adapter<MemberPledgeViewHolder>{

    private List<MemberPledgeObject> pledges;

    public PledgeHistoryAdapter(List<MemberPledgeObject> pledges) {
        this.pledges = pledges;
    }

    @NonNull
    @Override
    public MemberPledgeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_pledge_layout, parent, false);

        return new MemberPledgeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberPledgeViewHolder holder, int position) {

        Realm realm  = Realm.getDefaultInstance();
        double totals = pledges.get(position).getAmount();
        MemberPledge pledge = realm.where(MemberPledge.class).equalTo("id", pledges.get(position).getId()).findFirst();
        holder.memberName.setText(pledges.get(position).getMemberName());
        holder.pledgeAmount.setText("Bal : " + Util.formatMoney(Util.DECIMAL_FORMAT, pledges.get(position).getPaid()));
        holder.pledgeDate.setText(pledges.get(position).getDate());
        double percentage = (Double.parseDouble(pledge.getContributed()) / totals) * 100;
        holder.progressBar.setProgress((int)percentage);
        holder.pledgePercentage.setText( new DecimalFormat("#").format(percentage) + "%");
        holder.amountPaid.setText("KES " + Util.formatMoney(Util.DECIMAL_FORMAT, pledges.get(position).getAmount()));
    }

    @Override
    public int getItemCount() {
        return pledges.size();
    }

    public void remove(int position){
        pledges.remove(position);
        notifyDataSetChanged();
    }

    public long getLocalPledgeId(int position){

        return pledges.get(position).getLocalPledgeId();
    }


    public MemberPledgeObject getItem(int position){
        return pledges.get(position);
    }

    public void setModels(List<MemberPledgeObject> orders){
        pledges = new ArrayList<>(orders);
    }

    public void animateTo(List<MemberPledgeObject> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<MemberPledgeObject> newModels) {
        for (int i = pledges.size() - 1; i >= 0; i--) {
            final MemberPledgeObject model = pledges.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<MemberPledgeObject> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final MemberPledgeObject model = newModels.get(i);
            if (!pledges.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<MemberPledgeObject> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final MemberPledgeObject model = newModels.get(toPosition);
            final int fromPosition = pledges.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private MemberPledgeObject removeItem(int position) {
        final MemberPledgeObject model = pledges.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    private void addItem(int position, MemberPledgeObject model) {
        pledges.add(position, model);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        final MemberPledgeObject model = pledges.remove(fromPosition);
        pledges.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
}
