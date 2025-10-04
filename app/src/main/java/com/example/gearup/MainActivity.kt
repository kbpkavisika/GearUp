package com.example.gearup

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.gearup.databinding.ActivityMainBinding
import com.example.gearup.fragments.HabitsFragment
import com.example.gearup.fragments.MoodFragment
import com.example.gearup.fragments.SettingsFragment
import com.example.gearup.utils.HydrationReminderManager
import com.example.gearup.utils.PreferencesManager

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var hydrationReminderManager: HydrationReminderManager
    private lateinit var preferencesManager: PreferencesManager

    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferencesManager = PreferencesManager(this)
        hydrationReminderManager = HydrationReminderManager(this)

        // Set up toolbar
        setSupportActionBar(binding.toolbar)

        // Request notification permission for Android 13+
        requestNotificationPermission()

        // Set up bottom navigation
        setupBottomNavigation()

        // Handle notification actions
        handleNotificationActions()

        // Handle deep links
        handleDeepLinks()

        // Show default fragment
        if (savedInstanceState == null) {
            val openHabits = intent.getBooleanExtra("open_habits", false)
            if (openHabits) {
                replaceFragment(HabitsFragment())
                binding.bottomNavigation.selectedItemId = R.id.nav_habits
            } else {
                replaceFragment(HabitsFragment())
                binding.bottomNavigation.selectedItemId = R.id.nav_habits
            }
        }

        // Initialize hydration reminders
        initializeHydrationReminders()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        when (requestCode) {
            NOTIFICATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, schedule reminders if enabled
                    initializeHydrationReminders()
                    Toast.makeText(this, "Notification permission granted! 💧", Toast.LENGTH_SHORT).show()
                } else {
                    // Permission denied
                    Toast.makeText(this, "Notification permission required for hydration reminders", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun initializeHydrationReminders() {
        if (preferencesManager.isRemindersEnabled()) {
            hydrationReminderManager.scheduleReminders()
        }
    }

    private fun handleNotificationActions() {
        // Handle marking water habit from notification
        if (intent.getBooleanExtra("mark_water_habit", false)) {
            // This would typically update the water habit
            Toast.makeText(this, "💧 Water habit marked! Keep hydrating!", Toast.LENGTH_SHORT).show()
            
            // Open habits fragment
            replaceFragment(HabitsFragment())
            binding.bottomNavigation.selectedItemId = R.id.nav_habits
        }
    }

    private fun handleDeepLinks() {
        val data = intent.data
        if (data != null && data.scheme == "gearup" && data.host == "open") {
            when (data.getQueryParameter("section")) {
                "habits" -> {
                    replaceFragment(HabitsFragment())
                    binding.bottomNavigation.selectedItemId = R.id.nav_habits
                    binding.toolbar.title = getString(R.string.nav_habits)
                }
                "mood" -> {
                    replaceFragment(MoodFragment())
                    binding.bottomNavigation.selectedItemId = R.id.nav_mood
                    binding.toolbar.title = getString(R.string.nav_mood)
                }
                "settings" -> {
                    replaceFragment(SettingsFragment())
                    binding.bottomNavigation.selectedItemId = R.id.nav_settings
                    binding.toolbar.title = getString(R.string.nav_settings)
                }
            }
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_habits -> {
                    replaceFragment(HabitsFragment())
                    binding.toolbar.title = getString(R.string.nav_habits)
                    true
                }
                R.id.nav_mood -> {
                    replaceFragment(MoodFragment())
                    binding.toolbar.title = getString(R.string.nav_mood)
                    true
                }
                R.id.nav_settings -> {
                    replaceFragment(SettingsFragment())
                    binding.toolbar.title = getString(R.string.nav_settings)
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }
}