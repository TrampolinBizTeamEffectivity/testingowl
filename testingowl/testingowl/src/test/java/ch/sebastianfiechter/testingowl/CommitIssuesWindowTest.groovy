package ch.sebastianfiechter.testingowl;

import ch.sebastianfiechter.testingowl.Issues.IssueType
import static org.junit.Assert.*;

import java.awt.event.ActionEvent

import org.junit.Test;
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = [ "/applicationContext.xml"])
class CommitIssuesWindowTest {

	@Autowired
	CommitIssuesWindow window
	
	@Autowired
	Issues issues
	
	@Test
	public void testWaitForConfirm() {
		
		issues.issues = [
			['id':"1", 'type':IssueType.Bug, 'frameStart':0, 'frameEnd':'10', 'message':'message'],
			['id':"2", 'type':IssueType.Musthave,'frameStart':200, 'frameEnd':'210', 'message':'message to musthave']
		]
		
			
		window.showAndWaitForConfirm()
		println ("returned from dialog")
		
		sleep 10000
		
	}

}
