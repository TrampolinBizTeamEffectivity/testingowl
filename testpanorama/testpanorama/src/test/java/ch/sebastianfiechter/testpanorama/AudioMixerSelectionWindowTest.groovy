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
class AudioMixerSelectionWindowTest {

	@Autowired
	AudioMixerSelectionWindow window
	

	
	@Test
	public void testAll() {
		window.getMixerName();
		
		sleep 100000
		
	}

}
