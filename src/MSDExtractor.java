import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import ncsa.hdf.object.h5.H5File;

/**
 * The MSDExtractor class exists as an intermediate to hdf5_getters.java, which is part of the HDF5 group (http://www.hdfgroup.org/).
 * It's purpose is to extract the relevant information from the .h5 files which the MSD consists of.
 * @author Richard Nysäter.
 *
 */
public class MSDExtractor {
	H5File file;

	/**
	 * The constructor specifies the file to open.
	 * @param fileToOpen Full path to the .h5 file to open.
	 * @throws Exception
	 */
	public MSDExtractor(String fileToOpen) throws Exception{
		file = hdf5_getters.hdf5_open_readonly(fileToOpen);
	}

	/**
	 * @return The song's title.
	 * @throws Exception
	 */
	public String getTitle() throws Exception{
		return hdf5_getters.get_title(file);
	}
	/**
	 * 7Digital (http://about.7digital.com/) provides an API for developers to extract previews of songs (http://developer.7digital.net/).
	 * This method returns the song's 7digital ID.
	 * @return The song's 7digital ID.
	 * @throws Exception
	 */
	public int getId() throws Exception{
		return hdf5_getters.get_track_7digitalid(file);
	}

	/**
	 * @return The song's artist.
	 * @throws Exception
	 */
	public String getArtist() throws Exception{
		return hdf5_getters.get_artist_name(file);

	}

	/**
	 * Closes the file.
	 * @throws HDF5Exception
	 */
	public void close() throws HDF5Exception{
		file.close();
	}

	/**
	 * @return The song's tempo in BPM.
	 * @throws Exception
	 */
	public double getTempo() throws Exception{
		return hdf5_getters.get_tempo(file);
	}

	/**
	 * @return The song's mode (major/minor).
	 * @throws Exception
	 */
	public int getMode() throws Exception{
		return hdf5_getters.get_mode(file);
	}

	/**
	 * @return The mode's confidence value (between 0 and 1).
	 * @throws Exception
	 */
	public double getModeConfidence() throws Exception{
		return hdf5_getters.get_mode_confidence(file);
	}

	/**
	 * @return The song's key (0,1,...,11).
	 * @throws Exception
	 */
	public int getKey() throws Exception{
		return hdf5_getters.get_key(file);
	}

	/**
	 * @return The key's confidence.
	 * @throws Exception
	 */
	public double getKeyConfidence() throws Exception{
		return hdf5_getters.get_key_confidence(file);
	}

	/**
	 * @return The song's loudness. See http://labrosa.ee.columbia.edu/millionsong/blog/11-7-25-loudness-msd for more information about loudness in the MSD.
	 * @throws Exception
	 */
	public double getLoudness() throws Exception{
		return hdf5_getters.get_loudness(file);
	}

	/**
	 * The timbre of a song consists of twelve arrays per segment based on the song's texture features (MFCC + PCA-like).
	 * @return An array of the twelve timbre values averaged over all segments.
	 * @throws Exception
	 */
	public double[] getTimbre() throws Exception{
		double[] timbre = hdf5_getters.get_segments_timbre(file);
		double[] avgtimbre = new double[12];
		for(int j = 0; j<12;j++){
			int segments = 0;
			for(int i = 0; i<timbre.length;i+=12){
				avgtimbre[j]+=timbre[i+j];
				segments++;
			}
			avgtimbre[j] /= segments;
		}
		return avgtimbre;
	}

	/**
	 * @return An array of the twelve segment confidence values over all segments.
	 * @throws Exception
	 */
	public double[] getSegmentsConfidence() throws Exception{
		double[] confidence = hdf5_getters.get_segments_confidence(file);
		double[] averageConfidence = new double[12];
		for(int j = 0; j<12;j++){
			int segments = 0;
			for(int i = 0; i<confidence.length-11;i+=12){
				averageConfidence[j]+=confidence[i+j];
				segments++;
			}
			averageConfidence[j] /= segments;
		}
		return averageConfidence;
	}

	/**
	 * Returns the average timbre of all segments above a specified confidence (between 0 and 1) value.
	 * @param confidence The minimum confidence a segment is allowed to have.
	 * @return An array of the twelve timbre values averaged over all segments above a specified confidence value.
	 * @throws Exception
	 */
	public double[] getTimbre(double confidence) throws Exception{
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
	 * @return An array of tags including the artist's genre.
	 * @throws Exception
	 */
	public String[] getGenres() throws Exception {
		return hdf5_getters.get_artist_terms(file);
	}
	
	/**
	 * @return An array of term frequency (how often a term is used to describe an artist) which is mapped to the tags from getGenres().
	 * @throws Exception
	 */
	public double[] getFrequency() throws Exception{
		double[] frequency = hdf5_getters.get_artist_terms_freq(file);
		return frequency;
	}
	
	/**
	 * @return An array of term weight (how important a term is to an artist) which is mapped to the tags from getGenres().
	 * @throws Exception
	 */
	public double[] getWeight() throws Exception{
		double[] weight = hdf5_getters.get_artist_terms_weight(file);
		return weight;
	}
}
