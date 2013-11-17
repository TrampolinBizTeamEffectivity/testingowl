package ch.sebastianfiechter.testingowl

import org.apache.poi.hssf.util.CellReference
import org.apache.poi.openxml4j.opc.OPCPackage
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CreationHelper
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.ss.util.WorkbookUtil
import org.apache.poi.ss.usermodel.DateUtil

import org.springframework.stereotype.Component;
import groovy.util.logging.*

@Component
@Slf4j
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
		String comment
	}
	
	def fileNameWithoutEnding

	List<Issue> issues
	def topic
	int issuesIdCounter
	
	def reset() {
		issues = []
		issuesIdCounter = -1
	}

	def addTopic(String topi) {
		topic = topi
		addIssue(IssueType.Topic, 1, 1, topic)
	}

	def addIssue(def issueType, int frameStart, int frameEnd, String message) {
		issues << new Issue(id:++issuesIdCounter, type:issueType,
		frameStart:frameStart, frameEnd:frameEnd, message:message, comment:"")
	}
	
	def writeToExcelXlsx() {

		assert null != fileNameWithoutEnding
		
		Workbook wb = new XSSFWorkbook();
		CreationHelper createHelper = wb.getCreationHelper();
		Sheet sheet = wb.createSheet(WorkbookUtil.createSafeSheetName("Issues"));

		// Create a row and put some cells in it. Rows are 0 based.
		Row rowHead = sheet.createRow(0);

		//header
		def rowHeader = [
			'ID',
			'IssueType',
			'Start Frame',
			'End Frame',
			'Message', 
			'Review-Comment'
		]

		for (def i=0; i<rowHeader.size();i++) {
			// Or do it on one line.
			rowHead.createCell(i).setCellValue(rowHeader[i]);
		}

		//issues
		for (def i=0; i<issues.size();i++) {
			Row rowIssue = sheet.createRow(i+1);
			rowIssue.createCell(0).setCellValue(issues[i].id as int);
			rowIssue.createCell(1).setCellValue(issues[i].type as String);
			rowIssue.createCell(2).setCellValue(issues[i].frameStart as int);
			rowIssue.createCell(3).setCellValue(issues[i].frameEnd as int);
			rowIssue.createCell(4).setCellValue(issues[i].message as String);
			rowIssue.createCell(5).setCellValue(issues[i].comment as String);
		}

		//delete existing
		new File("${fileNameWithoutEnding}.cap.xlsx").delete()

		// Write the output to a file
		FileOutputStream fileOut = new FileOutputStream(
			"${fileNameWithoutEnding}.cap.xlsx");
		wb.write(fileOut);
		fileOut.close();
	}

	def readFromExcelXlsx() {
		
		assert null != fileNameWithoutEnding

		issues = []
		
		OPCPackage pkg = OPCPackage.open(new File("${fileNameWithoutEnding}.cap.xlsx"));
		XSSFWorkbook wb = new XSSFWorkbook(pkg);

		XSSFSheet sheet1 = wb.getSheetAt(0);
	
		for (XSSFRow row : sheet1) {
			
			//ignore first line
			if (row.getRowNum() == 0) {
				continue
			}
			
			def issue = new Issues.Issue()
						
			issue.id = row.getCell(0).getNumericCellValue() as int
			issue.type = row.getCell(1).getRichStringCellValue().toString()
			issue.frameStart = row.getCell(2).getNumericCellValue() as int
			issue.frameEnd = row.getCell(3).getNumericCellValue() as int
			issue.message = row.getCell(4).getRichStringCellValue() as String
			issue.comment = row.getCell(5).getRichStringCellValue() as String
			
			//ignore first line
			if (issue.id != "ID") {
				issues << issue
			}
		}
	
	}

}
