package com.henallux.yetee;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.henallux.model.User;
import com.henallux.yetee.common.NetworkUtil;

import java.text.NumberFormat;

public class BalanceActivity extends CustomAppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener
{
    private TextInputEditText depositAmountField;

    private RadioButton visaButton;
    private RadioButton paypalButton;

    private Button submitButton;

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_balance);

        Intent intent = getIntent();

        depositAmountField = (TextInputEditText)findViewById(R.id.balance_deposit_amount);
        depositAmountField.setFilters(new InputFilter[]{ new MinMaxFilter(1, 30)});

        visaButton = (RadioButton) findViewById(R.id.balance_visa_button);
        visaButton.setOnCheckedChangeListener(this);

        paypalButton = (RadioButton) findViewById(R.id.balance_paypal_button);
        paypalButton.setOnCheckedChangeListener(this);

        currentUser = (User) intent.getSerializableExtra(HomeActivity.EXTRA_USER_ID);

        updateBillingDetailsUI();

        submitButton = (Button) findViewById(R.id.balance_confirm_button);
        submitButton.setOnClickListener(this);
    }

    private void updateBillingDetailsUI()
    {
        TextView billingDetails = (TextView) findViewById(R.id.balance_billing_details);

        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        String replaceCharacter = getString(R.string.global_replace_character);

        String detailsText = getString(R.string.text_billing_details)
                .replaceFirst(replaceCharacter, formatter.format(currentUser.getBalance()))
                .replaceFirst(replaceCharacter, currentUser.getFirstName())
                .replaceFirst(replaceCharacter, currentUser.getLastName())
                .replaceFirst(replaceCharacter, currentUser.getAddress())
                .replaceFirst(replaceCharacter, currentUser.getCity())
                .replaceFirst(replaceCharacter, currentUser.getZipCode())
                .replaceFirst(replaceCharacter, getResources().getStringArray(R.array.countries)[0])
                .replaceFirst(replaceCharacter, currentUser.getIbanAccount() == null ? getString(R.string.text_no_account_associated) : currentUser.getIbanAccount());

        billingDetails.setText(detailsText);
    }

    private void showProgress(final boolean show)
    {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        final LinearLayout billingForm = (LinearLayout) findViewById(R.id.balance_form_layout);
        billingForm.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
        billingForm.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                billingForm.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
            }
        });

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.balance_progress);
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        progressBar.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked)
    {
        if(compoundButton == visaButton)
        {
            visaButton.setChecked(checked);
            paypalButton.setChecked(!checked);
        }
        else if(compoundButton == paypalButton)
        {
            paypalButton.setChecked(checked);
            visaButton.setChecked(!checked);
        }
    }

    @Override
    public void onClick(View view)
    {
        if(view == submitButton)
        {
            String rawDeposit = depositAmountField.getText().toString();

            if (TextUtils.isEmpty(rawDeposit))
            {
                depositAmountField.setError(getString(R.string.error_field_required));
                depositAmountField.requestFocus();
            }
            else
            {
                try
                {
                    final float deposit = Float.parseFloat(rawDeposit);

                    showProgress(true);

                    NetworkUtil netUtil = new NetworkUtil(BalanceActivity.this);

                    if(netUtil.isAppConnectedToNetwork())
                    {
                        // TODO : validation par un organisme bancaire

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run()
                            {
                                showProgress(false);

                                currentUser.addMoney(deposit);

                                updateBillingDetailsUI();

                                String replaceCharacter = getString(R.string.global_replace_character);

                                Toast.makeText(BalanceActivity.this, getString(R.string.info_successful_deposit).replace(replaceCharacter, "" + deposit), Toast.LENGTH_LONG).show();

                                Intent result = new Intent();
                                result.putExtra(HomeActivity.EXTRA_USER_ID, currentUser);

                                setResult(Activity.RESULT_OK, result);
                            }
                        }, 2500);
                    }
                    else
                        netUtil.buildNetworkConnectionRequiredDefaultDialog().show();
                }
                catch(NumberFormatException e)
                {
                    depositAmountField.setError(getString(R.string.error_invalid_amount));
                    depositAmountField.requestFocus();
                }
            }
        }
    }

    private class MinMaxFilter implements InputFilter
    {
        private int min;
        private int max;

        private MinMaxFilter(int minValue, int maxValue)
        {
            this.min = minValue;
            this.max = maxValue;
        }

        @Override
        public CharSequence filter(CharSequence input, int inputStart, int inputEnd, Spanned validatedInput, int vinputStart, int vinputEnd)
        {
            String toValidateInput = validatedInput.toString() + input.toString();

            try
            {
                if (toValidateInput.length() <= 3 + toValidateInput.indexOf('.'))
                {
                    if (isInRange(min, max, Float.parseFloat(toValidateInput)))
                        return null;
                }
            }
            catch(NumberFormatException e) { }

            return "";
        }

        private boolean isInRange(int a, int b, float c)
        {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }
}
