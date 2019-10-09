package com.threess.summership.treasurehunt.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;

import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.threess.summership.treasurehunt.R;
import com.threess.summership.treasurehunt.model.Treasure;
import com.threess.summership.treasurehunt.model.User;
import com.threess.summership.treasurehunt.viewmodel.LoginViewModel;
import com.threess.summership.treasurehunt.viewmodel.TreasureViewModel;
import com.threess.summership.treasurehunt.viewmodel.UserViewModel;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;


import de.hdodenhof.circleimageview.CircleImageView;

import static com.threess.summership.treasurehunt.Constants.AUTHORITY;
import static com.threess.summership.treasurehunt.Constants.NONE;
import static com.threess.summership.treasurehunt.Constants.PHOTO;
import static com.threess.summership.treasurehunt.Constants.SHARED_PREFERENCE_NAME;
import static com.threess.summership.treasurehunt.Constants.SUFFIX;
import static com.threess.summership.treasurehunt.Constants.USER_SCORE;
import static com.threess.summership.treasurehunt.Constants.USER_TRASURES_DISCOVERED;
import static com.threess.summership.treasurehunt.Constants.USER_TRASURES_HIDDEN;
import static com.threess.summership.treasurehunt.Constants.USER_USERNAME;

import static com.threess.summership.treasurehunt.Constants.TIME_INTERVAL;

public class UserProfileFragment extends Fragment {
    private static final String TAG = UserProfileFragment.class.getSimpleName();
    private UserViewModel mUserViewModel;
    private TextView mUserProfileUsername;
    private TextView mUserProfileScore;
    private TextView mUserProfileTreasuresDiscovered;
    private TextView mUserProfileTreasuresHidden;
    private Button mUserProfileUpdateButton;
    private Button mUserProfileLogOutButton;
    private CircleImageView mUserProfileImageView;
    private Uri mUserProfileImageViewUri;
    private String mReferenceFileKey;
    private String mUsername;
    private String mImagePath;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private String mScore;
    TreasureViewModel mTreasureViewModel;
    private int mTrasuresDiscovered = 0;
    private int mTresuresHidden = 0;

    private LoginViewModel mLoginViewModel;

    private long mBackPressed;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        mTreasureViewModel = ViewModelProviders.of(UserProfileFragment.this).get(TreasureViewModel.class);
        mTreasureViewModel.init();
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        mUserViewModel.init(getSharedPreferencesUsername());

        mLoginViewModel = ViewModelProviders.of(requireActivity()).get(LoginViewModel.class);
        final NavController navController = Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment);
        mLoginViewModel.getAuthenticationState().observe(getViewLifecycleOwner(),
                new Observer<LoginViewModel.AuthenticationState>() {
                    @Override
                    public void onChanged(LoginViewModel.AuthenticationState authenticationState) {
                        if (authenticationState == LoginViewModel.AuthenticationState.UNAUTHENTICATED) {
                            navController.navigate(R.id.action_userProfileFragment_to_loginFragment);
                        }
                    }
                });



        mUserViewModel.init(getSharedPreferencesUsername());
        mUserProfileTreasuresDiscovered = view.findViewById(R.id.userProfileTreasuresDiscoveredLabel);
        mUserProfileTreasuresHidden = view.findViewById(R.id.userProfileTreasuresHiddenLabel);
        mUserProfileImageView = view.findViewById(R.id.userProfileImageView);
        mUserProfileScore = view.findViewById(R.id.userProfileScoreLabel);
        mUserProfileUsername = view.findViewById(R.id.userProfileUsernameLabel);
        mUserProfileUpdateButton = view.findViewById(R.id.userProfileUpdateButton);
        mUserProfileLogOutButton = view.findViewById(R.id.userProfileLogOutButton);


        mTreasureViewModel.getTreasureRepository().observe(UserProfileFragment.this, mTreasureObserver);
        mUserViewModel.getUsersProfileRepository().observe(UserProfileFragment.this, mObserver);


        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                    getActivity().finish();
                    return;
                } else {
                    Toast.makeText(getActivity(), "Click two times to close an activity", Toast.LENGTH_SHORT).show();
                }
                mBackPressed = System.currentTimeMillis();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        mUserProfileLogOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSharedPreferencesUsername("");
                mLoginViewModel.authenticate(false);

            }
        });
        mUserProfileUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        super.onViewCreated(view, savedInstanceState);

    }

    private void setSharedPreferencesUsername(String username) {
        mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(com.threess.summership.treasurehunt.Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mEditor.putString(getString(R.string.preference_file_key_username), username);
        mEditor.apply();
        mReferenceFileKey = getResources().getString(R.string.preference_file_key_username);
        mUsername = mSharedPreferences.getString(getString(R.string.preference_file_key_username), mReferenceFileKey);
    }

    private void setSharedPreferencesImagePath(String imagePath) {
        mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(com.threess.summership.treasurehunt.Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mEditor.putString(getString(R.string.preference_file_key_image_path), imagePath);
        mEditor.apply();
        mReferenceFileKey = getResources().getString(R.string.preference_file_key_image_path);
        mImagePath = mSharedPreferences.getString(getString(R.string.preference_file_key_image_path), mReferenceFileKey);
    }

    private String getSharedPreferencesImagePath() {
        mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        mReferenceFileKey = getResources().getString(R.string.preference_file_key_image_path);
        return mSharedPreferences.getString(getString(R.string.preference_file_key_image_path), mReferenceFileKey);
    }

    private String getSharedPreferencesUsername() {
        mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        mReferenceFileKey = getResources().getString(R.string.preference_file_key_username);
        if (!mSharedPreferences.getString(getString(R.string.preference_file_key_username), mReferenceFileKey).isEmpty()) {
            return mSharedPreferences.getString(getString(R.string.preference_file_key_username), mReferenceFileKey);
        }
        return NONE;
    }

    private void setSharedPreferencesScore(Integer score) {
        mSharedPreferences = getActivity().getSharedPreferences(com.threess.summership.treasurehunt.Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mEditor.putString(getString(R.string.preference_file_key_score), score.toString());
        mEditor.apply();
        mReferenceFileKey = getResources().getString(R.string.preference_file_key_score);
        mScore = mSharedPreferences.getString(getString(R.string.preference_file_key_score), mReferenceFileKey);
    }

    private String getSharedPreferencesScore() {
        mSharedPreferences = getActivity().getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        mReferenceFileKey = getResources().getString(R.string.preference_file_key_score);
        if (!mSharedPreferences.getString(getString(R.string.preference_file_key_score), mReferenceFileKey).isEmpty()) {
            return mSharedPreferences.getString(getString(R.string.preference_file_key_score), mReferenceFileKey);
        }
        return NONE;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!TextUtils.isEmpty(mUsername) && resultCode == -1) {
            mUserViewModel.updatePhoto(mUsername, mImagePath);
            mUserViewModel.setProfilePicturePath(mImagePath, mUsername);
            mUserViewModel.setProfileUsername(mUsername);
        }
    }


    private Observer<List<Treasure>> mTreasureObserver = new Observer<List<Treasure>>() {
        @Override
        public void onChanged(List<Treasure> treasures) {
            for (Treasure treasure :treasures){
                if(treasure.mClaimedBy.equals(mUsername)){
                    mTrasuresDiscovered++;
                }
                if(treasure.mUsername.equals(mUsername)){
                    mTresuresHidden++;
                }
            }
            Resources res = getResources();
            mUserProfileTreasuresDiscovered.setText(String.format(res.getString(R.string.userProfileTreasuresDiscoveredLabel), String.valueOf(mTrasuresDiscovered)));
            mUserProfileTreasuresHidden.setText(String.format(res.getString(R.string.userProfileTreasuresHiddenLabel), String.valueOf(mTresuresHidden)));
        }
    };
    private Observer<User> mObserver = new Observer<User>() {
        @Override
        public void onChanged(User user) {
            if (!TextUtils.isEmpty(user.getUserName())) {
                Log.d(TAG, "ProfileImage");
                setSharedPreferencesUsername(user.getUserName());
            }
            try {
                setSharedPreferencesScore(Math.toIntExact(user.getScore()));
            } catch (ArithmeticException exception) {
                exception.printStackTrace();
            }


            mScore = getSharedPreferencesScore();
            mUsername = getSharedPreferencesUsername();
            mImagePath = user.getProfilePicture();
            Glide.with(UserProfileFragment.this)
                    .asBitmap()
                    .load(mImagePath)
                    .placeholder(R.drawable.user_default)
                    .dontAnimate()
                    .into(mUserProfileImageView);
            Resources res = getResources();
            mUserProfileScore.setText(String.format(res.getString(R.string.userProfileScoreLabel), mScore));
            mUserProfileUsername.setText(String.format(res.getString(R.string.userProfileUsernameLabel), mUsername));

        }
    };

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(Objects.requireNonNull(getActivity()).getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri mUserProfileImageViewUri = FileProvider.getUriForFile(Objects.requireNonNull(getContext()),
                        AUTHORITY,
                        photoFile);

                mImagePath = photoFile.getPath();

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mUserProfileImageViewUri);
                startActivityForResult(takePictureIntent, 1);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        if (getActivity() != null) {
            File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            return File.createTempFile(
                    PHOTO,  /* prefix */
                    SUFFIX,         /* suffix */
                    storageDir      /* directory */
            );
        }
        return null;
    }
}
