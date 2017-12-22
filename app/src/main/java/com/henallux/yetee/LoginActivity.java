package com.henallux.yetee;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.henallux.controller.ApplicationController;
import com.henallux.exception.DataAccessException;
import com.henallux.model.User;
import com.henallux.util.Encryption;
import com.henallux.yetee.common.NetworkUtil;

import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity
{
    public static final String EXTRA_USERNAME_ID = "desiredUsername";

    private LoginTask authTask = null;

    private TextView usernameField;
    private EditText passwordField;

    private View progressBar;
    private View loginForm;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        usernameField = (TextView) findViewById(R.id.login_username_field);
        passwordField = (EditText) findViewById(R.id.login_password_field);
        progressBar = findViewById(R.id.login_progress);
        loginForm = findViewById(R.id.login_form);

        Button signInButton = (Button) findViewById(R.id.login_sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                attemptLogin();
            }
        });

        Button registerButton = (Button) findViewById(R.id.login_register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.putExtra(EXTRA_USERNAME_ID, usernameField.getText().toString());

                startActivity(intent);
            }
        });


        View logo = findViewById(R.id.app_start_logo);

        TranslateAnimation animLogo = new TranslateAnimation(logo.getX(), 0 , logo.getY() + 200, 0 );

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);

        AnimationSet animSetLogo = new AnimationSet(true);

        animSetLogo.setDuration(1000);

        alphaAnimation.setDuration(1250);

        animSetLogo.addAnimation(alphaAnimation);
        animSetLogo.addAnimation(animLogo);

        logo.startAnimation(animSetLogo);
    }

    private void attemptLogin()
    {
        if (authTask == null)
        {
            View focusView = null;
            String username;
            String password;

            boolean cancel = false;

            usernameField.setError(null);
            passwordField.setError(null);

            username = usernameField.getText().toString();
            password = passwordField.getText().toString();

            if (TextUtils.isEmpty(password))
            {
                passwordField.setError(getString(R.string.error_field_required));
                focusView = passwordField;
                cancel = true;
            }
            else if (!isPasswordValid(password))
            {
                passwordField.setError(getString(R.string.error_invalid_password));
                focusView = passwordField;
                cancel = true;
            }

            if (TextUtils.isEmpty(username))
            {
                usernameField.setError(getString(R.string.error_field_required));
                focusView = usernameField;
                cancel = true;
            }
            else if (!isUsernameValid(username))
            {
                usernameField.setError(getString(R.string.error_invalid_username));
                focusView = usernameField;
                cancel = true;
            }

            if (cancel)
            {
                focusView.requestFocus();
            }
            else
            {
                try
                {
                    NetworkUtil netUtil = new NetworkUtil(LoginActivity.this);

                    if(netUtil.isAppConnectedToNetwork())
                    {
                        authTask = new LoginTask(username, password);

                        showProgress(true);

                        authTask.execute();
                    }
                    else
                        netUtil.buildNetworkConnectionRequiredDefaultDialog().show();
                }
                catch (NoSuchAlgorithmException e)
                {
                    Toast.makeText(LoginActivity.this, getString(R.string.error_sha512), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private boolean isUsernameValid(String username)
    {
        return username.length() < 40;
    }

    private boolean isPasswordValid(String password)
    {
        return password != null && (password.length() > 5 && password.length() < 40);
    }

    private void showProgress(final boolean show)
    {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        loginForm.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
        loginForm.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                loginForm.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
            }
        });

        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        progressBar.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private class LoginTask extends AsyncTask<Void, Void, User>
    {
        private final String username;
        private final String password;

        LoginTask(String username, String password) throws NoSuchAlgorithmException
        {
            this.username = username;
            this.password = Encryption.encryptSHA512(password);
        }

        @Override
        protected User doInBackground(Void... strings)
        {
            ApplicationController controller = new ApplicationController();
            User user = null;

            try
            {
                user = controller.getUserWithUsername(username);
            }
            catch (final DataAccessException e)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run(){
                        Toast.makeText(LoginActivity.this, getString(e.getMessageId()), Toast.LENGTH_LONG).show();
                    }
                });
            }

            return user;
        }

        @Override
        protected void onPostExecute(User user)
        {
            authTask = null;
            showProgress(false);

            if(user != null && user.getHashedPassword().equals(password))
            {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.putExtra(HomeActivity.EXTRA_USER_ID, user);

                startActivity(intent);

                finish();
            }
            else
            {
                // this delay is set to avoid a graphical bug where the error message goes under the buttons
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run()
                    {
                        passwordField.setError(getString(R.string.error_incorrect_password));
                        passwordField.requestFocus();
                    }
                }, 300);
            }
        }

        @Override
        protected void onCancelled()
        {
            authTask = null;
            showProgress(false);
        }
    }
}
