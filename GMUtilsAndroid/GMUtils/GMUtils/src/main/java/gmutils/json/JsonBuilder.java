package gmutils.json;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gmutils.listeners.ActionCallback;

public class JsonBuilder {
    private final Object rootJson;

    public JsonBuilder(@NotNull JSONObject rootJson) {
        this.rootJson = rootJson;
    }

    public JsonBuilder(@NotNull JSONArray rootJson) {
        this.rootJson = rootJson;
    }

    public static JsonBuilder ofJsonObject() {
        return new JsonBuilder(new JSONObject());
    }

    public static JsonBuilder ofJsonArray() {
        return new JsonBuilder(new JSONArray());
    }


    //----------------------------------------------------------------------------------------------

    /**
     *
     * @param value a {@link JsonBuilder}, {@link JSONObject}, {@link JSONArray}, String, Boolean,
     *      Integer, Long, Double, {@link JSONObject#NULL}, or {@code null}. May
     *      not be {@link Double#isNaN() NaNs} or {@link Double#isInfinite()
     *      infinities}. Unsupported values are not permitted and will cause the
     *      array to be in an inconsistent state.
     * @return {@link JsonBuilder}
     */
    public JsonBuilder add(Object value) {
        if (rootJson instanceof JSONArray) {
            try {
                if (value instanceof JsonBuilder) {
                    ((JSONArray) rootJson).put(((JsonBuilder) value).rootJson);
                }
                //
                else {
                    ((JSONArray) rootJson).put(value);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        //
        else {
            throw new IllegalStateException("rootJson is not JSONArray");
        }

        return this;
    }

    public JsonBuilder add(JsonBuilder value) {
        return add((Object) value);
    }

    //-----------------------------------------------
    
    /**
     *
     * @param count number of item which will add
     * @param value take the index of array item and returns a value from one of these types {@link JsonBuilder}, {@link JSONObject}, {@link JSONArray}, String, Boolean,
     *      Integer, Long, Double, {@link JSONObject#NULL}, or {@code null}. May not be
     *      {@link Double#isNaN() NaNs} or {@link Double#isInfinite()
     *      infinities}
     * @return
     */
    public JsonBuilder addList(int count, @NotNull ActionCallback<Integer, Object> value) {
        if (rootJson instanceof JSONArray) {
            for (int i = 0; i < count; i++) {
                add(value.invoke(i));
            }
        }
        //
        else {
            throw new IllegalStateException("rootJson is not JSONArray");
        }

        return this;
    }

    //-----------------------------------------------

    /**
     * @param key {@link String}
     * @param value a {@link JsonBuilder}, {@link JSONObject}, {@link JSONArray}, String, Boolean,
     *     Integer, Long, Double, {@link JSONObject#NULL}, or {@code null}. May not be
     *     {@link Double#isNaN() NaNs} or {@link Double#isInfinite()
     *     infinities}.
     */
    public JsonBuilder add(@NotNull String key, Object value) {
        if (rootJson instanceof JSONObject) {
            try {
                if (value instanceof JsonBuilder) {
                    ((JSONObject) rootJson).put(key, ((JsonBuilder) value).rootJson);
                } else {
                    ((JSONObject) rootJson).put(key, value);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        //
        else {
            throw new IllegalStateException("rootJson is not JSONObject");
        }

        return this;
    }

    public JsonBuilder add(@NotNull String key, JsonBuilder value) {
        return add(key, (Object) value);
    }

    //----------------------------------------------------------------------------------------------

    /**
     * @return {@link JSONObject} or {@link JSONArray}
     */
    public Object getJson() {
        return rootJson;
    }

    @NonNull
    @Override
    public String toString() {
        if (rootJson instanceof JSONObject) {
            return rootJson.toString();
        }
        //
        else if (rootJson instanceof JSONArray) {
            return rootJson.toString();
        }
        //
        else {
            return "";
        }
    }

    @NonNull
    public String toString(int indentSpace) {
        try {
            if (rootJson instanceof JSONObject) {
                return ((JSONObject) rootJson).toString(indentSpace);
            }
            //
            else if (rootJson instanceof JSONArray) {
                return ((JSONArray) rootJson).toString(indentSpace);
            }
            //
            else {
                return "";
            }
        } catch (Exception e) {
            return toString();
        }
    }
}
