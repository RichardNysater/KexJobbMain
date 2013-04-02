import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Create an ARFF-file with the relevant MSD attributes.
 *
 * @author Richard Nysäter.
 */
public class ArffCreator {
	FastVector      atts;
	Instances       data;
	
	/**
	 * Constructor for ArffCreator
	 * @param relationName The name to call the relation.
	 * @throws Exception 
	 */
	public ArffCreator(String relationName) throws Exception {
		// 1. set up attributes
		atts = new FastVector();
		atts.addElement(new Attribute("Artist",(FastVector) null));
		atts.addElement(new Attribute("Song",(FastVector) null));
		atts.addElement(new Attribute("Tempo"));
		atts.addElement(new Attribute("Loudness"));

		// - nominal
		FastVector mode = new FastVector();
		mode.addElement("0");
		mode.addElement("1");
		atts.addElement(new Attribute("Mode", mode));
		
		FastVector key = new FastVector();
		for(int i = 0; i<12;i++){
		key.addElement(""+i);
		}
		
		atts.addElement(new Attribute("Key", key));
		
		for(int i = 0; i<12;i++){
			atts.addElement(new Attribute("Timbre"+i));
		}
		
		for(int i = 0; i<12;i++){
			atts.addElement(new Attribute("TimbreConfidence"+i));
		}
		atts.addElement(new Attribute("Similarity"));
		data = new Instances(relationName, atts, 0);
	}
	
	/**
	 * Adds a comparison of two songs to be printed to the arff file later.
	 * @param artistSongOne The artist (and title) of the first song.
	 * @param artistSongTwo The artist (and title) of the second song.
	 * @param tempo The difference in tempo.
	 * @param loudness The difference in loudness.
	 * @param mode The difference in mode.
	 * @param key The difference in key.
	 * @param timbre The difference in timbre. The array represents the average timbre of each element over the entire song.
	 * @param timbreConfidence The confidence value for each timbre element.
	 */
	public void addSong(String artistSongOne, String artistSongTwo, double tempo, double loudness, int mode, int key, double[] timbre, double[] timbreConfidence){
		double[] vals = new double[data.numAttributes()];
		vals[0] = data.attribute(0).addStringValue(artistSongOne);
		vals[1] = data.attribute(1).addStringValue(artistSongTwo);
		vals[2] = tempo;
		vals[3] = loudness;
		vals[4] = mode;
		vals[5] = key;
		
		for(int i = 0; i<12;i++){
			vals[6+i] = timbre[i];
		}
		for(int i = 0; i<12;i++){
			vals[18+i] = timbreConfidence[i];
		}
		vals[30] = Instance.missingValue();
		data.add(new Instance(1.0, vals));
	}

	/**
	 * Prints the arff to the specified file.
	 * @param pathToFile Full path to the arff file.
	 */
	public void printData(String pathToFile){
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(pathToFile));
			writer.write(data+"\n");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
