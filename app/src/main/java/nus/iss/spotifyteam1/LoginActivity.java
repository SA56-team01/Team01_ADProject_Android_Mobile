package nus.iss.spotifyteam1;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.crashlytics.buildtools.api.net.Constants;
import com.google.gson.Gson;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    Button authSpotify;
    private static final int REQUEST_CODE = 1337;

    Thread bkgdThread;
    Thread checkUserThread;
    String TOKEN;
    String[] internet = {Manifest.permission.INTERNET};
    User user;
    private static final String REDIRECT_URI = "spotify-sdk://auth";
    private static final String CLIENT_ID = "318dffba43554089bf21083a6017c716";
    private static final String BASE_URL = "https://api.spotify.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        authSpotify = findViewById(R.id.authSpotify);
        authSpotify.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.authSpotify) {
            AuthorizationRequest.Builder builder =
                    new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);

            builder.setScopes(new String[]{"streaming","playlist-modify-public","user-read-private","user-top-read","playlist-modify-private","playlist-read-collaborative","playlist-read-private"});
            AuthorizationRequest request = builder.build();

            AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    // Handle successful response
                    TOKEN =  response.getAccessToken();
                    if (bkgdThread != null) {
                        bkgdThread.interrupt();
                        return;
                    }
                    bkgdThread = getUserProfile();
                    bkgdThread.start();
                    Toast.makeText(LoginActivity.this, "Verification successful", Toast.LENGTH_SHORT).show();
                    Intent toDashboard = new Intent(LoginActivity.this, HomepageActivity.class);
                    startActivity(toDashboard);
                    finish();
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }

    public Thread getUserProfile() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    HttpURLConnection connection = null;
                    BufferedReader reader = null;
                    try {
                        URL url = new URL(BASE_URL + "v1/me");
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setRequestProperty("Authorization", "Bearer " + TOKEN);
                        connection.setRequestProperty("Content-Type", "application/json"); // Set content type if needed
                        int responseCode = connection.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            StringBuilder response = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }
                            if(response.toString()!=null){
                                handleResponseData(response.toString());
                            }

                        }
                    } catch (IOException e) {
                        e.printStackTrace();

                    } finally {
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (connection != null) {
                            connection.disconnect();
                        }
                    };
                    bkgdThread = null;
                }catch (Exception e){
                    bkgdThread= null;
                    e.printStackTrace();
                }
            }
        });
}

    public Thread checkUser() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    HttpURLConnection connection = null;
                    BufferedReader reader = null;
                    try {
                        URL url = new URL(getString(R.string.BACKEND_URL) + "user/save?spotify_user_id="+user.getId()+"&user_email="+user.getEmail());
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("Content-Type", "application/json"); // Set content type if needed
                        int responseCode = connection.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_OK) {

                        }

                    } catch (IOException e) {
                        e.printStackTrace();

                    } finally {
                        if (connection != null) {
                            connection.disconnect();
                        }
                    };
                    checkUserThread = null;
                }catch (Exception e){
                    checkUserThread= null;
                    e.printStackTrace();
                }
            }
        });
    }

    private void handleResponseData(String responseData) {
        Gson gson = new Gson();
        user = gson.fromJson(responseData, User.class);
        SharedPreferences pref = getSharedPreferences("user_obj", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("user_id",user.getId());
        editor.putString("user_name",user.getDisplayName());
        editor.putString("user_email",user.getCountry());
        editor.putString("user_TOKEN",TOKEN);
        editor.commit();
        user.getId();
        user.getEmail();
        user.getId();

        checkUserThread = checkUser();
        checkUserThread.start();
    }

}


