# Hema Player AI

Android-native media player foundation.

## Included
- Kotlin + Jetpack Compose
- Android Media3 / ExoPlayer
- Music + video library UI
- Mini player
- Background playback service foundation
- AI command interface foundation
- Dark modern mobile-first UI
- Runtime media permissions
- Search/filter foundation

## Build
Open the project in Android Studio and let Gradle sync.

Then run:
./gradlew assembleDebug

The AI screen is intentionally provider-agnostic: connect a local model or your preferred API in the next layer without changing the player UI.
