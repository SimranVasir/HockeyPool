package ca.ubc.cs.cs211.hockeypool.ui;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import ca.ubc.cs.cs211.hockeypool.hpool.HockeyPool;
import ca.ubc.cs.cs211.hockeypool.hpool.HockeyStats;
import ca.ubc.cs.cs211.hockeypool.hpool.Player;
import ca.ubc.cs.cs211.hockeypool.hpool.PoolTeam;


public class HockeyPoolAppDriver {

	public static int readInt() throws Exception {

		String line = null;
		int val = 0;

		BufferedReader is = new BufferedReader(new InputStreamReader(System.in));
		line = is.readLine();
		val = Integer.parseInt(line);

		return val;
	}

	public static String readString() {
		String toReturn = "";
		try {
			BufferedReader is = new BufferedReader(new InputStreamReader(
					System.in));
			toReturn = is.readLine();
		} catch (Exception e) {
		}
		return toReturn;
	}

	public static void listPoolTeamsInPool(HockeyPool pool) {
		System.out.println("These are the current pool teams:");
		Iterator<PoolTeam> it = pool.poolTeamsIterator();
		while (it.hasNext())
			System.out.println(it.next());
		

	}

	public static void listPlayersOnPoolTeam(PoolTeam pt) {
		System.out.println("These are the players on the pool team "
				+ pt.getName() + ":");
		Iterator<Player> it = pt.playersIterator();
		while (it.hasNext())
			System.out.println(it.next());
	}

	public static int queryForMenuItem(String poolName) {
		while (true) {
			String text = "\n";
			text += poolName + " Hockey Pool\n";
			text += "Please select from the following options:\n";
			text += "1. Change Pool Name\n";
			text += "2. Add Pool Team\n";
			text += "3. Remove Pool Team\n";
			text += "4. Show Pool Team Menu\n";
			text += "5. Update Pool\n";
			text += "6. Show Pool Stats\n";
			text += "7. Load Pool from object file\n";
			text += "8. Load Pool from text file\n";
			text += "9. Save Pool as object file\n";
			text += "10. Quit\n";
			System.out.println(text);
			try {
				int selection = HockeyPoolAppDriver.readInt();
				if (selection > 0 && selection <= 10)
					return selection;
				System.err.println("Invalid entry: Not between 1-10\n");
			} catch (Exception e) {
				System.err.println("Invalid entry: Not an integer\n");
			}
		}
	}

	public static int queryForPoolTeamMenuItem(String poolTeamName) {
		while (true) {
			String text = "\n";
			text += poolTeamName + " Pool Team\n";
			text += "Please select from the following options:\n";
			text += "1. Change Pool Team Name\n";
			text += "2. Add Player\n";
			text += "3. Remove Player\n";
			text += "4. Show Pool Team Stats\n";
			text += "5. Return to Main Menu\n";
			System.out.println(text);
			try {
				int selection = HockeyPoolAppDriver.readInt();
				if (selection > 0 && selection <= 5)
					return selection;
				System.err.println("Invalid entry: Not between 1-5\n");
			} catch (Exception e) {
				System.err.println("Invalid entry: Not an integer\n");
			}
		}
	}

	public static void main(String[] args) {
		boolean quit = false;
		HockeyPool pool = new HockeyPool("Untitled");
		String name;
		PoolTeam pt = null;
		Player p;
		boolean showMainMenu = true;
		while(!quit) {
			int selection = (showMainMenu)?HockeyPoolAppDriver.queryForMenuItem(pool.getName()):HockeyPoolAppDriver.queryForPoolTeamMenuItem(pt.getName());
			if(showMainMenu) {
				switch(selection) {
					case 1: // Change pool name
						System.out.print("Enter new pool name: ");
						pool.setName(HockeyPoolAppDriver.readString());
						break;
					case 2: // Add pool team
						System.out.print("Enter new pool team name: ");
						name = HockeyPoolAppDriver.readString();
						if(pool.addPoolTeam(new PoolTeam(name)))
							System.out.println("Pool team " + name + " added successfully");
						else
							System.err.println("Error: There is already a pool team with this name.");
						break;
					case 3: // Remove pool team
						if(pool.getNumTeams() == 0)
							System.err.println("Error: There are no pool teams.");
						else {
							HockeyPoolAppDriver.listPoolTeamsInPool(pool);
							System.out.print("Enter the name of the team to remove: ");
							name = HockeyPoolAppDriver.readString();
							if(pool.removePoolTeam(name)) 
								System.out.println("Pool team " + name + " removed successfully");
							else
								System.err.println("Error: Team " + name + " does not exist");
						}
						break;
					case 4: // Show pool team menu
						if(pool.getNumTeams() == 0)
							System.out.println("Error: There are no pool teams.");
						else {
							HockeyPoolAppDriver.listPoolTeamsInPool(pool);
							System.out.print("Enter the name of the team to drill into: ");
							name = HockeyPoolAppDriver.readString();
							pt = pool.getPoolTeam(name);
							if(pt == null) {
								System.err.println("Error: Team " + name + " does not exist");
							} else {
								showMainMenu = false;
							}
						}
						break;
					case 5: // Update pool
						pool.update();
						pool.resort();
						break;
					case 6: // Show pool stats
						boolean validInt = false;
						int daysBack = 0;
						while(!validInt) {
							System.out.print("Enter how many days back to compare to: ");
							try {
								daysBack = HockeyPoolAppDriver.readInt();
								validInt = true;
							} catch(Exception e) {
								System.err.println("Error: Not a valid integer");
							}
						}
						System.out.printf("%10s %20s %10s %10s %10s %5s %10s %15s\n", "Rank", "Team Name", "GP", "Goals", "Assists", daysBack+"","Points", "Points Back" );
						int currentRank = 1;
						Iterator<PoolTeam> it = pool.poolTeamsIterator();
						int leaderPoints = -1;
						while(it.hasNext()) {
							pt = it.next();
							HockeyStats currentStats = pt.getMostRecentTotalStats();
							if(leaderPoints == -1)
								leaderPoints = currentStats.getPoints();
							HockeyStats daysBackStats = pt.getDaysBackTotalStats(daysBack);
							System.out.printf("%10d %20s %10d %10d %10d %5s %10d %15d\n", currentRank++, pt.getName(), currentStats.getGamesPlayed(), currentStats.getGoals(), currentStats.getAssists(), (daysBackStats==null)?"N/A":daysBackStats.getPoints()+"",currentStats.getPoints(), leaderPoints - currentStats.getPoints() );
						}
						
						break;
					case 7: // Load pool from object file
						System.out.print("Enter the filename of the pool to load: ");
						name = HockeyPoolAppDriver.readString();
						try {
							pool.load(name);
							System.out.println("Pool loaded successfully");
						} catch(Exception e) {
							System.err.println("Error: Unable to load pool with this filename");
						}
						break;
					case 8: // Load pool from text file
						System.out.print("Enter the filename of the textfile to load: ");
						name = HockeyPoolAppDriver.readString();
						try {
							pool = HockeyPool.loadFromTextFile(name);
							System.out.println("Pool loaded successfully");
						} catch(IOException e) {
							System.err.println("Error: Unable to load pool with this filename");
						}
						break;
					case 9: // Save pool as object file
						System.out.print("Enter the filename to save the pool as: ");
						name = HockeyPoolAppDriver.readString();
						try {
							pool.save(name);
							System.out.println("Pool saved successfully");
						} catch(Exception e) {
							System.err.println("Error: Unable to save pool");
						}
						break;
					case 10: // Quit
						quit = true;
						System.out.println("Goodbye!");
						break;
				}
			}else {
				switch(selection) {
					case 1: // Change pool team name
						System.out.print("Enter new pool team name: ");
						pt.setName(HockeyPoolAppDriver.readString());
						break;
					case 2:	// Add player to pool team	
						System.out.print("Enter the player's name: ");
						name = HockeyPoolAppDriver.readString();
						Player newPlayer = new Player(name);
						if(pool.getAllPlayers().contains(newPlayer)) 
							System.err.println("Player " + name + " is already in this hockey pool");
						else if(pt.addPlayer(new Player(name)))
							System.out.println("Player " + name + " was successfully added to pool team " + pt.getName());
						else
							System.err.println("Error: There is already a player named " + name + " on this team");
					
						break;
					case 3: // Remove player from pool team
						HockeyPoolAppDriver.listPlayersOnPoolTeam(pt);
						System.out.print("Enter the name of the player to remove: ");
						name = HockeyPoolAppDriver.readString();
						if(pt.removePlayer(name))
							System.out.println("Player " + name + " was successfully removed from pool team " + pt.getName());
						else
							System.err.println("Error: " + name + " is not on this pool team");
					
						break;
					case 4: //Show pool team stats
						boolean validInt = false;
						int daysBack = 0;
						while(!validInt) {
							System.out.print("Enter how many does back to compare to: ");
							try {
								daysBack = HockeyPoolAppDriver.readInt();
								validInt = true;
							} catch(Exception e) {
								System.err.println("Error: Not a valid integer");
							}
						}
						System.out.printf("%10s %20s %15s %10s %5s %10s %10s %10s %10s\n", "Rank", "Name", "Team", "Position", "GP", "Goals", "Assists", daysBack+"","Points");
						int currentRank = 1;
						Iterator<Player> it = pt.playersIterator();
						while(it.hasNext()) {
							p = it.next();
							HockeyStats currentStats = p.getMostRecentPlayerStats();
							HockeyStats daysBackStats = p.getDaysBackStats(daysBack);
							System.out.printf("%10d %20s %15s %10s %5d %10d %10d %10s %10d\n", currentRank++, p.toString(), p.getTeam(), p.getPos(), currentStats.getGamesPlayed(), currentStats.getGoals(), currentStats.getAssists(), (daysBackStats==null)?"N/A":daysBackStats.getPoints()+"",currentStats.getPoints());
						}
						HockeyStats totalStats = pt.getMostRecentTotalStats();
						HockeyStats totalDaysBackStats = pt.getDaysBackTotalStats(daysBack);
						System.out.printf("%10s %20s %15s %10s %5d %10d %10d %10s %10d\n", "", "Total", "", "", totalStats.getGamesPlayed(), totalStats.getGoals(), totalStats.getAssists(), (totalDaysBackStats==null)?"N/A":totalDaysBackStats.getPoints()+"",totalStats.getPoints());
						break;
					case 5:
						showMainMenu = true;
						break;
				}
			}
				
				
		}
		
	}
}
