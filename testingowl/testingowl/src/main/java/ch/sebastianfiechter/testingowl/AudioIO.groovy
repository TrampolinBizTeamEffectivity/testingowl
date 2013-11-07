package ch.sebastianfiechter.testingowl

import javax.sound.sampled.DataLine;

import java.util.ArrayList

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import java.util.regex.*

import org.springframework.stereotype.Component;

/**
 * @author rbqq
 *
 * U-LAW or A-LAW Encoding: http://stackoverflow.com/questions/10515174/conversion-of-audio-format
 *
 */
@Component
class AudioIO {

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

	public static void printSupportedAudioFormats(){
		javax.sound.sampled.Mixer.Info[] mixers = AudioSystem.getMixerInfo();
		//System.out.println("There are " + mixers.length + " mixer info objects");
		for(int i=0;i<mixers.length;i++){
			Mixer.Info mixerInfo = mixers[i];
			//System.out.println("Mixer Name:"+mixerInfo.getName());
			Mixer mixer = AudioSystem.getMixer(mixerInfo);
			Line.Info[] lineinfos = mixer.getTargetLineInfo();
			for(Line.Info lineinfo : lineinfos){
				//System.out.println("line:" + lineinfo);
				if (lineinfo instanceof DataLine.Info) {
					println mixerInfo.name + ": "
					lineinfo.formats.each {
						println " " + it
					}
				}
			}
		}
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
