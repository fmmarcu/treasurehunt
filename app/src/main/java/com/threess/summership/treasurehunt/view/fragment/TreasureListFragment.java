package com.threess.summership.treasurehunt.view.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.threess.summership.treasurehunt.R;
import com.threess.summership.treasurehunt.model.Treasure;
import com.threess.summership.treasurehunt.view.adapter.TreasureListAdapter;
import com.threess.summership.treasurehunt.viewmodel.TreasureLocalViewModel;
import com.threess.summership.treasurehunt.viewmodel.TreasureViewModel;

import java.util.ArrayList;
import java.util.List;

public class TreasureListFragment extends Fragment implements TreasureListAdapter.OnTreasureListener {
    public static final String TAG = TreasureListFragment.class.getSimpleName();

    private List<Treasure> mLocalTreasureArrayList = new ArrayList<>();

    private TreasureListAdapter mTreasureListAdapter;
    private TreasureViewModel mTreasureViewModel;
    private TreasureLocalViewModel mTreasureLocalViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_treasure_list, container, false);

        RecyclerView rvTreasureList = view.findViewById(R.id.rv_treasure_list);
        rvTreasureList.setLayoutManager(new LinearLayoutManager(getActivity()));

        final ToggleButton favoriteFilterToggle = view.findViewById(R.id.tb_fav_filter);
        favoriteFilterToggle.setChecked(false);

        mTreasureViewModel = ViewModelProviders.of(this).get(TreasureViewModel.class);
        mTreasureViewModel.init();
        mTreasureLocalViewModel = ViewModelProviders.of(this).get(TreasureLocalViewModel.class);

        favoriteFilterToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checkState) {
                if (checkState) {
                    mTreasureLocalViewModel.setFavorites();
                    Log.d(TAG, "onCheckedChanged fav: true");
                } else {
                    mTreasureLocalViewModel.setAllTreasures();
                    Log.d(TAG, "onCheckedChanged fav: false");
                }
            }
        });


        Observer remoteObserver = new Observer<List<Treasure>>() {
            @Override
            public void onChanged(List<Treasure> treasures) {
                Log.d(TAG, "onChanged: remote " + treasures.size());
                if (mLocalTreasureArrayList.size() == 1) {
                    mTreasureLocalViewModel.deleteAllTreasure();
                    insertAll(treasures);
                } else if (mLocalTreasureArrayList.size() < treasures.size() + 1 && !favoriteFilterToggle.isChecked()) {
                    List<Treasure> oldTreasures = new ArrayList<>();
                    oldTreasures.addAll(mLocalTreasureArrayList);
                    //remove the first because it is a place holder for the add treasures card
                    oldTreasures.remove(0);
                    int sizeOfLocalTreasures = mLocalTreasureArrayList.size();
                    int numberOfNewTreasures = treasures.size() - mLocalTreasureArrayList.size();
                    insertAfterRefresh(numberOfNewTreasures, sizeOfLocalTreasures, treasures, oldTreasures);
                }
            }
        };

        Observer localObserver = new Observer<List<Treasure>>() {
            @Override
            public void onChanged(List<Treasure> treasures) {
                mLocalTreasureArrayList.clear();
                mLocalTreasureArrayList.add(new Treasure());
                if (treasures != null) {
                    mLocalTreasureArrayList.addAll(treasures);
                    Log.d(TAG, "onChanged: local " + mLocalTreasureArrayList.size());
                    mTreasureListAdapter.notifyDataSetChanged();
                }
            }
        };

        mTreasureViewModel.getTreasureRepository().observe(getViewLifecycleOwner(), remoteObserver);

        mTreasureLocalViewModel.getAllTreasures().observe(this, localObserver);

        mTreasureListAdapter = new TreasureListAdapter(this, mLocalTreasureArrayList, mTreasureLocalViewModel);
        rvTreasureList.setAdapter(mTreasureListAdapter);

        return view;
    }

    private void insertAll(List<Treasure> treasures) {
        for (Treasure treasure : treasures) {
            mTreasureLocalViewModel.insert(treasure);
        }
        mTreasureLocalViewModel.getAllTreasures().setValue(treasures);
        Log.d(TAG, "insertAll: s a populat");
    }

    private void insertAfterRefresh(int numberOfNewTreasures, int sizeOfLocalTreasures, List<Treasure> newTreasures, List<Treasure> oldTreasures){
        Log.d(TAG, "insertAfterRefresh: " + oldTreasures.size());
        for (int i = 1; i < numberOfNewTreasures; i++) {
            mTreasureLocalViewModel.insert(newTreasures.get(sizeOfLocalTreasures + i));
            oldTreasures.add(newTreasures.get(sizeOfLocalTreasures + i));
        }
        mTreasureLocalViewModel.getAllTreasures().setValue(oldTreasures);
    }

    @Override
    public void onTreasureClick(int position) {

        final NavController navController = Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment);

        if (position == 0) {
            navController.navigate(R.id.action_treasureListFragment_to_createTreasureFragment);
        } else {
            mLocalTreasureArrayList.get(position);
            Treasure mSelectedTreasure = mLocalTreasureArrayList.get(position);
            mTreasureViewModel.select(mSelectedTreasure);
            if (navController.getCurrentDestination().getId() == R.id.treasureListFragment) {
                navController.navigate(R.id.action_treasureListFragment_to_treasureDetailsFragment);
            }
        }
    }

}
