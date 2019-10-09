package com.threess.summership.treasurehunt.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.threess.summership.treasurehunt.R;
import com.threess.summership.treasurehunt.model.User;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> {

    private static final String BASE_URL = "http://5.254.125.248:3000";
    private List<User> mUserList;
    private Context mContext;

    public UserListAdapter(List<User> mUserList, Context mContext) {
        this.mContext = mContext;
        this.mUserList = mUserList;
    }

    class UserListViewHolder extends RecyclerView.ViewHolder {

        final View mView;

        TextView mUserText;
        TextView mScoreText;
        private ImageView mCoverImage;

        UserListViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mUserText = mView.findViewById(R.id.hall_of_fame_user_name);
            mScoreText = mView.findViewById(R.id.hall_of_fame_score);
            mCoverImage = mView.findViewById(R.id.coverImage);
        }
    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_user, parent, false);
        return new UserListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserListViewHolder holder, int position) {
        User currentUser = mUserList.get(position);
        holder.mUserText.setText(currentUser.getUserName());
        String score = "Score: " + currentUser.getScore();
        holder.mScoreText.setText(score);
        if (currentUser.getProfilePicture() != null)
            Glide.with(mContext)
                    .load(BASE_URL + currentUser.getProfilePicture())
                    .placeholder((R.drawable.ic_launcher_background))
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.mCoverImage);

        else {
            holder.mCoverImage.setImageResource(R.drawable.user_default);
        }

    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }
}
