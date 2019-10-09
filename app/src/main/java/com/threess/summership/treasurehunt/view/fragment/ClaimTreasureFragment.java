package com.threess.summership.treasurehunt.view.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.threess.summership.treasurehunt.R;


import com.threess.summership.treasurehunt.viewmodel.TreasureViewModel;

import java.util.Objects;

import static com.threess.summership.treasurehunt.Constants.NONE;
import static com.threess.summership.treasurehunt.Constants.SHARED_PREFERENCE_NAME;
import static com.threess.summership.treasurehunt.Constants.WRONG_PASSCODE;

public class ClaimTreasureFragment extends Fragment {
    private static final String TAG = ClaimTreasureFragment.class.getSimpleName();
    private Button mClaimTrasureButton;
    private EditText mClaimTreasurePasscode;
    private View mView;
    private TreasureViewModel mTreasureViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_claim_treasure, container, false);
        mClaimTrasureButton = mView.findViewById(R.id.claimTreasureClaimTreasureButton);
        mClaimTreasurePasscode = mView.findViewById(R.id.claimTreasurePasscode);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTreasureViewModel = ViewModelProviders.of(this).get(TreasureViewModel.class);

        mClaimTrasureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(getSharedPreferencesScore()) && !TextUtils.isEmpty(mClaimTreasurePasscode.getText()) && mTreasureViewModel.getSelected().getPasscode().equals(mClaimTreasurePasscode.getText().toString())) {

                    mTreasureViewModel.claimTreasure(getSharedPreferencesUsername(), mClaimTreasurePasscode.getText().toString(), (mTreasureViewModel.getSelected().getPrizePoints() + Long.valueOf(getSharedPreferencesScore())));
                    mTreasureViewModel.getClaimTreasureResponse().observe(ClaimTreasureFragment.this, mObserver);

                } else {
                    Snackbar.make(mView.findViewById(R.id.fragmentClaimTreasure), WRONG_PASSCODE, Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    private Observer<String> mObserver = new Observer<String>() {
        @Override
        public void onChanged(String claimTreasureResponse) {
            Snackbar.make(mView.findViewById(R.id.fragmentClaimTreasure), claimTreasureResponse, Snackbar.LENGTH_LONG)
                    .show();
            Log.d(TAG, claimTreasureResponse);
        }
    };

    private String getSharedPreferencesUsername() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        String mReferenceFileKey = getResources().getString(R.string.preference_file_key_username);
        if (!mSharedPreferences.getString(getString(R.string.preference_file_key_username), mReferenceFileKey).isEmpty()) {
            return mSharedPreferences.getString(getString(R.string.preference_file_key_username), mReferenceFileKey);
        }
        return NONE;
    }

    private String getSharedPreferencesScore() {
        SharedPreferences mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        String mReferenceFileKey = getResources().getString(R.string.preference_file_key_score);
        if (!mSharedPreferences.getString(getString(R.string.preference_file_key_score), mReferenceFileKey).isEmpty()) {
            return mSharedPreferences.getString(getString(R.string.preference_file_key_score), mReferenceFileKey);
        }
        return NONE;
    }

}
