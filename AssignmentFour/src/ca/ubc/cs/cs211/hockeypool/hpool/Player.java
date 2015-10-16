package ca.ubc.cs.cs211.hockeypool.hpool;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import ca.ubc.cs.cs211.hockeypool.util.Utilities;

// Consider making a PlayerNotExistException

/**
 * 
 * @author Elan Dubrofsky
 * 
 * Implements a hockey player for a hockey pool
 * 
 */
public class Player implements Comparable<Player>, Serializable {
	private String m_name;

	private SortedMap<Calendar, HockeyStats> m_statsMap; // Store hockey stats
														// for each date of the
														// season

	private String m_pos;

	private String m_team;

	private static final long serialVersionUID = 1L;

	
	private class CalendarComparator implements Comparator<Calendar>, Serializable {
		private static final long serialVersionUID = 1L;

		/**
		 * If two dates fall into the same calendar day they are equal.
		 * Otherwise, they are ordered by the date comperator.
		 * 
		 * @param dae1
		 * @param date2
		 * @return
		 */
		public int compare(Calendar date1, Calendar date2) {
			boolean sameYear  = date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR);
			boolean sameMonth  = date1.get(Calendar.MONTH) == date2.get(Calendar.MONTH);
			boolean sameDay  = date1.get(Calendar.DAY_OF_MONTH) == date2.get(Calendar.DAY_OF_MONTH);
			if(sameYear&&sameMonth&&sameDay)
				return 0;
			
			return date1.compareTo(date2);
		}
	}

	/**
	 * Initializes a player object
	 * 
	 * @param name
	 *            the player's name
	 * @pre true
	 * @post A player has been created with a blank team and position and no
	 *       stats.
	 */
	public Player(String name) {
		m_name = name;
		m_statsMap = new TreeMap<Calendar, HockeyStats>(new CalendarComparator());
		this.m_pos = "";
		this.m_team = "";
	}

	/**
	 * Get the player's name
	 * 
	 * @pre true
	 * @post Nothing changes
	 * @return the player's name
	 */
	public String getName() {
		return m_name;
	}

	/**
	 * Get the player's position
	 * 
	 * @pre true
	 * @post Nothing changes
	 * @return the player's position
	 */
	public String getPos() {
		return m_pos;
	}

	/**
	 * Get the player's team
	 * 
	 * @pre true
	 * @post Nothing changes
	 * @return the player's team
	 */
	public String getTeam() {
		return m_team;
	}

	/**
	 * Get the most recent date that the player has stats for
	 * 
	 * @pre
	 * @post Nothing changes
	 * @return null if the player has no stats. Otherwise return the most recent
	 *         date that there are stats for
	 */
	public Calendar getMostRecentDate() {
		if (!containsStats())
			return null;
		return m_statsMap.lastKey();
	}

	/**
	 * Get the player's statistics for the given date
	 * 
	 * @param d
	 *            the date to return the player's stats for
	 * @pre true
	 * @post nothing changes
	 * @return zero stats if d is null or if the player does not contain stats for the
	 *         given date. Otherwise, return the stats on the given day
	 */
	public HockeyStats getPlayerStats(Calendar d) {
		if (d == null || !m_statsMap.containsKey(d))
			return new HockeyStats();
		return m_statsMap.get(d);
	}

	/**
	 * Get the player's most recent statistics
	 * 
	 * @pre true
	 * @post nothing changes
	 * @return zero stats if d is null or if the player does not contain stats for the
	 *         given date. Otherwise, return the player's most recent stats
	 */
	public HockeyStats getMostRecentPlayerStats() {
		return getPlayerStats(getMostRecentDate());
	}

	/**
	 * Get the player's stats for the time period between the date of the most
	 * recent stats and the given number of days back from that date
	 * 
	 * @param daysBack
	 *            the number of days back from the date of the most recent stats
	 *            to start counting statistics
	 * @pre true
	 * @post nothing changes
	 * @return most recent stats if there are no stats for the date calculated as the given
	 *         number of days back from the date of the most recent stats
	 *         Otherwise, return the player's stats for the time period between
	 *         the date of the most recent stats and the given number of days
	 *         back from that date
	 */
	public HockeyStats getDaysBackStats(int daysBack) {
		Calendar daysBackDate = Calendar.getInstance();
		daysBackDate.add(Calendar.DAY_OF_MONTH, -daysBack-1);
		
		//NEW
		if(daysBackDate.after(getMostRecentDate()))
			return new HockeyStats();
		
		HockeyStats statsOnDay = getPlayerStats(daysBackDate);
		HockeyStats mostRecentPlayerStatsClone = getMostRecentPlayerStats()
				.clone();
		return mostRecentPlayerStatsClone.subtract(statsOnDay);
	}

	/**
	 * Get wether or not the player has any stats yet
	 * 
	 * @return true if there are stats for this player, false otherwise
	 */
	public boolean containsStats() {
		return m_statsMap.size() > 0;
	}

	/**
	 * Get wether or not the player contains stats for the given date
	 * 
	 * @param date
	 *            the date to check if the player has stats for
	 * @return true if there player contains statistics for the given date,
	 *         false otherise
	 */
	public boolean conatinsDate(Calendar date) {
		return m_statsMap.containsKey(date);
	}

	/**
	 * Add stats to the player for the given date
	 * 
	 * @param d
	 *            the date to add stats for
	 * @param ps
	 *            the player stats to be added
	 * @pre true
	 * @post If the player already has stats for date d, nothing changes.
	 *       Otherwise, the stats are added for date d
	 * @return true if the stats were added to the player's stats collection,
	 *         false otherwise
	 */
	public boolean addStats(Calendar d, HockeyStats ps) {
		if (m_statsMap.containsKey(d))
			return false;
		m_statsMap.put(d, ps);
		return true;
	}

	/**
	 * Add zero stats for the player for the given date (i.e. no goals, assists
	 * or games played)
	 * 
	 * @pre true
	 * @post if the date isn't null and if the player doesn't already have stats
	 *       for that date, zero stats are added for the given date
	 * @param date
	 *            the date to add the zero stats on
	 */
	public void addZeroStats(Calendar date) {
		addStats(date, new HockeyStats());
	}
	
	/**
	 * Clears the structure that stores the hockey stats
	 * @pre true
	 * @post the player has no stats
	 */
	public void clearStats() {
		m_statsMap.clear();
	}

	/**
	 * Returns a string that identifies the player
	 * 
	 * @pre true
	 * @post nothing changes
	 * @return a string identifying the player
	 */
	public String toString() {
		return m_name;
	}

	public int compareTo(Player p) {
		// Players are primarily sorted by points (more points are higher)
		int pointDiff = p.getMostRecentPlayerStats().getPoints()
				- getMostRecentPlayerStats().getPoints();
		if (pointDiff != 0)
			return pointDiff;
		// If two players have the same number of points, they are sorted by
		// goals (more goals are higher)
		int goalDiff = p.getMostRecentPlayerStats().getGoals()
				- getMostRecentPlayerStats().getGoals();
		if (goalDiff != 0)
			return goalDiff;
		// If the two players have the same number of goals and points, they are
		// sorted by games player (less games played are higher)
		int gpDiff = getMostRecentPlayerStats().getGamesPlayed()
				- p.getMostRecentPlayerStats().getGamesPlayed();
		if (gpDiff != 0)
			return gpDiff;

		// If the two players have the same goals, points and games played, sort
		// by name
		return toString().compareTo(p.toString());
	}

	/**
	 * Compares the player to the specified object
	 * 
	 * @param o
	 *            the object to be compared
	 * @pre true
	 * @post true
	 * @return true if obj is a Player whose name matches the current player;
	 *         false otherwise
	 */
	public boolean equals(Object o) {
		if ((o == null) || (!(o instanceof Player)))
			return false;
		Player p = (Player) o;
		return p.toString().equals(toString());
	}
	
	/**
	 * Update the player's stats from the website up to yesterday
	 * @pre true
	 * @post The player has a full collection of stats, or not if he wasn't found
	 * @return true if the player is found, false otherwise
	 */
	public boolean update() {
		clearStats();
		String playerWebsiteName = m_name.replace(' ', '_');
		ArrayList<String> webpage = Utilities.getWebpage(Utilities.STATS_URL_PREFIX+playerWebsiteName+Utilities.STATS_URL_POSTFIX);
		SortedMap<Calendar,int[]> localStatsMap = new TreeMap<Calendar,int[]>(new CalendarComparator());
		int currentLineIndex = 0;
		String currentLine = webpage.get(currentLineIndex);
		boolean endOfPage = false;
		boolean firstTime = true;
		int i;
		StringTokenizer st;
		while(true) {
			while(currentLine.indexOf("<td class=\"scoreNameBottom_2\"><a href=\"/hockey/nhl/scores/") == -1) {
				if(currentLineIndex == (webpage.size()-1)) {
					endOfPage = true;
					break;
				} else {
					//Update Position
					if(currentLine.indexOf("| #")!=-1) {
						st = new StringTokenizer(currentLine, "-<", false);
						st.nextToken();
						m_pos = st.nextToken().trim();
					}
					currentLine = webpage.get(++currentLineIndex);
				}
			}
			if(endOfPage)
				break;
			
			// Get date
			st = new StringTokenizer(currentLine,"<>",false);
			Calendar date;
			for(i=0; i<3; ++i) {
				st.nextToken();
			}
			date = Utilities.getCalendarFromString(st.nextToken());
			
			currentLine = webpage.get(++currentLineIndex);
			
			//Get team
			st = new StringTokenizer(currentLine, "<>", false);
			st.nextToken(); st.nextToken();
			String team = st.nextToken();
			if((team.indexOf("West")==0) || (team.indexOf("East")==0))
				continue;
			if(firstTime) {
				m_team = team;
				firstTime = false;
			}
			
			++currentLineIndex;
			
			//Get goals and assists
			int[] goalsAssists = new int[2];
			for(i=0;i<2;++i) {
				currentLine = webpage.get(++currentLineIndex);
				st = new StringTokenizer(currentLine, "<>", false);
				st.nextToken(); st.nextToken();
				goalsAssists[i] = Integer.parseInt(st.nextToken());
			}
			
			localStatsMap.put(date, goalsAssists);
			
		}
		
		if(localStatsMap.isEmpty())
			return false;
		
		
		Calendar date = new GregorianCalendar();
		date.set(2008, Calendar.OCTOBER, 4);
		Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DAY_OF_MONTH, -1);
		HockeyStats stats = new HockeyStats();
		CalendarComparator calComparator = new CalendarComparator();
		while(calComparator.compare(date, yesterday) <= 0) {
			if(localStatsMap.containsKey(date)) {
				int[] goalsAssists = localStatsMap.get(date);
				stats.add(new HockeyStats(1,goalsAssists[0],goalsAssists[1]));
			} 
			addStats((Calendar)date.clone(),stats.clone());
			date.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		return true;
	}
	
}
