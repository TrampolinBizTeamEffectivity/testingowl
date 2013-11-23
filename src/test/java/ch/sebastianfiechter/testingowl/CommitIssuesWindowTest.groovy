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
			['id':"1", 'type':IssueType.Topic, 'frameStart':1, 'frameEnd':'11', 'message':'title'],
			['id':"2", 'type':IssueType.Bug, 'frameStart':11, 'frameEnd':'10', 'message':'2'],
			['id':"3", 'type':IssueType.Musthave,'frameStart':200, 'frameEnd':'210', 'message':'3'],
			['id':"4", 'type':IssueType.Musthave,'frameStart':250, 'frameEnd':'270', 'message':'4'],
			['id':"5", 'type':IssueType.Musthave,'frameStart':280, 'frameEnd':'290', 'message':'5']
		]
		issues.topic = "title"
			
		window.showAndWaitForConfirm()
		println ("returned from dialog")
		
		println "after: " + issues.topic + " " + issues.issues[0].message;
		
		
	}

}
