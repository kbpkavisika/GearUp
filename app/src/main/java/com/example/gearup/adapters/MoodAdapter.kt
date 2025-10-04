package com.example.gearup.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gearup.databinding.ItemMoodBinding
import com.example.gearup.models.MoodEntry

class MoodAdapter(
    private var moodEntries: MutableList<MoodEntry>,
    private val onMoodClick: (MoodEntry) -> Unit
) : RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val binding = ItemMoodBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        holder.bind(moodEntries[position])
    }

    override fun getItemCount(): Int = moodEntries.size

    fun updateMoodEntries(newEntries: List<MoodEntry>) {
        moodEntries.clear()
        moodEntries.addAll(newEntries)
        notifyDataSetChanged()
    }

    inner class MoodViewHolder(private val binding: ItemMoodBinding) : 
        RecyclerView.ViewHolder(binding.root) {

        fun bind(moodEntry: MoodEntry) {
            binding.apply {
                tvMoodEmoji.text = moodEntry.emoji
                
                val displayDate = MoodEntry.getDisplayDate(moodEntry.timestamp)
                val displayTime = MoodEntry.getDisplayTime(moodEntry.timestamp)
                tvMoodDate.text = "$displayDate, $displayTime"
                
                if (moodEntry.note.isNotEmpty()) {
                    tvMoodNote.text = moodEntry.note
                    tvMoodNote.visibility = View.VISIBLE
                } else {
                    tvMoodNote.visibility = View.GONE
                }

                root.setOnClickListener { onMoodClick(moodEntry) }
            }
        }
    }
}