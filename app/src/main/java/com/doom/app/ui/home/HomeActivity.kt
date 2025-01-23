package com.doom.app.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.doom.app.NoteDao
import com.doom.app.R
import com.doom.app.adapter.NotesAdapter
import com.doom.app.databinding.ActivityHomeBinding
import com.doom.app.ui.note.NoteActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityHomeBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var noteDao: NoteDao
    private lateinit var noteAdapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.md_theme_primary)
        setContentView(binding.root)
        noteAdapter = NotesAdapter(
            listOf(),
            onEditNote = { note ->
                val intent = Intent(this, NoteActivity::class.java)
                intent.putExtra("noteId", note.id)
                startActivity(intent)
            },
            onDeleteNote = { note ->
                lifecycleScope.launch(Dispatchers.IO) {
                    noteDao.delete(note)
                    val updatedNotes = noteDao.getAllNotes()
                    runOnUiThread {
                        noteAdapter.updateNotes(updatedNotes)
                    }
                }
            }
        )
        binding.recyclerView.adapter = noteAdapter
        binding.toolbar.tvTitle.text = getString(R.string.app_name)
        binding.addNoteFab.setOnClickListener {
            startActivity(Intent(this, NoteActivity::class.java))
        }
        loadNotes()
    }

    private fun loadNotes() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val notes = noteDao.getAllNotes()
                runOnUiThread {
                    noteAdapter.updateNotes(notes)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadNotes()
    }
}
