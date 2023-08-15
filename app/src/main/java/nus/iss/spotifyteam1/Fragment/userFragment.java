package nus.iss.spotifyteam1.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import nus.iss.spotifyteam1.LoginActivity;
import nus.iss.spotifyteam1.R;

public class userFragment extends Fragment implements View.OnClickListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        view.findViewById(R.id.logout).setOnClickListener(this);

        return view;
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.logout) {
            Intent toLoginActivity = new Intent(getContext(), LoginActivity.class);
            startActivity(toLoginActivity);
            getActivity().finish();
        }
    }

}