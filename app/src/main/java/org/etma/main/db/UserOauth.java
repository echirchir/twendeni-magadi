package org.etma.main.db;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class UserOauth extends RealmObject {

    @PrimaryKey @Index
    private long id;

    private String accessToken;
    private String encryptedAccessToken;
    private int expiresInSeconds;
    private int userId;
    private boolean requiresTwoFactorVerification;
    private String passwordResetCode;
    private boolean shouldResetPassword;
    private String twoFactorRememberClientToken;
    private String twoFactorAuthProviders;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public int getExpiresInSeconds() {
        return expiresInSeconds;
    }

    public void setExpiresInSeconds(int expiresInSeconds) {
        this.expiresInSeconds = expiresInSeconds;
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

    public String getPasswordResetCode() {
        return passwordResetCode;
    }

    public void setPasswordResetCode(String passwordResetCode) {
        this.passwordResetCode = passwordResetCode;
    }

    public boolean isShouldResetPassword() {
        return shouldResetPassword;
    }

    public void setShouldResetPassword(boolean shouldResetPassword) {
        this.shouldResetPassword = shouldResetPassword;
    }

    public String getTwoFactorRememberClientToken() {
        return twoFactorRememberClientToken;
    }

    public void setTwoFactorRememberClientToken(String twoFactorRememberClientToken) {
        this.twoFactorRememberClientToken = twoFactorRememberClientToken;
    }

    public String getTwoFactorAuthProviders() {
        return twoFactorAuthProviders;
    }

    public void setTwoFactorAuthProviders(String twoFactorAuthProviders) {
        this.twoFactorAuthProviders = twoFactorAuthProviders;
    }
}
