package com.example.projet;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

public class WebServiceUrl {

    private static final String HOST = "demo-lia.univ-avignon.fr";
    private static final String PATH_1 = "cerimuseum";

    private static Uri.Builder commonBuilder() {
        Uri.Builder builder = new Uri.Builder();

        builder.scheme("https")
                .authority(HOST)
                .appendPath(PATH_1);
        return builder;
    }

    // Get the collection's catalog
    // https://demo-lia.univ-avignon.fr/cerimuseum/catalog
    private static final String SEARCH_CATALOG = "catalog";

    public static URL buildSearchCatalog() throws MalformedURLException {
        Uri.Builder builder = commonBuilder();
        builder.appendPath(SEARCH_CATALOG);
        URL url = new URL(builder.build().toString());
        return url;
    }

    // Get the collection's demos
    // https://demo-lia.univ-avignon.fr/cerimuseum/demos
    private static final String SEARCH_DEMOS = "demos";

    public static URL buildSearchDemos() throws MalformedURLException {
        Uri.Builder builder = commonBuilder();
        builder.appendPath(SEARCH_DEMOS);
        URL url = new URL(builder.build().toString());
        return url;
    }

}
