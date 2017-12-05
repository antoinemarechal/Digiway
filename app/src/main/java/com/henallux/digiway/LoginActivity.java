package com.henallux.digiway;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.AsyncTask;
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

import com.henallux.controller.LoginController;
import com.henallux.exceptions.DataAccessException;
import com.henallux.model.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity
{
    private UserLoginTask authTask = null;

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
                intent.putExtra("desiredUsername", usernameField.getText());

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
            View focusView;
            String username;
            String password;

            boolean cancel;

            // Reset errors.
            usernameField.setError(null);
            passwordField.setError(null);

            // Store values at the time of the login attempt.
            username = usernameField.getText().toString();
            password = passwordField.getText().toString();

            focusView = null;
            cancel = false;

            // Check for a valid password, if the user entered one.
            if (!isPasswordValid(password))
            {
                passwordField.setError(getString(R.string.error_invalid_password));
                focusView = passwordField;
                cancel = true;
            }

            // Check for a valid username.
            if (TextUtils.isEmpty(username))
            {
                usernameField.setError(getString(R.string.error_field_required));
                focusView = usernameField;
                cancel = true;
            }
            else if (!isUsernameValid(username))
            {
                usernameField.setError(getString(R.string.error_invalid_email));
                focusView = usernameField;
                cancel = true;
            }

            if (cancel)
            {
                focusView.requestFocus();
            }
            else
            {
                showProgress(true);

                try
                {
                    authTask = new UserLoginTask(username, password);
                    authTask.execute();
                }
                catch (NoSuchAlgorithmException e)
                {
                    Toast.makeText(LoginActivity.this, getString(R.string.error_sha512), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private boolean isUsernameValid(String username) // TODO : vraie longueur
    {
        return username.length() < 30;
    }

    private boolean isPasswordValid(String password) // TODO : vraie longueur
    {
        return password != null && (password.length() > 3 && password.length() < 30);
    }

    private void showProgress(final boolean show)
    {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        loginForm.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
        loginForm.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                loginForm.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
            }
        });

        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        progressBar.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private class UserLoginTask extends AsyncTask<String, Void, ArrayList<User>>
    {
        private final String username;
        private final String password;

        UserLoginTask(String username, String password) throws NoSuchAlgorithmException
        {
            this.username = username;
            this.password = hashPassword(password);
        }

        private String hashPassword(String password) throws NoSuchAlgorithmException
        {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            byte[] digest = messageDigest.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();

            for (byte aDigest : digest)
            {
                sb.append(Integer.toString((aDigest & 0xff) + 0x100, 16).substring(1));
            }

            return sb.toString();
        }

        @Override
        protected ArrayList<User> doInBackground(String... strings)
        {
            LoginController controller = new LoginController();
            ArrayList<User> users = new ArrayList<>();

            try
            {
                users = controller.getAllUsers();
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

            return users;
        }

        @Override
        protected void onPostExecute(ArrayList<User> users)
        {
            User currentUser = null;

            authTask = null;
            showProgress(false);

            //if(users.stream().filter(u -> u.getUsername() == username && u.getHashedPassword() == password).count() == 1)

            for (int i = 0; currentUser == null && i < users.size(); i++)
            {
                User u = users.get(i);

                if (u.getUsername().equals(username))
                    if (u.getHashedPassword().equals(password))
                        currentUser = u;
            }

            if (currentUser == null)
            {
                passwordField.setError(getString(R.string.error_incorrect_password));
                passwordField.requestFocus();
            }
            else
            {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.putExtra("currentUser", currentUser);

                startActivity(intent);
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
