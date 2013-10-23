package ch.sebastianfiechter.testpanorama

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel
import javax.swing.SwingUtilities;

import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Autowired
import groovy.util.logging.*

@Slf4j
@Component
class SoundLevel extends JPanel {
	
	def level = 0
	
	public setLevel(def lev) {
		
		level = lev
		
		//log.info ("updating sound level: ${level} -> ${finalHeight}");
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				repaint(0)
			}
		});

	}
	
	public void paint(Graphics g) {
		
		def finalHeight = (level * height / 100) as int;
		
		g.setColor(Color.black);
		g.clearRect(0, 0, width, height);
		g.fillRect(0, (height-finalHeight), width, finalHeight);
	}
}
