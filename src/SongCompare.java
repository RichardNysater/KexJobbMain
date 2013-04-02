import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import ncsa.hdf.object.h5.H5File;

/**
 * Compares a list of songs to a specified amount of songs from the Million Song Dataset (http://labrosa.ee.columbia.edu/millionsong/)
 * @author Richard Nysäter
 *
 */
public class SongCompare {
	static final int COMPARE_AMOUNT= 30; //The amount of songs to compare against in the MSD.
	static ArffCreator arff;
	static int iterations;
	static double[] timbreOne;
	static double[] confOne;
	static double[] diffConf = new double[12];
	static double[] diffTimbre = new double[12];
	static String songOneArtist;
	static String songOneTitle;
	static double songOneTempo;
	static double songOneLoudness;
	static int songOneMode;
	static int songOneKey;
	static String[] songs;
	
	/**
	 * Main class of SongCompare. Prints the comparisons to an arff file and prints the time taken.
	 * @param args[0] The absolute path to the directory of the Million Song Dataset.
	 * @param args[1] The absolute path to the file you want to print the comparisons to.
	 * @param args[2] The absolute path to the file containing the absolute paths to the songs you want comparisons to.
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception{
		System.out.println("Starting...");
		//		File[] files = new File("C:\\Users\\Shaan\\Desktop\\songs").listFiles(); //input is filepath
		arff = new ArffCreator("MSD");
		importSongs(args[2]);
		File[] files = new File(args[0]).listFiles();
		long startTime = System.currentTimeMillis();
		compareSongs(files);
		System.out.println("Extraction finished after "+(System.currentTimeMillis()-startTime)/1000 +" seconds.");
		arff.printData(args[1]);
	}

	/**
	 * Imports the songs to compare against the MSD.
	 * @param pathToSongList Absolute path to a file containing absolute paths to the songs to compare against the MSD.
	 */
	private static void importSongs(String pathToSongList){
		try {
			File file = new File(pathToSongList);
			Scanner scan;
			songs = new String[count(pathToSongList)];
			scan = new Scanner(file);
			for(int i = 0; i<songs.length;i++){
				songs[i] = scan.nextLine();
			}
			scan.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Help method for importSongs(). Counts the amount of lines in the file.
	 * @param filename The file to count lines in.
	 * @return The total amount of lines in the file.
	 * @throws IOException
	 */
	private static int count(String filename) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		int lines = 0;
		while (reader.readLine() != null) lines++;
		reader.close();
		return lines;
	}

	/**
	 * Iterates over the MSD for every song.
	 * @param files The list of files to compare (including subfolders)
	 * @throws Exception 
	 */
	public static void compareSongs(File[] files) throws Exception{
		for(int i = 0; i<songs.length;i++){
			System.out.println("Now comparing against "+songs[i]);
			H5File songOneH5 = hdf5_getters.hdf5_open_readonly(songs[i]);
			timbreOne = MSDExtractor.getTimbre(songOneH5);
			confOne = MSDExtractor.getSegmentsConfidence(songOneH5);
			songOneArtist = MSDExtractor.getArtist(songOneH5);
			songOneTitle = MSDExtractor.getTitle(songOneH5);
			songOneLoudness = MSDExtractor.getLoudness(songOneH5);
			songOneMode = MSDExtractor.getMode(songOneH5);
			songOneTempo = MSDExtractor.getTempo(songOneH5);
			songOneKey = MSDExtractor.getKey(songOneH5);
			iterations = 0;
			songOneH5.close();
			recurseOverMSD(files);
		}
	}

	/**
	 * Recurses over the MSD until the COMPARE_AMOUNT of songs have been reached.
	 * @param files The list of files to recurse over.
	 * @param songOne The song to compare the MSD songs to.
	 */
	public static void recurseOverMSD(File[] files){
		try{
			for (File file : files){
				if(iterations < COMPARE_AMOUNT){
					if (file.isDirectory()){
						recurseOverMSD(file.listFiles());
					}
					else{
						iterations+= getDifference(file);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * If both songs are pop or rock, adds the difference between songs (Ratio of tempo, loudness, timbre and segment confidence. Absolute value of mode and key) and the song pairs artists and titles.
	 * @param songOne The path to the first song.
	 * @param songTwo The file of the second song.
	 * @return
	 */
	public static int getDifference(File songTwo){
		try{
			H5File songTwoH5 = hdf5_getters.hdf5_open_readonly(songTwo.getAbsolutePath());
			double[] timbreTwo = MSDExtractor.getTimbre(songTwoH5);
			double[] confTwo = MSDExtractor.getSegmentsConfidence(songTwoH5);
			double[] frequency;
			double[] weight;
			String[] genres;
			boolean sameGenre = false;

			for(int i = 0; i<12;i++){
				diffTimbre[i] = Math.max(timbreOne[i], timbreTwo[i])/Math.min(timbreOne[i],timbreTwo[i]);
				diffConf[i] = confOne[i]+confTwo[i];
			}


			try{
				genres = MSDExtractor.getGenres(songTwoH5);
			}catch(Exception e){
				return 0;
			}
			frequency = MSDExtractor.getFrequency(songTwoH5);
			weight = MSDExtractor.getWeight(songTwoH5);

			for(int i = 0; i< genres.length;i++){
				if((genres[i].equals("pop") && frequency[i] > 0.8 && weight[i] > 0.8) || (genres[i].equals("rock") && frequency[i] > 0.8 && weight[i] > 0.8)){
					sameGenre = true;
				}
			}
			if(sameGenre){
				arff.addSong(songOneArtist+" - "+songOneTitle,MSDExtractor.getArtist(songTwoH5)+" - "+MSDExtractor.getTitle(songTwoH5),Math.max(songOneTempo,MSDExtractor.getTempo(songTwoH5))/Math.min(songOneTempo, MSDExtractor.getTempo(songTwoH5)),Math.max(songOneLoudness,MSDExtractor.getLoudness(songTwoH5))/Math.min(songOneLoudness, MSDExtractor.getLoudness(songTwoH5)),Math.abs(songOneMode-MSDExtractor.getMode(songTwoH5)),Math.abs(songOneKey-MSDExtractor.getKey(songTwoH5)),diffTimbre,diffConf);
				MSDExtractor.close(songTwoH5);
				return 1;
			}
			songTwoH5.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return 0;
	}
}