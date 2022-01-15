package com.example.projet;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    static CeriDbHelper dbHelper;
    ItemAdapter adapter;
    RecyclerView recyclerview;
    List<Item> liste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        dbHelper = new CeriDbHelper(this);

        if (dbHelper.fetchAllItems().getCount() < 1) {
            AsyncTaskGettingAllItems asyncTaskItems = new AsyncTaskGettingAllItems();
            asyncTaskItems.execute();
            try {
                asyncTaskItems.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        liste = dbHelper.getAllItems();
        adapter = new ItemAdapter(liste);

        recyclerview.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerview.addItemDecoration(new DividerItemDecoration(MainActivity.this, LinearLayoutManager.VERTICAL));
        recyclerview.setAdapter(adapter);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        return true;
    }

    public boolean doMySearch(String query) {

        if (adapter != null){
            adapter.getFilter().filter(query);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.lettre) {
            List<Item> sortedItems1 = dbHelper.getItemsSortedByLetter();
            adapter.setFilter(sortedItems1);
            return true;
        }
        if (id == R.id.date) {
            List<Item> sortedItems2 = dbHelper.getItemsSortedByDate();
            adapter.setFilter(sortedItems2);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class AsyncTaskGettingAllItems extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {

            URL url1;
            JSONResponseHandlerCatalog JSONresp1 = new JSONResponseHandlerCatalog();
            try {
                url1 = WebServiceUrl.buildSearchCatalog();
                JSONresp1.readJsonStreamCatalog(url1.openConnection().getInputStream());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            URL url2;
            JSONResponseHandlerDemos JSONresp2 = new JSONResponseHandlerDemos();
            try {
                url2 = WebServiceUrl.buildSearchDemos();
                JSONresp2.readJsonStreamDemos(url2.openConnection().getInputStream());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    class ItemAdapter extends RecyclerView.Adapter<RowHolder> implements Filterable {

        List<Item> items;
        List<Item> itemsListFiltered;
        Filter filter;

        public ItemAdapter(List<Item> items) {
            this.items = items;
        }

        @Override
        public RowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
            return new RowHolder(view);
        }

        @Override
        public void onBindViewHolder(RowHolder holder, int position) {
            Item Item = items.get(position);
            holder.bind(Item);
        }

        public void setFilter(List<Item> items) {
            this.items = new ArrayList<>();
            this.items.addAll(items);
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        @Override
        public Filter getFilter() {
            if (filter == null) {
                filter = new Filter() {
                    @Override
                    protected FilterResults performFiltering(CharSequence constraint) {

                        FilterResults filterResults = new FilterResults();
                        if (constraint == null || constraint.length() == 0) {
                            filterResults.count = items.size();
                            filterResults.values = items;

                        } else {
                            List<Item> results = new ArrayList<>();
                            String searchStr = constraint.toString().toLowerCase();

                            Boolean addingItem = false;
                            for (Item item : items) {
                                if (item.getName().toLowerCase().contains(searchStr)) {
                                    addingItem = true;
                                }
                                if(item.getCategories() != null){
                                    if (Arrays.toString(item.getCategories()).toLowerCase().contains(searchStr)){
                                        addingItem = true;
                                    }
                                }
                                if(!item.getDescription().isEmpty()){
                                    if (item.getDescription().toLowerCase().contains(searchStr)){
                                        addingItem = true;
                                    }
                                }
                                if(item.getTimeFrame() != null){
                                    if (Arrays.toString(item.getTimeFrame()).contains(searchStr)){
                                        addingItem = true;
                                    }
                                }
                                if(item.getYear() != 0){
                                    if (Integer.toString(item.getYear()) == searchStr){
                                        addingItem = true;
                                    }
                                }
                                if(item.getBrand() != null){
                                    if (item.getBrand().toLowerCase().contains(searchStr)){
                                        addingItem = true;
                                    }
                                }
                                if(item.getTechnicalDetails() != null){
                                    if (Arrays.toString(item.getTechnicalDetails()).toLowerCase().contains(searchStr)){
                                        addingItem = true;
                                    }
                                }
                                if (addingItem) results.add(item);
                                addingItem = false;
                            }
                            filterResults.count = results.size();
                            filterResults.values = results;
                        }
                        return filterResults;
                    }

                    @Override
                    protected void publishResults(CharSequence constraint, FilterResults results) {

                        itemsListFiltered = (List<Item>) results.values;
                        adapter.setFilter(itemsListFiltered);
                        notifyDataSetChanged();
                    }
                };
            }
            return filter;
        }
    }

    class RowHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        long id;
        TextView textName;
        TextView textCategories;
        TextView textBrand;
        ImageView image;
        String url;

        RowHolder(View row){
            super(row);
            textName = (TextView)row.findViewById(R.id.name);
            textCategories = (TextView)row.findViewById(R.id.categories);
            textBrand = (TextView)row.findViewById(R.id.brand);
            image = (ImageView)row.findViewById(R.id.icone);
            row.setOnClickListener(this);
        }

        public void bind(Item item){

            id = item.getId();
            textName.setText(item.getName());
            textCategories.setText(Arrays.toString(item.getCategories()));
            textBrand.setText(item.getBrand());

            url = "https://demo-lia.univ-avignon.fr/cerimuseum/items/" + item.getIdItem() + "/thumbnail";
            AsyncTaskGettingBitmapFromUrl asyncTaskImage = new AsyncTaskGettingBitmapFromUrl();
            asyncTaskImage.execute();
        }

        @Override
        public void onClick(View view) {
            Item item = dbHelper.getItem(id);
            Intent myIntent = new Intent(MainActivity.this, ItemActivity.class);
            myIntent.putExtra(Item.TAG, item);
            startActivity(myIntent);
        }

        class AsyncTaskGettingBitmapFromUrl extends AsyncTask {

            @Override
            protected Bitmap doInBackground(Object[] objects) {

                Bitmap bitmap = null;
                URL urlImageBadge;
                try {
                    urlImageBadge = new URL(url);
                    bitmap = BitmapFactory.decodeStream(urlImageBadge.openConnection().getInputStream());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return bitmap;
            }

            @Override
            protected void onPostExecute(Object result){
                Bitmap bitmap = (Bitmap) result;
                image.setImageBitmap(bitmap);
            }
        }
    }
}
