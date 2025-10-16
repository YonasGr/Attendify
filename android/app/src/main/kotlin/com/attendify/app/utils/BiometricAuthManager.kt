package com.attendify.app.utils

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class BiometricAuthManager(private val context: Context) {

    private val biometricManager = BiometricManager.from(context)

    fun isBiometricAuthAvailable(): Boolean {
        return when (biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> false
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> false
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> false
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> false
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> false
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> false
            else -> false
        }
    }

    fun getBiometricAvailabilityMessage(): String {
        return when (biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )) {
            BiometricManager.BIOMETRIC_SUCCESS -> "Available"
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> "No biometric hardware available"
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> "Biometric hardware unavailable"
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> "No biometric credentials enrolled"
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> "Security update required"
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> "Biometric authentication not supported"
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> "Biometric status unknown"
            else -> "Biometric authentication unavailable"
        }
    }

    fun showBiometricPrompt(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (Int, CharSequence) -> Unit,
        onFailed: (() -> Unit)? = null
    ) {
        try {
            // Double-check biometric availability before showing prompt
            if (!isBiometricAuthAvailable()) {
                onError(BiometricPrompt.ERROR_HW_NOT_PRESENT, getBiometricAvailabilityMessage())
                return
            }

            val executor = ContextCompat.getMainExecutor(context)
            val biometricPrompt = BiometricPrompt(
                activity,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        try {
                            onSuccess()
                        } catch (e: Exception) {
                            onError(BiometricPrompt.ERROR_VENDOR, "Authentication callback failed: ${e.message}")
                        }
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        // User canceled authentication
                        if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                            errorCode == BiometricPrompt.ERROR_USER_CANCELED) {
                            // Don't treat cancellation as an error
                            return
                        }
                        onError(errorCode, errString)
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        // Biometric not recognized, but allow retry
                        onFailed?.invoke()
                    }
                }
            )

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Login")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .setConfirmationRequired(false)
                .build()

            biometricPrompt.authenticate(promptInfo)
        } catch (e: Exception) {
            onError(BiometricPrompt.ERROR_VENDOR, "Failed to show biometric prompt: ${e.message}")
        }
    }
}
