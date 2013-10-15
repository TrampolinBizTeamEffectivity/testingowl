package ch.sebastianfiechter.utils.buildbulb


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext

@Component
class Main {

	@Autowired
	IJenkinsWebSiteReaderService webSiteReader;
	
	static main(args) {
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/applicationContext.xml");
		Main p = applicationContext.getBean(Main.class)
		p.repeat()
		
	}
	
	def repeat() {
		
		println 'will read website'
		webSiteReader.read()
		
		
		new Timer().runAfter(1000) {
			repeat()
		}
	}

}
