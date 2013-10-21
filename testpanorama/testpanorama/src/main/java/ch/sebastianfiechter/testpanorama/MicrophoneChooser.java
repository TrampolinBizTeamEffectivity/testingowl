package ch.sebastianfiechter.testpanorama;

import java.util.ArrayList;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;
import javax.sound.sampled.TargetDataLine;

public class MicrophoneChooser {

	public static void main(String[] args) {
		getMixerSupportingMics();
	}
	
	public static ArrayList<String> getMixerSupportingMics() {
		
		ArrayList<String> mixerNames = new ArrayList<String>();
		
		Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
		for (Mixer.Info info : mixerInfos) {
			Mixer m = AudioSystem.getMixer(info);
			if (m.isLineSupported(new Line.Info(
					TargetDataLine.class))) {
				System.out.println("Mixer [" + info.getName() + "] supports Recording");
				mixerNames.add(info.getName());
			}
		}
		
		return mixerNames;
	}
	
	public static Mixer getMixer(String mixerName) {
		Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
		for (Mixer.Info info : mixerInfos) {
			if (info.getName().equals(mixerName)) return AudioSystem.getMixer(info);
		}		
		
		return null;
	}
	


}
