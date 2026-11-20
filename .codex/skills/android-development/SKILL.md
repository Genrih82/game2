---
name: android-development
description: Develop and maintain the MOZG3 Android application using Kotlin, Jetpack Compose, Android SDK, and Gradle.
---

# MOZG3 Android Development

Use this skill for changes to the MOZG3 Android app, including UI, settings, local persistence, scheduled launches, accessibility behavior, and APK builds.

- Keep the project source under `app/src/main` and use Kotlin with Jetpack Compose for UI.
- Keep first-launch state, child profile, exercise settings, PIN state, and schedule state in explicit local persistence. Do not rely on in-memory state for settings that must survive an app restart.
- Preserve the product rules in the project requirements. First launch requires a child name, parent PIN is optional, lock is off by default, the default schedule is 30 minutes, and the default scheduled unlock count is 10 tasks.
- Separate scheduled mode from manual mode. Scheduled mode uses the configured task count and can unlock the device after the required correct answers. Manual mode is started from the app shortcut and has no task-count limit while showing correct and incorrect totals.
- Keep settings readable on small screens. Use scrolling when the settings content can exceed the viewport. Apply selected background colors immediately to all app screens.
- Treat PIN changes, reset flows, database migrations, alarm scheduling, and accessibility service changes as state-sensitive changes. Preserve existing user data with explicit Room migrations when the schema changes.
- For scheduled behavior, validate the exact-alarm permission, notification permission, foreground service state, and boot recovery path. Provide a visible status or explanation when Android permissions prevent automatic launch.
- For each code change, run `./gradlew :app:compileDebugKotlin` or `./gradlew :app:assembleDebug` with the configured JDK and Android SDK. Report warnings separately from build errors.
- Do not publish or push Git changes unless the user explicitly asks for it.
