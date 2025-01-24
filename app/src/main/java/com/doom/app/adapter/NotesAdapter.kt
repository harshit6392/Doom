package com.doom.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.doom.app.databinding.ItemNoteBinding
import com.doom.app.model.Note
class NotesAdapter(
    private var notes: List<Note>,
    private val onEditNote: (Note) -> Unit,
    private val onDeleteNote: (Note) -> Unit
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.binding.noteTitle.text = note.title
        holder.binding.noteBody.text = note.body
        holder.binding.editButton.setOnClickListener {
            onEditNote.invoke(note)
        }
        holder.binding.deleteButton.setOnClickListener {
            onDeleteNote.invoke(note)
        }
    }

    override fun getItemCount(): Int = notes.size

    fun updateNotes(newNotes: List<Note>) {
        notes = newNotes
        notifyDataSetChanged()
    }
}
