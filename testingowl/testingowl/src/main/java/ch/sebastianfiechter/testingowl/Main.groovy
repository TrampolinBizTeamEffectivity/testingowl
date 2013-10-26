package ch.sebastianfiechter.testingowl


import org.springframework.beans.factory.annotation.*
import org.springframework.stereotype.Component
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext

import javax.swing.*

import com.wet.wired.jsr.recorder.*
import com.wet.wired.jsr.player.*
import com.wet.wired.jsr.converter.*
import ch.sebastianfiechter.testingowl.WelcomeWindow.Module


@Component
class Main {

	static main(args) {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/applicationContext.xml");

		WelcomeWindow welcome = applicationContext.getBean(WelcomeWindow.class)
		
		switch (welcome.show()) {
			case WelcomeWindow.Module.Cancel:
				println "Good bye."
				System.exit(0)
			case WelcomeWindow.Module.Recorder:
				applicationContext.getBean(JRecorder.class).init(args)
				break;
			case  WelcomeWindow.Module.Player:
				applicationContext.getBean(JPlayer.class).init(args)
				break;
			case  WelcomeWindow.Module.Converter:
				applicationContext.getBean(RecordingConverter.class).init(args)
				break;
		}
	}

}
