package com.threess.summership.treasurehunt.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.threess.summership.treasurehunt.Constants;
import com.threess.summership.treasurehunt.R;
import com.threess.summership.treasurehunt.model.Treasure;
import com.threess.summership.treasurehunt.model.repository.GenericResponse;
import com.threess.summership.treasurehunt.view.animations.CollapseDownAnimation;
import com.threess.summership.treasurehunt.viewmodel.TreasureViewModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static com.threess.summership.treasurehunt.Constants.AUTHORITY;
import static com.threess.summership.treasurehunt.Constants.NONE;
import static com.threess.summership.treasurehunt.Constants.PHOTO;
import static com.threess.summership.treasurehunt.Constants.REGEX;
import static com.threess.summership.treasurehunt.Constants.SHARED_PREFERENCE_NAME;
import static com.threess.summership.treasurehunt.Constants.SUFFIX;


public class CreateTreasureFragment extends Fragment {

    private TreasureViewModel mTreasureViewModel;
    private Uri mTreasureImageViewUri;
    private String mPhotoclue = "";
    private EditText mDescription;
    private TextView mErrorMessage;
    private static final String TAG = CreateTreasureFragment.class.getSimpleName();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final NavController navController = Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                navController.navigate(R.id.treasureListFragment);
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_treasure, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstance) {
        super.onViewCreated(view, savedInstance);
        initTreasureViewModel();
        initViews(view);

    }

    private String getSharedPreferencesUsername() {
        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        String mReferenceFileKey = getResources().getString(R.string.preference_file_key_username);
        if (!mSharedPreferences.getString(getString(R.string.preference_file_key_username), mReferenceFileKey).isEmpty()) {
            return mSharedPreferences.getString(getString(R.string.preference_file_key_username), mReferenceFileKey);
        }
        return NONE;
    }

    private void createTreasure(Treasure treasure) {
        mTreasureViewModel.createTreasure(treasure);
    }

    private final Observer<GenericResponse> observer = new Observer<GenericResponse>() {
        @Override
        public void onChanged(GenericResponse genericResponse) {
            if (genericResponse != null && getActivity() != null) {
                if (genericResponse.IsSuccess()) {
                    if (!mPhotoclue.isEmpty()) {
                        mErrorMessage.setText(genericResponse.getErrorMessage());
                        mErrorMessage.setVisibility(View.VISIBLE);
                    } else {
                        mErrorMessage.setText(Constants.FORGOT_PHOTO);
                        mErrorMessage.setVisibility(View.VISIBLE);
                    }

                } else {
                    mErrorMessage.setText(Constants.NETWORK_FAILURE);
                    mErrorMessage.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    private void initTreasureViewModel() {
        mTreasureViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(TreasureViewModel.class);
        mTreasureViewModel.init();
        mTreasureViewModel.getResponseTreasure().observe(getViewLifecycleOwner(), observer);
    }

    private void initViews(View view) {

        ImageView photobutton = view.findViewById(R.id.photobutton);
        mErrorMessage = view.findViewById(R.id.messageTreasure);
        //button for taking the picture
        photobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
                mPhotoclue = mTreasureImageViewUri.getPath();

            }
        });
        final String username = getSharedPreferencesUsername();
        final EditText title = view.findViewById(R.id.title);
        mDescription = view.findViewById(R.id.treasureDescription);
        mDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                mDescription.setAnimation(new CollapseDownAnimation(mDescription, b));
            }
        });

        final EditText latitude = view.findViewById(R.id.latitude);
        final EditText longitude = view.findViewById(R.id.longitude);
        final EditText prizepoints = view.findViewById(R.id.prizePoints);
        final EditText passcode = view.findViewById(R.id.passcode);
        final Button addTreasure = view.findViewById(R.id.add_treasure_button);
        final TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (checkTreasureFields(title.getText().toString(), mDescription.getText().toString(), longitude.getText().toString(), latitude.getText().toString(), prizepoints.getText().toString(), passcode.getText().toString())) {
                    addTreasure.setEnabled(true);
                } else {
                    addTreasure.setEnabled(false);
                }
            }
        };
        title.addTextChangedListener(watcher);
        mDescription.addTextChangedListener(watcher);
        longitude.addTextChangedListener(watcher);
        latitude.addTextChangedListener(watcher);
        prizepoints.addTextChangedListener(watcher);
        passcode.addTextChangedListener(watcher);

        addTreasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTreasureIfPossible(username, title.getText().toString(), mDescription.getText().toString(), longitude.getText().toString(), latitude.getText().toString(), prizepoints.getText().toString(), passcode.getText().toString());
                closeKeyboard();
            }
        });
    }

    private void createTreasureIfPossible(String username, String title, String description, String longitude, String latitude, String points, String passcode) {

        //check if there is an empty field
        //left it like this, if I want to put them in variables I have to do the empty check twice

        if (checkTreasureFields(title, description, longitude, latitude, points, passcode) && !username.isEmpty()) {
            //after the check is done I create the treasure here
            /*TODO create treasure with new constructor*/
            Treasure treasure = new Treasure(username, passcode, title, description, mPhotoclue,
                    Double.parseDouble(latitude), Double.parseDouble(longitude), Long.parseLong(points), false, "", "");
            createTreasure(treasure);


        } else {
            //if there are empty fields
            Log.d(TAG, "title is" + title);
            Snackbar.make(getActivity().findViewById(R.id.createTreasureScreen), R.string.fill_blanks, Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    //hide keyboard
    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getView();
        if (imm != null && view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mTreasureViewModel.getResponseTreasure().setValue(null);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (getActivity() != null && takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (getContext() != null && photoFile != null) {
                mTreasureImageViewUri = FileProvider.getUriForFile(getContext(),
                        AUTHORITY,
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mTreasureImageViewUri);
                startActivityForResult(takePictureIntent, 1);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = PHOTO + timeStamp;
        if (getActivity() != null) {
            File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            return File.createTempFile(
                    imageFileName,  /* prefix */
                    SUFFIX,         /* suffix */
                    storageDir      /* directory */
            );
        }
        return null;
    }

    private boolean checkTreasureFields(String title, String description, String longitude, String latitude, String points, String passcode) {
        return !title.isEmpty() && !description.isEmpty() &&
                longitude.matches(REGEX) && latitude.matches(REGEX) &&
                points.matches(REGEX) && passcode.matches(REGEX);
    }

}