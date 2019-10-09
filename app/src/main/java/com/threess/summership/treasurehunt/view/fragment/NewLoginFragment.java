package com.threess.summership.treasurehunt.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.threess.summership.treasurehunt.Constants;
import com.threess.summership.treasurehunt.R;
import com.threess.summership.treasurehunt.model.User;

import com.threess.summership.treasurehunt.viewmodel.LoginViewModel;
import com.threess.summership.treasurehunt.model.repository.GenericResponse;
import com.threess.summership.treasurehunt.viewmodel.UserViewModel;

import java.util.Objects;

import static com.threess.summership.treasurehunt.Constants.NONE;
import static com.threess.summership.treasurehunt.Constants.SHARED_PREFERENCE_NAME;

public class NewLoginFragment extends BaseFragment {

    private LoginViewModel mLoginViewModel;
    private UserViewModel mUserViewModel;
    private static final String TAG = NewLoginFragment.class.getSimpleName();
    private EditText mUsername, mPassword;
    private TextView mLoginError;

    @Override
    int layoutRes() {
        return R.layout.fragment_new_login;
    }

    @NonNull
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_new_login, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        mUserViewModel = ViewModelProviders.of((getActivity())).get(UserViewModel.class);
        mLoginViewModel = ViewModelProviders.of(requireActivity()).get(LoginViewModel.class);
        mUserViewModel.init();
        mUserViewModel.getResponse().observe(this, observer);
        mLoginViewModel.getAuthenticationState().observe(this, authenticationObserver);
        mLoginError = view.findViewById(R.id.loginErrorMessage);
        mUsername = view.findViewById(R.id.usernameEditText);
        mPassword = view.findViewById(R.id.passwordEditText);

        String sharedUserName = getSharedPreferenceUsername();

        if (!sharedUserName.equals(NONE)) {
            mUsername.setText(sharedUserName);
        }

        //navigation to register fragment on text click
        TextView createAccount = view.findViewById(R.id.registerAccount);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registrationFragment);
            }
        });
        Button login = view.findViewById(R.id.login_button);
        //Login button
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSharedPreferencesUsername(Constants.NONE);
                closeKeyboard();
                if (!mUsername.getText().toString().isEmpty() && !mPassword.getText().toString().isEmpty()) {
                    User user = new User(mUsername.getText().toString().trim(), mPassword.getText().toString());
                    userLogin(user);
                    //in case username or password are empty
                } else {
                    mLoginError.setText(R.string.fill_blanks);
                    mLoginError.setVisibility(View.VISIBLE);

                    Log.d(TAG, "Username and password null");
                }
            }
        });
    }

    private final Observer<LoginViewModel.AuthenticationState> authenticationObserver = new Observer<LoginViewModel.AuthenticationState>() {
        @Override
        public void onChanged(LoginViewModel.AuthenticationState authenticationState) {
            if (authenticationState == LoginViewModel.AuthenticationState.AUTHENTICATED) {
                final NavController navController = Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment);
                navController.navigate(R.id.action_loginFragment_to_treasureList);

            }
        }
    };

    private final Observer<GenericResponse> observer = new Observer<GenericResponse>() {

        @Override
        public void onChanged(GenericResponse genericResponse) {

            if (genericResponse != null && getActivity() != null) {
                if (genericResponse.IsSuccess()) {
                    String loginSuccess = "Login successful!";
                    if (genericResponse.getErrorMessage().equals(loginSuccess)) {
                        mLoginViewModel.authenticate(true);
                        setSharedPreferencesUsername(mUsername.getText().toString());
                    } else {
                        Log.d(TAG, "login unsuccessful");
                        //wrong username or password
                        mLoginError.setText(R.string.invalid_credentials);
                        mLoginError.setVisibility(View.VISIBLE);
                    }

                } else {
                    mLoginError.setText(R.string.network_failure);
                    mLoginError.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    private void userLogin(User user) {
        Log.d(TAG, "Login button clicked" + " " + user.getUserName() + " " + user.getPassword());
        mUserViewModel.loginUser(user);
    }

    //hide keyboard
    private void closeKeyboard() {
        if (getContext() != null) {
            InputMethodManager imm = (InputMethodManager) (getContext()).getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = getView();
            if (view != null && imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void setSharedPreferencesUsername(String username) {
        if (getActivity() != null && !TextUtils.isEmpty(username)) {
            SharedPreferences mSharedPreferences = getActivity().getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor mEditor = mSharedPreferences.edit();
            mEditor.putString(getString(R.string.preference_file_key_username), username);
            mEditor.apply();
        }
    }

    private String getSharedPreferenceUsername() {
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        String referenceFileKey = getResources().getString(R.string.preference_file_key_username);
        if (!sharedPreferences.getString(getString(R.string.preference_file_key_username), referenceFileKey).isEmpty()) {
            return sharedPreferences.getString(getString(R.string.preference_file_key_username), referenceFileKey);
        }
        return NONE;
    }
}