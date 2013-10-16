package ch.sebastianfiechter.utils.buildbulb


import org.springframework.beans.factory.annotation.*
import org.springframework.stereotype.Component
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext

@Component
class Main {

	public static final int POLL_INTERVAL = 2000
	
	@Autowired
	@Qualifier("jenkinsWebSiteReaderService")
	IJenkinsWebSiteReaderService webSiteReader
	
	@Autowired
	ConfigurationService configuration
	
	@Autowired
	JobsCheckerService jobsChecker
	
	TimerTask timerTask
	
	static main(args) {
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/applicationContext.xml");
		Main p = applicationContext.getBean(Main.class)
		p.startPolling()
		
	}
	
	def startPolling() {
		timerTask = new Timer().runAfter(POLL_INTERVAL) {
			poll()
			startPolling();
		}
	}
	
	def poll() {
		println 'will read website'
		
		//def html = webSiteReader.read(configuration.config.address, configuration.config.user, configuration.config.password)
		jobsChecker.checkSuccessfull("html", configuration.config.jobs.job as List)
	}
	
	def stopPolling() {
		timerTask.cancel();
	}

}
