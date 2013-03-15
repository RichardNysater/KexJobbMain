import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import ncsa.hdf.object.h5.H5File;


public class MillionSongInfoGetpls {
	H5File file;
	
	public MillionSongInfoGetpls(String fileToOpen) throws Exception{
		file = hdf5_getters.hdf5_open_readonly(fileToOpen);
	}
	public String getTitle() throws Exception{
		return hdf5_getters.get_title(file);
	}
	public int getId() throws Exception{
		return hdf5_getters.get_track_7digitalid(file);
	}
	public String getArtist() throws Exception{
		return hdf5_getters.get_artist_name(file);
		
	}
	public void close() throws HDF5Exception{
		file.close();
	}
	public double getTempo() throws Exception{
		return hdf5_getters.get_tempo(file);
	}
	public int getMode() throws Exception{
		return hdf5_getters.get_mode(file);
	}
	public double getModeConfidence() throws Exception{
		return hdf5_getters.get_mode_confidence(file);
	}
	public int getKey() throws Exception{
		return hdf5_getters.get_key(file);
	}
	public int getTimeSignature() throws Exception{
		return hdf5_getters.get_time_signature(file);
	}
	public double getTimeSignatureConfidence() throws Exception{
		return hdf5_getters.get_time_signature_confidence(file);
	}
	public double getKeyConfidence() throws Exception{
		return hdf5_getters.get_key_confidence(file);
	}
//	public double[] getTimbre() throws Exception{
//		return hdf5_getters.get_segments_timbre(file);
//	}
	public double getLoudness() throws Exception{
		return hdf5_getters.get_loudness(file);
	}
	public double getDanceability() throws Exception{
		return hdf5_getters.get_danceability(file);
	}
	public double getEnergy() throws Exception{
		
		return hdf5_getters.get_energy(file);
		
	}
	/**
	 * Returns the average timbre of all segments
	 * @return
	 * @throws Exception
	 */
	public double getTimbre() throws Exception{
		double[] timbre = hdf5_getters.get_segments_timbre(file);
		double avgtimbre = 0;
		int segments = 0;
		for(int i = 0; i<timbre.length;i+=12){
			avgtimbre+=timbre[i];
			segments++;
		}
		return (avgtimbre/segments);
	}
	/**
	 * Returns the average timbre of all segments above a certain confidence
	 * @param confidence Only segments above this confidence will be calculated
	 * @return
	 * @throws Exception
	 */
	public double getTimbre(double confidence) throws Exception{
		double[] timbre = hdf5_getters.get_segments_timbre(file);
		double[] confidences = hdf5_getters.get_segments_confidence(file);
		double avgtimbre = 0;
		int segments = 0;
		for(int i = 0; i<timbre.length;i+=12){
			if(confidences[i/12] > confidence){
				avgtimbre+=timbre[i];
				segments++;
			}
		}
		return (avgtimbre/segments);
	}
	
	public double getSegmentsConfidence() throws Exception{
		double[] confidence = hdf5_getters.get_segments_confidence(file);
		double avgconfidence = 0;
		int segments = 0;
		for(int i = 0; i<confidence.length;i++){
			avgconfidence+=confidence[i];
			segments++;
		}
		return (avgconfidence/segments);
	}
	
	
//	public static void run() throws Exception{
//
//		System.out.println(get.get_artist_name(file));
//		System.out.println(get.get_duration(file));
//		System.out.println(get.get_title(file));
//	}
}
