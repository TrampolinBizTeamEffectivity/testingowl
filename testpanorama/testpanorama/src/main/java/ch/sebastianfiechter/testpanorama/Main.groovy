package ch.sebastianfiechter.testpanorama


import org.springframework.beans.factory.annotation.*
import org.springframework.stereotype.Component
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext

import javax.swing.*

import com.wet.wired.jsr.recorder.*
import com.wet.wired.jsr.player.*
import com.wet.wired.jsr.converter.*

@Component
class Main {

	static main(args) {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/applicationContext.xml");

		switch (showChooseModule()) {
			case JOptionPane.CLOSED_OPTION:
				println "Good bye."
				System.exit(0)
			case 0:
				applicationContext.getBean(JRecorder.class).init(args)
				break;
			case 1:
				applicationContext.getBean(JPlayer.class).init(args)
				break;
			case 2:
				applicationContext.getBean(RecordingConverter.class).init(args)
				break;
		}
	}

	private static int showChooseModule() {
		Object[] options = [
			"Recorder",
			"Player",
			//"Converter"
		]

		return JOptionPane.showOptionDialog(null,
		"Please choose your modul:", "Testpanorama Welcome!",
		JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
		null, options, options[0]);
	}
}
