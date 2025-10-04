package com.example.gearup.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gearup.R
import com.example.gearup.adapters.MoodAdapter
import com.example.gearup.databinding.FragmentMoodBinding
import com.example.gearup.models.MoodEntry
import com.example.gearup.models.MoodType
import com.example.gearup.utils.PreferencesManager

class MoodFragment : Fragment() {
    private var _binding: FragmentMoodBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var moodAdapter: MoodAdapter
    private var moodEntries = mutableListOf<MoodEntry>()
    
    private var selectedMood: MoodType? = null
    private val moodButtons = mutableListOf<Button>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        preferencesManager = PreferencesManager(requireContext())
        setupMoodJournal()
        loadMoodEntries()
    }

    private fun setupMoodJournal() {
        // Setup emoji buttons
        setupEmojiButtons()
        
        // Setup RecyclerView
        moodAdapter = MoodAdapter(
            moodEntries = moodEntries,
            onMoodClick = { moodEntry -> showMoodDetails(moodEntry) }
        )
        
        binding.rvMoodHistory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = moodAdapter
        }

        // Setup save button
        binding.btnSaveMood.setOnClickListener {
            saveMoodEntry()
        }

        // Setup share button
        binding.btnShareMood.setOnClickListener {
            shareMoodSummary()
        }
        
        // Check if user already logged mood today
        checkTodaysMood()
    }

    private fun setupEmojiButtons() {
        moodButtons.addAll(listOf(
            binding.btnVeryHappy,
            binding.btnHappy,
            binding.btnExcited,
            binding.btnNeutral,
            binding.btnTired,
            binding.btnSad,
            binding.btnAngry
        ))
        
        val moods = MoodType.getAllMoods()
        
        moodButtons.forEachIndexed { index, button ->
            val mood = moods[index]
            button.text = mood.emoji
            button.setOnClickListener {
                selectMood(mood)
            }
        }
    }

    private fun selectMood(mood: MoodType) {
        selectedMood = mood
        updateMoodSelection()
    }

    private fun updateMoodSelection() {
        val moods = MoodType.getAllMoods()
        
        moodButtons.forEachIndexed { index, button ->
            val isSelected = moods[index] == selectedMood
            button.setBackgroundColor(
                if (isSelected) {
                    resources.getColor(R.color.primary_green, null)
                } else {
                    resources.getColor(android.R.color.transparent, null)
                }
            )
        }
        
        // Enable save button if mood is selected
        binding.btnSaveMood.isEnabled = selectedMood != null
        binding.btnSaveMood.alpha = if (selectedMood != null) 1.0f else 0.5f
    }

    private fun checkTodaysMood() {
        val todaysMood = preferencesManager.getTodaysMoodEntry()
        if (todaysMood != null) {
            // User already logged mood today
            binding.btnSaveMood.text = "Update Today's Mood"
            
            // Pre-select the mood
            val moodType = MoodType.fromEmoji(todaysMood.emoji)
            if (moodType != null) {
                selectedMood = moodType
                updateMoodSelection()
            }
            
            // Pre-fill the note
            binding.etMoodNote.setText(todaysMood.note)
        } else {
            binding.btnSaveMood.text = getString(R.string.save_mood)
        }
    }

    private fun saveMoodEntry() {
        val mood = selectedMood
        if (mood == null) {
            Toast.makeText(context, "Please select a mood", Toast.LENGTH_SHORT).show()
            return
        }
        
        val note = binding.etMoodNote.text.toString().trim()
        
        // Check if updating today's mood
        val existingEntry = preferencesManager.getTodaysMoodEntry()
        
        val moodEntry = MoodEntry(
            id = existingEntry?.id ?: MoodEntry().id,
            emoji = mood.emoji,
            moodName = mood.displayName,
            note = note
        )
        
        if (existingEntry != null) {
            // Update existing entry
            val allEntries = preferencesManager.getMoodEntries().toMutableList()
            val index = allEntries.indexOfFirst { it.id == existingEntry.id }
            if (index >= 0) {
                allEntries[index] = moodEntry
                preferencesManager.saveMoodEntries(allEntries)
            }
            Toast.makeText(context, "Mood updated! ${mood.emoji}", Toast.LENGTH_SHORT).show()
        } else {
            // Add new entry
            preferencesManager.addMoodEntry(moodEntry)
            Toast.makeText(context, "Mood saved! ${mood.emoji}", Toast.LENGTH_SHORT).show()
        }
        
        // Clear form
        clearMoodForm()
        
        // Reload data
        loadMoodEntries()
    }

    private fun clearMoodForm() {
        selectedMood = null
        updateMoodSelection()
        binding.etMoodNote.setText("")
        binding.btnSaveMood.text = getString(R.string.save_mood)
    }

    private fun loadMoodEntries() {
        moodEntries.clear()
        moodEntries.addAll(preferencesManager.getMoodEntries())
        moodAdapter.updateMoodEntries(moodEntries)
        
        // Show/hide empty state
        if (moodEntries.isEmpty()) {
            binding.rvMoodHistory.visibility = View.GONE
            binding.tvEmptyMoodHistory.visibility = View.VISIBLE
        } else {
            binding.rvMoodHistory.visibility = View.VISIBLE
            binding.tvEmptyMoodHistory.visibility = View.GONE
        }
    }

    private fun showMoodDetails(moodEntry: MoodEntry) {
        val message = buildString {
            append("${moodEntry.emoji} ${moodEntry.moodName}\n\n")
            append("Date: ${MoodEntry.getDisplayDate(moodEntry.timestamp)}\n")
            append("Time: ${MoodEntry.getDisplayTime(moodEntry.timestamp)}\n\n")
            if (moodEntry.note.isNotEmpty()) {
                append("Note: ${moodEntry.note}")
            } else {
                append("No note added")
            }
        }
        
        AlertDialog.Builder(requireContext())
            .setTitle("Mood Entry")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .setNeutralButton("Share") { _, _ ->
                shareMoodEntry(moodEntry)
            }
            .show()
    }

    private fun shareMoodSummary() {
        val recentEntries = moodEntries.take(7) // Last 7 entries
        if (recentEntries.isEmpty()) {
            Toast.makeText(context, "No mood entries to share", Toast.LENGTH_SHORT).show()
            return
        }
        
        val summary = buildString {
            append("My Mood Summary 📊\n\n")
            
            recentEntries.forEach { entry ->
                append("${entry.emoji} ${MoodEntry.getDisplayDate(entry.timestamp)}")
                if (entry.note.isNotEmpty()) {
                    append(" - ${entry.note}")
                }
                append("\n")
            }
            
            append("\nTracked with GearUp - Your Personal Wellness Companion 💚")
        }
        
        shareMoodText(summary)
    }

    private fun shareMoodEntry(moodEntry: MoodEntry) {
        val shareText = buildString {
            append("My mood today: ${moodEntry.emoji} ${moodEntry.moodName}")
            if (moodEntry.note.isNotEmpty()) {
                append("\n\n\"${moodEntry.note}\"")
            }
            append("\n\nTracked with GearUp 💚")
        }
        
        shareMoodText(shareText)
    }

    private fun shareMoodText(text: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_SUBJECT, "My Mood from GearUp")
        }
        
        startActivity(Intent.createChooser(shareIntent, "Share mood via"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}