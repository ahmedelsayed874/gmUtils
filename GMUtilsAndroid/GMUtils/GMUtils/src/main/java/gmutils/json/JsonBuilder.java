package gmutils.json;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public JsonBuilder add(Object value) {
        if (rootJson instanceof JSONArray) {
            ((JSONArray) rootJson).put(value);
        } else {
            throw new IllegalStateException("rootJson is not JSONArray");
        }

        return this;
    }

    public JsonBuilder add(@NotNull String key, Object value) {
        if (rootJson instanceof JSONObject) {
            try {
                ((JSONObject) rootJson).put(key, value);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        return this;
    }

    //-----------------------------------------

    public JsonBuilder addString(String value) {
        if (rootJson instanceof JSONArray) {
            ((JSONArray) rootJson).put(value);
        } else {
            throw new IllegalStateException("rootJson is not JSONArray");
        }

        return this;
    }

    public JsonBuilder addString(@NotNull String key, String value) {
        if (rootJson instanceof JSONObject) {
            try {
                ((JSONObject) rootJson).put(key, value);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        return this;
    }

    //-----------------------------------------

    public JsonBuilder addBoolean(boolean value) {
        if (rootJson instanceof JSONArray) {
            ((JSONArray) rootJson).put(value);
        } else {
            throw new IllegalStateException("rootJson is not JSONArray");
        }

        return this;
    }

    public JsonBuilder addBoolean(@NotNull String key, boolean value) {
        if (rootJson instanceof JSONObject) {
            try {
                ((JSONObject) rootJson).put(key, value);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        return this;
    }

    //-----------------------------------------

    public JsonBuilder addInt(int value) {
        if (rootJson instanceof JSONArray) {
            ((JSONArray) rootJson).put(value);
        } else {
            throw new IllegalStateException("rootJson is not JSONArray");
        }

        return this;
    }

    public JsonBuilder addInt(@NotNull String key, boolean value) {
        if (rootJson instanceof JSONObject) {
            try {
                ((JSONObject) rootJson).put(key, value);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        return this;
    }

    //-----------------------------------------

    public JsonBuilder addLong(long value) {
        if (rootJson instanceof JSONArray) {
            ((JSONArray) rootJson).put(value);
        } else {
            throw new IllegalStateException("rootJson is not JSONArray");
        }

        return this;
    }

    public JsonBuilder addLong(@NotNull String key, long value) {
        if (rootJson instanceof JSONObject) {
            try {
                ((JSONObject) rootJson).put(key, value);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        return this;
    }

    //-----------------------------------------

    public JsonBuilder addDouble(double value) {
        if (rootJson instanceof JSONArray) {
            try {
                ((JSONArray) rootJson).put(value);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalStateException("rootJson is not JSONArray");
        }

        return this;
    }

    public JsonBuilder addDouble(@NotNull String key, double value) {
        if (rootJson instanceof JSONObject) {
            try {
                ((JSONObject) rootJson).put(key, value);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        return this;
    }

    //------------------------------------------

    public JsonBuilder addSubObject(JsonBuilder value) {
        if (rootJson instanceof JSONArray) {
            ((JSONArray) rootJson).put(value.rootJson);
        } else {
            throw new IllegalStateException("rootJson is not JSONArray");
        }

        return this;
    }

    public JsonBuilder addSubObject(@NotNull String key, JsonBuilder value) {
        if (rootJson instanceof JSONObject) {
            try {
                ((JSONObject) rootJson).put(key, value.rootJson);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        return this;
    }

    public JsonBuilder addSubObject(Object value) {
        if (rootJson instanceof JSONArray) {
            ((JSONArray) rootJson).put(value);
        } else {
            throw new IllegalStateException("rootJson is not JSONArray");
        }

        return this;
    }

    public JsonBuilder addSubObject(@NotNull String key, Object value) {
        if (rootJson instanceof JSONObject) {
            try {
                ((JSONObject) rootJson).put(key, value);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        return this;
    }

    //------------------------------------------


    /**
     * @return JSONObject or JSONArray
     */
    public Object getJson() {
        return rootJson;
    }

    @NonNull
    @Override
    public String toString() {
        if (rootJson instanceof JSONObject) {
            return rootJson.toString();
        } else if (rootJson instanceof JSONArray) {
            return rootJson.toString();
        } else {
            return "";
        }
    }

    @NonNull
    public String toString(int indentSpace) {
        try {
            if (rootJson instanceof JSONObject) {
                return ((JSONObject) rootJson).toString(indentSpace);
            } else if (rootJson instanceof JSONArray) {
                return ((JSONArray) rootJson).toString(indentSpace);
            } else {
                return "";
            }
        } catch (Exception e) {
            return toString();
        }
    }
}
