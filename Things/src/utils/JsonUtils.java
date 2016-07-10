package utils;

import com.google.gson.Gson;

import java.io.Reader;
import java.io.Writer;

/**
 * Created by germangb on 10/07/16.
 */
public class JsonUtils {

    private static Gson gson = new Gson();

    /**
     *
     * @param obj
     * @return
     */
    public String toJson (Object obj) {
        return gson.toJson(obj);
    }

    /**
     *
     * @param object
     * @param writer
     */
    public void toJson (Object object, Writer writer) {
        gson.toJson(object, writer);
    }

    /**
     * Convert from json
     * @param json
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T fromJson (String json, Class<T> type) {
        return gson.fromJson(json, type);
    }

    /**
     *
     * @param json
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T fromJson (Reader json, Class<T> type) {
        return gson.fromJson(json, type);
    }
}
