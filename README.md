
---

# FitProPlus

**FitProPlus** is an all-in-one fitness application designed to enhance your health and fitness journey. The app combines workout tracking, meal logging, and performance analysis, while prioritizing user experience with a streamlined interface and robust functionality. Built with Kotlin and utilizing Firebase for backend services, FitProPlus aims to make fitness accessible, measurable, and enjoyable.

## Team Members

- **ST10090916** - Cian Brink
- **ST10090943** - Dylan Pather
- **ST10059881** - Kayur Betchu
- **ST10048945** - Joshua Reddy
- **ST10061733** - Yuvaan Pather

## Features

- **User Authentication**: Secure user registration and login using Firebase Authentication to protect user data and provide personalized experiences.
- **Workout Logging**: Track and log workouts including types of exercises, sets, reps, duration, and other relevant metrics. View workout history and track progress over time.
- **Calorie & Meal Logging**: Log daily meals with accurate calorie and nutritional information using the Nutritionix API, helping users make informed dietary choices.
- **Multi-Language Support**: Currently supports multiple languages (English, Afrikaans, Zulu), allowing users to select their preferred language on the login screen.
- **Data Analytics and Progress Tracking**: Visualize workout and nutrition data through easy-to-understand graphs and metrics that track fitness progress.
- **Social Features**: Connect with friends to share achievements, view leaderboards, and challenge each other in the pursuit of fitness goals.
- **Offline Sync**: Log workouts and meals offline, with automatic syncing when the internet connection is restored.

## Getting Started

To set up and run FitProPlus on your local machine, follow these steps:

### Prerequisites

- Android Studio (latest version)
- Kotlin support
- Firebase account for backend services

### Installation

1. Clone the repository to your local machine:
   ```bash
   git clone https://github.com/yourusername/FitProPlus.git
   ```
2. Open the project in **Android Studio** and sync the Gradle files.
3. Set up Firebase for authentication, database, and storage:
   - Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project.
   - Add an Android app to your Firebase project with your app’s package name.
   - Download the `google-services.json` file and place it in the `/app` directory of your project.
   - Enable Authentication, Firestore Database, and other Firebase features as needed.
4. Build and run the app on an Android device or emulator.

## Usage

### 1. Multi-Language Feature

Upon launching the app, users are presented with the option to select their preferred language (English, Afrikaans, or Zulu) on the login screen. This setting customizes the language throughout the app, with all text translated as per the chosen language preference. The app retains this preference, allowing users to seamlessly continue in their preferred language each time they log in.

### 2. Workout Tracking

Track workouts by adding exercises, specifying sets, reps, and duration. Access the workout history in the "Workout History" section, and monitor improvements over time through visual data.

### 3. Meal and Calorie Logging

With Nutritionix API integration, FitProPlus offers a comprehensive calorie tracking feature. Users can log their meals by searching for food items, with calorie information updated instantly to help track nutritional intake.

### 4. Progress Analytics

The app provides users with visual analytics through graphs for calorie intake and workouts, allowing a clear view of progress and fitness consistency. These visualizations support users in adjusting their goals and staying motivated.

## Roadmap

Future plans for FitProPlus include:
- **Biometric Authentication**: Enable secure login via fingerprint or facial recognition.
- **Workout Recommendations**: Use AI to recommend personalized workouts based on past activities and goals.
- **Customizable Notifications**: Users will be able to set reminders for workouts, meals, and hydration.
- **Enhanced Social Features**: Allow users to share achievements on social media and challenge friends to fitness goals.

## Technologies Used

- **Programming Language**: Kotlin
- **Backend**: Firebase (Authentication, Firestore Database)
- **API Integration**: Nutritionix API for meal and calorie tracking
- **Multi-Language Support**: Currently supporting English, Afrikaans, and Zulu

## Contributing

Contributions are welcome! Please fork this repository and submit a pull request for any feature additions or bug fixes. Make sure to follow coding best practices and provide adequate documentation for new features.

## Issues and Bug Reporting

If you encounter any issues, feel free to open an issue in the repository. Please provide detailed information about the problem, including screenshots if applicable.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---
