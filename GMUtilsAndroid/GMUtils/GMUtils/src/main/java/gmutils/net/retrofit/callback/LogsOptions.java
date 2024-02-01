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
            return coverText(text, null);
        }

        public Replacements coverText(String text, CoverOptions coverOptions) {
            if (TextUtils.isEmpty(text)) return this;
            //asd-efghi-jk
            int x = text.length(); //12

            if (coverOptions != null) {
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

                    x -= (opt.lettersFromLeading + opt.lettersFromTrailing);
                    String middle = coveredText.invoke(x);
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

            static CoverOptions allExcept(int lettersFromLeading, int lettersFromTrailing) {
                return new CoverOptions.AllExcept(lettersFromLeading, lettersFromTrailing);
            }

            class All implements CoverOptions {
            }

            class AllExcept implements CoverOptions {
                public final int lettersFromLeading;
                public final int lettersFromTrailing;

                public AllExcept(int lettersFromLeading, int lettersFromTrailing) {
                    this.lettersFromLeading = lettersFromLeading;
                    this.lettersFromTrailing = lettersFromTrailing;
                }
            }
        }

    }

    private boolean printHeaders = false;
    private boolean printRequestParameters = false;
    private Replacements replacements;
    private ActionCallback0<String> extraInfo;

    public LogsOptions() {
    }

    public LogsOptions printHeaders() {
        this.printHeaders = true;
        return this;
    }

    public LogsOptions printRequestParameters() {
        this.printRequestParameters = true;
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

    boolean allowPrintHeaders() {
        return printHeaders;
    }

    boolean allowPrintRequestParameters() {
        return printRequestParameters;
    }

    Replacements getReplacements() {
        return replacements;
    }

    ActionCallback0<String> getExtraInfo() {
        return extraInfo;
    }
}
