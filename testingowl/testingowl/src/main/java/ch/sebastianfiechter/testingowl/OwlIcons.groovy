package ch.sebastianfiechter.testingowl

import groovy.util.logging.Slf4j;

import java.awt.image.BufferedImage

import javax.swing.ImageIcon
import javax.imageio.*

import org.springframework.stereotype.Component;

@Slf4j
@Component
class OwlIcons {

	ImageIcon getWelcomeIcon() {
		BufferedImage img = ImageIO.read(this.class.classLoader.getResource("testingowl_welcome.png"))
		new ImageIcon(img)
	}
	

	
}
