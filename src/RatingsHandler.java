import java.util.ArrayList;
import java.util.Collections;

/**
 * RatingsHandler is used to display data from the database of user ratings.
 * @author Richard Nysäter
 *
 */
public class RatingsHandler {

	/**
	 * Main method.
	 * @param args Is not used.
	 */
	public static void main(String args[]){
		run();
	}

	/**
	 * Prints the name of the two songs in a pair and displays the rating, min and max votes as well as the standard deviation.
	 */
	public static void run(){
		final int OUTLIERS_TO_REMOVE = 2;
		
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

		ArrayList<UserRating> userRatings = new ArrayList<UserRating>();
		DatabaseExtractor db = new DatabaseExtractor("Databasedetails");
		
		System.out.println("Now running...");
		for(int i = 0; i<allsongs.length;i++){
			for(int j = i; j<allsongs.length;j++){
				if(j != i){
					userRatings.add(new UserRating(allSongNames[i],allSongNames[j],db.getRatings(""+allsongs[i],""+allsongs[j]),OUTLIERS_TO_REMOVE));
				}
			}
		}
		Collections.sort(userRatings);
		double stddev =0;
		for(UserRating u : userRatings){
			stddev+= u.getStddev();
			u.printRating();
		}
		System.out.println("Average standard devation is: "+stddev/userRatings.size());
	}

	/**
	 * Represents the user ratings for a song pair, only used internally. Calculates the average and standard deviation.
	 * @author Richard Nysäter
	 *
	 */
	private static class UserRating implements Comparable<UserRating>{
		String songOne, songTwo;
		int maxRating = 0;
		int minRating = 101; //Highest user rating is 100
		double avgRating, stddev = 0;

		/**
		 * Constructor for UserRating. Uses average rating.
		 * @param songOne Name of the first song
		 * @param songTwo Name of the second song
		 * @param ratings ArrayList containing all the votes for this pair
		 */
		UserRating(String songOne, String songTwo, ArrayList<Integer> ratings){
			this.songOne = songOne;
			this.songTwo = songTwo;
			setAverageRating(ratings);
			setStddev(ratings);
		}

		/**
		 * Constructor for UserRating. Removes outliers before calculating the average rating.
		 * @param songOne Name of the first song
		 * @param songTwo Name of the second song
		 * @param ratings ArrayList containing all the votes for this pair
		 * @param outliersToRemove How many outliers to remove from both the high and low end.
		 */
		UserRating(String songOne, String songTwo, ArrayList<Integer> ratings, int outliersToRemove){
			this.songOne = songOne;
			this.songTwo = songTwo;
			setFixedAverageRating(ratings,outliersToRemove);
			setFixedStddev(ratings,outliersToRemove);
		}

		/**
		 * Sets the min max and average ratings for this song pair
		 * @param ratings The ArrayList of ratings
		 */
		private void setAverageRating(ArrayList<Integer> ratings){
			for(int i : ratings){
				if(i>maxRating){
					maxRating = i;
				}
				if(i<minRating){
					minRating = i;
				}
				avgRating += i;
			}
			avgRating /= ratings.size();
		}

		/**
		 * First removes the specified amount of outliers from the higher and lower end.
		 * Then sets the min max and fixed average ratings for the song pair.
		 * @param ratings The ArrayList of ratings
		 */
		private void setFixedAverageRating(ArrayList<Integer> ratings, int outliersToRemove){
			Collections.sort(ratings);
			for(int i = outliersToRemove; (i+outliersToRemove)<ratings.size();i++){
				if(ratings.get(i)>maxRating){
					maxRating = ratings.get(i);
				}
				if(ratings.get(i)<minRating){
					minRating = ratings.get(i);
				}
				avgRating += ratings.get(i);
			}
			avgRating /= (ratings.size()-outliersToRemove*2);
		}

		/**
		 * Sets the min max and median ratings for this song pair
		 * @param ratings The ArrayList of ratings
		 */
		private void setMedianRating(ArrayList<Integer> ratings){
			Collections.sort(ratings);
			maxRating = ratings.get(ratings.size()-1);
			minRating = ratings.get(0);
			if(ratings.size()%2 == 1){
				avgRating = ratings.get(ratings.size()/2); 
			}
			else{
				avgRating = (ratings.get((int)Math.ceil(ratings.size()/2))+ratings.get((int)Math.floor(ratings.size()/2)))/2;
			}
		}

		/**
		 * Calculate the standard deviation.
		 * @param ratings
		 */
		private void setStddev(ArrayList<Integer> ratings){
			for(int i : ratings){
				stddev += Math.pow(i-avgRating,2);
			}
			stddev = Math.sqrt(stddev/ratings.size());
		}

		/**
		 * First removes the specified amount of outliers from the higher and lower end.
		 * The calculate the standard deviation.
		 * @param ratings
		 */
		private void setFixedStddev(ArrayList<Integer> ratings, int outliersToRemove){
			for(int i = outliersToRemove; (i+outliersToRemove)<ratings.size();i++){
				stddev += Math.pow(ratings.get(i)-avgRating,2);
			}
			stddev = Math.sqrt(stddev/(ratings.size()-outliersToRemove*2));
		}

		/**
		 * @return This song pair's calculated rating (average, median or fixed average)
		 */
		public double getRating(){
			return avgRating;
		}

		/**
		 * @return This song pair's standard deviation
		 */
		public double getStddev(){
			return stddev;
		}

		/**
		 * @return  The name of first song in the song pair.
		 */
		public String getSongOne(){
			return songOne;
		}

		/**
		 * @return The name of second song in the song pair.
		 */
		public String getSongTwo(){
			return songTwo;
		}

		/**
		 * @return The highest rating on this pair
		 */
		public int getMaxRating(){
			return maxRating;
		}

		/**
		 * @return The lowest rating on this pair
		 */
		@SuppressWarnings("unused")
		public int getMinRating(){
			return minRating;
		}

		/**
		 * Prints this song pairs info. (Names of songs, calculated rating etc)
		 */
		public void printRating(){
			System.out.println(songOne+" and\t"+songTwo+"\tAverage rating: "+Math.round(avgRating)+"\tStddev: "+Math.round(stddev)+"\tMax:"+maxRating+"\tMin: "+minRating);
		}

		@Override
		public int compareTo(UserRating userRating) {
			if (userRating instanceof UserRating){
				if((this.avgRating - ((UserRating)userRating).getRating())>0){
					return 1;
				}
				else if((this.avgRating - ((UserRating)userRating).getRating())<0){
					return -1;
				} 
				else{
					return 0;
				}
			}
			else{
				throw new ClassCastException("Expected UserRating but received "+userRating.getClass());
			}
		}

	}

}
