package ch.sebastianfiechter.testingowl;

import ch.sebastianfiechter.testingowl.Issues.IssueType
import static org.junit.Assert.*;
import ch.sebastianfiechter.testingowl.*

import java.awt.event.ActionEvent

import org.junit.Test;
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = [ "/applicationContext.xml"])
class AudioRecorderTest {

	@Autowired
	AudioRecorder audioRecorder
	
	@Autowired
	AudioIO audioIO
	
	@Test
	public void test10SecondsRecording() {
		audioRecorder.mixerName = audioIO.getNamesOfMixersSupportingRecording()[0]
		audioRecorder.startRecording()
		
		println "0"
		for (def i=0; i<10;i++) { 
			sleep 1000
			println "${i+1} passed"
		}
		
		audioRecorder.stopRecording()
		
		new File("src/test/resources/testRec.owl.wav").deleteOnExit();
		
		audioRecorder.writeToWavFile("src/test/resources/testRec")
		
		
		
	}

}
