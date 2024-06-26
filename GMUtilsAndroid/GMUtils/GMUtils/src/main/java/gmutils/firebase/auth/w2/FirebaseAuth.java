package gmutils.firebase.auth.w2;

import android.net.Uri;
import android.util.Pair;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import gmutils.listeners.ResultCallback;

/**
 * https://firebase.google.com/docs/auth/android/
 *
 * dependencies
 * implementation 'com.google.firebase:firebase-auth:20.0.1'
 */
public class FirebaseAuth {
    public interface CreateAccountCallback {
        void onAccountCreated(FirebaseAuth obj, FirebaseUser user);

        void onAccountNotCreated(FirebaseAuth obj, String msg);
    }

    public final com.google.firebase.auth.FirebaseAuth fAuth;

    public static FirebaseAuth init() {
        return new FirebaseAuth();
    }

    private FirebaseAuth() {
        try {
            Class.forName("com.google.firebase.auth.FirebaseAuth");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException("add this line to gradle script file:\n" +
                    "//https://firebase.google.com/docs/auth/android/\n" +
                    "implementation 'com.google.firebase:firebase-auth:20.0.2'");
        }

        // Initialize Firebase Auth
        fAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
    }

    //----------------------------------------------------------------------------------------------

    public void createNewAccount(String email, String password, CreateAccountCallback callback) {
        fAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = fAuth.getCurrentUser();
                        if (callback != null)
                            callback.onAccountCreated(FirebaseAuth.this, user);

                    } else {
                        if (callback != null)
                            callback.onAccountNotCreated(FirebaseAuth.this, task.getException().getMessage());
                    }
                });
    }

    public void sendVerificationEmail(ResultCallback<Boolean> callback) {
        FirebaseUser user = getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (callback != null)
                        callback.invoke(task.isSuccessful());
                });
    }

    //----------------------------------------------------------------------------------------------

    public void signIn(String email, String password, ResultCallback<Pair<FirebaseUser, String>> callback) {
        fAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = fAuth.getCurrentUser();
                            if (callback != null)
                                callback.invoke(new Pair<>(user, ""));

                        } else {
                            if (callback != null)
                                callback.invoke(new Pair<>(null, task.getException().getMessage()));
                        }
                    }
                });
    }

    public void signOut() {
        fAuth.signOut();
    }

    //----------------------------------------------------------------------------------------------

    public FirebaseUser getCurrentUser() {
        return fAuth.getCurrentUser();
    }

    public void changeLanguageCode(String langCode) {
        fAuth.setLanguageCode(langCode);
    }

    public String getLanguageCode() {
        return fAuth.getLanguageCode();
    }

    public void changeAccountPhoto(Uri uri, ResultCallback<Boolean> callback) {
        FirebaseUser user = getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                //.setDisplayName("Jane Q. User")
                .setPhotoUri(uri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (callback != null)
                        callback.invoke(task.isSuccessful());
                });
    }

    public void changeAccountDisplayedName(String name, ResultCallback<Boolean> callback) {
        FirebaseUser user = getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (callback != null)
                        callback.invoke(task.isSuccessful());
                });
    }

    public void changeAccountEmail(String newEmail, ResultCallback<Boolean> callback) {
        FirebaseUser user = getCurrentUser();

        user.updateEmail(newEmail)
                .addOnCompleteListener(task -> {
                    if (callback != null)
                        callback.invoke(task.isSuccessful());
                });
    }

    public void changeAccountPassword(String newPassword, ResultCallback<Boolean> callback) {
        FirebaseUser user = getCurrentUser();

        user.updatePassword(newPassword)
                .addOnCompleteListener(task -> {
                    if (callback != null)
                        callback.invoke(task.isSuccessful());
                });
    }

    public void sendPasswordResetEmail(ResultCallback<Boolean> callback) {
        String emailAddress = getCurrentUser().getEmail();

        fAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (callback != null)
                            callback.invoke(task.isSuccessful());
                    }
                });
    }

    public void removeAccount(ResultCallback<Boolean> callback) {
        FirebaseUser user = getCurrentUser();

        user.delete()
                .addOnCompleteListener(task -> {
                    if (callback != null)
                        callback.invoke(task.isSuccessful());
                });
    }
}
