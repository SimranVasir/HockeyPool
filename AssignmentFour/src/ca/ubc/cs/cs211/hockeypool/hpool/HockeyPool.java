package ca.ubc.cs.cs211.hockeypool.hpool;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * 
 * @author Elan Dubrofsky
 *
 * Implements a hockey pool
 * 
 */
public class HockeyPool implements Serializable{
	private SortedSet<PoolTeam> m_poolTeams;
	private String m_name;
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Initializes a HockeyPool Object
	 * @param name the name of the hockey pool
	 * @pre true
	 * @post The Hockey pool is created with the given name and an empty collection of pool teams
	 */
	public HockeyPool(String name) {
		m_poolTeams = new TreeSet<PoolTeam>();
		m_name = name;
	}
	
	/**
	 * Gets the name of the hockey pool
	 * @pre true
	 * @post nothing changes
	 * @return The name of the hockey pool
	 */
	public String getName() {
		return m_name;
	}
	
	/**
	 * Returns the number of pool teams in the hockey pool
	 * @pre true
	 * @post nothing changes
	 * @return The number of pool teams in the hockey pool
	 */
	public int getNumTeams() {
		return m_poolTeams.size();
	}
	
	/**
	 * Returns the pool team with the given name, or null if the pool doesn't contain a pool team with that name
	 * @param name The name of the pool team to search for
	 * @pre true
	 * @post nothing changes
	 * @return The pool team with the given name, or null if the pool doesn't contain a pool team with that name
	 */
	public PoolTeam getPoolTeam(String name) {
		for(PoolTeam pt : m_poolTeams) {
			if(pt.getName().equals(name))
				return pt;
		}
		return null;
	}
	
	/**
	 * Get a List of all of the players in the pool
	 * @pre true
	 * @post nothing changes
	 * @return A collection of all of the players in the hockey pool
	 */
	public List<Player> getAllPlayers() {
		List<Player> players = new ArrayList<Player>();
		for(PoolTeam pt : m_poolTeams) {
			Iterator<Player> it = pt.playersIterator();
			while(it.hasNext()) {
				players.add(it.next());
			}
		}
		return players;
	}
	
	/**
	 * Get an iterator over all of the pool teams in the hockey pool
	 * @pre true
	 * @post nothing changes
	 * @return An iterator object over all of the pool teams in the hockey pool
	 */
	public Iterator<PoolTeam> poolTeamsIterator() {
		return m_poolTeams.iterator();
	}
	
	/**
	 * Change the name of the hockey pool
	 * @pre true
	 * @post The name of the hockey is set to the passed in value
	 * @param name The new name of the hockey pool
	 */
	public void setName(String name) {
		m_name = name;
	}
	
	/**
	 * Add a new pool team to the hockey pool
	 * @param pt The pool team to add to the hockey pool
	 * @pre true
	 * @post The pool team is added to the pool, unless a team with the same name is already in the pool
	 * @return true if the team was successfully added to the pool, false otherwise
	 */
	public boolean addPoolTeam(PoolTeam pt) {
		return m_poolTeams.add(pt);
	}
	
	/**
	 * Remove a pool team with a given name from the pool
	 * @param name The name of the pool team to remove
	 * @pre true
	 * @post If the pool contains a pool team with the given name, the team is removed
	 * @return true if the team was successfully removed from the pool, false otherwise
	 */
	public boolean removePoolTeam(String name) {
		for(PoolTeam pt : m_poolTeams) {
			if(pt.getName().equals(name)) {
				return m_poolTeams.remove(pt);
			}	
		}
		return false;
	}
	
	/**
	 * Returns a string that identifies the hockey pool
	 * @pre true
	 * @post nothing changes
	 * @return a string identifying the hockey pool
	 */
	public String toString() {
		return m_name;
	}
	
	/**
	 * Update all of the players in the hockey pool
	 * @pre true
	 * @post All of the players are updated, and is they weren't updated (they weren't found) they are given zero stats for yesterday
	 */
	public void update() {
		List<Player> playersToUpdate = getAllPlayers();
		
		for(Player p : playersToUpdate) {
			System.out.println(p);
			if(!p.update()) {
				Calendar yesterday = Calendar.getInstance();
				yesterday.add(Calendar.DAY_OF_MONTH, -1);
				p.addZeroStats(yesterday);
			}
		}
		
	}
	
	/**
	 * Resort all of the players and teams in the pool
	 * @pre true
	 * @post All of the players are teams are sorted
	 */
	public void resort() {
		//Resort players
		for(PoolTeam pt : m_poolTeams) {
			Iterator<Player> it = pt.playersIterator();
			List<Player> players = new ArrayList<Player>();
			while(it.hasNext())
				players.add(it.next());
			pt.clearPlayers();
			for(Player p : players) {
				pt.addPlayer(p);
			}
		}
		
		//Resort Pool Teams
		List<PoolTeam> tempTeamList = new ArrayList<PoolTeam>();
		for(PoolTeam pt : m_poolTeams) {
			tempTeamList.add(pt);
		}
		m_poolTeams.clear();
		for(PoolTeam pt : tempTeamList) {
			m_poolTeams.add(pt);
		}
	}
	
	/**
	 * Load and return a hockey pool from a text file
	 * @param filename the name of the text file to load the hockey pool from
	 * @pre The text file is formatted as follows:
	 * 	The first line is the name of the hockey pool.
	 * 	Pool teams are listed with their name first and then their list of players
	 * 	There is an empty line to seperate pool teams
	 * @post A hockey pool is created and returned
	 * @return A hockey pool loaded from the text file. All players have zero stats
	 * @throws IOException
	 */
	public static HockeyPool loadFromTextFile(String filename) throws IOException{
		HockeyPool hp = new HockeyPool("");
		
		BufferedReader in = new BufferedReader(new FileReader(filename));
		hp.setName(in.readLine());
		String line;
		PoolTeam currentPoolTeam = new PoolTeam(in.readLine());
		while((line = in.readLine()) != null) {
			if(line.trim().length() == 0) {
				hp.addPoolTeam(currentPoolTeam);
				currentPoolTeam = new PoolTeam(in.readLine());
			} else {
				currentPoolTeam.addPlayer(new Player(line));
			}
		}
		hp.addPoolTeam(currentPoolTeam);
		in.close();
		return hp;
	}
	
	/**
	 * Saves the hockey pool object to the given filename
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
			out.writeObject(m_poolTeams);
			out.writeObject(m_name);
		}
		finally {
			out.close();
		}
	}
	
	/**
	 * Loads the hockey pool object from the given filename
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
			m_poolTeams = (TreeSet<PoolTeam>) in.readObject();
			m_name = (String) in.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			in.close();
		}
		
	}
	
}
