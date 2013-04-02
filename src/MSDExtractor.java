import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import ncsa.hdf.object.h5.H5File;

/**
 * The MSDExtractor utility class exists as an intermediate to hdf5_getters.java, which is part of the HDF5 group (http://www.hdfgroup.org/).
 * It's purpose is to extract the relevant information from the .h5 files which the MSD consists of.
 * @author Richard Nysäter.
 *
 */
public final class MSDExtractor {
	/**
	 * @return The song's title.
	 * @param file The H5File to open
	 * @throws Exception
	 */
	public static String getTitle(H5File file) throws Exception{
		return hdf5_getters.get_title(file);
	}
	/**
	 * 7Digital (http://about.7digital.com/) provides an API for developers to extract previews of songs (http://developer.7digital.net/).
	 * This method returns the song's 7digital ID.
	 * @return The song's 7digital ID.
	 * @param file The H5File to open
	 * @throws Exception
	 */
	public static int getId(H5File file) throws Exception{
		return hdf5_getters.get_track_7digitalid(file);
	}

	/**
	 * @return The song's artist.
	 * @param file The H5File to open
	 * @throws Exception
	 */
	public static String getArtist(H5File file) throws Exception{
		return hdf5_getters.get_artist_name(file);

	}

	/**
	 * Closes the file.
	 * @param file The H5File to close
	 * @throws HDF5Exception
	 */
	public static void close(H5File file) throws HDF5Exception{
		file.close();
	}

	/**
	 * @return The song's tempo in BPM.
	 * @param file The H5File to open
	 * @throws Exception
	 */
	public static double getTempo(H5File file) throws Exception{
		return hdf5_getters.get_tempo(file);
	}

	/**
	 * @return The song's mode (major/minor).
	 * @param file The H5File to open
	 * @throws Exception
	 */
	public static int getMode(H5File file) throws Exception{
		return hdf5_getters.get_mode(file);
	}

	/**
	 * @return The mode's confidence value (between 0 and 1).
	 * @param file The H5File to open
	 * @throws Exception
	 */
	public static double getModeConfidence(H5File file) throws Exception{
		return hdf5_getters.get_mode_confidence(file);
	}

	/**
	 * @return The song's key (0,1,...,11).
	 * @param file The H5File to open
	 * @throws Exception
	 */
	public static int getKey(H5File file) throws Exception{
		return hdf5_getters.get_key(file);
	}

	/**
	 * @return The key's confidence.
	 * @param file The H5File to open
	 * @throws Exception
	 */
	public static double getKeyConfidence(H5File file) throws Exception{
		return hdf5_getters.get_key_confidence(file);
	}

	/**
	 * @return The song's loudness. See http://labrosa.ee.columbia.edu/millionsong/blog/11-7-25-loudness-msd for more information about loudness in the MSD.
	 * @param file The H5File to open
	 * @throws Exception
	 */
	public static double getLoudness(H5File file) throws Exception{
		return hdf5_getters.get_loudness(file);
	}

	/**
	 * The timbre of a song consists of twelve arrays per segment based on the song's texture features (MFCC + PCA-like).
	 * @return An array of the twelve timbre values averaged over all segments.
	 * @param file The H5File to open
	 * @throws Exception
	 */
	public static double[] getTimbre(H5File file) throws Exception{
		double[] timbre = hdf5_getters.get_segments_timbre(file);
		double[] avgtimbre = new double[12];
		for(int j = 0; j<12;j++){
			for(int i = 0; i<timbre.length;i+=12){
				avgtimbre[j]+=timbre[i+j];
			}
			avgtimbre[j] /= timbre.length/12;
		}
		return avgtimbre;
	}

	/**
	 * @return An array of the twelve segment confidence values over all segments.
	 * @param file The H5File to open
	 * @throws Exception
	 */
	public static double[] getSegmentsConfidence(H5File file) throws Exception{
		double[] confidence = hdf5_getters.get_segments_confidence(file);
		double[] averageConfidence = new double[12];
		for(int j = 0; j<12;j++){
			for(int i = 0; i<confidence.length-11;i+=12){
				averageConfidence[j]+=confidence[i+j];
			}
			averageConfidence[j] /= confidence.length/12;
		}
		return averageConfidence;
	}

	/**
	 * Returns the average timbre of all segments above a specified confidence (between 0 and 1) value.
	 * @param confidence The minimum confidence a segment is allowed to have.
	 * @param file The H5File to open
	 * @return An array of the twelve timbre values averaged over all segments above a specified confidence value.
	 * @throws Exception
	 */
	public static double[] getTimbre(double confidence,H5File file) throws Exception{
		double[] confidences = hdf5_getters.get_segments_confidence(file);
		double[] timbre = hdf5_getters.get_segments_timbre(file);
		double[] averageTimbre = new double[12];
		for(int j = 0; j<12;j++){
			int segments = 0;
			for(int i = 0; i<timbre.length;i+=12){
				if(confidences[i/12] > confidence){
					averageTimbre[j]+=timbre[i+j];
					segments++;
				}
			}
			averageTimbre[j] /= segments;
		}
		return averageTimbre;
	}

	/**
	 * Return the artist's genres (and other info) as specified by The Echo Nest.
	 * @param file The H5File to open
	 * @return An array of tags including the artist's genre.
	 * @throws Exception
	 */
	public static String[] getGenres(H5File file) throws Exception {
		return hdf5_getters.get_artist_terms(file);
	}
	
	/**
	 * @param file The H5File to open
	 * @return An array of term frequency (how often a term is used to describe an artist) which is mapped to the tags from getGenres().
	 * @throws Exception
	 */
	public static double[] getFrequency(H5File file) throws Exception{
		return hdf5_getters.get_artist_terms_freq(file);
	}
	
	/**
	 * @param file The H5File to open
	 * @return An array of term weight (how important a term is to an artist) which is mapped to the tags from getGenres().
	 * @throws Exception
	 */
	static public double[] getWeight(H5File file) throws Exception{
		return hdf5_getters.get_artist_terms_weight(file);
	}
}
