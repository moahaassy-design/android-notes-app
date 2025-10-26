package com.notes.android

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class NoteAdapter(
    private val notes: MutableList<Note>,
    private val onNoteClick: (Note) -> Unit,
    private val onNoteDelete: (Note) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: MaterialCardView = itemView.findViewById(R.id.cardView)
        val titleText: TextView = itemView.findViewById(R.id.textTitle)
        val contentText: TextView = itemView.findViewById(R.id.textContent)
        val dateText: TextView = itemView.findViewById(R.id.textDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.titleText.text = note.title
        holder.contentText.text = note.content
        holder.dateText.text = note.getFormattedCreatedDate()

        holder.cardView.setOnClickListener {
            onNoteClick(note)
        }
    }

    override fun getItemCount(): Int = notes.size

    fun removeAt(position: Int) {
        if (position >= 0 && position < notes.size) {
            notes.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}