package ch.sebastianfiechter.testpanorama

import javax.sound.sampled.DataLine;

import java.util.ArrayList

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import java.util.regex.*

import org.springframework.stereotype.Component;

@Component
class AudioIO {
	
//	static targetDataLine = new Line.Info(TargetDataLine.class)
	
	static AudioFormat audioFormat = new AudioFormat(44100.0F, 16, 1, true, true)
	
	static getNamesOfMixersSupportingRecording() {
		def mixerNames = []
		
		AudioSystem.getMixerInfo().each {
			Mixer m = AudioSystem.getMixer(it)
			if (isMixerSupportingAudioFormat(m)) {
				mixerNames << it.name
			}			
		}
	
		return mixerNames
	} 
	
	static getRecordingMixer(def mixerNameOrPartOfIt) {
		for (def it : AudioSystem.getMixerInfo()) {
			Mixer m = AudioSystem.getMixer(it)
			if (it.name =~ Pattern.quote(mixerNameOrPartOfIt) && isMixerSupportingAudioFormat(m)) {
				return m
			}
		}
				
		return null
	}
	
	static isMixerSupportingAudioFormat(Mixer mixer) {
		return mixer.isLineSupported(
			new DataLine.Info(TargetDataLine.class, audioFormat));
		
	}
	
	// Calculate the level of the audio
	// http://forums.sun.com/thread.jspa?threadID=5433582
	//
	private int calculateRMSLevel(byte[] audioData)
	{
	  // audioData might be buffered data read from a data line
	  long lSum = 0;
	  for(int i=0; i<audioData.length; i++)
		  lSum = lSum + audioData[i];

	  double dAvg = lSum / audioData.length;

	  double sumMeanSquare = 0d;
	  for(int j=0; j<audioData.length; j++)
		  sumMeanSquare = sumMeanSquare + Math.pow(audioData[j] - dAvg, 2d);

	  double averageMeanSquare = sumMeanSquare / audioData.length;
	  return (int)(Math.pow(averageMeanSquare,0.5d) + 0.5);
   }

}
