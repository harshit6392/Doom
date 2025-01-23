package com.doom.app.ui.note

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.doom.app.NoteDao
import com.doom.app.R
import com.doom.app.databinding.ActivityNoteBinding
import com.doom.app.model.Note
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoteBinding

    @Inject
    lateinit var noteDao: NoteDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.md_theme_primary)
        binding.toolbar.tvTitle.text = getString(R.string.edit)
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }
        binding.toolbar.ivSearchToolbar.setImageResource(R.drawable.ic_save)
        val noteId = intent.getIntExtra("noteId", -1)
        if (noteId != -1) {
            lifecycleScope.launch(Dispatchers.IO) {
                val note = noteDao.getNoteById(noteId)
                runOnUiThread {
                    binding.noteTitle.setText(note.title)
                    binding.noteBody.setText(note.body)
                }
            }
        }
        binding.toolbar.ivSearchToolbar.setOnClickListener {
            saveOrUpdateNote(noteId)
        }
    }


    private fun saveOrUpdateNote(noteId: Int) {
        val title = binding.noteTitle.text.toString()
        val body = binding.noteBody.text.toString()

        if (title.isEmpty() && body.isEmpty()) {
            Toast.makeText(this, "Note is empty", Toast.LENGTH_SHORT).show()
            return
        }

        val note = if (noteId != -1) {
            Note(id = noteId, title = title, body = body)
        } else {
            Note(title = title, body = body)
        }

        lifecycleScope.launch(Dispatchers.IO) {
            if (noteId == -1) {
                noteDao.insert(note)
            } else {
                noteDao.update(note)
            }

            runOnUiThread {
                Toast.makeText(this@NoteActivity, "Note saved", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
