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
import org.etma.main.ui.MemberObject;

import java.util.ArrayList;
import java.util.List;

public class MembersAdapter extends RecyclerView.Adapter<MemberViewHolder> {

    private List<MemberObject> members;
    private Context context;

    public MembersAdapter(List<MemberObject> members, Context context) {
        this.members = members;
        this.context = context;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_card_layout, parent, false);

        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {

        holder.fullName.setText(members.get(position).getFullName());
        holder.email.setText(members.get(position).getEmail());
        holder.amount.setText(members.get(position).getAmount());

        String firstLetter = String.valueOf(members.get(position).getFullName().charAt(0));

        if (position % 2 == 0 ){
            TextDrawable drawable = TextDrawable.builder().buildRound(firstLetter, ContextCompat.getColor(context, R.color.red));
            holder.icon.setImageDrawable(drawable);
        }else {
            TextDrawable drawable = TextDrawable.builder().buildRound(firstLetter, ContextCompat.getColor(context, R.color.grey));
            holder.icon.setImageDrawable(drawable);
        }
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public void remove(int position){
        members.remove(position);
        notifyDataSetChanged();
    }


    public MemberObject getItem(int position){
        return members.get(position);
    }

    public void setModels(List<MemberObject> orders){
        members = new ArrayList<>(orders);
    }

    public void animateTo(List<MemberObject> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<MemberObject> newModels) {
        for (int i = members.size() - 1; i >= 0; i--) {
            final MemberObject model = members.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<MemberObject> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final MemberObject model = newModels.get(i);
            if (!members.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<MemberObject> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final MemberObject model = newModels.get(toPosition);
            final int fromPosition = members.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private MemberObject removeItem(int position) {
        final MemberObject model = members.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    private void addItem(int position, MemberObject model) {
        members.add(position, model);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        final MemberObject model = members.remove(fromPosition);
        members.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
}
