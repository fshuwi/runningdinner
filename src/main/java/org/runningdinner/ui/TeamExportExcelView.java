package org.runningdinner.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.runningdinner.core.MealClass;
import org.runningdinner.core.Participant;
import org.runningdinner.core.Team;
import org.runningdinner.core.util.CoreUtil;
import org.runningdinner.service.email.FormatterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.document.AbstractExcelView;

public class TeamExportExcelView extends AbstractExcelView {

	protected MessageSource messages;

	@SuppressWarnings("unchecked")
	@Override
	protected void buildExcelDocument(Map<String, Object> model, HSSFWorkbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Locale userLocale = RequestContextUtils.getLocale(request);

		List<Team> teams = (List<Team>)model.get("regularTeams");
		List<Participant> notAssignedParticipants = (List<Participant>)model.get("notAssignedParticipants");

		HSSFSheet sheet = workbook.createSheet("Teams");

		HSSFRow headlineRow = sheet.createRow(0);
		int headlineIndex = 0;
		addBoldStringCell(headlineRow, headlineIndex++, messages.getMessage("label.team", null, userLocale));
		addBoldStringCell(headlineRow, headlineIndex++, messages.getMessage("label.teammembers", null, userLocale));
		addBoldStringCell(headlineRow, headlineIndex++, messages.getMessage("label.meal", null, userLocale));
		addBoldStringCell(headlineRow, headlineIndex++, messages.getMessage("label.receives", null, userLocale));
		addBoldStringCell(headlineRow, headlineIndex++, messages.getMessage("label.visits", null, userLocale));
		addBoldStringCell(headlineRow, headlineIndex++, " ");

		int rowCounter = 1;

		for (Team team : teams) {

			sheet.createRow(rowCounter++); // Empty row as separator

			int colCounter = 0;
			HSSFRow row = sheet.createRow(rowCounter++);

			int teamNumber = team.getTeamNumber();
			Set<Team> hostTeams = team.getVisitationPlan().getHostTeams();
			Set<Team> guestTeams = team.getVisitationPlan().getGuestTeams();
			MealClass mealClass = team.getMealClass();

			addPlainStringCell(row, colCounter++, String.valueOf(teamNumber));

			int numSubRows = team.getTeamMembers().size();
			numSubRows = Math.max(numSubRows, hostTeams.size());
			List<HSSFRow> subRows = new ArrayList<HSSFRow>(numSubRows);
			subRows.add(row);
			for (int i = 1; i < numSubRows; i++) {
				subRows.add(sheet.createRow(rowCounter++));
			}

			int teamMembersCol = colCounter++; // Remember column on which to place the team member names

			addPlainStringCell(row, colCounter++, mealClass.getLabel());

			int guestTeamsCol = colCounter++;
			int hostTeamsCol = colCounter++;
			int hostTeamMealsCol = colCounter++;

			int cnt = 0;
			for (Participant teamMember : team.getTeamMembers()) {
				HSSFRow subRow = subRows.get(cnt++);
				if (teamMember.isHost()) {
					String zipAddition = " (" + messages.getMessage("label.zip", null, userLocale) + ": "
							+ teamMember.getAddress().getZip() + ")";
					addBoldStringCell(subRow, teamMembersCol, teamMember.getName().getFullnameFirstnameFirst() + zipAddition);
				}
				else {
					addPlainStringCell(subRow, teamMembersCol, teamMember.getName().getFullnameFirstnameFirst());
				}
			}

			cnt = 0;
			for (Team guestTeam : guestTeams) {
				HSSFRow subRow = subRows.get(cnt++);
				addPlainStringCell(subRow, guestTeamsCol, generateTeamNrWithName(guestTeam));
			}

			cnt = 0;
			for (Team hostTeam : hostTeams) {
				HSSFRow subRow = subRows.get(cnt++);
				addPlainStringCell(subRow, hostTeamsCol, generateTeamNrWithName(hostTeam));
			}

			cnt = 0;
			for (Team hostTeam : hostTeams) {
				HSSFRow subRow = subRows.get(cnt++);
				addPlainStringCell(subRow, hostTeamMealsCol, hostTeam.getMealClass().getLabel());
			}

		}

		if (!CoreUtil.isEmpty(notAssignedParticipants)) {
			// TODO
		}
	}

	private String generateTeamNrWithName(Team team) {
		String teamMemberNames = FormatterUtil.generateParticipantNamesWithCommas(team);
		return team.getTeamNumber() + " - (" + teamMemberNames + ")";
	}

	// protected HSSFCell addMultilineStringCell(HSSFRow row, int index, String value) {
	// HSSFCell cell = addStringCell(row, index, value, HSSFCell.CELL_TYPE_STRING);
	// HSSFCellStyle cellStyle = row.getSheet().getWorkbook().createCellStyle();
	// cellStyle.setWrapText(true);
	// cell.setCellStyle(cellStyle);
	// row.setHeight((short)(100 * 6));
	// return cell;
	// }

	protected HSSFCell addPlainStringCell(HSSFRow row, int index, String value) {
		return addStringCell(row, index, value, HSSFCell.CELL_TYPE_STRING);
	}

	protected HSSFCell addBoldStringCell(HSSFRow row, int index, String value) {
		HSSFCell cell = addStringCell(row, index, value, HSSFCell.CELL_TYPE_STRING);
		cell.setCellStyle(getHeadlineCellStyle(row.getSheet()));
		return cell;
	}

	protected HSSFCell addStringCell(HSSFRow row, int index, String value, int cellType) {
		HSSFCell cell = row.createCell(index, cellType);
		cell.setCellValue(new HSSFRichTextString(value));
		return cell;
	}

	protected HSSFCellStyle getHeadlineCellStyle(final HSSFSheet sheet) {
		HSSFWorkbook workbook = sheet.getWorkbook();
		HSSFCellStyle headlineCellStyle = workbook.createCellStyle();
		HSSFFont font = workbook.createFont();
		font.setColor(HSSFFont.COLOR_NORMAL);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		headlineCellStyle.setFont(font);
		return headlineCellStyle;
	}

	@Autowired
	public void setMessages(MessageSource messages) {
		this.messages = messages;
	}
}