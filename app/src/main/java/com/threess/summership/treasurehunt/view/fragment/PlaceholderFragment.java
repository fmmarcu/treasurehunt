package com.threess.summership.treasurehunt.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.threess.summership.treasurehunt.R;
import com.threess.summership.treasurehunt.viewmodel.LoginViewModel;

public class PlaceholderFragment extends Fragment {
    private LoginViewModel mLoginViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_placeholder, container, false);
        mLoginViewModel = ViewModelProviders.of(requireActivity()).get(LoginViewModel.class);
        final NavController navController = Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment);

        mLoginViewModel.getAuthenticationState().observe(getViewLifecycleOwner(),
                new Observer<LoginViewModel.AuthenticationState>() {
                    @Override
                    public void onChanged(LoginViewModel.AuthenticationState authenticationState) {
                        switch (authenticationState) {
                            case UNAUTHENTICATED:
                                navController.navigate(R.id.newLoginFragment);
                                break;
                            case AUTHENTICATED:
                                navController.navigate(R.id.treasureListFragment);
                                break;
                        }
                    }
                });
        return view;
    }
}
