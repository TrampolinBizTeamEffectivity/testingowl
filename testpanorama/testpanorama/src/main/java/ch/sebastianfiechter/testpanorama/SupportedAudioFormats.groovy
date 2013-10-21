package ch.sebastianfiechter.testpanorama

import javax.sound.sampled.AudioFormat
import javax.sound.sampled.DataLine
import javax.sound.sampled.Mixer
import javax.sound.sampled.*

class SupportedAudioFormats {

	static main(args) {
		Vector<AudioFormat> formats = getSupportedFormats(SourceDataLine.class);
		formats.each {
			println it
		}
		printMixers()
	}
	
	static Vector<AudioFormat> getSupportedFormats(Class<?> dataLineClass) {
		/*
		 * These define our criteria when searching for formats supported
		 * by Mixers on the system.
		 */
		def sampleRates = new float[3]
		sampleRates[0] = (float) 8000.0
		sampleRates[1] = (float) 16000.0
		sampleRates[2] = (float) 44100.0
		def channels = new int[2]
		channels[0] = 1;
		channels[1] = 2;

		def bytesPerSample = new int[1]
		bytesPerSample[0] = 2;
	
		AudioFormat format;
		DataLine.Info lineInfo;
	
	
		Vector<AudioFormat> formats = new Vector<AudioFormat>();
	
		for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
			for (int a = 0; a < sampleRates.length; a++) {
				for (int b = 0; b < channels.length; b++) {
					for (int c = 0; c < bytesPerSample.length; c++) {
						format = new AudioFormat(new AudioFormat.Encoding().@PCM_SIGNED,
								sampleRates[a] as float, (8 * bytesPerSample[c]) as int, 
								channels[b] as int, 
								bytesPerSample[c] as int, sampleRates[a] as float, false);
						lineInfo = new DataLine.Info(dataLineClass, format);
						if (AudioSystem.isLineSupported(lineInfo)) {
							/*
							 * TODO: To perform an exhaustive search on supported lines, we should open
							 * TODO: each Mixer and get the supported lines. Do this if this approach
							 * TODO: doesn't give decent results. For the moment, we just work with whatever
							 * TODO: the unopened mixers tell us.
							 */
							if (AudioSystem.getMixer(mixerInfo).isLineSupported(lineInfo)) {
								formats.add(format);
							}
						}
					}
				}
			}
		}
		return formats;
	}

	public static void printMixers() {
		Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
		for (Mixer.Info info: mixerInfos){
		 Mixer m = AudioSystem.getMixer(info);
		 Line.Info[] lineInfos = m.getSourceLineInfo();
		 for (Line.Info lineInfo:lineInfos){
		  System.out.println (info.getName()+" : "+lineInfo);
		  Line line = m.getLine(lineInfo);
		  System.out.println("\t : "+line);
		 }
		 lineInfos = m.getTargetLineInfo();
		 for (Line.Info lineInfo:lineInfos){
		  System.out.println (m.dump() + " : " + lineInfo.dump());
		  Line line = m.getLine(lineInfo);
		  System.out.println("\t : "+line.dump());
	   
		 }
	   
		}
	}
}


