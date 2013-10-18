package ch.sebastianfiechter.utils.buildbulb

import org.springframework.stereotype.Service;

import java.util.regex.*;

@Service
class JobsCheckerService {

	enum JobStatus {
		Failed, Unstable, Disabled, Pending, Success, Job_Not_Found
	}

	/**
	 * @param htmlSource
	 * @param jobs
	 * @return JobStatus.Failed if any of jobs failed, JobStatus.Disabled if no one failed, but one or more are disabled, JobStatus.Success, if all Jobs success
	 */
	def JobStatus checkStatus(htmlSource, List jobsToSucceed) {

		def disabled = 0
		def success = 0
		def unstable = 0
		def failed = 0
		def pending = 0

		def slurper = new XmlSlurper().parseText(htmlSource);

		for (jobToSucceed in jobsToSucceed) {

			def job = slurper.job.find{it.name == jobToSucceed}

			//jobToSuccedd not found, break
			if (job.name != jobToSucceed) return JobStatus.Job_Not_Found;

			switch (job.color) {
				case 'disabled':
					disabled++
					break
				case 'blue':
				case 'blue_anime':
					success++
					break
				case 'yellow':
				case 'yellow_anime':
					unstable++
					break
				case 'red':
				case 'red_anime':
					failed++
					break
				case 'grey':
				case 'grey_anime':
					pending++
					break
				default:
					throw new Error("Uups, state not known")
			}
		}

		if (failed > 0) return JobStatus.Failed

		if (unstable > 0) return JobStatus.Unstable;

		if (disabled > 0) return JobStatus.Disabled;

		if (pending > 0) return JobStatus.Pending;

		if (success > 0) return JobStatus.Success;

		/*
		 for ( job in slurper.job ) {
		 println("${job.name} - ${job.color}")
		 }
		 */

		//	return JobStatus.Success
		//			def jobHtmlEncoded = Pattern.quote(encodeStringPercentEncoding(it.toString()))
		//			//single line mode //between the word Success and the job name are some characters
		//			//see http://groovy.codehaus.org/Documenting+Regular+Expressions+in+Groovy
		//			def patternFailed = '(?s).*<img alt=\"Failed\".*/></td><td.*><a href=\"job/'+jobHtmlEncoded+'.*'
		//			if (htmlSource ==~ patternFailed) {
		//				return JobStatus.Failed
		//			}
		//
		//			def patternDisabled = '(?s).*<img alt=\"Disabled\".*/></td><td.*><a href=\"job/'+jobHtmlEncoded+'.*'
		//			println patternDisabled
		//			def res = htmlSource ==~ patternDisabled
		//			disabled += (htmlSource ==~ patternDisabled) ? 1:0;
		//
		//			def patternSuccess = '(?s).*<img alt=\"Success\".*/></td><td.*><a href=\"job/'+jobHtmlEncoded+'.*'
		//			println patternSuccess
		//			def ress = htmlSource ==~ patternSuccess
		//			success += (htmlSource ==~ patternSuccess) ? 1:0;
		//}

		//if (disabled > 0) return JobStatus.Disabled;

		//if (success > 0) return JobStatus.Success;

		//should get here
		//println "Something bad happened: One or more of jobs doen't match Job Status regex pattern";
		//throw new Exception ("One or more of jobs doen't match Job Status regex pattern");
	}

	def encodeStringPercentEncoding(def stringToEncode){

		def reservedCaracters = [32:1, 33:1, 42:1, 34:1, 39:1, 40:1, 41:1, 59:1, 58:1, 64:1, 38:1, 61:1, 43:1, 36:1, 33:1, 47:1, 63:1, 37:1, 91:1, 93:1, 35:1]

		def encoded =  stringToEncode.collect { letter ->
			reservedCaracters[(int)letter] ? "%" +Integer.toHexString((int)letter).toString().toUpperCase() : letter
		}
		return encoded.join("")
	}
}
