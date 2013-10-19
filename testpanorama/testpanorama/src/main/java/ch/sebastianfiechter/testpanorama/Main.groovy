package ch.sebastianfiechter.testpanorama


import org.springframework.beans.factory.annotation.*
import org.springframework.stereotype.Component
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext

import com.wet.wired.jsr.recorder.*

@Component
class Main {
	
	static main(args) {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/applicationContext.xml");
		JRecorder.main(args)
		
	}

}
