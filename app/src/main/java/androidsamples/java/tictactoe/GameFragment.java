package androidsamples.java.tictactoe;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import androidsamples.java.tictactoe.models.GameModel;

public class GameFragment extends Fragment {
  private static final String TAG = "GameFragment";
  private static final int GRID_SIZE = 9;

  private final Button[] mButtons = new Button[GRID_SIZE];
  private NavController mNavController;

  private boolean isSinglePlayer = true;
  private String myChar = "X";
  private String otherChar = "O";
  private boolean myTurn = true;
  private String[] gameArray = new String[]{"", "", "", "", "", "", "", "", ""};
  private GameModel game;
  private boolean isHost = true;
  private DatabaseReference gameReference, userReference;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
//    setHasOptionsMenu(true); // Needed to display the action menu for this fragment

    // Extract the argument passed with the action in a type-safe way
    GameFragmentArgs args = GameFragmentArgs.fromBundle(getArguments());
    isSinglePlayer = (args.getGameType().equals("One-Player"));

    userReference = FirebaseDatabase.getInstance("https://tic-tac-toe-9b22f-default-rtdb.firebaseio.com/").getReference("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

    if (!isSinglePlayer) {
      gameReference = FirebaseDatabase.getInstance("https://tic-tac-toe-9b22f-default-rtdb.firebaseio.com/").getReference("games").child(args.getGameId());
      gameReference.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
          game = snapshot.getValue(GameModel.class);
          assert game != null;
            if (game.getHost().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
              isHost = true;
              myChar = "X";
              otherChar = "O";
            } else {
              isHost = false;
              myChar = "O";
              otherChar = "X";
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
      });
    }

    // Handle the back press by adding a confirmation dialog
    OnBackPressedCallback callback = new OnBackPressedCallback(true) {
      @Override
      public void handleOnBackPressed() {
        Log.d(TAG, "Back pressed");
        AlertDialog dialog = new AlertDialog.Builder(requireActivity())
            .setTitle(R.string.confirm)
            .setMessage(R.string.forfeit_game_dialog_message)
            .setPositiveButton(R.string.yes, (d, which) -> {
              if (isSinglePlayer)
                endGame(-2);
              else {
                if (isHost)
                  gameReference.child("status").setValue(-2);
                else
                  gameReference.child("status").setValue(2);
              }
            })
            .setNegativeButton(R.string.cancel, (d, which) -> d.dismiss())
            .create();
        dialog.show();
        
      }
    };
    requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_game, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mNavController = Navigation.findNavController(view);

    mButtons[0] = view.findViewById(R.id.button0);
    mButtons[1] = view.findViewById(R.id.button1);
    mButtons[2] = view.findViewById(R.id.button2);

    mButtons[3] = view.findViewById(R.id.button3);
    mButtons[4] = view.findViewById(R.id.button4);
    mButtons[5] = view.findViewById(R.id.button5);

    mButtons[6] = view.findViewById(R.id.button6);
    mButtons[7] = view.findViewById(R.id.button7);
    mButtons[8] = view.findViewById(R.id.button8);

    for (int i = 0; i < mButtons.length; i++) {
      int finalI = i;
      mButtons[i].setOnClickListener(v -> {
        if (myTurn) {
          Log.d(TAG, "Button " + finalI + " clicked");
          ((Button) v).setText(myChar);
          v.setClickable(false);
          gameArray[finalI] = myChar;
          if (!isSinglePlayer) {
            gameReference.child("gameArray").setValue(Arrays.asList(gameArray));
            if (isHost)
              gameReference.child("turn").setValue(-1);
            else
              gameReference.child("turn").setValue(1);
          } else {
            myTurn = !myTurn;
          }
          int status = check_status();
          if (status != 3) {
            if (isSinglePlayer)
              endGame(status);
            else
              gameReference.child("status").setValue(status);

            return;
          }

          if (isSinglePlayer) {
            doRobot();
          }
        } else {
          Toast.makeText(getContext(), "Please wait for your turn!", Toast.LENGTH_SHORT).show();
        }
      });
    }

    if (!isSinglePlayer) {
      gameReference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
          game = snapshot.getValue(GameModel.class);
          gameArray = (game.getGameArray()).toArray(new String[9]);
          updateUI();
          if ((game.getTurn() == 1 && isHost) || (game.getTurn() == -1 && !isHost)) {
            myTurn = true;
          } else {
            myTurn = false;
          }
          if(game.getStatus() != 3)
            endGame(game.getStatus());
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
      });
    }

  }

  private void endGame(int status) {
    String message = "";

      if (status == 0)
        message = getString(R.string.draw);
      else if ((status == 1 && isHost) || (status == 2 && isHost) || (status == -1 && !isHost) || (status == -2 && !isHost)) {
        message = getString(R.string.win);
        incWon();
      }
      else {
        message = getString(R.string.lose);
        incLost();
      }

    AlertDialog dialog = new AlertDialog.Builder(requireActivity())
            .setTitle("Game Over")
            .setMessage(message)
            .setPositiveButton("OK", (d, which) -> {
              mNavController.popBackStack();
            })
            .create();
    dialog.show();
  }

  private void updateUI() {
    for (int i = 0; i < 9; i++) {
      String v = gameArray[i];
      if (!v.isEmpty()) {
        mButtons[i].setText(v);
        mButtons[i].setClickable(false);
      }
    }
  }

  private void incWon() {
    userReference.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        int won_value = Integer.parseInt(snapshot.child("won").getValue().toString());
        userReference.child("won").setValue(won_value + 1);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
      }
    });
  }

  private void incLost() {
    userReference.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        int lost_value = Integer.parseInt(snapshot.child("lost").getValue().toString());
        userReference.child("lost").setValue(lost_value + 1);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
      }
    });
  }

  private void doRobot() {
    Random rand = new Random();
    int x = rand.nextInt(9);

    while (!gameArray[x].isEmpty())
      x = rand.nextInt(9);
    Log.i("CHECKING CONDITIONS", "Complete");
    gameArray[x] = otherChar;
    mButtons[x].setText(otherChar);
    mButtons[x].setClickable(false);
    myTurn = !myTurn;
    int status = check_status();
    if(status != 3)
      endGame(status);
  }

  private boolean checkDraw() {
    for (int i = 0; i < 9; i++) {
      if (gameArray[i].isEmpty()) {
        return false;
      }
    }
    return true;
  }

  private int check_status() {
    String winChar = "";
    if  (gameArray[0].equals(gameArray[1]) && gameArray[1].equals(gameArray[2]) && !gameArray[0].isEmpty()) winChar = gameArray[0];
    else if (gameArray[3].equals(gameArray[4]) && gameArray[4].equals(gameArray[5]) && !gameArray[3].isEmpty()) winChar = gameArray[3];
    else if (gameArray[6].equals(gameArray[7]) && gameArray[7].equals(gameArray[8]) && !gameArray[6].isEmpty()) winChar = gameArray[6];
    else if (gameArray[0].equals(gameArray[3]) && gameArray[3].equals(gameArray[6]) && !gameArray[0].isEmpty()) winChar = gameArray[0];
    else if (gameArray[4].equals(gameArray[1]) && gameArray[1].equals(gameArray[7]) && !gameArray[1].isEmpty()) winChar = gameArray[1];
    else if (gameArray[2].equals(gameArray[5]) && gameArray[5].equals(gameArray[8]) && !gameArray[2].isEmpty()) winChar = gameArray[2];
    else if (gameArray[0].equals(gameArray[4]) && gameArray[4].equals(gameArray[8]) && !gameArray[0].isEmpty()) winChar = gameArray[0];
    else if (gameArray[6].equals(gameArray[4]) && gameArray[4].equals(gameArray[2]) && !gameArray[2].isEmpty()) winChar = gameArray[2];
    else if (checkDraw()) return 0;
    else return 3;

    if ((winChar.equals(myChar) && isHost) || (!winChar.equals((myChar)) && !isHost))
      return 1;
    else
      return -1;
  }

  @Override
  public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_logout, menu);
    // this action menu is handled in MainActivity
  }
}