import java.io.BufferedWriter;
import java.io.File;


public class InfoExtractor {
	static ArffCreator arff;
	static BufferedWriter outfile;
	static int counter = 0;
	static int threads = 0;

	public static void main(String args[]) throws Exception{
		final double keyConfidence = 0.7;
		final double modeConfidence = 0.7;
		final double timeSignatureConfidence = 0.7;
		final double timbreConfidence = 0.7;
		System.out.println("Starting...");
		File[] files = new File("C:\\Users\\Shaan\\Desktop\\songs").listFiles(); //input is filepath

		arff = new ArffCreator("MSD");
//		String leurl = getSong(files,"F:\\Million Song Dataset\\A\\P\\D\\TRAPDIH128F426DE71.h5");
		arff.printData();
		System.out.println("lmao");
		showFiles(files, keyConfidence, modeConfidence, timeSignatureConfidence, timbreConfidence);
		System.out.println("Extraction complete.");
	}

	public static String getSong(File[] files, String songPath){
		String id = "951886";
		String consumerkey = "7dtywchqwpuh";
		String secretkey = "x6dgajvyu83sygcr";
		try{
			counter++;
			System.out.println(counter);
			for (File file : files){
				if (file.isDirectory()){
					if(counter >50 ){
						return getSong(file.listFiles(), songPath);
					}
					else{
						getSong(file.listFiles(), songPath);
					}
				}
				else{
					File filetwo = new File(songPath);
					getDifference(file,filetwo);
					MillionSongInfoGetpls getter = new MillionSongInfoGetpls(file.getAbsolutePath());
					String url = "http://api.7digital.com/1.2/track/preview?trackid=".concat(""+getter.getId()).concat("&oauth_consumer_key=").concat(consumerkey); 
					getter.close();	
					return url;
					//					System.out.println(getter.getTimbre(0.8));
					//					System.out.print(getter.getArtist()+" - "+ getter.getTitle()+ " Tempo: " +getter.getTempo()+" Mode "+getter.getMode() +" Loudness: " + getter.getLoudness()+"\n");

				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return "Error";
	}

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
//					if(counter%10000 == 0){
//						arff.printData();
//						System.exit(1);
//					}
//					if(counter<10000 && getter.getKeyConfidence() > keyConfidence && getter.getModeConfidence() > modeConfidence && getter.getTimeSignatureConfidence() > timeSignatureConfidence){
////						arff.addSong(getter.getTempo(),getter.getLoudness(),getter.getMode(),getter.getKey(),getter.getTimeSignature(),getter.getTimbre(timbreConfidence));
//					}
					System.out.println(counter);
					//					System.out.println(getter.getTimbre(0.8));
					System.out.print(getter.getArtist()+" - "+ getter.getTitle()+ ".#. Tempo: "+getter.getTempo() +" Id: " + getter.getId()+"\n");
					getter.close();					
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void getDifference(File fileOne, File fileTwo){
		try{
			MillionSongInfoGetpls getterOne = new MillionSongInfoGetpls(fileOne.getAbsolutePath());
			MillionSongInfoGetpls getterTwo = new MillionSongInfoGetpls(fileTwo.getAbsolutePath());
			
			arff.addSong(Math.abs(getterOne.getTempo()-getterTwo.getTempo()),Math.abs(getterOne.getLoudness()-getterTwo.getLoudness()),Math.abs(getterOne.getMode()-getterTwo.getMode()),Math.abs(getterOne.getKey()-getterTwo.getKey()),Math.abs(getterOne.getTimbre()-getterTwo.getTimbre()));

		}catch(Exception e){
			e.printStackTrace();
		}
	}
}