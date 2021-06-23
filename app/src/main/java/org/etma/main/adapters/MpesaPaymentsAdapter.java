package org.etma.main.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.etma.main.R;
import org.etma.main.ui.MpesaPaymentObject;

import java.util.ArrayList;
import java.util.List;

public class MpesaPaymentsAdapter extends RecyclerView.Adapter<MpesaPaymentViewHolder> {

    private List<MpesaPaymentObject> payments;

    private Context context;

    public MpesaPaymentsAdapter(List<MpesaPaymentObject> payments, Context context) {
        this.payments = payments;
        this.context = context;
    }

    @NonNull
    @Override
    public MpesaPaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mpesa_payment_card, parent, false);

        return new MpesaPaymentViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MpesaPaymentViewHolder holder, int position) {

        holder.paymentMode.setText(payments.get(position).getPayMode());
        holder.paymentAmount.setText(payments.get(position).getAmount());
        holder.paymentDate.setText(payments.get(position).getDate());
        holder.paymentStatus.setText(payments.get(position).getStatus());

        String responseCode = payments.get(position).getCode();

        switch (responseCode){

            case "0":
                holder.status.setImageResource(R.drawable.success);
                break;
                default:
                    holder.status.setImageResource(R.drawable.cancel);
                    break;
        }


    }

    @Override
    public int getItemCount() {
        return payments.size();
    }

    public void setModels(List<MpesaPaymentObject> orders){
        payments = new ArrayList<>(orders);
    }

    public void animateTo(List<MpesaPaymentObject> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<MpesaPaymentObject> newModels) {
        for (int i = payments.size() - 1; i >= 0; i--) {
            final MpesaPaymentObject model = payments.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<MpesaPaymentObject> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final MpesaPaymentObject model = newModels.get(i);
            if (!payments.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<MpesaPaymentObject> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final MpesaPaymentObject model = newModels.get(toPosition);
            final int fromPosition = payments.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private MpesaPaymentObject removeItem(int position) {
        final MpesaPaymentObject model = payments.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    private void addItem(int position, MpesaPaymentObject model) {
        payments.add(position, model);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        final MpesaPaymentObject model = payments.remove(fromPosition);
        payments.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
}
