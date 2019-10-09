package com.threess.summership.treasurehunt.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {


    @LayoutRes
    abstract int layoutRes();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(layoutRes(), container, false);
    }









}
