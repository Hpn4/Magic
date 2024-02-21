# Magic

This project is a simple simulator for Magic the Gathering card game.
I used this project to learn more about POO, network programming, JavaFX and to create robust architecture.

There is a small visual sample (`Magic [javafx:run]`) if you would like to try it, but it was just for testing purposes
so you cannot play a
game with it.

If you would like to see how card are registered, you can take a look at the `CoreSet2020` class.

I don't plan to maintain this project since big games like MTGA already exists, but I still think it's a good project to
learn from.

## Rules

I have built a big part of the game logic (before the 2020 core set) and rules like:

- Some static abilities (until, would, etc.)
- Triggered abilities (whenever, when)
- Cost abilities (mana, life, sacrifice, etc.)
- Stack
- Most of the basics effect (add/remove counter, destroy, add mana, gain/lose life, sacrifice, reveal, scry, draw,
  gain/lose abilities, etc.)
- Some keywords (flying, trample, etc.)
- All card type
- ...