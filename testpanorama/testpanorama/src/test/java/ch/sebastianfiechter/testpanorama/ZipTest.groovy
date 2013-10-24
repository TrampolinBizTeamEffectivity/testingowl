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
class ZipTest {

	@Autowired
	Zip zip
	
	
	@Test
	public void testAll() {
		zip.zip("src/test/resources/testissues")
		
		zip.unzip("src/test/resources/testissues.cap.zip", "src/test/resources/unzip/")
		
	}

	
	@Test
	public void testUnzipOnlyFilename() {

		
		zip.unzip("src/test/resources/unzip/testissues.cap.zip")
		
	}
}
