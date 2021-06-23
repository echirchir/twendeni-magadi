package org.etma.main.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthenticationResult {

    @SerializedName("accessToken")
    @Expose
    private String accessToken;
    @SerializedName("encryptedAccessToken")
    @Expose
    private String encryptedAccessToken;
    @SerializedName("expireInSeconds")
    @Expose
    private int expireInSeconds;
    @SerializedName("shouldResetPassword")
    @Expose
    private boolean shouldResetPassword;
    @SerializedName("passwordResetCode")
    @Expose
    private Object passwordResetCode;
    @SerializedName("userId")
    @Expose
    private int userId;
    @SerializedName("requiresTwoFactorVerification")
    @Expose
    private boolean requiresTwoFactorVerification;
    @SerializedName("twoFactorAuthProviders")
    @Expose
    private Object twoFactorAuthProviders;
    @SerializedName("twoFactorRememberClientToken")
    @Expose
    private Object twoFactorRememberClientToken;
    @SerializedName("returnUrl")
    @Expose
    private String returnUrl;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getEncryptedAccessToken() {
        return encryptedAccessToken;
    }

    public void setEncryptedAccessToken(String encryptedAccessToken) {
        this.encryptedAccessToken = encryptedAccessToken;
    }

    public int getExpireInSeconds() {
        return expireInSeconds;
    }

    public void setExpireInSeconds(int expireInSeconds) {
        this.expireInSeconds = expireInSeconds;
    }

    public boolean isShouldResetPassword() {
        return shouldResetPassword;
    }

    public void setShouldResetPassword(boolean shouldResetPassword) {
        this.shouldResetPassword = shouldResetPassword;
    }

    public Object getPasswordResetCode() {
        return passwordResetCode;
    }

    public void setPasswordResetCode(Object passwordResetCode) {
        this.passwordResetCode = passwordResetCode;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isRequiresTwoFactorVerification() {
        return requiresTwoFactorVerification;
    }

    public void setRequiresTwoFactorVerification(boolean requiresTwoFactorVerification) {
        this.requiresTwoFactorVerification = requiresTwoFactorVerification;
    }

    public Object getTwoFactorAuthProviders() {
        return twoFactorAuthProviders;
    }

    public void setTwoFactorAuthProviders(Object twoFactorAuthProviders) {
        this.twoFactorAuthProviders = twoFactorAuthProviders;
    }

    public Object getTwoFactorRememberClientToken() {
        return twoFactorRememberClientToken;
    }

    public void setTwoFactorRememberClientToken(Object twoFactorRememberClientToken) {
        this.twoFactorRememberClientToken = twoFactorRememberClientToken;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

}
