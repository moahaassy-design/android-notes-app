package com.notes.android

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.SearchView

class MainActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NoteAdapter
    private lateinit var fab: FloatingActionButton
    private var notesList: MutableList<Note> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        databaseHelper = DatabaseHelper(this)
        setupViews()
        setupRecyclerView()
        loadNotes()
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.recyclerView)
        fab = findViewById(R.id.fab)
        
        fab.setOnClickListener {
            val intent = Intent(this, AddEditNoteActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        adapter = NoteAdapter(notesList, { note ->
            val intent = Intent(this, AddEditNoteActivity::class.java)
            intent.putExtra("note_id", note.id)
            startActivity(intent)
        }, { note ->
            databaseHelper.deleteNote(note.id)
            loadNotes()
            Toast.makeText(this, "Catatan dihapus", Toast.LENGTH_SHORT).show()
        })

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun loadNotes() {
        notesList.clear()
        notesList.addAll(databaseHelper.getAllNotes())
        adapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredNotes = if (newText.isNullOrEmpty()) {
                    databaseHelper.getAllNotes()
                } else {
                    databaseHelper.searchNotes(newText)
                }
                
                notesList.clear()
                notesList.addAll(filteredNotes)
                adapter.notifyDataSetChanged()
                return true
            }
        })
        
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        loadNotes()
    }
}