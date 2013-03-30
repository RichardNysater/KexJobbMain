import java.io.BufferedWriter;
import java.io.File;


public class InfoExtractor {
	static ArffCreator arff;
	static BufferedWriter outfile;
	static int counter = 0;
	static int threads = 0;
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


	public static void main(String args[]) throws Exception{
		final double keyConfidence = 0.7;
		final double modeConfidence = 0.7;
		final double timeSignatureConfidence = 0.7;
		final double timbreConfidence = 0.7;
		System.out.println("Starting...");
		File[] files = new File("C:\\Users\\Shaan\\Desktop\\songs").listFiles(); //input is filepath
		//		File[] files = new File("F:\\Million Song Dataset").listFiles();
		arff = new ArffCreator("MSD");
		//		String leurl = getSong(files,"F:\\Million Song Dataset\\A\\P\\D\\TRAPDIH128F426DE71.h5");
		arff.printData();
		System.out.println("lmao");
		//		showFiles(files, keyConfidence, modeConfidence, timeSignatureConfidence, timbreConfidence);
		System.out.println("Extraction complete.");
		compareSongs();
	}

	public static void compareSongs(){
		for(int i = 0; i<songs.length;i++){
			for(int j = i; j<songs.length;j++){
				if(i != j){
					getDifference(songs[i],songs[j]);
				}
			}
		}
		arff.printData();
	}
	//	public static String getSong(File[] files, String songPath){
	//		String id = "951886";
	//		String consumerkey = "7dtywchqwpuh";
	//		String secretkey = "x6dgajvyu83sygcr";
	//		try{
	//			counter++;
	//			System.out.println(counter);
	//			for (File file : files){
	//				if (file.isDirectory()){
	//					if(counter >50 ){
	//						return getSong(file.listFiles(), songPath);
	//					}
	//					else{
	//						getSong(file.listFiles(), songPath);
	//					}
	//				}
	//				else{
	//					File filetwo = new File(songPath);
	//					getDifference(file,filetwo);
	//					MillionSongInfoGetpls getter = new MillionSongInfoGetpls(file.getAbsolutePath());
	//					String url = "http://api.7digital.com/1.2/track/preview?trackid=".concat(""+getter.getId()).concat("&oauth_consumer_key=").concat(consumerkey); 
	//					getter.close();	
	//					return url;
	//					//					System.out.println(getter.getTimbre(0.8));
	//					//					System.out.print(getter.getArtist()+" - "+ getter.getTitle()+ " Tempo: " +getter.getTempo()+" Mode "+getter.getMode() +" Loudness: " + getter.getLoudness()+"\n");
	//
	//				}
	//			}
	//		}catch(Exception e){
	//			e.printStackTrace();
	//		}
	//		return "Error";
	//	}

	public static void showFiles(File[] files, double keyConfidence, double modeConfidence, double timeSignatureConfidence, double timbreConfidence){
		try{
			System.out.println("Now running, please wait.");
			for (File file : files){
				if (file.isDirectory()){
					showFiles(file.listFiles(),keyConfidence, modeConfidence, timeSignatureConfidence, timbreConfidence);
					threads++;
				}
				else{
					counter++;
					MillionSongInfoGetpls getter = new MillionSongInfoGetpls(file.getAbsolutePath());
					if(counter%100000 == 0){
						arff.printData();
						System.exit(1);
					}
					arff.addSong(getter.getTempo(),getter.getLoudness(),getter.getMode(),getter.getKey(),getter.getTimbre(),getter.getSegmentsConfidence());
					System.out.println(counter);
					//					System.out.println(getter.getTimbre(0.8));
					System.out.print(getter.getArtist()+" - "+ getter.getTitle()+ ".#. Tempo: "+getter.getTempo() +" Id: " + getter.getId()+" filepath: "+file.getAbsolutePath()+"\n");
					getter.close();					
				}
			}
			arff.printData();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void getDifference(String songOne, String songTwo){
		try{
			File fileOne = new File(songOne);
			File fileTwo = new File(songTwo);
			MillionSongInfoGetpls gOne = new MillionSongInfoGetpls(fileOne.getAbsolutePath());
			MillionSongInfoGetpls gTwo = new MillionSongInfoGetpls(fileTwo.getAbsolutePath());
			double[] timbreOne = gOne.getTimbre();
			double[] timbreTwo = gTwo.getTimbre();
			double[] diffTimbre = new double[timbreOne.length];
			for(int i = 0; i<timbreOne.length;i++){
				diffTimbre[i] = Math.max(timbreOne[i], timbreTwo[i])/Math.min(timbreOne[i],timbreTwo[i]);
			}
			double[] confOne = gOne.getSegmentsConfidence();
			double[] confTwo = gTwo.getSegmentsConfidence();
			double[] diffConf = new double[confOne.length];
			for(int i = 0; i<confOne.length;i++){
				diffConf[i] = confOne[i]+ confTwo[i];
			}


			arff.addSong(Math.max(gOne.getTempo(),gTwo.getTempo())/Math.min(gOne.getTempo(), gTwo.getTempo()),Math.max(gOne.getLoudness(),gTwo.getLoudness())/Math.min(gOne.getLoudness(), gTwo.getLoudness()),Math.abs(gOne.getMode()-gTwo.getMode()),Math.abs(gOne.getKey()-gTwo.getKey()),diffTimbre,diffConf);

		}catch(Exception e){
			e.printStackTrace();
		}
	}
}