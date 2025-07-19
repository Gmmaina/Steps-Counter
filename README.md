# DailyBurn

DailyBurn is a fitness tracking application designed to help users monitor their health and fitness progress. The app includes features such as step tracking, BMI calculation, fitness tips, and user authentication.

## Features

- **Step Tracking**: Tracks daily and weekly steps using a step counter service and displays progress in a bar chart.
- **BMI Calculator**: Allows users to calculate their BMI, view health risk categories, and save their BMI history.
- **Fitness Tips**: Provides categorized fitness tips for nutrition, cardio, strength training, recovery, and mental wellness.
- **User Authentication**: Supports user registration, login, and logout using Firebase Authentication.
- **Data Persistence**: Saves user data such as steps, BMI records, and goals to Firebase Firestore.

## Project Structure

```DailyBurn/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/dailyburn/
│   │   │   │   ├── activities/       # Activities like MainActivity and WorkoutActivity
│   │   │   │   ├── adapters/         # RecyclerView adapters (e.g., BmiHistoryAdapter)
│   │   │   │   ├── auth/             # Authentication-related activities (e.g., LoginActivity, SignUpActivity)
│   │   │   │   ├── models/           # Data models (e.g., BmiRecord, StepData, FitnessTip)
│   │   │   │   ├── repositories/     # Data repositories for Firebase interactions
│   │   │   │   ├── services/         # Background services (e.g., StepCounterService)
│   │   │   │   ├── ui/               # Fragments for different app features
│   │   │   │   ├── viewmodels/       # ViewModels for managing UI-related data
│   │   │   ├── res/                  # Resources (layouts, drawables, etc.)
│   │   ├── test/                     # Unit tests
├── build.gradle.kts                  # Project build configuration
├── settings.gradle.kts               # Gradle settings
```

## Getting Started

### Prerequisites

- Android Studio (latest version)
- Firebase account with Firestore and Authentication enabled
- Android device or emulator

### Setup

1. Clone the repository:

   ```bash
   git clone https://github.com/Gmmaina/Steps-Counter.git
   cd Steps-Counter
   ```

2. Open the project in Android Studio.

3. Configure Firebase:
   - Add your `google-services.json` file to the `app/` directory.
   - Ensure Firestore and Authentication are enabled in your Firebase project.

4. Build and run the project:
   - Connect your device or start an emulator.
   - Click the "Run" button in Android Studio.

### Permissions

The app requires the following permissions:

- `ACTIVITY_RECOGNITION`: To track steps.
- `INTERNET`: For Firebase interactions.

## Screenshots
