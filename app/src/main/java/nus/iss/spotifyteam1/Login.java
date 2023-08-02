package nus.iss.spotifyteam1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class Login extends AppCompatActivity implements View.OnClickListener {
    Button login;
    Button authSpotify;
    TextInputLayout username;
    TextInputLayout password;
    private static final String REDIRECT_URI = "spotify-sdk://auth";
    private static final String CLIENT_ID = "318dffba43554089bf21083a6017c716";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        login = findViewById(R.id.loginBtn);
        login.setOnClickListener(this);
        authSpotify = findViewById(R.id.authSpotify);
        authSpotify.setOnClickListener(this);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);


    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.loginBtn) {
            Toast.makeText(this, "Login...", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.authSpotify) {
            AuthorizationRequest.Builder builder =
                    new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);

            builder.setScopes(new String[]{"streaming"});
            AuthorizationRequest request = builder.build();

            AuthorizationClient.openLoginInBrowser(this, request);

        }


    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Uri uri = intent.getData();
        if (uri != null) {
            AuthorizationResponse response = AuthorizationResponse.fromUri(uri);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    // Handle successful response
                    Toast.makeText(this,"Verification successful",Toast.LENGTH_SHORT).show();
                    Intent toDashboard = new Intent(Login.this,Dashboard.class);
                    startActivity(toDashboard);
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

}

//

