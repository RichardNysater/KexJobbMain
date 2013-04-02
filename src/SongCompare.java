import java.io.File;

/**
 * Compares a list of songs to a specified amount of songs from the Million Song Dataset (http://labrosa.ee.columbia.edu/millionsong/)
 * @author Richard Nysäter
 *
 */
public class SongCompare {
	static ArffCreator arff;
	static int iterations;
	static final int COMPARE_AMOUNT= 30;
	static String[] songs = {
		"C:\\Users\\Shaan\\Desktop\\songs\\celine\\TRWAHLU128F42799E6.h5",
		"C:\\Users\\Shaan\\Desktop\\songs\\dido\\TRALLSG128F425A685.h5",
		"C:\\Users\\Shaan\\Desktop\\songs\\green day\\TRJKVRF128E07857BF.h5",
		"C:\\Users\\Shaan\\Desktop\\songs\\Iron maider\\TRXEWRK128F147FB6A.h5",
		"C:\\Users\\Shaan\\Desktop\\songs\\metallica\\TRRNZBN128F147CC7A.h5",
		"C:\\Users\\Shaan\\Desktop\\songs\\natalie\\TRZRSVX128F425A689.h5",
		"C:\\Users\\Shaan\\Desktop\\songs\\oasis\\TRENOOE128F148EF23.h5",
		"C:\\Users\\Shaan\\Desktop\\songs\\scorpions\\TRCZIUO12903CE5274.h5",
		"C:\\Users\\Shaan\\Desktop\\songs\\timo\\TRJVKUM12903CCC29D.h5",
		"C:\\Users\\Shaan\\Desktop\\songs\\whitesnake\\TROOQSD128F4289971.h5"
	};

	/**
	 * Main class of SongCompare. Prints the comparisons to an arff file and prints the time taken.
	 * @param args[0] The absolute path to the directory of the Million Song Dataset.
	 * * @param args[1] The absolute path to the file you want to print the comparisons to.
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception{
		System.out.println("Starting...");
		//		File[] files = new File("C:\\Users\\Shaan\\Desktop\\songs").listFiles(); //input is filepath
		arff = new ArffCreator("MSD");
		File[] files = new File(args[0]).listFiles();
		long startTime = System.currentTimeMillis();
		compareSongs(files);
		System.out.println("Extraction finished after "+(System.currentTimeMillis()-startTime)/1000 +" seconds.");
		arff.printData(args[1]);
	}

	/**
	 * Iterates over the MSD for every song.
	 * @param files The list of files to compare (including subfolders)
	 */
	public static void compareSongs(File[] files){
		for(int i = 0; i<songs.length;i++){
			System.out.println("Now comparing against "+songs[i]);
			iterations = 0;
			recurseOverMSD(files,songs[i]);
		}
	}

	/**
	 * Recurses over the MSD until the COMPARE_AMOUNT of songs have been reached.
	 * @param files The list of files to recurse over.
	 * @param songOne The song to compare the MSD songs to.
	 */
	public static void recurseOverMSD(File[] files, String songOne){
		try{
			for (File file : files){
				if(iterations < COMPARE_AMOUNT){
					if (file.isDirectory()){
						recurseOverMSD(file.listFiles(), songOne);
					}
					else{
						iterations+= getDifference(songOne,file);
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
	public static int getDifference(String songOne, File songTwo){
		try{
			MSDExtractor gOne = new MSDExtractor(songOne);
			MSDExtractor gTwo = new MSDExtractor(songTwo.getAbsolutePath());
			double[] timbreOne = gOne.getTimbre();
			double[] timbreTwo = gTwo.getTimbre();
			double[] confOne = gOne.getSegmentsConfidence();
			double[] confTwo = gTwo.getSegmentsConfidence();
			double[] diffConf = new double[12];
			double[] diffTimbre = new double[12];
			double[] frequency;
			double[] weight;
			boolean sameGenre = false;

			for(int i = 0; i<12;i++){
				diffTimbre[i] = Math.max(timbreOne[i], timbreTwo[i])/Math.min(timbreOne[i],timbreTwo[i]);
				diffConf[i] = confOne[i]+confTwo[i];
			}

			String[] genres;
			try{
				genres = gTwo.getGenres();
			}catch(Exception e){
				return 0;
			}
			frequency = gTwo.getFrequency();
			weight = gTwo.getWeight();

			for(int i = 0; i< genres.length;i++){
				if((genres[i].equals("pop") && frequency[i] > 0.8 && weight[i] > 0.8) || (genres[i].equals("rock") && frequency[i] > 0.8 && weight[i] > 0.8)){
					sameGenre = true;
				}
			}
			if(sameGenre){
				arff.addSong(gOne.getArtist()+" - "+gOne.getTitle(),gTwo.getArtist()+" - "+gTwo.getTitle(),Math.max(gOne.getTempo(),gTwo.getTempo())/Math.min(gOne.getTempo(), gTwo.getTempo()),Math.max(gOne.getLoudness(),gTwo.getLoudness())/Math.min(gOne.getLoudness(), gTwo.getLoudness()),Math.abs(gOne.getMode()-gTwo.getMode()),Math.abs(gOne.getKey()-gTwo.getKey()),diffTimbre,diffConf);
				gOne.close();
				gTwo.close();
				return 1;
			}
			gOne.close();
			gTwo.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return 0;
	}
}