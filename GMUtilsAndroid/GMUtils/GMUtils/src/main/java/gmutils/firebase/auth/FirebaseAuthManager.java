package gmutils.firebase.auth;

import android.text.TextUtils;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Future;

import gmutils.StringSet;
import gmutils.firebase.Response;
import gmutils.listeners.ResultCallback;


public class FirebaseAuthManager implements IFirebaseAuthManager {
    public static final String anonymousUserName = "anonymous@anonymous.com";
    public static final String anonymousPassword = "pw0fAnonymous123";

    public static void anonymousLogin(ResultCallback<Response<FBUser>> callback) {
        new FirebaseAuthManager().loginByEmail(anonymousUserName, anonymousPassword, new ResultCallback<Response<FBUser>>() {
            @Override
            public void invoke(Response<FBUser> result) {
                if (callback != null) callback.invoke(result);
            }
        });
    }

    //------------------------------------------------------------------------------------------------

    private com.google.firebase.auth.FirebaseAuth fbAuth;

    public FirebaseAuthManager() {
        try {
            Class.forName("com.google.firebase.auth.FirebaseAuth");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException("add this line to gradle script file:\n" +
                    "//https://firebase.google.com/docs/auth/android/\n" +
                    "implementation 'com.google.firebase:firebase-auth:20.0.2'");
        }

        fbAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
    }

    //----------------------------------------------------------------------------

    @Override
    public void registerByNonEmail(String text, String password, ResultCallback<Response<Boolean>> callback) {
        registerByEmail(formatNonEmailToEmail(text), password, callback);
    }

    @Override
    public void registerByEmail(String email, String password, ResultCallback<Response<Boolean>> callback) {
        fbAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (callback != null) {
                if (task.isSuccessful()) {
                    callback.invoke(Response.success(true));
                } else {
                    String ee = "";
                    String ea = "";

                    if (task.getException() != null) {
                        ee = "\nDetails: " + task.getException().getMessage();
                        ea = "\n" + "تفاصيل: " + task.getException().getMessage();
                    }

                    callback.invoke(Response.failed(
                            new StringSet(
                                    "Register failed." + ee,
                                    "تعذر تسجيل الحساب" + ea
                            )
                    ));
                }
            }
        });
    }

    @Override
    public void loginByNonEmail(String text, String password, ResultCallback<Response<FBUser>> callback) {
        loginByEmail(formatNonEmailToEmail(text), password, callback);
    }

    @Override
    public void loginByEmail(String email, String password, ResultCallback<Response<FBUser>> callback) {
        fbAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FBUser user = new FBUser();
                user.email = task.getResult().getUser().getEmail();
                user.displayName = task.getResult().getUser().getDisplayName();
                user.photoUrl = task.getResult().getUser().getPhotoUrl();
                user.phoneNumber = task.getResult().getUser().getPhoneNumber();
                user.creationTimestamp = task.getResult().getUser().getMetadata().getCreationTimestamp();
                user.lastSignInTimestamp = task.getResult().getUser().getMetadata().getLastSignInTimestamp();
                user.username = task.getResult().getAdditionalUserInfo().getUsername();
                user.extraInfo = task.getResult().getAdditionalUserInfo().getProfile();

                callback.invoke(Response.success(user));
            } else {
                String ee = "";
                String ea = "";
                if (task.getException() != null) {
                    ee = "\nDetails: " + task.getException().getMessage();
                    ea = "\n" + "تفاصيل: " + task.getException().getMessage();
                }
                callback.invoke(Response.failed(
                        new StringSet(
                                "Login failed." + ee,
                                "تعذر تسجيل الدخول" + ea
                        )
                ));
            }
        });
    }

    @Override
    public void logout() {
        fbAuth.signOut();
    }

    //----------------------------------------------------------------------------

    @Override
    public void startPasswordReset(String identifier, boolean isEmailIdentifier, ResultCallback<Response<Boolean>> callback) {
        String email = isEmailIdentifier ? identifier : formatNonEmailToEmail(identifier);
        fbAuth.sendPasswordResetEmail(email.trim()).addOnCompleteListener(task -> {
            if (callback != null) {
                if (task.isSuccessful()) {
                    callback.invoke(Response.success(true));
                } else {
                    String ee = "";
                    String ea = "";

                    if (task.getException() != null) {
                        ee = "\nDetails: " + task.getException().getMessage();
                        ea = "\n" + "تفاصيل: " + task.getException().getMessage();
                    }

                    callback.invoke(Response.failed(
                            new StringSet(
                                    "Reset password failed." + ee,
                                    "تعذر تغيير كلمة السر" + ea
                            )
                    ));
                }
            }
        });
    }

    @Override
    public void verifyResetPasswordCode(String identifier, boolean isEmailIdentifier, String resetPasswordCode, ResultCallback<Response<Boolean>> callback) {
        String email = isEmailIdentifier ? identifier : formatNonEmailToEmail(identifier);
        fbAuth.verifyPasswordResetCode(resetPasswordCode).addOnCompleteListener(task -> {
            var emailRelatedCode = task.getResult();
            if (TextUtils.equals(emailRelatedCode, email)) {
                callback.invoke(Response.success(true));
            } else {
                callback.invoke(
                        Response.failed(
                                new StringSet(
                                        "The resetting code is wrong",
                                        "الكود خاطيء"
                                ))
                );
            }
        });

    }

    @Override
    public void changePasswordAfterResetting(String resetPasswordCode, String newPassword, ResultCallback<Response<Boolean>> callback) {
        fbAuth.confirmPasswordReset(resetPasswordCode, newPassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.invoke(Response.success(true));
            } else {
                callback.invoke(
                        Response.failed(
                                new StringSet(
                                        "Resetting password failed",
                                        "تعذر تغيير كلمة السر"
                                ))
                );
            }
        });
    }

    //----------------------------------------------------------------------------

    @Override
    public void changePassword(String identifier, boolean isEmailIdentifier, String currentPassword, String newPassword, ResultCallback<Response<Boolean>> callback) {
        String email = isEmailIdentifier ? identifier : formatNonEmailToEmail(identifier);

        if (fbAuth.getCurrentUser() == null) {
            fbAuth.signInWithEmailAndPassword(email, currentPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    changePassword(fbAuth.getCurrentUser(), newPassword, callback);
                } else {
                    String ee = "";
                    String ea = "";

                    if (task.getException() != null) {
                        ee = "\nDetails: " + task.getException().getMessage();
                        ea = "\n" + "تفاصيل: " + task.getException().getMessage();
                    }

                    callback.invoke(Response.failed(
                            new StringSet(
                                    "Changing password failed." + ee,
                                    "تعذر تغيير كلمة السر" + ea
                            )
                    ));
                }
            });
        } else {
            changePassword(fbAuth.getCurrentUser(), newPassword, callback);
        }
    }

    public void changePassword(FirebaseUser firebaseUser, String newPassword, ResultCallback<Response<Boolean>> callback) {
        firebaseUser.updatePassword(newPassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.invoke(Response.success(true));
            } else {
                String ee = "";
                String ea = "";

                if (task.getException() != null) {
                    ee = "\nDetails: " + task.getException().getMessage();
                    ea = "\n" + "تفاصيل: " + task.getException().getMessage();
                }

                callback.invoke(Response.failed(
                        new StringSet(
                                "Changing password failed." + ee,
                                "تعذر تغيير كلمة السر" + ea
                        )
                ));
            }
        });
    }

    //----------------------------------------------------------------------------


    @Override
    public <T> Response<T> firebaseAuthExceptionMessage(FirebaseAuthException e) {
        String errEn = e.getErrorCode().replace("-", " ");
        String errAr = errEn;

        if (TextUtils.equals(e.getErrorCode(), "network-request-failed")) {
            errEn = "No internet connection";
            errAr = "لا يوجد اتصال بالإنترنت";
            return Response.failed(
                    new StringSet(errEn, errAr),
                    true
            );
        }

        if (TextUtils.equals(e.getErrorCode(), "email-already-in-use")) {
            errEn = IFirebaseAuthManager.EMAIL_ALREADY_IN_USE;
            errAr = "اسم المستخدم مسجل سابقا بالفعل";
            //errAr = "البريد الإلكتروني مستخدم بالفعل";
        }

        //
        else if (TextUtils.equals(e.getErrorCode(), "invalid-email")) {
            errEn = "Invalid Email";
            errAr = "بريد إلكتروني غير صحيح";
        }

        //
        else if (TextUtils.equals(e.getErrorCode(), "operation-not-allowed")) {
            errEn = "Operation not allowed";
            errAr = "عملية غير مسموح بها";
        }

        //
        else if (TextUtils.equals(e.getErrorCode(), "weak-password")) {
            errEn = "Weak password";
            errAr = "كلمة مرور ضعيفة";
        }

        //
        else if (TextUtils.equals(e.getErrorCode(), "wrong-password")) {
            errEn = "Wrong password";
            errAr = "كلمة مرور خاطئة";
        }

        //
        else if (TextUtils.equals(e.getErrorCode(), "user-disabled")) {
            errEn = "User disabled";
            errAr = "تم تعطيل الحساب";
        }

        //
        else if (TextUtils.equals(e.getErrorCode(), "user-not-found")) {
            errEn = "User is not found";
            errAr = "الحساب غير موجود";
        }

        if (e.getMessage() != null) {
            errEn += "\n\n<<" + e.getMessage() + ">>";
            errAr += errEn;
        }

        return Response.failed(new StringSet(errEn, errAr));
    }
}
