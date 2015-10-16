package ca.ubc.cs.cs211.hockeypool.hpool;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * 
 * @author Elan Dubrofsky
 *
 * Implements a pool team for a hockey pool
 * 
 */
public class PoolTeam implements Comparable<PoolTeam>, Serializable{
	private String m_name;
	private SortedSet<Player> m_players;
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Initializes a Pool Team Object
	 * @param name the name of the pool team
	 * @pre true
	 * @post The Pool Team is created with the given name and an empty collection of players
	 */
	public PoolTeam(String name) {
		m_name = name;
		m_players = new TreeSet<Player>();
	}
	
	/**
	 * Get the name of the pool team
	 * @pre true
	 * @post nothing changes
	 * @return the name of the pool team
	 */
	public String getName() {
		return m_name;
	}
	
	/**
	 * Get the total statistics (sum of each players) of the pool team on the given date
	 * @param d the date to get the total statistics for
	 * @pre All of the players on a pool team contain stats for the same dates
	 * @post nothing changes
	 * @return HockeyStats object containing the sum of all of the statistics for the pool team on the given date.
	 * 	If the date is null or if the team doesn't contain players with the date, return zero stats
	 * 	If the team has no players, return all zero stats
	 */
	public HockeyStats getTotalStats(Calendar d) { // Games Played, Goals, Assists
		HockeyStats totalStats = new HockeyStats();
		for(Player p : m_players) {
			HockeyStats playerStatsForDate = p.getPlayerStats(d);
			if(playerStatsForDate != null) {
				totalStats.add(playerStatsForDate);
			}
		}
		return totalStats;
	}
	
	/**
	 * Get the total statistics (sum of each players) of the pool team for the most recent date stored
	 * @pre All of the players on a pool team contain stats for the same dates
	 * @post nothing changes
	 * @return HockeyStats object containing the sum of all of the statistics for the pool team for the most recent date.
	 * 	If the team has no players or if it contains players but none of the players have any stats, return all zero stats.
	 */
	public HockeyStats getMostRecentTotalStats() {
		if((m_players.isEmpty()) || (!m_players.first().containsStats()))
			return new HockeyStats();
		return getTotalStats(m_players.first().getMostRecentDate());
	}
	
	/**
	 * Get the number of players on the pool team
	 * @pre true
	 * @post Nothing changes
	 * @return the number of players on the pool team
	 */
	public int getNumPlayers() {
		return m_players.size();
	}
	
	/**
	 * Get the teams's total stats for the time period between the date of the most recent stats and the given number of days back from that date
	 * @param daysBack the number of days back from the date of the most recent stats to start counting statistics
	 * @pre true
	 * @post nothing changes
	 * @return most recent total stats if there are no stats for the date calculated as the given number of days back from the date of the most recent stats
	 * 	Otherwise, return the team's total stats for the time period between the date of the most recent stats and the given number of days back from that date
	 */
	public HockeyStats getDaysBackTotalStats(int daysBack) {
		Calendar daysBackDate = Calendar.getInstance();
		daysBackDate.add(Calendar.DAY_OF_MONTH, -daysBack-1);
		
		//NEW
		if(daysBackDate.after(m_players.first().getMostRecentDate()))
			return new HockeyStats();
		
		HockeyStats statsOnDay = getTotalStats(daysBackDate);
		return getMostRecentTotalStats().subtract(statsOnDay);
	}
	
	/**
	 * Get an iterator over all of the players on the pool team
	 * @pre true
	 * @false nothing changes
	 * @return an iterator over all of the players on the pool team
	 */
	public Iterator<Player> playersIterator() {
		return m_players.iterator();
	}
	
	/**
	 * Change the name of the team
	 * @pre true
	 * @post The name of the team has been changed
	 * @param name The new name of the pool team
	 */
	public void setName(String name) {
		m_name = name;
	}
	
	/**
	 * Clear the pool team of players
	 * @pre true
	 * @post The pool team contains zero players
	 */
	public void clearPlayers() {
		m_players.clear();
	}
	
	/**
	 * Add a player to the pool team
	 * @param p The player to add to the team
	 * @pre The player isn't on another PoolTeam in the pool
	 * @post If the player is unique to the team, the player is added to the team
	 * @return false if the team already contains a player with the same name, true otherwise
	 */
	public boolean addPlayer(Player p) {
		return m_players.add(p);
	}
	
	/**
	 * Remove a player from the pool team with the given name
	 * @param name name of the player to remove
	 * @pre true
	 * @post If the team contains a player with the given name, the player is removed
	 * @return true if the player was successfully removed from the team, false otherwise
	 */
	public boolean removePlayer(String name) {
		for(Player p : m_players) {
			if(p.getName().equals(name)) {
				return m_players.remove(p);
			}
		}
		return false;
	}
	
	/**
	 * Returns a string that identifies the pool team
	 * @pre true
	 * @post nothing changes
	 * @return a string identifying the pool team
	 */
	public String toString() {
		return m_name;
	}

	public int compareTo(PoolTeam pt) {
		// Pool Teams should be compared the same way players are.
		// Therefore simulate the pool teams as players and let the player class do the calculation
		Player thisPlayer = new Player(m_name);
		HockeyStats totalStatsThis = getMostRecentTotalStats();
		thisPlayer.addStats(Calendar.getInstance(), totalStatsThis);
		
		Player ptPlayer = new Player(pt.m_name);
		HockeyStats totalStatsPt = pt.getMostRecentTotalStats();
		ptPlayer.addStats(Calendar.getInstance(), totalStatsPt);
		
		return thisPlayer.compareTo(ptPlayer);
	}
	
	/**
	 * Compares the pool team to the specified object
	 * @param o the object to be compared
	 * @pre true
	 * @post true
	 * @return true if obj is a pool team whose name matches the current 
	 * pool team; false otherwise
	 */
	public boolean equals(Object o) {
		if((o==null)||(!(o instanceof PoolTeam)))
			return false;
		PoolTeam pt = (PoolTeam)o;
		return pt.getName().equals(getName());
	}
	
	/**
	 * Saves the PoolTeam object to the given filename
	 * @param filename the name of the file to save this object to
	 * @throws IOException
	 * @pre true
	 * @post If not exception was thrown, the object is saved to the file
	 */
	public void save(String filename) throws IOException
	{
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(filename));
			out.writeObject(m_players);
			out.writeObject(m_name);
		}
		finally {
			out.close();
		}
	}
	
	/**
	 * Loads the PoolTeam object from the given filename
	 * @param filename the name of the file to load this object from
	 * @throws IOException
	 * @pre true
	 * @post If not exception was thrown, the object is loaded from the file
	 */
	public void load(String filename) throws IOException
	{
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(filename));
			m_players = (TreeSet<Player>) in.readObject();
			m_name = (String) in.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			in.close();
		}
		
	}
}
