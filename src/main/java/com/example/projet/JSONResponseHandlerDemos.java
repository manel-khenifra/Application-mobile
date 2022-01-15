package com.example.projet;

import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.example.projet.MainActivity.dbHelper;

/**
 * Process the response to a GET request to the Web service
 * https://demo-lia.univ-avignon.fr/cerimuseum/
 * Responses must be provided in JSON.
 *
 */

public class JSONResponseHandlerDemos {

    private static final String TAG = JSONResponseHandlerDemos.class.getSimpleName();

    private Item item;

    public JSONResponseHandlerDemos() {
    }

    /**
     * @param response done by the Web service
     * @return items with demos' attribute filled with the collected information if response was
     * successfully analyzed
     */
    public void readJsonStreamDemos(InputStream response) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(response, "UTF-8"));
        try {
            readDemos(reader);
        } finally {
            reader.close();
        }
    }

    private void readDemos(JsonReader reader) throws IOException {

        reader.beginObject();
        while (reader.hasNext()) {
            String ids = reader.nextName();
            if (ids != null) {
                item = new Item(ids);
                item.setDemos(reader.nextString());
                dbHelper.updateItemDemos(item);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }
}