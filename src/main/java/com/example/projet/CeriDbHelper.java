package com.example.projet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE;

public class CeriDbHelper extends SQLiteOpenHelper {

    private static final String TAG = CeriDbHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "ceri.db";

    public static final String ITEMS_TABLE_NAME = "items";
    public static final String CATEGORIES_TABLE_NAME = "categories";
    public static final String TIMEFRAME_TABLE_NAME = "timeframe";
    public static final String DETAILS_TABLE_NAME = "details";
    public static final String PICTURES_TABLE_NAME = "pictures";

    public static final String _ID = "_id";
    public static final String COLUMN_ITEM_NAME = "name";
    public static final String COLUMN_ITEM_ID = "idItem";
    public static final String COLUMN_CATEGORIE = "categorie";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_TIME_FRAME = "timeFrame";
    public static final String COLUMN_YEAR = "year";
    public static final String COLUMN_BRAND = "brand";
    public static final String COLUMN_TECHNICAL_DETAILS = "technicalDetails";
    public static final String COLUMN_WORKING = "working";
    public static final String COLUMN_PICTURES_ID = "idPic";
    public static final String COLUMN_CAPTION = "caption";
    public static final String COLUMN_DEMOS = "demos";

    public CeriDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_CERI_TABLE = "CREATE TABLE " + ITEMS_TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_ITEM_NAME + " TEXT, " +
                COLUMN_ITEM_ID + " TEXT NOT NULL, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_YEAR + " INTEGER, " +
                COLUMN_BRAND + " TEXT, " +
                COLUMN_WORKING + " BOOLEAN, " +
                COLUMN_DEMOS + " TEXT, " +
                "UNIQUE (" + COLUMN_ITEM_ID + ") ON CONFLICT ROLLBACK);";
        db.execSQL(SQL_CREATE_CERI_TABLE);

        final String SQL_CREATE_CATEGORIES_TABLE = "CREATE TABLE " + CATEGORIES_TABLE_NAME + " (" +
                COLUMN_ITEM_ID + " TEXT NOT NULL, " +
                COLUMN_CATEGORIE + " TEXT);";
        db.execSQL(SQL_CREATE_CATEGORIES_TABLE);

        final String SQL_CREATE_TIMEFRAME_TABLE = "CREATE TABLE " + TIMEFRAME_TABLE_NAME + " (" +
                COLUMN_ITEM_ID + " TEXT NOT NULL, " +
                COLUMN_TIME_FRAME + " INTEGER);";
        db.execSQL(SQL_CREATE_TIMEFRAME_TABLE);

        final String SQL_CREATE_DETAILS_TABLE = "CREATE TABLE " + DETAILS_TABLE_NAME + " (" +
                COLUMN_ITEM_ID + " TEXT NOT NULL, " +
                COLUMN_TECHNICAL_DETAILS + " TEXT);";
        db.execSQL(SQL_CREATE_DETAILS_TABLE);

        final String SQL_CREATE_PICTURES_TABLE = "CREATE TABLE " + PICTURES_TABLE_NAME + " (" +
                COLUMN_ITEM_ID + " TEXT NOT NULL, " +
                COLUMN_PICTURES_ID + " TEXT NOT NULL, " +
                COLUMN_CAPTION + " TEXT);";
        db.execSQL(SQL_CREATE_PICTURES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + ITEMS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CATEGORIES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TIMEFRAME_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DETAILS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PICTURES_TABLE_NAME);
        onCreate(db);
    }

    /**
     * Fills ContentValues result from item
     */
    private ContentValues fill(Item item) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_NAME, item.getName());
        values.put(COLUMN_ITEM_ID, item.getIdItem());
        values.put(COLUMN_DESCRIPTION, item.getDescription());
        values.put(COLUMN_YEAR, item.getYear());
        values.put(COLUMN_BRAND, item.getBrand());
        values.put(COLUMN_WORKING, item.isWorking());
        values.put(COLUMN_DEMOS, item.getDemos());
        return values;
    }

    /**
     * Adds a new item
     * @return  true if the item was added to the table ; false otherwise
     */
    public boolean addItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values1 = fill(item);
        Log.d(TAG, "adding item's id: "+ item.getIdItem());

        // Inserting Row on items'table
        // The unique used for creating table ensures to have only one copy of each idItem
        long rowID1 = db.insertWithOnConflict(ITEMS_TABLE_NAME, null, values1, CONFLICT_IGNORE);

        // Inserting Rows on categories' table
        long rowID2 = -1;
        String[] categories = item.getCategories();
        ContentValues values2 = new ContentValues();
        for (int i=0; i<categories.length; i++) {
            if (categories[i] != null) {
                values2.put(COLUMN_ITEM_ID, item.getIdItem());
                values2.put(COLUMN_CATEGORIE, categories[i]);
                rowID2 = db.insert(CATEGORIES_TABLE_NAME, null, values2);
            }
        }

        // Inserting Rows on timeframe's table
        long rowID3 = -1;
        int[] timeFrame = item.getTimeFrame();
        ContentValues values3 = new ContentValues();
        for (int i=0; i<timeFrame.length; i++) {
            if (timeFrame[i] != 0) {
                values3.put(COLUMN_ITEM_ID, item.getIdItem());
                values3.put(COLUMN_TIME_FRAME, timeFrame[i]);
                rowID3 = db.insert(TIMEFRAME_TABLE_NAME, null, values3);
            }
        }

        // Inserting Rows on details' table
        long rowID4 = -1;
        String[] technicalDetails = item.getTechnicalDetails();
        if (technicalDetails != null) {
            ContentValues values4 = new ContentValues();
            for (int i = 0; i < technicalDetails.length; i++) {
                if (technicalDetails[i] != null) {
                    values4.put(COLUMN_ITEM_ID, item.getIdItem());
                    values4.put(COLUMN_TECHNICAL_DETAILS, technicalDetails[i]);
                    rowID4 = db.insert(DETAILS_TABLE_NAME, null, values4);
                }
            }
        }

        // Inserting Rows on pictures' table
        long rowID5 = -1;
        String[][] pictures = item.getPictures();
        if(pictures!= null) {
            ContentValues values5 = new ContentValues();
            for (int i = 0; i < pictures.length; i++) {
                if (pictures[i][0] != null) {
                    values5.put(COLUMN_ITEM_ID, item.getIdItem());
                    values5.put(COLUMN_PICTURES_ID, pictures[i][0]);
                    values5.put(COLUMN_CAPTION, pictures[i][1]);
                    rowID5 = db.insert(PICTURES_TABLE_NAME, null, values5);
                }
            }
        }

        db.close();

        // If rowIDX = -1, an error occured
        return (rowID1 != -1 && rowID2 != -1 && rowID3 != -1 && rowID4 != -1 && rowID5 != -1);
    }

    /**
     * Updates the information of an item inside the data base
     * @return the number of updated rows
     */
    public boolean updateItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = fill(item);

        long rowID = db.updateWithOnConflict(ITEMS_TABLE_NAME, values, _ID + " = ?",
                new String[] { String.valueOf(item.getId()) }, CONFLICT_IGNORE);

        db.close();
        return (rowID != -1);
    }

    /**
     * Updates the demos attribute of an item inside the data base
     * @return the number of updated rows
     */
    public boolean updateItemDemos(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_DEMOS, item.getDemos());

        long rowID = db.updateWithOnConflict(ITEMS_TABLE_NAME, values, COLUMN_ITEM_ID + " = ?",
                new String[] { String.valueOf(item.getIdItem()) }, CONFLICT_IGNORE);

        db.close();
        return (rowID != -1);
    }

    /**
     * Returns a cursor on all the items of the data base
     */
    public Cursor fetchAllItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(ITEMS_TABLE_NAME, null, null, null, null, null, null, null);
        Log.d(TAG, "call fetchAllItems()");
        if (cursor != null) {
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    /**
     * Returns a list on all the items of the data base sorted by letter
     */
    public List<Item> getItemsSortedByLetter() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(ITEMS_TABLE_NAME, null, null, null, null, null, COLUMN_ITEM_NAME +" ASC", null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        db.close();

        List<Item> res = new ArrayList<>();

        int cpt = 0;
        do {
            Item item = cursorToItem(cursor);
            res.add(item);
            cpt++;
        } while(cursor.moveToNext());

        return res;
    }

    /**
     * Returns a list on all the items of the data base sorted by date
     */
    public List<Item> getItemsSortedByDate() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT* FROM " + ITEMS_TABLE_NAME + " NATURAL JOIN " + TIMEFRAME_TABLE_NAME + " GROUP BY " + COLUMN_ITEM_ID + " ORDER BY " + COLUMN_TIME_FRAME + " ASC", null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        db.close();

        List<Item> res = new ArrayList<>();

        int cpt = 0;
        do {
            Item item = cursorToItem(cursor);
            res.add(item);
            cpt++;
        } while(cursor.moveToNext());

        return res;
    }

    /**
     * Returns a list on all the items of the data base
     */
    public List<Item> getAllItems() {

        List<Item> res = new ArrayList<>();
        Cursor cursor = fetchAllItems();

        int cpt = 0;
        do {
            Item item = cursorToItem(cursor);
            res.add(item);
            cpt++;
        } while(cursor.moveToNext());

        return res;
    }

    /**
     * Returns the categorie attribute of an item of the data base
     */
    public String[] getItemCat(String idItem){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor catCursor = db.rawQuery("SELECT* FROM " + CATEGORIES_TABLE_NAME + " WHERE " + COLUMN_ITEM_ID + " = '" + idItem + "'", null);
        if (catCursor != null) catCursor.moveToFirst();

        String[] categories = new String[catCursor.getCount()];
        int i = 0;
        do {
            categories[i] = catCursor.getString(catCursor.getColumnIndex(COLUMN_CATEGORIE));
            i++;
        } while(catCursor.moveToNext());

        db.close();
        return categories;
    }

    /**
     * Returns the time frame attribute of an item of the data base
     */
    public int[] getItemTime(String idItem){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor timeCursor = db.rawQuery("SELECT* FROM " + TIMEFRAME_TABLE_NAME + " WHERE " + COLUMN_ITEM_ID + " = '" + idItem + "'", null);
        if (timeCursor != null) timeCursor.moveToFirst();

        int[] timeFrame = new int[timeCursor.getCount()];
        int i = 0;
        do {
            timeFrame[i] = timeCursor.getInt(timeCursor.getColumnIndex(COLUMN_TIME_FRAME));
            i++;
        } while(timeCursor.moveToNext());

        db.close();
        return timeFrame;
    }

    /**
     * Returns the technical details attribute of an item of the data base
     */
    public String[] getItemDetails(String idItem){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor detailsCursor = db.rawQuery("SELECT* FROM " + DETAILS_TABLE_NAME + " WHERE " + COLUMN_ITEM_ID + " = '" + idItem + "'", null);
        if (detailsCursor != null) detailsCursor.moveToFirst();

        String[] technicalDetails = null;
        if (detailsCursor.getCount() != 0) {
            technicalDetails = new String[detailsCursor.getCount()];
            int i = 0;
            do {
                technicalDetails[i] = detailsCursor.getString(detailsCursor.getColumnIndex(COLUMN_TECHNICAL_DETAILS));
                i++;
            } while (detailsCursor.moveToNext());
        }

        db.close();
        return technicalDetails;
    }

    /**
     * Returns the pictures attribute of an item of the data base
     */
    public String[][] getItemPic(String idItem) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor picCursor = db.rawQuery("SELECT* FROM " + PICTURES_TABLE_NAME + " WHERE " + COLUMN_ITEM_ID + " = '" + idItem + "'", null);
        if (picCursor != null) picCursor.moveToFirst();

        String[][] pics = null;
        if (picCursor.getCount() != 0) {
            pics = new String[picCursor.getCount()][2];
            int i = 0;
            do {
                String idPic = picCursor.getString(picCursor.getColumnIndex(COLUMN_PICTURES_ID));
                String caption = picCursor.getString(picCursor.getColumnIndex(COLUMN_CAPTION));
                pics[i][0] = idPic;
                pics[i][1] = caption;
                i++;
            } while (picCursor.moveToNext());
        }

        db.close();
        return pics;
    }

    public Item cursorToItem(Cursor itemCursor) {

        SQLiteDatabase db = this.getReadableDatabase();
        String idItem = itemCursor.getString(itemCursor.getColumnIndex(COLUMN_ITEM_ID));

        String[] categories = getItemCat(idItem);
        int[] timeFrame = getItemTime(idItem);
        String[] technicalDetails = getItemDetails(idItem);
        String[][] pictures = getItemPic(idItem);

        Item item = new Item(itemCursor.getLong(itemCursor.getColumnIndex(_ID)),
                itemCursor.getString(itemCursor.getColumnIndex(COLUMN_ITEM_NAME)),
                idItem,
                categories,
                itemCursor.getString(itemCursor.getColumnIndex(COLUMN_DESCRIPTION)),
                timeFrame,
                itemCursor.getInt(itemCursor.getColumnIndex(COLUMN_YEAR)),
                itemCursor.getString(itemCursor.getColumnIndex(COLUMN_BRAND)),
                technicalDetails,
                itemCursor.getInt(itemCursor.getColumnIndex(COLUMN_WORKING)) == 1,
                pictures,
                itemCursor.getString(itemCursor.getColumnIndex(COLUMN_DEMOS))
        );

        db.close();
        return item;
    }

    public Item getItem(long id) {

        Item item;
        List<Item> items = getAllItems();

        for(int i = 0; i< items.size(); i++) {
            item = items.get(i);
            if (item.getId() == id) return item;
        }

	    return null;
    }
}
