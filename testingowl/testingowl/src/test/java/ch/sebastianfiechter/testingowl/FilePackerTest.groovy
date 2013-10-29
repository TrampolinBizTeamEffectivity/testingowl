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
class FilePackerTest {

	@Autowired
	FilePacker packer
	
	
	@Test
	public void testAll() {
		packer.pack("src/test/resources/testissues")
		
		packer.unpack("src/test/resources/testissues")
		
	}

	
}
