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
         *  The MIME type for a content URI that would return multiple rows
         *  <P>Type: TEXT</P>
         */
        public static final String MIME_TYPE_ROWS =
                "vnd.android.cursor.dir/vnd.edu.illinois.jchen93.bitstampapiandroid3";

        /**
         * The MIME type for a content URI that would return a single row
         *  <P>Type: TEXT</P>
         *
         */
        public static final String MIME_TYPE_SINGLE_ROW =
                "vnd.android.cursor.item/vnd.edu.illinois.jchen93.bitstampapiandroid3";

        /**
         * Transaction table primary key column name
         */
        public static final String ROW_ID = BaseColumns._ID;
        
        /**
         * Transaction table name
         */
        public static final String TRANSACTIONURL_TABLE_NAME = "TransactionUrlData";

        /**
         * Transaction table content URI
         */
        public static final Uri TRANSACTIONURL_TABLE_CONTENTURI =
                Uri.withAppendedPath(CONTENT_URI, TRANSACTIONURL_TABLE_NAME);

        /**
         * Transaction table tid URL column name
         */
        public static final String TRANSACTION_TID_COLUMN = "TidUrl";
        
        /**
         * Transaction table date column name
         */
        public static final String TRANSACTION_DATE_COLUMN = "DateUrl";
        
        /**
         * Transaction table price URL column name
         */
        public static final String TRANSACTION_PRICE_COLUMN = "PriceUrl";
        
        /**
         * Transaction table amount column name
         */
        public static final String TRANSACTION_AMOUNT_COLUMN = "AmountUrl";
        
        /**
         * Modification date table name
         */
        public static final String DATE_TABLE_NAME = "DateMetadatData";

        /**
         * Content URI for modification date table
         */
        public static final Uri DATE_TABLE_CONTENTURI =
                Uri.withAppendedPath(CONTENT_URI, DATE_TABLE_NAME);

        /**
         * Modification date table date column name
         */
        public static final String DATA_DATE_COLUMN = "CreateDate";
        
        // The content provider database name
        public static final String DATABASE_NAME = "TransactionDataDB";

        // The starting version of the database
        public static final int DATABASE_VERSION = 1;
}
