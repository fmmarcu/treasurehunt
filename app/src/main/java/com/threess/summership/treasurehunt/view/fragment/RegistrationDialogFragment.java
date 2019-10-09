package com.threess.summership.treasurehunt.view.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.threess.summership.treasurehunt.R;
import com.threess.summership.treasurehunt.model.User;
import com.threess.summership.treasurehunt.model.repository.GenericResponse;
import com.threess.summership.treasurehunt.viewmodel.UserViewModel;

import static com.threess.summership.treasurehunt.Constants.NONE;

public class RegistrationDialogFragment extends Fragment {

    private static final String TAG = RegistrationDialogFragment.class.getSimpleName();
    private UserViewModel mUserViewModel;
    private TextView mErrorTextView;
    private EditText mUserName;
    private EditText mPassword;

    private Observer<GenericResponse> mObserver = new Observer<GenericResponse>() {
        @Override
        public void onChanged(@Nullable final GenericResponse registrationResponse) {
            if (registrationResponse.IsSuccess()) {
                final NavController navController = Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment);
                setSharedPreferencesUserName(mUserName.getText().toString());
                navController.navigate(R.id.action_registrationFragment_to_loginFragment);
            } else {
                if (mErrorTextView != null) {
                    mErrorTextView.setText(registrationResponse.getErrorMessage());
                    mErrorTextView.setVisibility(View.VISIBLE);
                }
                setSharedPreferencesUserName(NONE);
            }
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_registration, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUserName = view.findViewById(R.id.user_name);
        mErrorTextView = view.findViewById(R.id.error_message);
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        mUserViewModel.getUserLiveData().observe(this, mObserver);

        mPassword  = view.findViewById(R.id.password);
        final EditText confirmPassword = view.findViewById(R.id.confirm_password);

        final CheckBox checkBoxTerms = view.findViewById(R.id.terms);

        view.findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mUserViewModel.registerUser(new User(mUserName.getText().toString(),
                                mPassword.getText().toString()),
                        confirmPassword.getText().toString(), checkBoxTerms.isChecked());
            }
        });
    }

    private void setSharedPreferencesUserName(String username) {
        if (getActivity() != null && !TextUtils.isEmpty(username)) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(com.threess.summership.treasurehunt.Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.preference_file_key_username), username);
            editor.apply();
        }
    }
}
