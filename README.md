# Baseball Parlay Simulator

This program was used to fuel my newest hobby: gambling

Jokes aside, this is a console program which uses webscraping to get the starting baseball players for a given game and their statistics, and then simulates hundreds of thousands of games between the two teams.

### Parlay Building
The program then provides average results for each player in the game, and allows the user to construct a Same Game Parlay consisting of multiple bets. 
The parlay's can be tested to find their expected win rate, which is compared to the win rate predicted by the sportsbook.

![baseballParlayBuilder](https://user-images.githubusercontent.com/90343697/196532901-a2edadad-c2be-462f-8cf6-57c5741a5498.PNG)

The image above shows an example parlay. The simulation expects a win rate of 2.08%, which beats the Sportsbook's odds of 1.36%

Theoretically if the simulation win rate is better than the Sportsbook's win rate, the parlay is a winning bet on average. This is only true if my simulation is better than whatever system is in place to determine the Sportsbook odds. Sports betting is a multi-million dollar industry, and I'm just one guy. For reference, I would only put $1 on a parlay like this.

### Live Monitoring

Once a parlay has been made, it is added to the main home screen, which shows all of the current parlays. The program regularly goes and finds live play-by-play data for each relevant game, and updates the lists to show which bets within the parlays have been won, lost, or still in progress.

![baseballLive](https://user-images.githubusercontent.com/90343697/196537511-06e318d2-8921-4796-8087-aaac704be14c.PNG)


### Usage
Windows:
double click 'run.bat'

Mac:
It's just a java program. The jar is in /target, run it as you normally would

### Known issues and limitations
- The house always wins. I'm out $100... bet at your own risk
- Program cannot handle multiple parlays for the same game
- Google Chrome must be installed on your machine
- Starting pitcher is simply assumed to throw 6 innings in the simulations
- Statistics for each player are from the current season. At the start of the season, there won't be enough data to simulate accurately
- Game start time is always converted to Mountain Time
- Successful web scraping is dependent on the relevant websites. If their code changes, the program may stop working. In no way do I guarantee I'll maintain this to keep it working
- Be careful when interfacing with the console. There may be input edge cases that I didn't consider. If things go crazy, restart the program

