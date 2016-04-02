package karan.com.popularmovies1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Karan on 02-04-2016.
 */
public class DBHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Movies.db";
    private static final String TABLE_NAME = "Favorites";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_POSTERPATH = "posterPath";
    private static final String COLUMN_ADULT = "adult";
    private static final String COLUMN_OVERVIEW = "overView";
    private static final String COLUMN_RELEASE_DATE = "releaseDate";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_LANGUAGE = "language";
    private static final String COLUMN_POPULARITY = "popularity";
    private static final String COLUMN_VOTE_COUNT = "voteCount";
    private static final String COLUMN_VOTE_AVERAGE = "voteAverage";

    private static final int DB_VERSION=1;


    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY," +
                    COLUMN_POSTERPATH + TEXT_TYPE + COMMA_SEP +
                    COLUMN_ADULT + TEXT_TYPE + COMMA_SEP +
                    COLUMN_OVERVIEW + TEXT_TYPE + COMMA_SEP +
                    COLUMN_RELEASE_DATE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_TITLE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_LANGUAGE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_POPULARITY + TEXT_TYPE + COMMA_SEP +
                    COLUMN_VOTE_COUNT + TEXT_TYPE + COMMA_SEP +
                    COLUMN_VOTE_AVERAGE + TEXT_TYPE +
            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    private HashMap hashMap;

    public DBHandler(Context context)
    {
        super(context, DATABASE_NAME , null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public boolean addFavrotite(MovieUtils movieUtils)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, movieUtils.id);
        contentValues.put(COLUMN_POSTERPATH, movieUtils.posterPath);
        contentValues.put(COLUMN_OVERVIEW, movieUtils.overView);
        contentValues.put(COLUMN_ADULT, movieUtils.adult);
        contentValues.put(COLUMN_POPULARITY, movieUtils.popularity);
        contentValues.put(COLUMN_LANGUAGE, movieUtils.language);
        contentValues.put(COLUMN_RELEASE_DATE, movieUtils.releaseDate);
        contentValues.put(COLUMN_TITLE, movieUtils.title);
        contentValues.put(COLUMN_VOTE_COUNT, movieUtils.voteCount);
        contentValues.put(COLUMN_VOTE_AVERAGE, movieUtils.voteAverage);
        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from " + TABLE_NAME + " where id="+id+"", null);
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return numRows;
    }

    public boolean updateFavorite(MovieUtils movieUtils)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_POSTERPATH, movieUtils.posterPath);
        contentValues.put(COLUMN_OVERVIEW, movieUtils.overView);
        contentValues.put(COLUMN_ADULT, movieUtils.adult);
        contentValues.put(COLUMN_POPULARITY, movieUtils.popularity);
        contentValues.put(COLUMN_LANGUAGE, movieUtils.language);
        contentValues.put(COLUMN_RELEASE_DATE, movieUtils.releaseDate);
        contentValues.put(COLUMN_TITLE, movieUtils.title);
        contentValues.put(COLUMN_VOTE_COUNT, movieUtils.voteCount);
        contentValues.put(COLUMN_VOTE_AVERAGE, movieUtils.voteAverage);
        db.update(TABLE_NAME, contentValues, "id = ? ", new String[] { movieUtils.id } );
        return true;
    }

    public Integer deleteFavorite(Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<MovieUtils> getAllFavorites()
    {
        ArrayList<MovieUtils> array_list = new ArrayList<MovieUtils>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + TABLE_NAME, null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            MovieUtils movieUtils = new MovieUtils();
            movieUtils.id = res.getString(res.getColumnIndex(COLUMN_ID));
            movieUtils.popularity = res.getString(res.getColumnIndex(COLUMN_POPULARITY));
            movieUtils.posterPath = res.getString(res.getColumnIndex(COLUMN_POSTERPATH));
            movieUtils.overView = res.getString(res.getColumnIndex(COLUMN_OVERVIEW));
            movieUtils.adult = res.getString(res.getColumnIndex(COLUMN_ADULT));
            movieUtils.title = res.getString(res.getColumnIndex(COLUMN_TITLE));
            movieUtils.language = res.getString(res.getColumnIndex(COLUMN_LANGUAGE));
            movieUtils.voteCount = res.getString(res.getColumnIndex(COLUMN_VOTE_COUNT));
            movieUtils.voteAverage = res.getString(res.getColumnIndex(COLUMN_VOTE_AVERAGE));
            array_list.add(movieUtils);
            res.moveToNext();
        }
        return array_list;
    }
}