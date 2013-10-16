package ch.sebastianfiechter.utils.buildbulb

import org.springframework.stereotype.Service;

@Service
class JobsCheckerService {

	def checkSuccessfull(htmlSource, List jobs) {
		jobs.each {
			def pattern = "'Success'([^>]*>){3}[^>]*'${it}'"
			println pattern
			if (!pattern.matcher(htmlSource.toString()).matches()) {
				return false
			}
		}
		
		return true
	}
	
}
