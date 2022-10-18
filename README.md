# Baseball Parlay Simulator

This program was used to fuel my newest hobby: gambling

Jokes aside, this is a console program which uses webscraping to get current baseball players and their statistics, and then simulates hundreds of thousands of games between the two teams.
The program then provides average results for each player in the game, and allows the user to construct a Same Game Parlay consisting of multiple bets. 
The parlay's can be tested to find their expected win rate, which is compared to the win rate predicted by the sportsbook.

Once a parlay has been made, it is added to the main home screen, which shows all of the current parlays. The program regularly goes and finds live play-by-play data for each relevant game, and updates the lists to show which bets within the parlays have been won, lost, or still in progress.

The only downside is the house always wins, and I'm out $100

RUN WITH:
run.bat

if you're on mac, it's just a maven java program. The jar is in /target, run it as you normally would
