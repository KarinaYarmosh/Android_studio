package ru.startandroid;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;

public class NoteAuth extends BiometricPrompt.AuthenticationCallback {
    @Override
    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
        super.onAuthenticationError(errorCode, errString);
    }

    @Override
    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
    }
}
