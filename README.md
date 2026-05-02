# Minesweeper Multiplayer (Android)

A real-time multiplayer Minesweeper game built using Android and Firebase. Players can create or join rooms, compete on the same grid, and race to finish with the best time. The app also includes a classic arcade mode for single-player gameplay.

---

## Features

- Real-time multiplayer gameplay using Firebase Realtime Database  
- Room-based system with join by ID or browse available rooms  
- Waiting lobby with host-controlled game start  
- Shared grid for all players  
- Timer and penalty system for incorrect moves  
- Automatic leaderboard based on completion time  
- Support for multiple difficulty levels  
- Classic arcade mode for single-player gameplay  
- Clean and responsive UI  

---

## Download

Download the latest APK from the Releases section of this repository.

---

## How to Play

### Multiplayer Mode

1. Create a room or join an existing one  
2. Wait in the lobby until the host starts the game  
3. Reveal cells while avoiding mines  
4. Finish as quickly as possible  
5. Leaderboard ranks players based on completion time  

### Arcade Mode

1. Start a single-player game  
2. Choose a difficulty level  
3. Reveal all safe cells without hitting a mine  
4. Complete the board in the shortest time possible  

---

## Tech Stack

- Android (Java)
- Firebase Realtime Database
- XML for UI design

---

## Project Structure

- `ui/` – Activities and UI logic  
- `model/` – Data models such as Room and Player  
- `firebase/` – Firebase helper classes  
- `res/` – Layouts, drawables, and resources  

---

## Setup Instructions

1. Clone the repository  
2. Open the project in Android Studio  
3. Add your Firebase configuration file (`google-services.json`)  
4. Build and run the project  

---

## Firebase Requirements

- Realtime Database enabled  
- Proper database rules configured for multiplayer access  

---

## License

This project is licensed under the MIT License. See the LICENSE file for details.

---

## Notes

- The app is intended for Android devices  
- Multiplayer functionality requires an active internet connection  
- Make sure to allow installation from unknown sources when installing the APK  

---

## Future Improvements

- Live player progress indicators  
- Improved animations and sound effects  
- Enhanced matchmaking and room filtering  
- Play again functionality  
