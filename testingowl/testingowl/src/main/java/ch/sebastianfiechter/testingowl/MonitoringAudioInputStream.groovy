package ch.sebastianfiechter.testingowl

import java.io.IOException;

import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.SourceDataLine
import javax.sound.sampled.TargetDataLine

import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Autowired
import groovy.util.logging.*

@Slf4j
class MonitoringAudioInputStream extends AudioInputStream {

	AudioIO audioIO
	
	SoundLevel soundLevel
	
	def level = 0
	
	public MonitoringAudioInputStream(AudioInputStream stream, def io, def level) {
		super(stream, stream.format, stream.frameLength);
		
		audioIO = io
		soundLevel = level	
	}
	
	public MonitoringAudioInputStream(TargetDataLine targetDataLine, def io, def level) {
		super(targetDataLine)
		audioIO = io
		soundLevel = level
	}
	

	@Override
	public int read() throws IOException {	
		def data = super.read()
		monitor([data])
		return data;
	}

	@Override
	public int read(byte[] arg0, int arg1, int arg2) throws IOException {
		def val = super.read(arg0, arg1, arg2)
		monitor(arg0)
		return val
	}

	@Override
	public int read(byte[] arg0) throws IOException {	
		def val = super.read(arg0); 
		monitor(arg0)
		return val
	}
	
	def monitor(byte[] data) {
		//cut audio data to get more significant results
		level = audioIO.calculateRMSLevel(data[0..200] as byte[])
		soundLevel.setLevel(level)
		//log.info ("recording level: " + level);
	}
	
}

