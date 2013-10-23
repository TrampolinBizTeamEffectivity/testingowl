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
	
	def writeToExcel(String filenameWithoutEnding) {
	

		Workbook wb = new XSSFWorkbook();
		CreationHelper createHelper = wb.getCreationHelper();
		Sheet sheet = wb.createSheet("new sheet");
	
		// Create a row and put some cells in it. Rows are 0 based.
		Row row = sheet.createRow((short)0);
		// Create a cell and put a value in it.
		Cell cell = row.createCell(0);
		cell.setCellValue(1);
	
		// Or do it on one line.
		row.createCell(1).setCellValue(1.2);
		row.createCell(2).setCellValue(
			 createHelper.createRichTextString("This is a string"));
		row.createCell(3).setCellValue(true);
	
		// Write the output to a file
		FileOutputStream fileOut = new FileOutputStream("workbook.xls");
		wb.write(fileOut);
		fileOut.close();
		
			
				//delete existing
				new File("${filenameWithoutEnding}.cap.csv").delete()
		
				def out = new File("${filenameWithoutEnding}.cap.xlsx")
		
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

	
	static List readFromExcel(String filenameWithoutEnding) {
		
		OPCPackage pkg = OPCPackage.open(new File("file.xlsx"));
		XSSFWorkbook wb = new XSSFWorkbook(pkg);
		
		Sheet sheet1 = wb.getSheetAt(0);
		for (Row row : sheet1) {
			for (Cell cell : row) {
				CellReference cellRef = new CellReference(row.getRowNum(), cell.getColumnIndex());
				System.out.print(cellRef.formatAsString());
				System.out.print(" - ");
	
				switch (cell.getCellType()) {
					case Cell.CELL_TYPE_STRING:
						System.out.println(cell.getRichStringCellValue().getString());
						break;
					case Cell.CELL_TYPE_NUMERIC:
						if (DateUtil.isCellDateFormatted(cell)) {
							System.out.println(cell.getDateCellValue());
						} else {
							System.out.println(cell.getNumericCellValue());
						}
						break;
					case Cell.CELL_TYPE_BOOLEAN:
						System.out.println(cell.getBooleanCellValue());
						break;
					case Cell.CELL_TYPE_FORMULA:
						System.out.println(cell.getCellFormula());
						break;
					default:
						System.out.println();
				}
			}
		}
		
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
