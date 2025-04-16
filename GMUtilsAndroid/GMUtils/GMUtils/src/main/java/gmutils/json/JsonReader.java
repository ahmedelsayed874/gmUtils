package gmutils.json;

import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer (Java/Kotlin, Swift) also Flutter (Dart)
 * Have precedent experience with:
 * - (C/C++, C#) languages
 * - .NET environment
 * - Java swing
 * - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */
public class JsonReader {
    private final Object json;

    public JsonReader(@NotNull JSONObject json) {
        this.json = json;
    }

    public JsonReader(@NotNull JSONArray json) {
        this.json = json;
    }

    //----------------------------------------------------------------------------------------------

    public List<Object> getKeys() {
        if (json instanceof JSONObject) {
            Iterator<String> keys0 = ((JSONObject) json).keys();
            ArrayList<Object> keys = new ArrayList<>();
            while (keys0.hasNext()) {
                keys.add(keys0.next());
            }
            return keys;
        }
        //
        else if (json instanceof JSONArray) {
            var x = ((JSONArray) json).length();
            ArrayList<Object> keys = new ArrayList<>();
            for (int i = 0; i < x; i++) {
                keys.add(i);
            }
            return keys;
        } else {
            return null;
        }
    }

    //----------------------------------------------------

    public Object get(Object key) {
        if (json instanceof JSONObject) {
            try {
                return ((JSONObject) json).get((String) key);
            } catch (Exception e) {
                return null;
            }
        }
        //
        else if (json instanceof JSONArray) {
            try {
                return ((JSONArray) json).get((Integer) key);
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }

    //----------------------------------------------------

    public String getString(Object key) {
        return getString(key, "");
    }

    public String getString(Object key, String defaultValue) {
        String value = defaultValue;

        if (json instanceof JSONObject) {
            try {
                var key2 = (String) key;
                if (((JSONObject) json).has(key2)) {
                    value = ((JSONObject) json).getString(key2);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //
        else if (json instanceof JSONArray) {
            try {
                var key2 = (int) key;
                value = ((JSONArray) json).getString(key2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return value;
    }

    //----------------------------------------------------

    public Integer getInt(Object key) {
        return getInt(key, null);
    }

    public Integer getInt(Object key, Integer defaultValue) {
        Integer value = defaultValue;

        if (json instanceof JSONObject) {
            try {
                var key2 = (String) key;
                if (((JSONObject) json).has(key2)) {
                    value = ((JSONObject) json).getInt(key2);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //
        else if (json instanceof JSONArray) {
            try {
                var key2 = (int) key;
                value = ((JSONArray) json).getInt(key2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return value;
    }

    //----------------------------------------------------

    public Long getLong(Object key) {
        return getLong(key, null);
    }

    public Long getLong(Object key, Long defaultValue) {
        Long value = defaultValue;

        if (json instanceof JSONObject) {
            try {
                var key2 = (String) key;
                if (((JSONObject) json).has(key2)) {
                    value = ((JSONObject) json).getLong(key2);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //
        else if (json instanceof JSONArray) {
            try {
                var key2 = (int) key;
                value = ((JSONArray) json).getLong(key2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return value;
    }

    //----------------------------------------------------

    public Double getDouble(Object key) {
        return getDouble(key, null);
    }

    public Double getDouble(Object key, Double defaultValue) {
        Double value = defaultValue;

        if (json instanceof JSONObject) {
            try {
                var key2 = (String) key;
                if (((JSONObject) json).has(key2)) {
                    value = ((JSONObject) json).getDouble(key2);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //
        else if (json instanceof JSONArray) {
            try {
                var key2 = (int) key;
                value = ((JSONArray) json).getDouble(key2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return value;
    }

    //----------------------------------------------------

    public Boolean getBoolean(Object key) {
        return getBoolean(key, null);
    }

    public Boolean getBoolean(Object key, Boolean defaultValue) {
        Boolean value = defaultValue;

        if (json instanceof JSONObject) {
            try {
                var key2 = (String) key;
                if (((JSONObject) json).has(key2)) {
                    value = ((JSONObject) json).getBoolean(key2);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //
        else if (json instanceof JSONArray) {
            try {
                var key2 = (int) key;
                value = ((JSONArray) json).getBoolean(key2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return value;
    }


    //----------------------------------------------------

    public JsonReader getJSONArray(Object key) {
        JSONArray jsonArray = null;

        if (json instanceof JSONObject) {
            try {
                var key2 = (String) key;
                if (!TextUtils.isEmpty(key2) && ((JSONObject) json).has(key2)) {
                    Object o = ((JSONObject) json).get(key2);
                    if (o instanceof JSONArray) {
                        jsonArray = (JSONArray) o;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        //
        else if (json instanceof JSONArray) {
            try {
                var key2 = (int) key;
                Object o = ((JSONArray) json).get(key2);
                if (o instanceof JSONArray) {
                    jsonArray = (JSONArray) o;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (jsonArray != null) {
            return new JsonReader(jsonArray);
        }

        return null;
    }

    public JsonReader getJSONObject(Object key) {
        JSONObject jsonObject = null;

        if (json instanceof JSONObject) {
            try {
                var key2 = (String) key;
                if (((JSONObject) json).has(key2)) {
                    jsonObject = ((JSONObject) json).getJSONObject(key2);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        //
        else if (json instanceof JSONArray) {
            try {
                var key2 = (int) key;
                jsonObject = ((JSONArray) json).getJSONObject(key2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (jsonObject != null) {
            return new JsonReader(jsonObject);
        }

        return null;
    }

}
