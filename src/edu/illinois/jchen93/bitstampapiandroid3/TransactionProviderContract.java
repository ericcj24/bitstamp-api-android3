package edu.illinois.jchen93.bitstampapiandroid3;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 *
 * Defines constants for accessing the content provider defined in DataProvider. A content provider
 * contract assists in accessing the provider's available content URIs, column names, MIME types,
 * and so forth, without having to know the actual values.
 */
public final class TransactionProviderContract implements BaseColumns {

    private TransactionProviderContract() { }
        
        // The URI scheme used for content URIs
        public static final String SCHEME = "content";

        // The provider's authority
        public static final String AUTHORITY = "edu.illinois.jchen93.bitstampapiandroid3";

        /**
         * The DataProvider content URI
         */
        public static final Uri CONTENT_URI = Uri.parse(SCHEME + "://" + AUTHORITY);
        
        /**
         * Transaction table name
         */
        public static final String TRANSACTION_TABLE_NAME = "TransactionData";

        /**
         * Transaction table content URI
         */
        public static final Uri TRANSACTIONURL_TABLE_CONTENTURI =
                Uri.withAppendedPath(CONTENT_URI, TRANSACTION_TABLE_NAME);


        /**
         * Transaction table primary key column name
         */
        public static final String ROW_ID = BaseColumns._ID;
        
        /**
         * Transaction table tid column name
         */
        public static final String TRANSACTION_TID_COLUMN = "Tid";
        
        /**
         * Transaction table date column name
         */
        public static final String TRANSACTION_DATE_COLUMN = "Date";
        
        /**
         * Transaction table price column name
         */
        public static final String TRANSACTION_PRICE_COLUMN = "Price";
        
        /**
         * Transaction table amount column name
         */
        public static final String TRANSACTION_AMOUNT_COLUMN = "Amount";
        
        
        // The content provider database name
        public static final String DATABASE_NAME = "TransactionDataDB";

        // The starting version of the database
        public static final int DATABASE_VERSION = 1;
}
