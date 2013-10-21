package ch.sebastianfiechter.testpanorama;

import ch.sebastianfiechter.testpanorama.Issues.IssueType
import static org.junit.Assert.*;
import ch.sebastianfiechter.testpanorama.*

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
		assert AudioIO.getRecordingMixer(AudioIO.getNamesOfMixersSupportingRecording()[0])
		assert null != AudioIO.getRecordingMixer(AudioIO.getNamesOfMixersSupportingRecording()[0][0..2])
		assert true == AudioIO.isMixerSupportingAudioFormat(AudioIO.getNamesOfMixersSupportingRecording()[0][0..2])
		
	}

}
