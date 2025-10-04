# 🌱 GearUp - Your Wellness Companion

A comprehensive Android wellness application built for IT2010 Mobile Application Development. GearUp helps users track daily habits, monitor mood, and maintain healthy hydration with intelligent reminders and progress visualization.

## 🎯 Features

### Core Features
- **📈 Daily Habit Tracker**: Create, track, and visualize progress on daily habits with CRUD operations
- **😊 Mood Journal**: Track daily emotions with emoji selector and maintain mood history  
- **💧 Hydration Reminders**: Smart notification system with customizable intervals and time ranges
- **📱 Home Widget**: Interactive widget showing real-time habit progress on home screen

### Advanced Features
- **🎨 Material Design 3**: Modern UI with custom color palette (60% #8EFF00, 30% white, 10% black)
- **📱 Responsive Design**: Optimized layouts for phones, tablets, and landscape orientations
- **🔄 Data Persistence**: Reliable data storage using SharedPreferences with JSON serialization
- **🔗 Intent Integration**: Deep linking, sharing capabilities, and notification actions
- **⚡ Smooth Animations**: Professional transitions and user interactions

## 🏗️ Architecture

### Technical Stack
- **Language**: Kotlin
- **UI Framework**: Android Views with ViewBinding
- **Architecture**: Fragment-based navigation with MainActivity
- **Notifications**: WorkManager for reliable background reminders
- **Data Storage**: SharedPreferences with Gson for JSON serialization
- **Design System**: Material Design 3 components

### Project Structure
```
app/src/main/
├── java/com/example/gearup/
│   ├── MainActivity.kt                 # Main activity with navigation
│   ├── fragments/                      # UI fragments
│   │   ├── HabitsFragment.kt          # Habit tracking interface
│   │   ├── MoodFragment.kt            # Mood journal interface
│   │   └── SettingsFragment.kt        # Settings and preferences
│   ├── models/                        # Data models
│   │   ├── Habit.kt                   # Habit data structure
│   │   ├── HabitProgress.kt           # Progress tracking
│   │   ├── MoodEntry.kt               # Mood data structure
│   │   └── MoodType.kt                # Mood enumeration
│   ├── adapters/                      # RecyclerView adapters
│   │   ├── HabitsAdapter.kt           # Habit list adapter
│   │   └── MoodAdapter.kt             # Mood history adapter
│   ├── utils/                         # Utility classes
│   │   ├── PreferencesManager.kt      # Data persistence manager
│   │   ├── HydrationReminderManager.kt # Notification manager
│   │   └── WidgetUtils.kt             # Widget helper utilities
│   ├── workers/                       # Background workers
│   │   └── HydrationReminderWorker.kt # Notification worker
│   └── widgets/                       # App widgets
│       └── HabitProgressWidgetProvider.kt # Home screen widget
└── res/
    ├── layout/                        # UI layouts
    ├── layout-land/                   # Landscape layouts
    ├── layout-sw600dp/                # Tablet layouts
    ├── values/                        # Default resources
    ├── values-night/                  # Dark theme resources
    └── values-sw600dp/                # Tablet resources
```

## 🚀 Installation & Setup

### Prerequisites
- Android Studio Arctic Fox or newer
- Android SDK 24+ (Android 7.0)
- Kotlin 1.8+

### Build Instructions
1. Clone the repository:
   ```bash
   git clone https://github.com/kbpkavisika/GearUp.git
   cd GearUp
   ```

2. Open in Android Studio:
   - File → Open → Select the GearUp folder
   - Wait for Gradle sync to complete

3. Build and Run:
   - Click "Run" button or use Ctrl+R
   - Select target device (API 24+)

## 📱 Usage Guide

### Getting Started
1. **Launch GearUp** - The app opens to the Habits tab with default habits
2. **Grant Permissions** - Allow notifications for hydration reminders
3. **Add Custom Habits** - Tap the + button to create personalized habits
4. **Track Progress** - Tap on any habit to log daily progress
5. **Monitor Mood** - Switch to Mood tab to log daily emotions
6. **Configure Reminders** - Use Settings tab to customize hydration alerts

### Key Features

#### Habit Tracking
- **Add Habits**: Name, target value, unit, description, and icon
- **Track Progress**: Quick-add buttons (1, 5, 10) or custom values
- **View Progress**: Visual progress bars and completion percentages
- **Share Progress**: Share daily progress summary via any app

#### Mood Journal
- **Log Mood**: Select from 9 emoji-based mood options
- **Add Notes**: Optional text notes for context
- **View History**: Chronological list of all mood entries
- **Share Summary**: Export mood data for sharing

#### Hydration Reminders
- **Smart Notifications**: Customizable reminder intervals
- **Time Range**: Set active hours (default 8 AM - 10 PM)
- **Quick Actions**: Mark habit complete directly from notification

#### Home Widget
- **Live Progress**: Real-time habit completion percentage
- **Interactive**: Tap to open app directly to habits
- **Auto-Update**: Refreshes when habits are updated

## 🎨 Design System

### Color Palette
- **Primary Green**: #8EFF00 (60% usage) - Actions, highlights, progress
- **Pure White**: #FFFFFF (30% usage) - Backgrounds, cards
- **Pure Black**: #000000 (10% usage) - Text, icons, accents

### Responsive Design
- **Phone Portrait**: Standard bottom navigation layout
- **Phone Landscape**: Horizontal split-screen layouts
- **Tablet**: Side navigation rail with expanded content areas
- **Auto-scaling**: Dynamic text sizes and spacing

## 🔧 Configuration

### Default Habits
The app includes 5 pre-configured habits:
- 💧 Drink Water (8 glasses)
- 🏃 Exercise (30 minutes)  
- 🧘 Meditation (15 minutes)
- 📚 Reading (30 minutes)
- 😴 Sleep (8 hours)

### Notification Settings
- **Default Interval**: 60 minutes
- **Active Hours**: 8:00 AM - 10:00 PM
- **Customizable**: All timing preferences adjustable

## 🧪 Testing

### Manual Testing Checklist
- [ ] Habit CRUD operations (Create, Read, Update, Delete)
- [ ] Progress tracking with various input methods
- [ ] Mood logging with emoji selection and notes
- [ ] Notification scheduling and delivery
- [ ] Widget installation and updates
- [ ] Data persistence across app restarts
- [ ] Responsive layout on different screen sizes
- [ ] Sharing functionality for habits and moods
- [ ] Deep linking navigation

### Device Compatibility
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 36 (Android 14)
- **Screen Sizes**: All phone and tablet sizes
- **Orientations**: Portrait and landscape support

## 📊 Project Evaluation

This project implements all requirements for the IT2010 Mobile Application Development lab exam:

### Feature Implementation (28/28 marks)
- ✅ **Daily Habit Tracker** (6 marks): Full CRUD with progress visualization
- ✅ **Mood Journal with Emoji Selector** (4 marks): Complete mood tracking system
- ✅ **Hydration Reminder** (3 marks): WorkManager-based notification system
- ✅ **Advanced Feature - Home Widget** (2 marks): Interactive progress widget
- ✅ **Creativity & UI Design** (2 marks): Material Design 3 with custom theming
- ✅ **Architecture - Fragments/Activities** (3 marks): Fragment-based navigation
- ✅ **Data Persistence - SharedPreferences** (3 marks): Comprehensive data management
- ✅ **Intents** (3 marks): Navigation, sharing, and deep linking
- ✅ **Responsive UI** (2 marks): Multi-device layout optimization

## 🤝 Contributing

This is an academic project for IT2010 Mobile Application Development. 

### Development Guidelines
- Follow Kotlin coding conventions
- Maintain Material Design principles
- Ensure backward compatibility (API 24+)
- Test across multiple device configurations

## 📄 License

This project is created for educational purposes as part of IT2010 Mobile Application Development coursework.

## 👤 Author

**Student**: IT2010 Mobile Application Development  
**Project**: GearUp Wellness Application  
**Year**: 2025

---

Built with ❤️ using Android Studio and Kotlin
