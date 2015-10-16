package ca.ubc.cs.cs211.hockeypool.hpool;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 
 * @author Elan Dubrofsky
 *
 * Stores hockey statistics
 * 
 */
public class HockeyStats implements Serializable{
	private int m_gamesPlayed, m_goals, m_assists;
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Initializes a hockey statistics object with given stats
	 * @param gamesPlayed
	 * @param goals
	 * @param assists
	 * @pre true
	 * @post A hockey stats object has been created with the given statistics
	 */
	public HockeyStats(int gamesPlayed, int goals, int assists) {
		m_gamesPlayed = gamesPlayed;
		m_goals = goals;
		m_assists = assists;
	}
	
	/**
	 * Initializes a hockey statistics object with zero statistics
	 * @pre true
	 * @post A hockey stats object has been created with zero statistics
	 */
	public HockeyStats() {
		this(0,0,0);
	}
	
	/**
	 * Returns a clone of the HockeyStats object
	 * @pre true
	 * @post Nothing Changes
	 * @return a clone of the hockey stats object
	 */
	public HockeyStats clone() {
		return new HockeyStats(m_gamesPlayed, m_goals, m_assists);
	}
	
	/**
	 * Get the number of games played
	 * @pre true
	 * @post nothing changes
	 * @return the number of games played
	 */
	public int getGamesPlayed() {
		return m_gamesPlayed;
	}
	
	/**
	 * Get the number of goals
	 * @pre true
	 * @post nothing changes
	 * @return the number of goals
	 */
	public int getGoals() {
		return m_goals;
	}
	
	/**
	 * Get the number of assists
	 * @pre true
	 * @post nothing changes
	 * @return the number of assists
	 */
	public int getAssists() {
		return m_assists;
	}
	
	/**
	 * Get the number of points (goals + assists)
	 * @pre true
	 * @post nothing changes
	 * @return the number of points (goals + assists)
	 */
	public int getPoints() {
		return m_goals + m_assists;
	}
	
	/**
	 * Saves the hockey stats object to the given filename
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
			out.write(m_gamesPlayed);
			out.write(m_goals);
			out.write(m_assists);
		}
		finally {
			out.close();
		}
	}
	
	/**
	 * Loads the hockey stats object from the given filename
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
			m_gamesPlayed = in.readInt();
			m_goals = in.readInt();
			m_assists = in.readInt();
		}
		finally {
			in.close();
		}
		
	}
	
	/**
	 * Subtract the values of the passed in HockeyStats object from this object
	 * @param hs HockeyStats object whose values will be subtracted from this
	 * @pre true
	 * @post The HockeyStats object has been changed. The values are the new subtracted values.
	 * @return this object 
	 */
	public HockeyStats subtract(HockeyStats hs) {
		m_gamesPlayed -= hs.m_gamesPlayed;
		m_goals -= hs.m_goals;
		m_assists -= hs.m_assists;
		return this;
	}
	
	/**
	 * Add the values of the passed in HockeyStats object to this object
	 * @param hs HockeyStats object whose values will be added to this
	 * @pre true
	 * @post The HockeyStats object has been changed. The values are the new added values.
	 * @return this object
	 */
	public HockeyStats add(HockeyStats hs) {
		m_gamesPlayed += hs.m_gamesPlayed;
		m_goals += hs.m_goals;
		m_assists += hs.m_assists;
		return this;
	}
}
