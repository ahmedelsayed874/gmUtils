package gmutils.firebase.fcm;


public class SendFcmMessageParameters {
    public enum FileSource { RawResources, Storage }
    public final String firebaseProjectId;
    public final FileSource firebaseServiceAccountFileSource;
    public final Object firebaseServiceAccountFileAddress;

    public SendFcmMessageParameters(String firebaseProjectId, FileSource firebaseServiceAccountFileSource, Object firebaseServiceAccountFileAddress) {
        this.firebaseProjectId = firebaseProjectId;
        this.firebaseServiceAccountFileSource = firebaseServiceAccountFileSource;
        this.firebaseServiceAccountFileAddress = firebaseServiceAccountFileAddress;

        if (firebaseServiceAccountFileSource == FileSource.RawResources) {
            assert firebaseServiceAccountFileAddress instanceof Number;
        } else {
            assert firebaseServiceAccountFileAddress instanceof String;
            assert !((String) firebaseServiceAccountFileAddress).isEmpty();
        }
    }
}
