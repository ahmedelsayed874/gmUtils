package gmutils.firebase.auth;

import android.net.Uri;

import com.google.firebase.auth.FirebaseAuthException;

import java.util.Map;

import gmutils.firebase.FirebaseUtils;
import gmutils.firebase.Response;
import gmutils.listeners.ResultCallback;


public interface IFirebaseAuthManager {
    public static class FBUser {
        public String email;
        public String displayName;
        public Uri photoUrl;
        public String phoneNumber;
        public long creationTimestamp;
        public long lastSignInTimestamp;
        public String username;
        public Map<String, Object> extraInfo;
    }

    public static String DEFAULT_EMAIL_HOST_NAME = "myusers.org";

    void registerByNonEmail(String text, String password, ResultCallback<Response<Boolean>> callback);

    void registerByEmail(String email, String password, ResultCallback<Response<Boolean>> callback);

    void loginByNonEmail(String text, String password, ResultCallback<Response<FBUser>> callback);

    void loginByEmail(String email, String password, ResultCallback<Response<FBUser>> callback);

    void logout();

    //----------------------------------------------------------------------------

    default String formatNonEmailToEmail(String text) {
        text = FirebaseUtils.refinePhoneNumber(text);
        text = FirebaseUtils.refineKeyName(text);
        return "un" + text + DEFAULT_EMAIL_HOST_NAME;
    }

    //----------------------------------------------------------------------------

    void startPasswordReset(String identifier, boolean isEmailIdentifier, ResultCallback<Response<Boolean>> callback);

    void verifyResetPasswordCode(String identifier, boolean isEmailIdentifier, String resetPasswordCode, ResultCallback<Response<Boolean>> callback);

    void changePasswordAfterResetting(String resetPasswordCode, String newPassword, ResultCallback<Response<Boolean>> callback);

    //----------------------------------------------------------------------------

    void changePassword(String identifier, boolean isEmailIdentifier, String currentPassword, String newPassword, ResultCallback<Response<Boolean>> callback);

    //----------------------------------------------------------------------------

    public static final String EMAIL_ALREADY_IN_USE = "User identifier already in use";

    <T> Response<T> firebaseAuthExceptionMessage(FirebaseAuthException e);
}
