package com.notes.android

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddEditNoteActivity : AppCompatActivity() {
    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var databaseHelper: DatabaseHelper
    private var noteId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_note)

        setupViews()
        setupActionBar()
        databaseHelper = DatabaseHelper(this)
        
        noteId = intent.getLongExtra("note_id", -1)
        if (noteId != -1L) {
            loadNoteForEditing()
        }
    }

    private fun setupViews() {
        titleEditText = findViewById(R.id.editTextTitle)
        contentEditText = findViewById(R.id.editTextContent)
    }

    private fun setupActionBar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = if (noteId == -1L) "Tambah Catatan" else "Edit Catatan"
        }
    }

    private fun loadNoteForEditing() {
        val note = databaseHelper.getNote(noteId)
        if (note != null) {
            titleEditText.setText(note.title)
            contentEditText.setText(note.content)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add_edit_note, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_save -> {
                saveNote()
                true
            }
            R.id.action_delete -> {
                if (noteId != -1L) {
                    deleteNote()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveNote() {
        val title = titleEditText.text.toString().trim()
        val content = contentEditText.text.toString().trim()

        if (title.isEmpty()) {
            Toast.makeText(this, "Judul tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val note = Note(
            id = if (noteId == -1L) 0 else noteId,
            title = title,
            content = content
        )

        if (noteId == -1L) {
            databaseHelper.addNote(note)
            Toast.makeText(this, "Catatan berhasil ditambahkan", Toast.LENGTH_SHORT).show()
        } else {
            databaseHelper.updateNote(note)
            Toast.makeText(this, "Catatan berhasil diperbarui", Toast.LENGTH_SHORT).show()
        }

        finish()
    }

    private fun deleteNote() {
        databaseHelper.deleteNote(noteId)
        Toast.makeText(this, "Catatan berhasil dihapus", Toast.LENGTH_SHORT).show()
        finish()
    }
}