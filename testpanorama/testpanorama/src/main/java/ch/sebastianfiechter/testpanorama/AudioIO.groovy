package ch.sebastianfiechter.testpanorama

import javax.sound.sampled.DataLine;
import java.util.ArrayList

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import org.springframework.stereotype.Component;

@Component
class AudioIO {
	
	static targetDataLine = new Line.Info(TargetDataLine.class)
	
	static AudioFormat audioFormat = new AudioFormat(44100.0F, 16, 1, true, true)
	
	static getNamesOfMixersSupportingRecording() {
		def mixerNames = []
		
		AudioSystem.getMixerInfo().each {
			Mixer m = AudioSystem.getMixer(it)
			if (m.isLineSupported(targetDataLine)) {
				mixerNames << it.name
			}
			
		}
	
		return mixerNames
	} 
	
	static getMixer(def mixerName) {
		for (def it : AudioSystem.getMixerInfo()) {
			Mixer m = AudioSystem.getMixer(it)
			if (it.name == mixerName) {
				return m
			}
		}
				
		return null
	}
	
	static isMixerSupportingAudioFormat(String mixerName) {
		return getMixer(mixerName).isLineSupported(
			new DataLine.Info(TargetDataLine.class, audioFormat));
		
	}

}
