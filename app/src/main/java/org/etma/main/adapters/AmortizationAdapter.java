package org.etma.main.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amulyakhare.textdrawable.TextDrawable;

import org.etma.main.R;
import org.etma.main.db.MemberPledge;
import org.etma.main.helpers.Util;
import org.etma.main.ui.AmortizationObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class AmortizationAdapter extends RecyclerView.Adapter<AmortizationViewHolder> {

    private List<AmortizationObject> amortizations;
    private Context context;
    private Realm realm;

    public AmortizationAdapter(List<AmortizationObject> amortizations, Context context, Realm realm) {

        this.amortizations = amortizations;
        this.context = context;
        this.realm = realm;
    }

    @NonNull
    @Override
    public AmortizationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.amortization_item_layout, parent, false);

        return new AmortizationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AmortizationViewHolder holder, int position) {

        long pledgeId = amortizations.get(position).getLocalPledgeId();

        MemberPledge pledge = realm.where(MemberPledge.class).equalTo("localPledgeId", pledgeId).findFirst();

        holder.amount.setText(Util.formatMoney(Util.DECIMAL_FORMAT, (amortizations.get(position).getAmount() * 1.0)));
        holder.date.setText("Due date : " + amortizations.get(position).getContributionDate());

        //holder.pledge.setText(pledge.getName());

        String firstLetter = "#"+String.valueOf(position+1);

        if (position % 2 == 0 ){
            TextDrawable drawable = TextDrawable.builder().buildRound(firstLetter, ContextCompat.getColor(context, R.color.red));
            holder.icon.setImageDrawable(drawable);
        }else {
            TextDrawable drawable = TextDrawable.builder().buildRound(firstLetter, ContextCompat.getColor(context, R.color.grey));
            holder.icon.setImageDrawable(drawable);
        }

    }

    public void remove(int position){
        amortizations.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return amortizations.size();
    }

    public int getAmortizationId( int position ){

        return amortizations.get(position).getAmortizationId();
    }

    public long getItemId( int position ){

        return amortizations.get( position ).getId();
    }

    public void setModels(List<AmortizationObject> orders){
        amortizations = new ArrayList<>(orders);
    }

    public void animateTo(List<AmortizationObject> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<AmortizationObject> newModels) {
        for (int i = amortizations.size() - 1; i >= 0; i--) {
            final AmortizationObject model = amortizations.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<AmortizationObject> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final AmortizationObject model = newModels.get(i);
            if (!amortizations.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<AmortizationObject> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final AmortizationObject model = newModels.get(toPosition);
            final int fromPosition = amortizations.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private AmortizationObject removeItem(int position) {
        final AmortizationObject model = amortizations.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    private void addItem(int position, AmortizationObject model) {
        amortizations.add(position, model);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        final AmortizationObject model = amortizations.remove(fromPosition);
        amortizations.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
}
