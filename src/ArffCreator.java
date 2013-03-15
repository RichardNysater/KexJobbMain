import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Create an ARFF-file with the relevant MSD attributes.
 *
 * @author Richard Nysater
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
//		atts.addElement(new Attribute("Song", (FastVector) null));
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
		atts.addElement(new Attribute("Timbre"));
		
		data = new Instances(relationName, atts, 0);
	}
	
	/**
	 * Add a data instance to the relation.
	 * @param tempo The tempo of the song
	 * @param loudness The song's loudness
	 * @param mode The song's mode
	 * @param key The song's key
	 * @param the time signature
	 */
	public void addSong(double tempo, double loudness, int mode, int key, double timbre){
		double[] vals = new double[data.numAttributes()];
		vals[0] = tempo;
		vals[1] = loudness;
		vals[2] = mode;
		vals[3] = key;
		vals[4] = timbre;
		data.add(new Instance(1.0, vals));
	}

	public void printData(){
		System.out.println(data+"\n");
	}

}
