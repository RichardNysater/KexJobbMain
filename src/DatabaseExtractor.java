import java.io.File;
import java.io.FileNotFoundException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
/**
 * Handles queries to the mysql database.
 * Url to the database, user and password to database are stored separately a file called Databasedetails.
 * @author Richard Nysäter.
 */
public class DatabaseExtractor {
	String url = "";
	String user = "";
	String password = "";
	String query = "";

	/**
	 * Constructor for the DatabaseExtractor. Reads the database details from the specified file.
	 * @param databaseDetailsPath The full path to the file containing URL, user and password to the database.
	 */
	public DatabaseExtractor(String databaseDetailsPath){
		try {
			File file = new File(databaseDetailsPath);
			Scanner scan;
			scan = new Scanner(file);
			url = scan.nextLine();
			user = scan.nextLine();
			password = scan.nextLine();
			scan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param songOne The ID of the first song.
	 * @param songTwo The ID of the second song.
	 * @return An ArrayList of every rating on the song pair.
	 */
	public ArrayList<Integer> getRatings(String songOne, String songTwo){
		try {
			query = "SELECT * FROM reviews WHERE Songone = ? AND Songtwo = ?";
			ArrayList<Integer> songRatings = new ArrayList<Integer>();
			Connection con = (Connection) DriverManager.getConnection(url, user, password);
			PreparedStatement pst;

			pst = (PreparedStatement) con.prepareStatement(query);

			pst.setString(1, songOne);
			pst.setString(2, songTwo);
			ResultSet rs = pst.executeQuery();
			while(rs.next()){
				songRatings.add(Integer.parseInt(rs.getString("Rating")));
			}
			pst.close();
			con.close();
			return songRatings;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
	 * @return An ArrayList of integer arrays. Every integer array represents a pair of songs.
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
