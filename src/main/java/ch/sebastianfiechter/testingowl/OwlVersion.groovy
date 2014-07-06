package ch.sebastianfiechter.testingowl

import groovy.util.logging.Slf4j;

import java.awt.image.BufferedImage

import javax.swing.ImageIcon
import javax.imageio.*
import com.jcabi.manifests.Manifests
import org.springframework.stereotype.Component;

@Slf4j
@Component
class OwlVersion {

	String getVersion() {
		return Manifests.exists("App-Version")==true ? Manifests.read("App-Version"): "[version]";
	}
	
}
