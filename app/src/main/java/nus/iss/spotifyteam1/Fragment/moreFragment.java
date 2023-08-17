package nus.iss.spotifyteam1.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import nus.iss.spotifyteam1.HomepageActivity;
import nus.iss.spotifyteam1.LoginActivity;
import nus.iss.spotifyteam1.R;

public class moreFragment extends Fragment {

    Button submit;
    EditText message;
    Thread bkgdThread;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null) {
            submit = view.findViewById(R.id.submit);
            message = view.findViewById(R.id.feedback);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String feedbackText = message.getText().toString().trim(); // 获取去掉前后空白的反馈内容

                    if (feedbackText.isEmpty()) {
                        Toast.makeText(requireContext(), "Please enter you feedback", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.AlertDialogCustom);

                    builder.setTitle("Confirm Submission");
                    builder.setMessage("Are you sure you want to submit your feedback?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences pref = requireActivity().getSharedPreferences("user_obj", Context.MODE_PRIVATE);
                            String userId = pref.getString("user_id", "");
                            bkgdThread = saveFeedBack(userId);
                            bkgdThread.start();
                            message.setText("");
                            Toast.makeText(requireContext(), "Submit successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("No", null);
                    builder.show();
                }
            });
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_more, container, false);
    }

    public Thread saveFeedBack(String userId){
        return new Thread(new Runnable() {
            @Override
            public void run() {
                String msg = message.getText().toString();
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(getString(R.string.BACKEND_URL)+"feedback/save?spotify_user_id="+userId+"&feedback_text="+msg);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json"); // Set content type
                    connection.setDoOutput(true);
//                    JSONObject jsonInput = new JSONObject();
//                    jsonInput.put("spotify_user_id", userId);
//                    jsonInput.put("feedback_text", msg);
//                    try (OutputStream os = connection.getOutputStream()) {
//                        byte[] input = jsonInput.toString().getBytes("utf-8");
//                        os.write(input, 0, input.length);
//                    }
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bkgdThread = null;
            }

        });



    }




}