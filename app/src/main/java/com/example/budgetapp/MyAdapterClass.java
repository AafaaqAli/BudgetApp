package com.example.budgetapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapterClass extends RecyclerView.Adapter<MyAdapterClass.MyViewHolder> {
    private ArrayList<Stats> arrayListStats = new ArrayList<Stats>();
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView mImageView;
        TextView mTextViewDescription;
        TextView mTextViewPercentage;
        TextView mTextViewAmount;

        MyViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.imageViewItemRecyclerView);
            mTextViewDescription = itemView.findViewById(R.id.textViewRecyclerItemDescription);
            mTextViewPercentage = itemView.findViewById(R.id.textViewRecyclerItemPercentage);
            mTextViewAmount = itemView.findViewById(R.id.textViewRecyclerItemAmount);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }

                    }
                }
            });
        }
    }

    MyAdapterClass() {
    }

    MyAdapterClass(ArrayList<Stats> mArrayListStats) {
        this.arrayListStats = mArrayListStats;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerview, viewGroup, false);
        MyViewHolder myViewHolder = new MyViewHolder(view, mListener);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        Stats currentItem = arrayListStats.get(position);
        myViewHolder.mImageView.setImageResource(currentItem.getImageResource());
        myViewHolder.mTextViewDescription.setText(currentItem.getDescription());
        myViewHolder.mTextViewPercentage.setText(currentItem.getPercentage());
        myViewHolder.mTextViewAmount.setText(currentItem.getAmount());
    }

    @Override
    public int getItemCount() {
        return arrayListStats.size();
    }


}
