package com.henallux.digiway;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.henallux.controller.ApplicationController;
import com.henallux.exceptions.DataAccessException;
import com.henallux.model.User;
import com.henallux.util.Encryption;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity
{
    private RegistrationTask registrationTask = null;

    private EditText firstNameField;
    private EditText lastNameField;
    private EditText birthdayField;
    private EditText streetNumberField;
    private EditText streetNameField;
    private EditText cityField;
    private EditText zipCodeField;
    private Spinner countrySpinner;
    private EditText usernameField;
    private EditText passwordField;
    private EditText passwordConfirmationField;

    private View progressBar;
    private View registrationForm;

    private final Calendar enteredBirthday = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        Intent intent = getIntent();

        firstNameField = (EditText) findViewById(R.id.registration_first_name);
        lastNameField = (EditText) findViewById(R.id.registration_last_name);
        birthdayField = (EditText) findViewById(R.id.registration_birthday);
        streetNumberField = (EditText) findViewById(R.id.registration_street_number);
        streetNameField = (EditText) findViewById(R.id.registration_street_name);
        cityField = (EditText) findViewById(R.id.registration_city);
        zipCodeField = (EditText) findViewById(R.id.registration_zip_code);
        countrySpinner = (Spinner) findViewById(R.id.registration_country);
        usernameField = (EditText) findViewById(R.id.registration_username);
        passwordField = (EditText) findViewById(R.id.registration_password);
        passwordConfirmationField = (EditText) findViewById(R.id.registration_confirm_password);

        progressBar = findViewById(R.id.login_progress);//FIXME
        registrationForm = findViewById(R.id.login_form);//FIXME

        Button registerButton = (Button) findViewById(R.id.registration_confirm_button);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view)
            {
                attemptRegistration();
            }
        });

        usernameField.setText(intent.getStringExtra(LoginActivity.EXTRA_MESSAGE_ID));

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day)
            {
                enteredBirthday.set(Calendar.YEAR, year);
                enteredBirthday.set(Calendar.MONTH, month);
                enteredBirthday.set(Calendar.DAY_OF_MONTH, day);

                updateLabel();
            }
        };

        birthdayField.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                DatePickerDialog dialog = new DatePickerDialog(RegisterActivity.this,
                        date,
                        enteredBirthday.get(Calendar.YEAR),
                        enteredBirthday.get(Calendar.MONTH),
                        enteredBirthday.get(Calendar.DAY_OF_MONTH));

                dialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
                dialog.show();
            }
        });
    }

    private void updateLabel()
    {
        SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.global_date_format));

        birthdayField.setText(sdf.format(enteredBirthday.getTime()));
    }

    private void attemptRegistration()
    {
        if (registrationTask == null)
        {
            View focusView = null;

            String firstName = firstNameField.getText().toString();
            String lastName = firstNameField.getText().toString();
            String streetNumber = streetNumberField.getText().toString();
            String streetName = streetNameField.getText().toString();
            String city = cityField.getText().toString();
            String zipCode = zipCodeField.getText().toString();
            String country = (String) countrySpinner.getSelectedItem();
            String username = usernameField.getText().toString();
            String password = passwordField.getText().toString();
            String passwordConfirmation = passwordConfirmationField.getText().toString();

            boolean cancel = false;

            firstNameField.setError(null);
            lastNameField.setError(null);
            birthdayField.setError(null);
            streetNumberField.setError(null);
            streetNameField.setError(null);
            cityField.setError(null);
            zipCodeField.setError(null);
            usernameField.setError(null);
            passwordField.setError(null);
            passwordConfirmationField.setError(null);

            if(TextUtils.isEmpty(passwordConfirmation))
            {
                passwordConfirmationField.setError(getString(R.string.error_field_required));
                focusView = passwordConfirmationField;
                cancel = true;
            }
            else if(!isPasswordConfirmationValid(password, passwordConfirmation))
            {
                passwordConfirmationField.setError(getString(R.string.error_invalid_confirmation));
                focusView = passwordConfirmationField;
                cancel = true;
            }

            if(TextUtils.isEmpty(password))
            {
                passwordField.setError(getString(R.string.error_field_required));
                focusView = passwordField;
                cancel = true;
            }
            else if(!isPasswordValid(password))
            {
                passwordField.setError(getString(R.string.error_malformed_password));
                focusView = passwordField;
                cancel = true;
            }

            if (TextUtils.isEmpty(username))
            {
                usernameField.setError(getString(R.string.error_field_required));
                focusView = usernameField;
                cancel = true;
            }
            else if(!isUsernameValid(username))
            {
                usernameField.setError(getString(R.string.error_invalid_username));
                focusView = usernameField;
                cancel = true;
            }

            if (TextUtils.isEmpty(zipCode))
            {
                zipCodeField.setError(getString(R.string.error_field_required));
                focusView = zipCodeField;
                cancel = true;
            }
            else if(!isZipCodeValid(zipCode))
            {
                zipCodeField.setError(getString(R.string.error_invalid_zip_code));
                focusView = zipCodeField;
                cancel = true;
            }

            if (TextUtils.isEmpty(city))
            {
                cityField.setError(getString(R.string.error_field_required));
                focusView = cityField;
                cancel = true;
            }
            else if(!isCityValid(city))
            {
                cityField.setError(getString(R.string.error_invalid_city));
                focusView = cityField;
                cancel = true;
            }

            if (TextUtils.isEmpty(streetName))
            {
                streetNameField.setError(getString(R.string.error_field_required));
                focusView = streetNameField;
                cancel = true;
            }
            else if(!isStreetNameValid(streetName))
            {
                streetNameField.setError(getString(R.string.error_invalid_street_name));
                focusView = streetNameField;
                cancel = true;
            }

            if (TextUtils.isEmpty(streetNumber))
            {
                streetNumberField.setError(getString(R.string.error_field_required));
                focusView = streetNumberField;
                cancel = true;
            }
            else if(!isStreetNumberValid(streetNumber))
            {
                streetNumberField.setError(getString(R.string.error_invalid_street_number));
                focusView = streetNumberField;
                cancel = true;
            }

            if(TextUtils.isEmpty(birthdayField.getText().toString()))
            {
                birthdayField.setError(getString(R.string.error_field_required));
                focusView = birthdayField;
                cancel = true;
            }

            if (TextUtils.isEmpty(lastName))
            {
                lastNameField.setError(getString(R.string.error_field_required));
                focusView = lastNameField;
                cancel = true;
            }
            else if(!isLastNameValid(lastName))
            {
                lastNameField.setError(getString(R.string.error_invalid_last_name));
                focusView = lastNameField;
                cancel = true;
            }

            if (TextUtils.isEmpty(firstName))
            {
                firstNameField.setError(getString(R.string.error_field_required));
                focusView = firstNameField;
                cancel = true;
            }
            else if(!isFirstNameValid(firstName))
            {
                firstNameField.setError(getString(R.string.error_invalid_first_name));
                focusView = firstNameField;
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
                    User user = new User(
                            firstName,
                            lastName,
                            enteredBirthday,
                            streetNumber,
                            streetName,
                            city,
                            zipCode,
                            country,
                            username,
                            Encryption.encryptSHA512(password),
                            Secure.getString(getContentResolver(), Secure.ANDROID_ID));

                    showProgress(true);

                    registrationTask = new RegistrationTask(user);
                    registrationTask.execute((Void) null);
                }
                catch (NoSuchAlgorithmException e)
                {
                    Toast.makeText(RegisterActivity.this, getString(R.string.error_sha512), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private boolean isFirstNameValid(String firstName)
    {
        return firstName.length() < 40;
    }

    private boolean isLastNameValid(String lastName)
    {
        return lastName.length() < 40;
    }

    private boolean isStreetNumberValid(String streetNumber)
    {
        return streetNumber.length() < 15;
    }

    private boolean isStreetNameValid(String streetName)
    {
        return streetName.length() < 120;
    }

    private boolean isCityValid(String city)
    {
        return city.length() < 50;
    }

    private boolean isZipCodeValid(String zipCode)
    {
        Pattern pattern = Pattern.compile("^(\\d\\d\\d\\d){1}$");
        Matcher matcher = pattern.matcher(zipCode);

        return matcher.find();
    }

    private boolean isUsernameValid(String username)
    {
        return username.length() < 40;
    }

    private boolean isPasswordValid(String password)
    {
        Pattern digitPattern = Pattern.compile("\\d");
        Pattern letterPattern = Pattern.compile("(?i)[a-z]");

        Matcher digitMatcher = digitPattern.matcher(password);
        Matcher letterMatcher = letterPattern.matcher(password);

        return digitMatcher.find() && letterMatcher.find() && password.length() > 5 && password.length() < 40;
    }

    private boolean isPasswordConfirmationValid(String password, String passwordConfirmation)
    {
        return passwordConfirmation.equals(password);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
        private void showProgress(final boolean show) {/*
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }*/
    }






    public class RegistrationTask extends AsyncTask<Void, Void, Boolean> {

        private final User user;

        RegistrationTask(User user)
        {
            this.user = user;
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            ApplicationController controller = new ApplicationController();

            try
            {
                controller.registerUser(user);

                return true;
            }
            catch (DataAccessException e)
            {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success)
        {
            registrationTask = null;
            showProgress(false);

            if (success)
            {
                finish();

                // TODO toast comfirmant l'inscription
            }
            else // FIXME
            {
                passwordField.setError(getString(R.string.error_incorrect_password));
                passwordField.requestFocus();

                //Toast.makeText(LoginActivity.this, getString(e.getMessageId()), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled()
        {
            registrationTask = null;
            showProgress(false);
        }
    }
}

