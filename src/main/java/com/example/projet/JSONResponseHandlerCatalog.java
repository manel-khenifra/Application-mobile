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


public class JSONResponseHandlerCatalog {

    private static final String TAG = JSONResponseHandlerCatalog.class.getSimpleName();

    private Item item;

    public JSONResponseHandlerCatalog() { }

    /**
     * @param response done by the Web service
     * @return items with attributes filled with the collected information if response was
     * successfully analyzed
     */
    public void readJsonStreamCatalog(InputStream response) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(response, "UTF-8"));
        try {
            readCatalog(reader);
        } finally {
            reader.close();
        }
    }

    public void readCatalog(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String ids = reader.nextName();
            if (ids != null) {
                item = new Item(ids);
                readArrayCatalog(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    private void readArrayCatalog(JsonReader reader) throws IOException {

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("name")) {
                item.setName(reader.nextString());
            } else if (name.equals("categories")) {

                String[] categories = new String[10];
                int i = 0;
                reader.beginArray();
                while (reader.hasNext()){
                    categories[i] = reader.nextString();
                    i++;
                }
                reader.endArray();
                item.setCategories(categories);

            } else if (name.equals("description")) {
                item.setDescription(reader.nextString());
            } else if (name.equals("timeFrame")) {

                int[] timeFrame = new int[10];
                int i = 0;
                reader.beginArray();
                while (reader.hasNext()){
                    timeFrame[i] = reader.nextInt();
                    i++;
                }
                reader.endArray();
                item.setTimeFrame(timeFrame);

            } else if (name.equals("year")) {
                item.setYear(reader.nextInt());
            } else if (name.equals("brand")) {
                item.setBrand(reader.nextString());
            } else if (name.equals("technicalDetails")) {

                String[] technicalDetails = new String[10];
                int i = 0;
                reader.beginArray();
                while (reader.hasNext()){
                    technicalDetails[i] = reader.nextString();
                    i++;
                }
                reader.endArray();
                item.setTechnicalDetails(technicalDetails);

            } else if (name.equals("working")) {
                item.setWorking(reader.nextBoolean());

            } else if (name.equals("pictures")) {

                String[][] pics = new String[20][2];
                reader.beginObject();
                int i = 0;
                while (reader.hasNext()) {
                    pics[i][0] = reader.nextName();
                    pics[i][1] = reader.nextString();
                    i++;
                }
                reader.endObject();
                item.setPictures(pics);
            }
            else {
                reader.skipValue();
            }
        }
        reader.endObject();
        dbHelper.addItem(item);
    }
}
