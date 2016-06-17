package com.widiarifki.outdoorrent;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.widiarifki.outdoorrent.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = RegisterActivity.class.getSimpleName();

    // UI references.
    private EditText mNameView;
    private EditText mPhoneView;
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mPasswordConfView;
    private Button mRegisterButton;
    private View mProgressView;
    private View mRegisterFormView;

    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setupActionBar();

        session = new SessionManager(getApplicationContext());

        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = (ProgressBar) findViewById(R.id.register_progress);
        mNameView = (EditText) findViewById(R.id.register_name);
        mPhoneView = (EditText) findViewById(R.id.register_phone);
        mEmailView = (EditText) findViewById(R.id.register_email);
        mPasswordView = (EditText) findViewById(R.id.register_password);
        mPasswordConfView = (EditText) findViewById(R.id.register_confirm_password);
        mPasswordConfView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.register || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        mRegisterButton = (Button) findViewById(R.id.register_submit_button);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegister() {

        // Reset errors.
        mNameView.setError(null);
        mPhoneView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mPasswordConfView.setError(null);

        // Store values at the time of the login attempt.
        String name = mNameView.getText().toString();
        String phone = mPhoneView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String passwordConf = mPasswordConfView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        }

        /*if (TextUtils.isEmpty(phone) && TextUtils.isEmpty(email)) {
            mPhoneView.setError(getString(R.string.error_phone_email_required));
            focusView = mPhoneView;
            cancel = true;
        }*/
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        if(!isPasswordValid(password)){
            mPasswordView.setError(getString(R.string.error_password_length));
            focusView = mPasswordView;
            cancel = true;
        }

        if (!password.equals(passwordConf)) {
            mPasswordConfView.setError(getString(R.string.error_invalid_password_confirm));
            focusView = mPasswordConfView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            if (isNetworkAvailable()) {
                showProgress(true);

                TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

                RequestBody formBody = new FormBody.Builder()
                        .add("name", name)
                        .add("phone", phone)
                        .add("email", email)
                        .add("password", password)
                        .add("device_id", telephonyManager.getDeviceId())
                        .build();

                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .url("http://restapp-widiarifki.rhcloud.com/member")
                        .post(formBody)
                        .build();

                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.v(TAG, String.valueOf(e));
                        alertUserAboutError();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showProgress(false);
                            }
                        });
                    }

                    /* when request get response */
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            JSONObject userData = null;
                            try {
                                userData = new JSONObject(jsonData);
                                User user = new User();
                                user.setId(userData.getInt("id"));
                                user.setName(userData.getString("name"));
                                user.setEmail(userData.getString("email"));
                                user.setPhone(userData.getString("phone"));
                                user.setDeviceId(userData.getString("deviceId"));
                                session.createLoginSession(user);
                                finish();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showProgress(false);
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            finish();
                        } else {
                            alertUserAboutError();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showProgress(false);
                                }
                            });
                        }
                    }
                });

            } else {
                Toast.makeText(this, "Tidak ada jaringan", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        mRegisterFormView.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
    }
}

