package com.example.smokecounter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private Button googleLoginBtn, sendOtpBtn, verifyOtpBtn;
    private EditText phoneInput, otpInput;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        // Views
        googleLoginBtn = findViewById(R.id.googleLoginBtn);
        sendOtpBtn = findViewById(R.id.sendOtpBtn);
        verifyOtpBtn = findViewById(R.id.verifyOtpBtn);
        phoneInput = findViewById(R.id.phoneInput);
        otpInput = findViewById(R.id.otpInput);

        // Persistent login
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            openMainActivity(currentUser.getDisplayName());
        }

        setupGoogleLogin();
        setupPhoneLogin();
    }

    private void setupGoogleLogin() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleLoginBtn.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, 1001);
        });
    }

    private void setupPhoneLogin() {
        sendOtpBtn.setOnClickListener(v -> {
            String phone = phoneInput.getText().toString().trim();
            if (phone.isEmpty()) {
                Toast.makeText(this, "Enter phone number", Toast.LENGTH_SHORT).show();
                return;
            }
            sendOtp(phone);
        });

        verifyOtpBtn.setOnClickListener(v -> {
            String code = otpInput.getText().toString().trim();
            if (code.isEmpty() || verificationId == null) {
                Toast.makeText(this, "Enter valid OTP", Toast.LENGTH_SHORT).show();
                return;
            }
            verifyOtp(code);
        });
    }

    private void sendOtp(String phoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber("+91" + phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        signInWithCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(LoginActivity.this, "OTP failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("OTP_ERROR", e.getMessage(), e);
                    }

                    @Override
                    public void onCodeSent(@NonNull String verId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        verificationId = verId;
                        Toast.makeText(LoginActivity.this, "OTP sent!", Toast.LENGTH_SHORT).show();
                    }
                })
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyOtp(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                openMainActivity(user != null ? user.getPhoneNumber() : "User");
            } else {
                Toast.makeText(LoginActivity.this, "OTP verification failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openMainActivity(String identifier) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("userIdentifier", identifier);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001) {
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult();
                if (account != null) {
                    openMainActivity(account.getDisplayName());
                }
            } catch (Exception e) {
                Toast.makeText(this, "Google login failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}