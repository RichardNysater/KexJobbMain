import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import ncsa.hdf.object.h5.H5File;


public class SongExtractor {
	static int counter = 0;
	
	static BufferedWriter outfile;
	public static void main(String args[]) throws IOException{
		FileWriter tmp = new FileWriter("F:/songs.txt",true);
		outfile = new BufferedWriter(tmp);
		File[] files = new File("D:/Million Song Dataset").listFiles(); //input is filepath
		System.out.println("Now rsading, please wait.");
		showFiles(files);
		outfile.flush();
		outfile.close();
		System.out.println("Extraction complete.");
	}

	public static void showFiles(File[] files){
		try{
			for (File file : files){
				if (file.isDirectory()){
					showFiles(file.listFiles());
				}else{
					if(counter%1000 == 0){
						System.out.println(counter);
					}
					H5File songOneH5 = hdf5_getters.hdf5_open_readonly(file.getAbsolutePath());
//					System.out.print(getter.getArtist()+" - "+ getter.getTitle()+ " " +getter.getKey()+" Loudness "+getter.getLoudness() +" Energy: " + getter.getEnergy()+"\n");
					outfile.write(MSDExtractor.getArtist(songOneH5)+" - "+MSDExtractor.getTitle(songOneH5)+" - "+file.getName()+"\n");
					MSDExtractor.close(songOneH5);					
					counter++;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
