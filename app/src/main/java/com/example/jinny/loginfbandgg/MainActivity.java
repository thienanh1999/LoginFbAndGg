package com.example.jinny.loginfbandgg;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    LoginButton FbLogin;
    ImageView IvFbAvatar;
    TextView TvFbName;

    GoogleSignInClient googleSignInClient;
    SignInButton signInButton;
    ImageView IvGgAvatar;
    TextView TvGgName;

    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create();

        FbLogin = findViewById(R.id.fb_login_button);
        IvFbAvatar = findViewById(R.id.iv_fb_avatar);
        TvFbName = findViewById(R.id.tv_fb_name);
        FbLogin.setReadPermissions(Arrays.asList("public_profile"));
        TvFbName.setText("Name");

      /*  try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.jinny.loginfbandgg",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }*/

        FbLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "Success");
                GraphRequest graphRequest = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    TvFbName.setText(object.getString("name"));
                                    Picasso.get().load("https://graph.facebook.com/" + object.getString("id") + "/picture?type=large")
                                            .into(IvFbAvatar);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "Cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "Error");
            }
        });



        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        signInButton = findViewById(R.id.gg_login_button);
        IvGgAvatar = findViewById(R.id.iv_gg_avatar);
        TvGgName = findViewById(R.id.tv_gg_name);

        signInButton.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            TvGgName.setText(account.getDisplayName());
            Picasso.get()
                    .load(account.getPhotoUrl())
                    .into(IvGgAvatar);
        } catch (ApiException e) {
          //  Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.gg_login_button:
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 0);
                break;
        }
    }
}
