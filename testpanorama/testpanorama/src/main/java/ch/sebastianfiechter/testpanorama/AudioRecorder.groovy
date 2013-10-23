package ch.sebastianfiechter.testpanorama

import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.wet.wired.jsr.recorder.FileHelper;

import groovy.util.logging.*

@Component
@Slf4j
class AudioRecorder {

	@Autowired
	AudioIO audioIO

	def mixerName = null
	Thread recordingThread;
	File tempFile;

	TargetDataLine targetDataLine
	AudioFileFormat.Type targetType
	AudioInputStream audioInputStream
	
	AudioLevelMonitorThread levelMonitorThread

	def startRecording() {

		if (mixerName == null) {
			log.info "No mixer defined - won't record audio."
			return
		}

		tempFile = new File("tempwav");
		tempFile.deleteOnExit();

		prepareRecording()


		recordingThread = new Thread() {
					void run() {
						try {
							levelMonitorThread = new AudioLevelMonitorThread()
							levelMonitorThread.start()
							AudioSystem.write(audioInputStream, targetType, tempFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

		targetDataLine.start()
		recordingThread.start()
	}

	def prepareRecording() {
		Mixer mixer = audioIO.getRecordingMixer(mixerName);

		DataLine.Info lineInfo = new DataLine.Info(
				TargetDataLine.class, audioIO.audioFormat);

		targetDataLine = (TargetDataLine) mixer.getLine(lineInfo);
		targetDataLine.open(audioIO.audioFormat);

		targetType = AudioFileFormat.Type.WAVE;

		audioInputStream = new AudioInputStream(targetDataLine);
	}

	def stopRecording() {

		if (levelMonitorThread != null && levelMonitorThread.isAlive()) {
			levelMonitorThread.running = false
			levelMonitorThread.join()
		}
		
		if (targetDataLine != null && targetDataLine.active) {
			targetDataLine.stop();
			targetDataLine.close();
		}

		if (recordingThread != null && recordingThread.isAlive()) {
			recordingThread.join()
		}
	}

	def writeToWavFile(String filenameWithoutEnding) {

		//delete existing
		new File("${filenameWithoutEnding}.cap.wav").delete()

		def out = new File("${filenameWithoutEnding}.cap.wav")

		FileHelper.copy(tempFile, out);

		tempFile.delete()

	}

	def cancelSave() {
		if (tempFile.exists()) {
			tempFile.delete()
		}
	}

	@Slf4j
	class AudioLevelMonitorThread extends Thread {

		byte[] tempBuffer = new byte[500];
		def running = true

		public void run() {

			while(running){
				int cnt = targetDataLine.read(tempBuffer, 0, tempBuffer.length);
				if(cnt > 0){
					log.info ("recording level: " + audioIO.calculateRMSLevel(tempBuffer));
				}
			}
		}
	}
}
