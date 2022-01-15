package com.example.projet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;


public class ItemActivity extends AppCompatActivity {

    private static final String TAG = ItemActivity.class.getSimpleName();

    private TextView textName, textCategories, textDescription, textTimeFrame, textYear, textBrand, textTechnicalDetails, textWorking, textDemos;
    private ImageView image;

    private ImageView image1;
    private TextView caption1;

    private ImageView image2;
    private TextView caption2;

    private ImageView image3;
    private TextView caption3;

    private Bitmap bitmap;

    private Bitmap bitmap1;
    private Bitmap bitmap2;
    private Bitmap bitmap3;

    private Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        textName = (TextView) findViewById(R.id.name);
        textCategories = (TextView) findViewById(R.id.categories);
        textDescription = (TextView) findViewById(R.id.description);
        textTimeFrame = (TextView) findViewById(R.id.timeFrame);
        textYear = (TextView) findViewById(R.id.year);
        textBrand = (TextView) findViewById(R.id.brand);
        textTechnicalDetails = (TextView) findViewById(R.id.technicalDetails);
        textWorking = (TextView) findViewById(R.id.working);
        textDemos = (TextView) findViewById(R.id.demos);

        image = (ImageView) findViewById(R.id.imageView);

        image1 = (ImageView) findViewById(R.id.image1);
        caption1 = (TextView) findViewById(R.id.caption1);
        image2 = (ImageView) findViewById(R.id.image2);
        caption2 = (TextView) findViewById(R.id.caption2);
        image3 = (ImageView) findViewById(R.id.image3);
        caption3 = (TextView) findViewById(R.id.caption3);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(Item.TAG)) {

                item = intent.getParcelableExtra(Item.TAG);
                if (item != null) {
                    updateView();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void updateView() {

        textName.setText(item.getName());
        textCategories.setText(Arrays.toString(item.getCategories()));
        if (!item.getDescription().isEmpty()) textDescription.setText(item.getDescription());
        else textDescription.setText("Inconnu");
        textTimeFrame.setText(Arrays.toString(item.getTimeFrame()));
        if (item.getYear() != 0) textYear.setText(Integer.toString(item.getYear()));
        else textYear.setText("Inconnu");
        if (item.getBrand() != null) textBrand.setText(item.getBrand());
        else textBrand.setText("Inconnu");
        if (item.getTechnicalDetails() != null) textTechnicalDetails.setText(Arrays.toString(item.getTechnicalDetails()));
        else textTechnicalDetails.setText("Inconnu");
        if (item.isWorking()) textWorking.setText("Fonctionnel");
        else textWorking.setText("Non fonctionnel");
        if (item.getDemos() != null) textDemos.setText(item.getDemos());
        else textDemos.setText("Inconnu");

        AsyncTaskGettingBitmapFromUrl asyncTaskImage = new AsyncTaskGettingBitmapFromUrl();
        asyncTaskImage.execute();

    }

    class AsyncTaskGettingBitmapFromUrl extends AsyncTask {

        String pictures[][];

        @Override
        protected Object doInBackground(Object[] objects) {

            URL urlImage;

            URL urlImage1;
            URL urlImage2;
            URL urlImage3;

            pictures = item.getPictures();
            try {
                urlImage = new URL("https://demo-lia.univ-avignon.fr/cerimuseum/items/" + item.getIdItem() + "/thumbnail");
                bitmap = BitmapFactory.decodeStream(urlImage.openConnection().getInputStream());

                if(pictures != null) {

                    urlImage1 = new URL("https://demo-lia.univ-avignon.fr/cerimuseum/items/" + item.getIdItem() + "/images/" + pictures[0][0]);
                    bitmap1 = BitmapFactory.decodeStream(urlImage1.openConnection().getInputStream());

                    if (pictures.length > 1) {
                        urlImage2 = new URL("https://demo-lia.univ-avignon.fr/cerimuseum/items/" + item.getIdItem() + "/images/" + pictures[1][0]);
                        bitmap2 = BitmapFactory.decodeStream(urlImage2.openConnection().getInputStream());
                    }

                    if (pictures.length > 2) {
                        urlImage3 = new URL("https://demo-lia.univ-avignon.fr/cerimuseum/items/" + item.getIdItem() + "/images/" + pictures[2][0]);
                        bitmap3 = BitmapFactory.decodeStream(urlImage3.openConnection().getInputStream());
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object result){

            image.setImageBitmap(bitmap);

            if(pictures != null) {
                image1.setImageBitmap(bitmap1);
                caption1.setText(pictures[0][1]);

                if (pictures.length > 2) {
                    image2.setImageBitmap(bitmap2);
                    caption2.setText(pictures[1][1]);
                }

                if (pictures.length > 2) {
                    image3.setImageBitmap(bitmap3);
                    caption3.setText(pictures[2][1]);
                }
            }
        }
    }
}
