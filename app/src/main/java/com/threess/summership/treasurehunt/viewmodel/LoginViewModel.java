package com.threess.summership.treasurehunt.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {
    public enum AuthenticationState {
        UNAUTHENTICATED,        // Initial state, the user needs to authenticate
        AUTHENTICATED,          // The user has authenticated successfully
        INVALID_AUTHENTICATION  // Authentication failed
    }

    private MutableLiveData<AuthenticationState> authenticationState =
            new MutableLiveData<>();


    public LoginViewModel() {
        authenticationState.setValue(AuthenticationState.UNAUTHENTICATED);
    }
    public void authenticate(boolean cool) {
        if (cool) {
            authenticationState.setValue(AuthenticationState.AUTHENTICATED);
        } else {
            authenticationState.setValue(AuthenticationState.UNAUTHENTICATED);
        }
    }

    public MutableLiveData<AuthenticationState> getAuthenticationState() {
        return authenticationState;
    }
}
