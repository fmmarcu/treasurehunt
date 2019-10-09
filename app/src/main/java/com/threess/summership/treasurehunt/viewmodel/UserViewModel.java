package com.threess.summership.treasurehunt.viewmodel;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.threess.summership.treasurehunt.model.User;
import com.threess.summership.treasurehunt.model.network.GenericCallback;
import com.threess.summership.treasurehunt.model.repository.GenericResponse;
import com.threess.summership.treasurehunt.model.repository.UserRepository;

import java.util.List;

import retrofit2.http.Body;

import static com.threess.summership.treasurehunt.Constants.AGREE;
import static com.threess.summership.treasurehunt.Constants.NONE;
import static com.threess.summership.treasurehunt.Constants.PASS_NON_MATCH;

public class UserViewModel extends ViewModel {

    private UserRepository mUserRepository;
    private static final String TAG = UserViewModel.class.getSimpleName();
    private MutableLiveData<GenericResponse> mUserMutableLiveData;
    private MutableLiveData<List<User>> mAllUserLiveData;
    private MutableLiveData<User> mMutableLiveData;

    public UserViewModel() {
        super();
        mUserRepository = UserRepository.getInstance();
        mUserMutableLiveData = new MutableLiveData<>();
        mAllUserLiveData = new MutableLiveData<>();
    }

    public void init() { mUserRepository = UserRepository.getInstance(); }
    public void init(String userName) {
        if (mMutableLiveData != null) {
            return;
        }
        if (!TextUtils.isEmpty(userName)) {
            UserRepository mUsersRepository = UserRepository.getInstance();
            mMutableLiveData = mUsersRepository.getUsersProfile(userName);
        }
    }

    public void updatePhoto(String username, String filePath) {
        mUserRepository.updatePhoto(username, filePath);
    }

    public void loginUser(@Body User user) {
        mUserRepository.loginUser(user);
    }

    public MutableLiveData<GenericResponse> getResponse() {
        return mUserRepository.getResponse();
    }

    public MutableLiveData<GenericResponse> getUserLiveData() {
        return mUserMutableLiveData;
    }

    public MutableLiveData<List<User>> getAllUserLiveData() {
        return mAllUserLiveData;
    }

    public MutableLiveData<User> getUsersProfileRepository() {
        return mMutableLiveData;
    }


    public void registerUser(User userFromFragment, String confirmPassword, boolean termsAccepted) {
        String userPassword = userFromFragment.getPassword();
        if (!termsAccepted) {
            mUserMutableLiveData.setValue(new GenericResponse(AGREE, false));
        }
        else if (passwordsDoMatch(userPassword, confirmPassword)) {
            mUserMutableLiveData.setValue(new GenericResponse(PASS_NON_MATCH, false));
        }
        else {
            mUserRepository.registerUser(userFromFragment, new GenericCallback<GenericResponse>() {
                @Override
                public void onResponseReady(GenericResponse genericResponse) {
                    mUserMutableLiveData.setValue(genericResponse);
                }
            });
        }
    }

    private boolean passwordsDoMatch(String userPassword, String confirmPassword) {
        return !userPassword.equals(confirmPassword);
    }

    public void getAllUsers() {
        mUserRepository.getAllUsers(new GenericCallback<List<User>>() {
            @Override
            public void onResponseReady(List<User> generic) {
                mAllUserLiveData.setValue(generic);
            }
        });
    }

    public void setProfilePicturePath(String photoURI, String username) {
        User newUser = mMutableLiveData.getValue();
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(photoURI) && newUser != null) {
            newUser.setProfilePicture(photoURI);
        }
        mMutableLiveData.setValue(newUser);

    }

    public void setProfileUsername(String username) {
        if (TextUtils.isEmpty(username)) {
            username = NONE;
        }
        User newUser = mMutableLiveData.getValue();
        if (newUser != null) {
            newUser.setUserName(username);
        }
        mMutableLiveData.setValue(newUser);

    }
}
