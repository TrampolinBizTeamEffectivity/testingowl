package ch.sebastianfiechter.testpanorama;

import static org.junit.Assert.*;

import ch.sebastianfiechter.testpanorama.Issues
import java.awt.event.ActionEvent
import org.junit.Test;
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = [ "/applicationContext.xml"])
class IssuesTest {

	@Autowired
	Issues issues
	
	@Test
	public void testReadFromExcelCsv() {
		

		List readIssues = Issues.readFromExcelCsv("src/test/resources/testissues")
		
		assert 4 == readIssues.size()
		
		//fail("Not yet implemented");
	}

}
