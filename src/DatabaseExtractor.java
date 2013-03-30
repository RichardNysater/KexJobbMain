import java.sql.DriverManager;
import java.sql.SQLException;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Handles queries to the mysql database.
 * Url to the database, user and password to database are stored separately a file called Databasedetails.
 * @author Shaan.
 */
public class DatabaseExtractor {
	static String url = "";
	static String user = "";
	static String password = "";
	static String query = "";

	/**
	 * Reads the URL, username and password for the mysql database from a file called 'Databasedetails'.
	 */
	public static void main(String[] args) {
		try {
			int[] allsongs = new int[10];
			allsongs[0] = 3253551;				//Celine Dion - A New Day Has Come
			allsongs[1] = 3107323;				//Dido - White Flag
			allsongs[2] = 73499;			//Green Day - Basket Case
			allsongs[3] = 417192;		   //Metallica - The Unforgiven
			allsongs[4] = 434647;		  //Iron Maiden - The Trooper
			allsongs[5] = 3107327;		  //Natalie Imbruglia - Smoke
			allsongs[6] = 518324;				//Oasis - Wonderwall
			allsongs[7] = 9048617;				//Scorpions - Wind Of Change
			allsongs[8] = 8470520;		  //Timo Räisänen - About You Now
			allsongs[9] = 1079677;				//Whitesnake - Here I Go Again
			
			String[] allSongNames = new String[10];
			allSongNames[0] = "Celine Dion\t";				//Celine Dion - A New Day Has Come
			allSongNames[1] = "Dido\t\t";				//Dido - White Flag
			allSongNames[2] = "Green Day\t";			//Green Day - Basket Case
			allSongNames[3] = "Metallica\t";		   //Metallica - The Unforgiven
			allSongNames[4] = "Iron Maiden\t";		  //Iron Maiden - The Trooper
			allSongNames[5] = "Natalie Imbruglia";		  //Natalie Imbruglia - Smoke
			allSongNames[6] = "Oasis\t\t";				//Oasis - Wonderwall
			allSongNames[7] = "Scorpions\t";				//Scorpions - Wind Of Change
			allSongNames[8] = "Timo Räisänen\t";		  //Timo Räisänen - About You Now
			allSongNames[9] = "Whitesnake\t";				//Whitesnake - Here I Go Again
			
			File file = new File("Databasedetails");
			Scanner scan;
			scan = new Scanner(file);
			url = scan.nextLine();
			user = scan.nextLine();
			password = scan.nextLine();
			scan.close();
			ArrayList<String> avgRatings = new ArrayList<String>();
			try {
				for(int i = 0; i<allsongs.length;i++){
					for(int j = i; j<allsongs.length;j++){
						if(j != i){
							ArrayList<String> songRatings = getRatings(""+allsongs[i],""+allsongs[j]);
							double avgRating = 0;
							int max = 0;
							int min = 101;
							double deviation = 0;
							for(String s : songRatings){
								if(Integer.parseInt(s)>max){
									max = Integer.parseInt(s);
								}
								if(Integer.parseInt(s)<min){
									min = Integer.parseInt(s);
								}
								avgRating += Integer.parseInt(s);
							}
							avgRating /= songRatings.size();
							
							for(String s : songRatings){
								deviation += Math.pow(Integer.parseInt(s)-avgRating,2);
							}
							deviation = Math.sqrt(deviation/songRatings.size());
							avgRatings.add(allSongNames[i]+" and\t"+allSongNames[j]+"\tAverage rating: "+Math.round(avgRating)+"\tStddev: "+Math.round(deviation)+"\tMax:"+max+"\tMin: "+min);
						}
					}
				}
				for(String s : avgRatings){
					System.out.println(s);
				}


			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	
	
	public static ArrayList<String> getRatings(String songOne, String songTwo) throws SQLException{
		query = "SELECT * FROM reviews WHERE Songone = ? AND Songtwo = ?";
		ArrayList<String> songRatings = new ArrayList<String>();
		Connection con = (Connection) DriverManager.getConnection(url, user, password);
		PreparedStatement pst = (PreparedStatement) con.prepareStatement(query);
		pst.setString(1, songOne);
		pst.setString(2, songTwo);
		ResultSet rs = pst.executeQuery();
		while(rs.next()){
			songRatings.add(rs.getString("Rating"));
		}
		pst.close();
		con.close();
		return songRatings;
	}
	
	/**
	 * Adds a similarity rating between a pair of songs to the database.
	 * @param id The rater's session id.
	 * @param songOne The id of the first song.
	 * @param songTwo The id of the second song.
	 * @param Rating The assigned rating between the songs.
	 * @param Ip The rater's ip.
	 * @param exampleReview Determines whether this was an example rating or not.
	 */
	public void addRating(String id, String songOne, String songTwo, String Rating, String Ip, String exampleReview) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		if(exampleReview.equals("rate")){
			query = "INSERT INTO reviews (Id, Songone, Songtwo, Time, Rating, Ip) VALUES (?,?,?,?,?,?)";
		}
		else if(exampleReview.equals("rateexample")){
			query = "INSERT INTO example (Id, Songone, Songtwo, Time, Rating, Ip) VALUES (?,?,?,?,?,?)";
		}
		else{
			return;
		}
		try {
			Connection con = (Connection) DriverManager.getConnection(url, user, password);
			PreparedStatement pst = (PreparedStatement) con.prepareStatement(query);
			pst.setString(1, id);
			pst.setString(2, songOne);
			pst.setString(3, songTwo);

			java.util.Date utilDate = new java.util.Date();
			java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(utilDate.getTime());

			pst.setTimestamp(4, sqlTimestamp);
			pst.setInt(5, Integer.parseInt(Rating));
			pst.setString(6, Ip);
			pst.executeUpdate();

			pst.close();
			con.close();

		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		System.out.println("Finished addRating()");
	}

	/**
	 * Returns the votes the user has already completed.
	 * @param Ip The user's ip.
	 * @return Returns an ArrayList of integer arrays. Every integer array represents a pair of songs.
	 */
	public ArrayList<int[]> getVoted(String Ip){
		ArrayList<int[]> returnArray = new ArrayList<int[]>();
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		query = "SELECT * FROM reviews WHERE Ip = ?";

		try {
			Connection con = (Connection) DriverManager.getConnection(url, user, password);
			PreparedStatement pst = (PreparedStatement) con.prepareStatement(query);
			pst.setString(1, Ip);
			ResultSet rs = pst.executeQuery();

			while(rs.next()){
				int[] pair = new int[2];
				pair[0] = Integer.parseInt(rs.getString("Songone"));
				pair[1] = Integer.parseInt(rs.getString("Songtwo"));
				returnArray.add(pair);
			}
			pst.close();
			con.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		System.out.println("Finished getVoted()");
		return returnArray;
	}

	/**
	 * Determines if a user has completed the example ratings.
	 * @param Ip The user's ip.
	 * @return True if the examples are completed, false if they aren't.
	 */
	public boolean hasCompletedExample(String Ip){
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		query = "SELECT * FROM example WHERE Ip = ?";

		try {
			Connection con = (Connection) DriverManager.getConnection(url, user, password);
			PreparedStatement pst = (PreparedStatement) con.prepareStatement(query);
			pst.setString(1, Ip);
			ResultSet rs = pst.executeQuery();

			if(rs.last()){
				if(rs.getRow() > 2){
					return true;
				}
			}
			pst.close();
			con.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		System.out.println("Finished hasCompletedExample()");
		return false;
	}

	/**
	 * Determines if the user has rated all 45 pairs.
	 * @return True if the user has rated all of them, false if they haven't.
	 */
	public boolean hasFinishedRating(String ip){
		boolean finishedRating = false;
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}

		query = "SELECT * FROM reviews WHERE Ip = ?";

		try {
			Connection con = (Connection) DriverManager.getConnection(url, user, password);
			PreparedStatement pst = (PreparedStatement) con.prepareStatement(query);
			pst.setString(1, ip);
			ResultSet rs = pst.executeQuery();

			rs.last();
			if(rs.getRow() >= 45){
				finishedRating = true;
			}
			pst.close();
			con.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		System.out.println("Finished hasFinishedRating()");
		return finishedRating;
	}
}
