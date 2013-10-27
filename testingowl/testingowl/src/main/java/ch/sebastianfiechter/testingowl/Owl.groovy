package ch.sebastianfiechter.testingowl

import groovy.util.logging.Slf4j;

import java.awt.image.BufferedImage

import javax.swing.ImageIcon
import javax.imageio.*

import org.springframework.stereotype.Component;

@Slf4j
@Component
class Owl {

	ImageIcon getWelcome() {
		BufferedImage img = ImageIO.read(this.class.classLoader.getResource("testingowl_welcome.png"))
		new ImageIcon(img)
	}
	
	ImageIcon getWaiting() {
		BufferedImage img = ImageIO.read(this.class.classLoader.getResource("testingowl_waiting.gif"))
		new ImageIcon(img)
	}
	
}
