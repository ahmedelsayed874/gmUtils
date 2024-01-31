package gmutils.net.retrofit.callback;

public class LogsOptions {
    public final String[] excludedTextsFromLog;
    public final boolean printHeaders;
    public final boolean printRequestParameters;

    public LogsOptions(String[] excludedTextsFromLog) {
        this(excludedTextsFromLog, true, true);
    }

    public LogsOptions(String[] excludedTextsFromLog, boolean printHeaders, boolean printRequestParameters) {
        this.excludedTextsFromLog = excludedTextsFromLog;
        this.printHeaders = printHeaders;
        this.printRequestParameters = printRequestParameters;
    }
}
