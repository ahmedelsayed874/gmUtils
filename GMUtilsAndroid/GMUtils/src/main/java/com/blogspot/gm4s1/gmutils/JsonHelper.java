package com.blogspot.gm4s1.gmutils;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer with (Java/Kotlin, Swift)
 * Have experience with:
 * - (C/C++, C#) languages
 * - .NET environment
 * - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */
public class JsonHelper {

    public static JsonHelper createInstance() {
        return new JsonHelper();
    }

    //----------------------------------------------------------------------------------------------

    public String getString(JSONObject jsonObject, String key) {
        return getString(jsonObject, key, "");
    }

    public String getString(JSONObject jsonObject, String key, String defaultValue) {
        try {
            if (jsonObject.has(key)) return jsonObject.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return defaultValue;
    }


    public Integer getInt(JSONObject jsonObject, String key) {
        return getInt(jsonObject, key, null);
    }

    public Integer getInt(JSONObject jsonObject, String key, Integer defaultValue) {
        try {
            if (jsonObject.has(key)) return jsonObject.getInt(key);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return defaultValue;
    }


    public Double getDouble(JSONObject jsonObject, String key) {
        return getDouble(jsonObject, key, null);
    }

    public Double getDouble(JSONObject jsonObject, String key, Double defaultValue) {
        try {
            if (jsonObject.has(key)) return jsonObject.getDouble(key);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return defaultValue;
    }


    public Float getFloat(JSONObject jsonObject, String key) {
        return getFloat(jsonObject, key, null);
    }

    public Float getFloat(JSONObject jsonObject, String key, Float defaultValue) {
        Double v = getDouble(jsonObject, key, defaultValue == null ? null : defaultValue.doubleValue());
        return v == null ? null : v.floatValue();
    }


    public Boolean getBoolean(JSONObject jsonObject, String key) {
        return getBoolean(jsonObject, key, null);
    }

    public Boolean getBoolean(JSONObject jsonObject, String key, Boolean defaultValue) {
        try {
            if (jsonObject.has(key)) return jsonObject.getBoolean(key);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return defaultValue;
    }


    public List<Integer> getIntegerList(JSONObject jsonObject, String key) {
        return getIntegerList(jsonObject, key, false);
    }

    public List<Integer> getIntegerList(JSONObject jsonObject, String key, boolean defaultValueNull) {
        try {
            if (!TextUtils.isEmpty(key) && jsonObject.has(key)) {
                Object o = jsonObject.get(key);
                if (o instanceof JSONArray) {
                    JSONArray jsonArray = (JSONArray) o;
                    List<Integer> integers = new ArrayList<>();

                    int length = jsonArray.length();
                    for (int i = 0; i < length; i++) {
                        try {
                            integers.add(jsonArray.getInt(i));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    return integers;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return defaultValueNull ? null : new ArrayList<Integer>();
    }


    public List<String> getStringList(JSONObject jsonObject, String key) {
        return getStringList(jsonObject, key, false);
    }

    public List<String> getStringList(JSONObject jsonObject, String key, boolean defaultValueNull) {
        try {
            if (!TextUtils.isEmpty(key) && jsonObject.has(key)) {
                Object o = jsonObject.get(key);
                if (o instanceof JSONArray) {
                    JSONArray jsonArray = (JSONArray) o;
                    List<String> strings = new ArrayList<>();

                    int length = jsonArray.length();
                    for (int i = 0; i < length; i++) {
                        try {
                            strings.add(jsonArray.getString(i));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    return strings;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return defaultValueNull ? null : new ArrayList<String>();
    }


    public JSONArray getJSONArray(JSONObject jsonObject, String key) {
        return getJSONArray(jsonObject, key, false);
    }

    public JSONArray getJSONArray(JSONObject jsonObject, String key, boolean defaultValueNull) {
        try {
            if (!TextUtils.isEmpty(key) && jsonObject.has(key)) {
                Object o = jsonObject.get(key);
                if (o instanceof JSONArray) {
                    return (JSONArray) o;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return defaultValueNull ? null : new JSONArray();
    }


    public JSONObject getJSONObject(JSONObject jsonObject, String key) {
        return getJSONObject(jsonObject, key, false);
    }

    public JSONObject getJSONObject(JSONObject jsonObject, String key, boolean defaultValueNull) {
        try {
            if (jsonObject.has(key)) return jsonObject.getJSONObject(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValueNull ? null : new JSONObject();
    }


    public List<JSONObject> getJSONObjectList(JSONArray jsonArray) {
        return getJSONObjectList(jsonArray, false);
    }

    public List<JSONObject> getJSONObjectList(JSONArray jsonArray, boolean defaultValueNull) {
        try {
            int length = jsonArray.length();

            if (length > 0) {
                List<JSONObject> jsonObjects = new ArrayList<>();

                for (int i = 0; i < length; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    jsonObjects.add(jsonObject);
                }

                return jsonObjects;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return defaultValueNull ? null : new ArrayList<JSONObject>();
    }

}
