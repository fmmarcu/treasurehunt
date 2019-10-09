package com.threess.summership.treasurehunt.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
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
import com.threess.summership.treasurehunt.model.User;
import com.threess.summership.treasurehunt.view.adapter.UserListAdapter;
import com.threess.summership.treasurehunt.viewmodel.UserViewModel;

import java.util.List;
import java.util.Objects;

public class HallOfFameFragment extends Fragment {

    private static final String TAG = HallOfFameFragment.class.getSimpleName();

    private View mView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final NavController navController = Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                navController.navigate(R.id.action_hallOfFameFragment_to_userProfileFragment);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        mView = inflater.inflate(R.layout.fragment_hall_of_fame, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        UserViewModel mUserViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(UserViewModel.class);
        mUserViewModel.getAllUsers();
        mUserViewModel.getAllUserLiveData().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                RecyclerView recyclerView = mView.findViewById(R.id.hall_of_fame_recycler_view);
                UserListAdapter adapter = new UserListAdapter(users, getActivity());

                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
            }
        });
    }

}
