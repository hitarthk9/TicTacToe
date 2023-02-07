# Tic Tac Toe

This is a starter code for the Tic Tac Toe multiplayer game app assignment.

It uses Android Navigation Component, with a single activity and three fragments:

- The DashboardFragment is the home screen. If a user is not logged in, it should navigate to the
  LoginFragment. (See the TODO comment in code.)

- The floating button in the dashboard creates a dialog that asks which type of game to create and
  passes that information to the GameFragment (using SafeArgs).

- The GameFragment UI has a 3x3 grid of buttons. They are initialized in the starter code.
  Appropriate listeners and game play logic needs to be provided.

- Pressing the back button in the GameFragment opens a dialog that confirms if the user wants to
  forfeit the game. (See the FIXME comment in code.)

- A "log out" action bar menu is shown on both the dashboard and the game fragments. Clicking it
  should log the user out and show the LoginFragment. This click is handled in the MainActivity.

### a.

**Name of the Project** - Tic Tac Toe

**Name of Student** - Hitarth Kothari

**BITS ID** - 2019A7PS0178G

**Email** - f20190178@goa.bits-pilani.ac.in

### b. What does the app do? Any known bugs?

This app is a simple Tic Tac Toe game between two player or a single player on a device. The games and the user data are recorded in Firebase.
The app has 3 major bugs:
1. A third player can join an ongoing game and team up with the non-host player.
2. If a person creates a two player game, and nobody joins, he/she can exit that game only by forfeiting it.
3. When the game ends a dialog box is displayed. This dialog box can be exited and the user can be returned to the dashboard only on clicking 'OK', pressing back button when the dialog box is shown causes the app to run into error.
4. If a player logs out in between the game, the app runs into error.

![image](https://user-images.githubusercontent.com/87115681/206524345-3d6bf221-86de-4a65-b417-473a0cc0b593.png)
![image](https://user-images.githubusercontent.com/87115681/206524103-f7159bd0-4600-4d9c-b7c7-7b8081577f2c.png)
![image](https://user-images.githubusercontent.com/87115681/206524405-381177a2-568e-493f-bb4c-a65a20289d69.png)

### c. Description of completed tasks

**Task 1 - Sign-in Screen and dashboard**

Login is done using `LoginFragment`, a `FirebaseDatabase` object and a `DatabaseReference` object is used for authentication and realtime database respectively. User can sign in using email and password. New user is created if the user email is not recognized, user is also added in firebase realtime database. In case email is recognized, login is attempted. On entering correct password user is redirected to dashboard - `DashboardFragment`. Dashboard has win count, loss count and open games.

**Task 2 - Implementing Single-Player Mode**

User can choose a single player game from + icon in dashboard. For the single player game, the computer will choose any block randomly after the player's turn.

**Task 3 - Implementing Two-Player Mode**

User can choose a two player game from + icon in dashboard. This takes the user to a tic-tac-toe game, and their username is displayed in open games for other logged in users, to select and join the game. The game is programmed in the `GameFragment`. A variable keeps check if the game is single player or two player. The two player game is coordinated using the game data in the database. A listener for both users in their respective fragments fetches the latest data. The status of the game is maintaned using a `status` variable with the following status codes: 0 = draw, 1 = host win, -1 = non-host win, 2 = non-host forfeiture, -2 = host forfeiture, 3 = ongoing game. When the game ends, the appropriate dialog box is displayed, on clicking ok on the box the user is redirected to the dashboard with an updated win/loss count.

**Task 4 - Accessibility**

I ran the application using TalkBack. The service is fairly easy to use and guides the user on each and every step. I ran Accessibility Scanner but did not cover all issues this time. I focussed more on the app logic and user experience.

### d. How to run the app?

Running the app is fairly simple. Open this project in android studio, connect your phone and run the app. If you want to run the app using the apk, it can be found in the following path: app -> build -> outputs -> apk -> debug

### e. Testing using written test cases and monkey stress-testing

For monkey stress testing, I ran the monkey tool successfully for 10000 iterations using the command `adb shell monkey -p androidsamples.java.tictactoe -v 10000`. The app did not crash on any run.

### f. Approximate number of hours it took to complete the assignment : 

Lost track of time :(, probably 15 hrs

### g. Difficulty of Assignment : 

10/10
