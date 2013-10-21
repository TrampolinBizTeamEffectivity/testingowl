package ch.sebastianfiechter.testpanorama

import org.springframework.stereotype.Component;

@Component
class Issues {
	enum IssueType {
		Topic, Bug, Musthave, Wish
	}

	class Issue {
		int id
		IssueType type
		int frameStart
		int frameEnd
		String message
	}

	List<Issue> issues = []
	int issuesIdCounter = 0

	def setTopic(String topic) {
		issues << new Issue(id:++issuesIdCounter, type:IssueType.Topic,
		frameStart:0, frameEnd:0, message:topic)
	}
	
	def addIssue(def issueType, int frameStart, int frameEnd, String message) {
		issues << new Issue(id:++issuesIdCounter, type:issueType,
			frameStart:frameStart, frameEnd:frameEnd, message:message)
	}


	def writeToExcelCsv(String filenameWithoutEnding) {

		//delete existing
		new File("${filenameWithoutEnding}.cap.csv").delete()

		def out = new File("${filenameWithoutEnding}.cap.csv")

		//header
		def rowHeader = [
			'"ID"',
			'"IssueType"',
			'"Start Frame"',
			'"End Frame"',
			'"Message"'
		]
		out.append(rowHeader.join(';'), "ISO-8859-1")
		out.append System.getProperty("line.separator")

		//rows
		issues.each {
			//replace ; in message with , because of cell limiter
			def row = [
				'"'+it.id+'"',
				'"'+it.type+'"',
				'"'+it.frameStart+'"',
				'"'+it.frameEnd+'"',
				'"'+it.message.replace(";", ",").replace('"', /'/)+'"'
			]
			out.append(row.join(';'), "ISO-8859-1")
			out.append "\n"
		}

	}

	static List readFromExcelCsv(String filenameWithoutEnding) {

		List<Issue> readIssues = []

		def file = new File("${filenameWithoutEnding}.cap.csv").splitEachLine(
				";", "ISO-8859-1") {fields ->

					//ignore first line
					if ("ID" == fields[0][1..-2]) {
						return
					}

					def issue = new Issues.Issue()
					issue.id = fields[0][1..-2]
					issue.type = fields[1][1..-2]
					issue.frameStart = fields[2][1..-2] as int
					issue.frameEnd = fields[3][1..-2] as int
					issue.message = fields[4][1..-2]
				
					readIssues << issue
				}

		return readIssues
	}


}
