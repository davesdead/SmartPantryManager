# Smart Pantry Manager

## Description
Smart Pantry Manager is an Android app written in Java. It helps you cut down on food waste.

You add the ingredients you have at home to your pantry. The app then shows you recipes you can cook right now. A recipe is only shown if you have every ingredient it needs, in a big enough amount.

The app can:
- Add, edit and delete pantry items
- Show suggested recipes based on your pantry
- Show the full ingredients and method for a recipe
- Highlight items that are about to expire (this can be changed in Settings)

## Database Choice
The app uses **SQLite**, through the **Room** library.

Why:
- The data is stored on the phone, so no internet or server is needed.
- The data stays saved after the app is closed.
- It is the database covered in the module, so it was the best fit.

## Setup Instructions
1. Clone the repository:
   ```
   git clone https://github.com/davesdead/SmartPantryManager.git
   ```
2. Open the project in **Android Studio**.
3. Wait for Gradle to finish syncing.
4. Connect an Android phone or start an emulator (Android 7.0 / API 24 or newer).
5. Press the green **Run** button.

The recipes are loaded automatically the first time the app opens.
