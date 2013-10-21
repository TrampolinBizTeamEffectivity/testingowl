package ch.sebastianfiechter.testpanorama;

import ch.sebastianfiechter.testpanorama.Issues.IssueType
import static org.junit.Assert.*;

import ch.sebastianfiechter.testpanorama.Issues
import ch.sebastianfiechter.testpanorama.IssuesFrame
import java.awt.event.ActionEvent
import org.junit.Test;
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = [ "/applicationContext.xml"])
class IssuesFrameTest {

	@Autowired
	IssuesFrame issuesFrame
	
	@Test
	public void testSelection() {
		
		issuesFrame.issues = [
			['id':"1", 'type':IssueType.Bug, 'frameStart':0, 'frameEnd':'10', 'message':'message'], 
			['id':"2", 'type':IssueType.Musthave,'frameStart':200, 'frameEnd':'210', 'message':'message to musthave']
		]
		
		issuesFrame.show()
		
		sleep 50000
		
		//fail("Not yet implemented");
	}

}
