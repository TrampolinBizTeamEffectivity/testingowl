package ch.sebastianfiechter.utils.buildbulb


import org.springframework.beans.factory.annotation.*
import org.springframework.stereotype.Component
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext

@Component
class Main {

	
	@Autowired
	@Qualifier("jenkinsWebSiteReaderService")
	IJenkinsWebSiteReaderService webSiteReader
	
	@Autowired
	@Qualifier("bulbService")
	IBulbService bulb
	
	@Autowired
	ConfigurationService configuration
	
	@Autowired
	JobsCheckerService jobsChecker
	
	TimerTask timerTask
	
	static main(args) {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/applicationContext.xml");
		Main p = applicationContext.getBean(Main.class)
		p.startUp()
		
	}
	
	def startUp() {
		bulb.showStartup()
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				stopPolling();
			}
		});
		startPolling()
	}
	
	def startPolling() {
		timerTask = new Timer().runAfter(configuration.config.pollintervall.toString() as int) {
			poll()
			startPolling();
		}
	}
	
	def poll() {
		println 'poll'
		
		configuration.readConfig()
		def restXml = webSiteReader.read(configuration.config.address, configuration.config.user, configuration.config.password)
		def status = jobsChecker.checkStatus(restXml, configuration.config.jobs.job as List)
		switch (status) {
			case JobsCheckerService.JobStatus.Success:
				bulb.showGreen()
				break
			case JobsCheckerService.JobStatus.Failed:
			case JobsCheckerService.JobStatus.Unstable:
			case JobsCheckerService.JobStatus.Disabled:
			case JobsCheckerService.JobStatus.Pending:
				bulb.showRed()
				break
			case JobsCheckerService.JobStatus.Job_Not_Found:
				bulb.showError()
				break
			default:	
				throw new Exception("Uups, JobStatus undefined: ${status}")
			
		}
	}
	
	def stopPolling() {
		bulb.switchOff()
		timerTask.cancel()
	}

}
