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
	def recordingThread;
	File tempFile;

	TargetDataLine targetDataLine
	AudioFileFormat.Type targetType
	AudioInputStream audioInputStream

	def startRecording() {

		assert mixerName != null
		assert true == audioIO.isMixerSupportingAudioFormat(mixerName)

		tempFile = new File("tempwav");
		tempFile.deleteOnExit();

		prepareRecording()


		recordingThread = new Thread() {
			void run() {
				try {
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
		Mixer mixer = audioIO.getMixer(mixerName);

		DataLine.Info lineInfo = new DataLine.Info(
				TargetDataLine.class, audioIO.audioFormat);

		targetDataLine = (TargetDataLine) mixer.getLine(lineInfo);
		targetDataLine.open(audioIO.audioFormat);

		targetType = AudioFileFormat.Type.WAVE;

		audioInputStream = new AudioInputStream(targetDataLine);
	}

	def stopRecording() {

		targetDataLine.stop();
		targetDataLine.close();

		recordingThread.join()
	}

	def writeToWavFile(String filenameWithoutDotCsv) {

		//delete existing
		new File("${filenameWithoutDotCsv}.cap.wav").delete()

		def out = new File("${filenameWithoutDotCsv}.cap.wav")

		FileHelper.copy(tempFile, out);

		tempFile.delete()

	}

	def cancelSave() {
		tempFile.delete()
	}
}
