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
class AudioPlayerTest {

	@Autowired
	AudioPlayer audioPlayer
	
	
	@Test
	public void test10SecondsPlaying() {
		audioPlayer.readFromWav("src/test/resources/testcountto10")
		audioPlayer.playFromTime(0.0 as long)
		
		sleep 10000
		
		audioPlayer.stopPlaying()
		
	}
	
	@Test
	public void testPlayFromTime() {
		audioPlayer.readFromWav("src/test/resources/testcountto10")
		audioPlayer.playFromTime(2.0 as long)
		
		sleep 10000
		
		audioPlayer.stopPlaying()
		
	}

	@Test
	public void testAdjustPlayTimes() {
		audioPlayer.readFromWav("src/test/resources/testcountto10")
		audioPlayer.playFromTime(2.0 as long)
		
		sleep 2000
		
		audioPlayer.playFromTime(1.0 as long)
		
		sleep 2000
		
		audioPlayer.stopPlaying()
		
	}

	
}
