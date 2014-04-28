package edu.illinois.jchen93.bitstampapiandroid3;

import android.content.ContentProvider;
import android.content.Context;
import android.content.UriMatcher;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.SparseArray;

public class TransactionProvider extends ContentProvider{
	
	private static final String TAG = TransactionProvider.class.getSimpleName();
	
    // Constants for building SQLite tables during initialization
    private static final String TEXT_TYPE = "TEXT";
    private static final String PRIMARY_KEY_TYPE = "INTEGER PRIMARY KEY";
    private static final String INTEGER_TYPE = "INTEGER";
    
    // Defines an SQLite statement that builds the Transaction URL table
    private static final String CREATE_TRANSACTIONURL_TABLE_SQL = "CREATE TABLE" + " " +
            TransactionProviderContract.TRANSACTIONURL_TABLE_NAME + " " +
            "(" + " " +
            TransactionProviderContract.ROW_ID + " " + PRIMARY_KEY_TYPE + " ," +
            TransactionProviderContract.TRANSACTION_TID_COLUMN + " " + TEXT_TYPE + " ," +
            TransactionProviderContract.TRANSACTION_DATE_COLUMN + " " + TEXT_TYPE + " ," +
            TransactionProviderContract.TRANSACTION_PRICE_COLUMN + " " + TEXT_TYPE + " ," +
            TransactionProviderContract.TRANSACTION_AMOUNT_COLUMN + " " + TEXT_TYPE +
            ")";
	
    // Defines an SQLite statement that builds the URL modification date table
    private static final String CREATE_DATE_TABLE_SQL = "CREATE TABLE" + " " +
    		TransactionProviderContract.DATE_TABLE_NAME + " " +
            "(" + " " +
            TransactionProviderContract.ROW_ID + " " + PRIMARY_KEY_TYPE + " ," +
            TransactionProviderContract.DATA_DATE_COLUMN + " " + INTEGER_TYPE +
            ")";
    
    // Defines an helper object for the backing database
    private SQLiteOpenHelper mHelper;

    // Defines a helper object that matches content URIs to table-specific parameters
    private static final UriMatcher sUriMatcher;

    // Stores the MIME types served by this provider
    private static final SparseArray<String> sMimeTypes;
    
    /*
     * Initializes meta-data used by the content provider:
     * - UriMatcher that maps content URIs to codes
     * - MimeType array that returns the custom MIME type of a table
     */
    static {
        
        // Creates an object that associates content URIs with numeric codes
        sUriMatcher = new UriMatcher(0);

        /*
         * Sets up an array that maps content URIs to MIME types, via a mapping between the
         * URIs and an integer code. These are custom MIME types that apply to tables and rows
         * in this particular provider.
         */
        sMimeTypes = new SparseArray<String>();

        // Adds a URI "match" entry that maps picture URL content URIs to a numeric code
        sUriMatcher.addURI(
        		TransactionProviderContract.AUTHORITY,
        		TransactionProviderContract.TRANSACTIONURL_TABLE_NAME,
                TRANSACTION_URL_QUERY);

        // Adds a URI "match" entry that maps modification date content URIs to a numeric code
        sUriMatcher.addURI(
        		TransactionProviderContract.AUTHORITY,
        		TransactionProviderContract.DATE_TABLE_NAME,
        		URL_DATE_QUERY);
        
        // Specifies a custom MIME type for the picture URL table
        sMimeTypes.put(
                TRANSACTION_URL_QUERY,
                "vnd.android.cursor.dir/vnd." +
                TransactionProviderContract.AUTHORITY + "." +
                TransactionProviderContract.TRANSACTIONURL_TABLE_NAME);

        // Specifies the custom MIME type for a single modification date row
        sMimeTypes.put(
                URL_DATE_QUERY,
                "vnd.android.cursor.item/vnd."+
                TransactionProviderContract.AUTHORITY + "." +
                TransactionProviderContract.DATE_TABLE_NAME);
    }
	
    // Closes the SQLite database helper class, to avoid memory leaks
    public void close() {
        mHelper.close();
    }
    
    
    
    /**
     * Defines a helper class that opens the SQLite database for this provider when a request is
     * received. If the database doesn't yet exist, the helper creates it.
     */
    private class TransactionProviderHelper extends SQLiteOpenHelper {
        
    	/**
         * Instantiates a new SQLite database using the supplied database name and version
         *
         * @param context The current context
         */
        TransactionProviderHelper(Context context) {
            super(context,
                    TransactionProviderContract.DATABASE_NAME,
                    null,
                    TransactionProviderContract.DATABASE_VERSION);
        }
        
        /**
         * Executes the queries to drop all of the tables from the database.
         *
         * @param db A handle to the provider's backing database.
         */
        private void dropTables(SQLiteDatabase db) {

            // If the table doesn't exist, don't throw an error
            db.execSQL("DROP TABLE IF EXISTS " + TransactionProviderContract.TRANSACTIONURL_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + TransactionProviderContract.DATE_TABLE_NAME);
        }
	
        /**
         * Does setup of the database. The system automatically invokes this method when
         * SQLiteDatabase.getWriteableDatabase() or SQLiteDatabase.getReadableDatabase() are
         * invoked and no db instance is available.
         *
         * @param db the database instance in which to create the tables.
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            // Creates the tables in the backing database for this provider
            db.execSQL(CREATE_TRANSACTIONURL_TABLE_SQL);
            db.execSQL(CREATE_DATE_TABLE_SQL);

        }
        
        /**
         * Handles upgrading the database from a previous version. Drops the old tables and creates
         * new ones.
         *
         * @param db The database to upgrade
         * @param version1 The old database version
         * @param version2 The new database version
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int version1, int version2) {
            Log.w(TransactionProviderHelper.class.getName(),
                    "Upgrading database from version " + version1 + " to "
                            + version2 + ", which will destroy all the existing data");

            // Drops all the existing tables in the database
            dropTables(db);

            // Invokes the onCreate callback to build new tables
            onCreate(db);
        }
        /**
         * Handles downgrading the database from a new to a previous version. Drops the old tables
         * and creates new ones.
         * @param db The database object to downgrade
         * @param version1 The old database version
         * @param version2 The new database version
         */
        @Override
        public void onDowngrade(SQLiteDatabase db, int version1, int version2) {
            Log.w(TransactionProviderHelper.class.getName(),
                "Downgrading database from version " + version1 + " to "
                        + version2 + ", which will destroy all the existing data");
    
            // Drops all the existing tables in the database
            dropTables(db);
    
            // Invokes the onCreate callback to build new tables
            onCreate(db);
            
        }
    }

        
}
