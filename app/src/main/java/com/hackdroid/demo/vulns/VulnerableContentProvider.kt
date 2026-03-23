package com.hackdroid.demo.vulns

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.util.Log

/**
 * INTENTIONALLY VULNERABLE: ContentProvider with SQL injection via raw string concatenation.
 *
 * HOW TO HACK:
 *   adb shell content query \
 *     --uri content://com.hackdroid.demo.provider/users \
 *     --where "name='x' OR '1'='1'"
 *
 * WHAT HAPPENS:
 *   The `selection` parameter is directly concatenated into a SQL query.
 *   The classic '1'='1' tautology dumps the entire users table including tokens.
 *
 * OWASP M4: Insufficient Input/Output Validation
 * OWASP M9: Insecure Data Storage
 */
class VulnerableContentProvider : ContentProvider() {

    private lateinit var db: SQLiteDatabase

    companion object {
        private const val AUTHORITY = "com.hackdroid.demo.provider"
        private const val TABLE     = "users"

        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$TABLE")

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, TABLE, 1)
        }
    }

    override fun onCreate(): Boolean {
        val ctx = context ?: return false

        // Create and seed demo database
        db = ctx.openOrCreateDatabase("hackdroid.db", android.content.Context.MODE_PRIVATE, null)

        db.execSQL(
            """CREATE TABLE IF NOT EXISTS $TABLE (
                id    INTEGER PRIMARY KEY,
                name  TEXT,
                email TEXT,
                token TEXT
            )"""
        )

        // Seed demo users
        db.execSQL("INSERT OR IGNORE INTO $TABLE VALUES (1, 'alice', 'alice@example.com', 'tok_alice_secret')")
        db.execSQL("INSERT OR IGNORE INTO $TABLE VALUES (2, 'bob',   'bob@example.com',   'tok_bob_secret')")
        db.execSQL("INSERT OR IGNORE INTO $TABLE VALUES (3, 'admin', 'admin@hackdroid.io', 'tok_admin_SUPER_SECRET')")

        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor {
        Log.d("HackDroid_VULN", "ContentProvider.query() called with selection: $selection")

        return if (selection != null) {
            // INTENTIONALLY VULNERABLE: Raw string concatenation — classic SQL injection vector
            // A safe implementation would use: db.query(TABLE, projection, selection, selectionArgs, null, null, sortOrder)
            val rawQuery = "SELECT * FROM $TABLE WHERE $selection"
            Log.d("HackDroid_LEAK", "Executing raw query: $rawQuery")
            db.rawQuery(rawQuery, null)
        } else {
            db.query(TABLE, projection, null, null, null, null, sortOrder)
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val id = db.insert(TABLE, null, values)
        return Uri.parse("$CONTENT_URI/$id")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int = db.update(TABLE, values, selection, selectionArgs)

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int = db.delete(TABLE, selection, selectionArgs)

    override fun getType(uri: Uri): String =
        "vnd.android.cursor.dir/vnd.$AUTHORITY.$TABLE"
}
