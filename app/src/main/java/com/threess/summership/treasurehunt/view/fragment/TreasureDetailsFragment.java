package com.threess.summership.treasurehunt.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.threess.summership.treasurehunt.R;
import com.threess.summership.treasurehunt.model.Treasure;
import com.threess.summership.treasurehunt.viewmodel.TreasureViewModel;

public class TreasureDetailsFragment extends Fragment {
    private static final String TAG = TreasureDetailsFragment.class.getSimpleName();
    private static final String BASE_PHOTO_URL = "http://5.254.125.248:3000";

    TreasureViewModel mTreasureViewModel;
    Treasure mTreasure;

    private TextView mTreasureTitle;
    private TextView mTreasureDescription;
    private TextView mTreasurePrizePoints;
    private ImageView mTreasurePhotoClue;
    private String mImagePath;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_treasure_details, container, false);

        mTreasureViewModel = ViewModelProviders.of(this).get(TreasureViewModel.class);

        mTreasure = mTreasureViewModel.getSelected();

        mTreasureTitle = view.findViewById(R.id.tv_title);
        mTreasureTitle.setText(mTreasure.getTitle());
        mTreasureDescription = view.findViewById(R.id.tv_description);
        mTreasureDescription.setText(mTreasure.getDescription());
        mTreasurePrizePoints = view.findViewById(R.id.tv_prize_points);
        mTreasurePrizePoints.setText("" + mTreasure.getPrizePoints());
        mTreasurePrizePoints = view.findViewById(R.id.tv_claimed_by);
        mTreasurePrizePoints.setText(mTreasure.getClaimedBy());
        mTreasurePrizePoints = view.findViewById(R.id.tv_created_at);
        mTreasurePrizePoints.setText(mTreasure.getClaimedAt());

        mTreasurePhotoClue = view.findViewById(R.id.iv_photo_clue);

        mImagePath = BASE_PHOTO_URL + mTreasure.getPhotoClue();
        Glide.with(TreasureDetailsFragment.this)
                .asBitmap()
                .centerCrop()
                .load(mImagePath)
                .into(mTreasurePhotoClue);

        Button buttonClaim = view.findViewById(R.id.btn_try_claim);
        buttonClaim.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_treasureDetailsFragment_to_claimTreasureFragment, null));



        return view;
    }

}
