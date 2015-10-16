package ca.ubc.cs.cs211.hockeypool.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;


public class Utilities {
	public static final String STATS_URL_PREFIX = "http://www.sportsnet.ca/hockey/nhl/players/";
	public static final String STATS_URL_POSTFIX = "/by_game";

	/**
	 * Get a webpage as a list of Strings
	 * @param url The URL of the webpage
	 * @pre The url is of a valid webpage
	 * @post true
	 * @return A list of strings storing each line of HTML code for the webpage
	 */
	public static ArrayList<String> getWebpage(String url) {
		
		ArrayList<String> webpage = new ArrayList<String>();
		
		try {
			URL u = new URL(url);
			URLConnection uc = u.openConnection();
			DataInputStream dis = new DataInputStream(uc.getInputStream());
			String inputLine;
			while ((inputLine = dis.readLine()) != null) {
				webpage.add(inputLine);
			}
			dis.close();
		} catch (MalformedURLException me) {
			System.out.println("MalformedURLException: " + me);
		} catch (IOException e) {
			System.out.println("IOException: " + e);
		}	
		
		return webpage;
		
	}
	
	/**
	 * Gets a Calendar object representing the date represented by the String
	 * @param s String representing the date in the form "Oct 4, 2008"
	 * @pre s must be of the form "Oct 4, 2008"
	 * @return A Calendar object representing the date from the inputted String, or null if the string has improper format
	 */
	public static Calendar getCalendarFromString(String s) {
		StringTokenizer st = new StringTokenizer(s," ,", false);
		
		// Get the month
		String monthString = st.nextToken();
		int month;
		if(monthString.equals("Jan")) month = Calendar.JANUARY;
		else if(monthString.equals("Feb")) month = Calendar.FEBRUARY;
		else if(monthString.equals("Mar")) month = Calendar.MARCH;
		else if(monthString.equals("Apr")) month = Calendar.APRIL;
		else if(monthString.equals("May")) month = Calendar.MAY;
		else if(monthString.equals("Jun")) month = Calendar.JUNE;
		else if(monthString.equals("Jul")) month = Calendar.JULY;
		else if(monthString.equals("Aug")) month = Calendar.AUGUST;
		else if(monthString.equals("Sep")) month = Calendar.SEPTEMBER;
		else if(monthString.equals("Oct")) month = Calendar.OCTOBER;
		else if(monthString.equals("Nov")) month = Calendar.NOVEMBER;
		else if(monthString.equals("Dec")) month = Calendar.DECEMBER;
		else return null;
		
		int day,year;
		try {
			// Get the day
			day = Integer.parseInt(st.nextToken());
			
			// Get the year
			year = Integer.parseInt(st.nextToken());
		} catch(NumberFormatException e) {
			return null;
		}
		
		Calendar cal = new GregorianCalendar();
		cal.set(year, month, day);
		return cal;
	}
	
}
