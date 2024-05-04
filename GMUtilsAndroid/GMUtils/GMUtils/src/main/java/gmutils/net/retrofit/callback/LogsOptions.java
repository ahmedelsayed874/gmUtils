package gmutils.net.retrofit.callback;

import android.text.TextUtils;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import gmutils.listeners.ActionCallback;
import gmutils.listeners.ActionCallback0;
import gmutils.listeners.ActionCallback2;


public class LogsOptions {
    public static class Replacements {
        private List<Pair<String, String>> replacements;

        public Replacements replace(String text, String alter) {
            if (TextUtils.isEmpty(text)) return this;
            if (this.replacements == null) this.replacements = new ArrayList<>();
            this.replacements.add(new Pair<>(text, alter));
            return this;
        }

        public Replacements coverText(String text) {
            return coverText(text, CoverOptions.all());
        }

        public Replacements coverText(String text, CoverOptions coverOptions) {
            if (TextUtils.isEmpty(text)) return this;

            if (coverOptions != null) {
                //asd-efghi-jk
                int x = text.length(); //12

                ActionCallback<Integer, String> coveredText = length -> {
                    if (length <= 0) return "";
                    else return String.format(
                            "%0" + length + "d",
                            "0"
                    ).replaceAll("0", "*");
                };

                if (coverOptions instanceof CoverOptions.All) {
                    return replace(text, coveredText.invoke(x));
                }
                //
                else if (coverOptions instanceof CoverOptions.AllExcept opt) {
                    if (opt.lettersFromLeading + opt.lettersFromTrailing >= x) {
                        return replace(text, coveredText.invoke(x));
                    }

                    String start = "";
                    if (opt.lettersFromLeading > 0) {
                        start = text.substring(0, opt.lettersFromLeading);
                    }

                    String end = "";
                    if (opt.lettersFromTrailing > 0 && (x - opt.lettersFromTrailing) > 0) {
                        end = text.substring(x - opt.lettersFromTrailing);
                    }

                    String middle = "";
                    if (opt.middleAlterText == null) {
                        x -= (opt.lettersFromLeading + opt.lettersFromTrailing);
                        middle = coveredText.invoke(x);
                    } else {
                        middle = opt.middleAlterText;
                    }

                    return replace(text, start + middle + end);
                }
            }

            return replace(text, "******");
        }

        List<Pair<String, String>> getReplacements() {
            return replacements;
        }

        public interface CoverOptions {
            static CoverOptions all() {
                return new CoverOptions.All();
            }

            static CoverOptions allExcept(int lettersFromLeading, String middleAlterText, int lettersFromTrailing) {
                return new CoverOptions.AllExcept(lettersFromLeading, middleAlterText, lettersFromTrailing);
            }

            class All implements CoverOptions {
            }

            class AllExcept implements CoverOptions {
                public final int lettersFromLeading;
                public final String middleAlterText;
                public final int lettersFromTrailing;

                public AllExcept(int lettersFromLeading) {
                    this(lettersFromLeading, null, 0);
                }

                public AllExcept(int lettersFromLeading, int lettersFromTrailing) {
                    this(lettersFromLeading, null, lettersFromTrailing);
                }

                public AllExcept(int lettersFromLeading, String middleAlterText, int lettersFromTrailing) {
                    this.lettersFromLeading = lettersFromLeading;
                    this.middleAlterText = middleAlterText;
                    this.lettersFromTrailing = lettersFromTrailing;
                }
            }
        }

    }

    public static class RequestOptions {
        private boolean printHeaders = false;
        private boolean printRequestParameters = false;

        public RequestOptions(boolean printHeaders, boolean printRequestParameters) {
            this.printHeaders = printHeaders;
            this.printRequestParameters = printRequestParameters;
        }

        boolean allowPrintHeaders() {
            return printHeaders;
        }

        boolean allowPrintRequestParameters() {
            return printRequestParameters;
        }
    }


    private RequestOptions requestOptions;
    private Replacements replacements;
    private ActionCallback0<String> extraInfo;

    public LogsOptions() {
    }

    public LogsOptions setRequestOptions(RequestOptions requestOptions) {
        this.requestOptions = requestOptions;
        return this;
    }

    public LogsOptions replace(String text, String alter) {
        if (this.replacements == null) this.replacements = new Replacements();
        this.replacements.replace(text, alter);
        return this;
    }

    public LogsOptions coverText(String text) {
        return coverText(text, null);
    }

    public LogsOptions coverText(String text, Replacements.CoverOptions coverOptions) {
        if (this.replacements == null) this.replacements = new Replacements();
        this.replacements.coverText(text, coverOptions);
        return this;
    }

    public LogsOptions setExtraInfo(ActionCallback0<String> extraInfo) {
        this.extraInfo = extraInfo;
        return this;
    }

    //--------------------------------------------------------------------

    RequestOptions getRequestOptions() {
        return requestOptions;
    }

    Replacements getReplacements() {
        return replacements;
    }

    ActionCallback0<String> getExtraInfo() {
        return extraInfo;
    }
}
