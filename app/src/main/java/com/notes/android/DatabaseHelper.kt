package com.notes.android

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "NotesDB"
        private const val DATABASE_VERSION = 1

        private const val TABLE_NOTES = "notes"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_CONTENT = "content"
        private const val COLUMN_CREATED_AT = "created_at"
        private const val COLUMN_UPDATED_AT = "updated_at"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NOTES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_CONTENT TEXT,
                $COLUMN_CREATED_AT INTEGER NOT NULL,
                $COLUMN_UPDATED_AT INTEGER NOT NULL
            )
        """
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTES")
        onCreate(db)
    }

    fun addNote(note: Note): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, note.title)
            put(COLUMN_CONTENT, note.content)
            put(COLUMN_CREATED_AT, note.createdAt)
            put(COLUMN_UPDATED_AT, System.currentTimeMillis())
        }
        return db.insert(TABLE_NOTES, null, values)
    }

    fun updateNote(note: Note): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, note.title)
            put(COLUMN_CONTENT, note.content)
            put(COLUMN_UPDATED_AT, System.currentTimeMillis())
        }
        return db.update(
            TABLE_NOTES,
            values,
            "$COLUMN_ID = ?",
            arrayOf(note.id.toString())
        )
    }

    fun deleteNote(id: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_NOTES, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun getAllNotes(): MutableList<Note> {
        val notes = mutableListOf<Note>()
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM $TABLE_NOTES ORDER BY $COLUMN_UPDATED_AT DESC",
            null
        )

        if (cursor.moveToFirst()) {
            do {
                notes.add(cursorToNote(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return notes
    }

    fun getNote(id: Long): Note? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NOTES,
            null,
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            val note = cursorToNote(cursor)
            cursor.close()
            return note
        }
        cursor.close()
        return null
    }

    fun searchNotes(query: String): MutableList<Note> {
        val notes = mutableListOf<Note>()
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM $TABLE_NOTES WHERE $COLUMN_TITLE LIKE ? OR $COLUMN_CONTENT LIKE ? ORDER BY $COLUMN_UPDATED_AT DESC",
            arrayOf("%$query%", "%$query%")
        )

        if (cursor.moveToFirst()) {
            do {
                notes.add(cursorToNote(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return notes
    }

    private fun cursorToNote(cursor: Cursor): Note {
        return Note(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
            title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
            content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)),
            createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)),
            updatedAt = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_UPDATED_AT))
        )
    }
}