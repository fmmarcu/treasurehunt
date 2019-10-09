package com.threess.summership.treasurehunt.view.adapter;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.threess.summership.treasurehunt.R;
import com.threess.summership.treasurehunt.model.Treasure;
import com.threess.summership.treasurehunt.viewmodel.TreasureLocalViewModel;

import java.util.List;

import static com.threess.summership.treasurehunt.Constants.TYPE_FIRST_ITEM;
import static com.threess.summership.treasurehunt.Constants.TYPE_ITEM;


public class TreasureListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String TAG = TreasureListAdapter.class.getSimpleName();

    private OnTreasureListener mOnTreasureListener;
    private List<Treasure> mTreasureData;
    private TreasureLocalViewModel mTreasureLocalViewModel;


    public TreasureListAdapter(OnTreasureListener onTreasureListener, List<Treasure> treasures,
                               TreasureLocalViewModel viewModel) {
        this.mTreasureData = treasures;

        this.mOnTreasureListener = onTreasureListener;

        this.mTreasureLocalViewModel = viewModel;

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case TYPE_FIRST_ITEM:
                final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_treasure_list_add, parent, false);
                return new AddTreasureViewHolder(view, mOnTreasureListener);
            case TYPE_ITEM:
                final View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_treasure_list, parent, false);
                return new TreasureViewHolder(view2, mOnTreasureListener);
            default:
                return null;
        }


    }

    @Override
    public int getItemViewType(int position) {
        return position == 0
                ?TYPE_FIRST_ITEM
                :TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case TYPE_FIRST_ITEM:
                AddTreasureViewHolder firstHolder = (AddTreasureViewHolder) holder;
                // Do what you need for the first item
                break;
            case TYPE_ITEM:
                TreasureViewHolder normalHolder = (TreasureViewHolder) holder;
                normalHolder.itemView.setTag(position);
                normalHolder.treasureNameTextView.setText(mTreasureData.get(position).getTitle());
                normalHolder.treasureDescriptionTextView.setText(mTreasureData.get(position).getDescription());
                normalHolder.treasureClaimedToggleButton.setClickable(false);
                normalHolder.treasureClaimedToggleButton.setChecked(mTreasureData.get(position).isClaimed());
                normalHolder.treasureFavoriteToggleButton.setChecked(mTreasureData.get(position).isFavorite());
                break;
        }

    }

    @Override
    public int getItemCount() {
        return mTreasureData.size();
    }

    public class TreasureViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView treasureNameTextView;
        private TextView treasureDescriptionTextView;
        private ToggleButton treasureFavoriteToggleButton;
        private ToggleButton treasureClaimedToggleButton;
        private OnTreasureListener onTreasureListener;

        TreasureViewHolder(View itemView, OnTreasureListener onTreasureListener) {
            super(itemView);

            treasureNameTextView = itemView.findViewById(R.id.tv_treasure_name);
            treasureDescriptionTextView = itemView.findViewById(R.id.tv_treasure_description);
            treasureFavoriteToggleButton = itemView.findViewById(R.id.tb_treasure_favorite);
            treasureClaimedToggleButton = itemView.findViewById(R.id.tb_treasure_claimed);

            this.onTreasureListener = onTreasureListener;
            itemView.setOnClickListener(this);

            treasureFavoriteToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checkState) {
                    if (checkState && !mTreasureData.get(getAdapterPosition()).isFavorite()) {
                        mTreasureData.get(getAdapterPosition()).setmIsFavorite(true);
                        mTreasureLocalViewModel.update(mTreasureData.get(getAdapterPosition()));
                        Log.d(TAG, "onCheckedChanged: update true " + getAdapterPosition());
                    } else if (!checkState && mTreasureData.get(getAdapterPosition()).isFavorite()) {
                        mTreasureData.get(getAdapterPosition()).setmIsFavorite(false);
                        mTreasureLocalViewModel.update(mTreasureData.get(getAdapterPosition()));
                        Log.d(TAG, "onCheckedChanged: update false " + getAdapterPosition());

                    }
                }
            });
        }

        @Override
        public void onClick(View view) {
            onTreasureListener.onTreasureClick(getAdapterPosition());
        }
    }

    public class AddTreasureViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private OnTreasureListener onTreasureListener;

        public AddTreasureViewHolder(@NonNull View itemView, OnTreasureListener onTreasureListener) {
            super(itemView);
            this.onTreasureListener = onTreasureListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onTreasureListener.onTreasureClick(getAdapterPosition());
        }
    }

    public interface OnTreasureListener {
        void onTreasureClick(int position);
    }
}
