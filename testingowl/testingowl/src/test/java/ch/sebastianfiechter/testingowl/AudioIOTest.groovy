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
class AudioIOTest {

	@Autowired
	AudioRecorder audioRecorder
	
	@Autowired
	AudioIO audioIO
	
	@Test
	public void testAll() {
		assert null != AudioIO.getNamesOfMixersSupportingRecording()
		def m = AudioIO.getRecordingMixer(AudioIO.getNamesOfMixersSupportingRecording()[0])
		assert null != AudioIO.getRecordingMixer(AudioIO.getNamesOfMixersSupportingRecording()[0][0..2])
		assert true == AudioIO.isMixerSupportingAudioFormat(m)
		
	}

}
