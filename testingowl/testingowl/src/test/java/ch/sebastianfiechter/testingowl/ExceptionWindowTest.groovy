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
class ExceptionWindowTest {

	@Autowired
	ExceptionWindow window

	@Test
	public void testAll() {

		try {
			def i = Integer.parseInt("error");
		} catch (NumberFormatException e) {
			e.printStackTrace();
			window.show(e);
		}

	}
}
