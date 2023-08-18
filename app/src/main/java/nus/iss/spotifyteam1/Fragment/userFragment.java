package nus.iss.spotifyteam1.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import nus.iss.spotifyteam1.LoginActivity;
import nus.iss.spotifyteam1.R;

public class userFragment extends Fragment implements View.OnClickListener{

    String userid;
    String username;

    TextView userName;

    TextView userData;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences user = requireActivity().getSharedPreferences("user_obj", Context.MODE_PRIVATE);
        userid = user.getString("user_id","");
        username = user.getString("user_name","");



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        view.findViewById(R.id.logout).setOnClickListener(this);
        userName = view.findViewById(R.id.userName);
        userName.setText(username);

        userData = view.findViewById(R.id.userData);
        userData.setText(userid);


        return view;
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.logout) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("manual_location", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("manual",false);
            editor.commit();

            Intent toLoginActivity = new Intent(getContext(), LoginActivity.class);
            startActivity(toLoginActivity);
            getActivity().finish();
        }
    }

}