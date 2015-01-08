package org.runningdinner.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.runningdinner.core.MealClass;
import org.runningdinner.core.Participant;
import org.runningdinner.core.util.CoreUtil;
import org.runningdinner.ui.json.TeamMemberWrapper;
import org.runningdinner.ui.json.TeamReferenceWrapper;
import org.runningdinner.ui.json.TeamWrapper;
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

		List<TeamWrapper> teams = (List<TeamWrapper>)model.get("regularTeams");
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

		Map<Integer, String> teamMemberNameMappings = generateTeamMemberNames(teams);

		for (TeamWrapper team : teams) {

			sheet.createRow(rowCounter++); // Empty row as separator

			int colCounter = 0;
			HSSFRow row = sheet.createRow(rowCounter++);

			int teamNumber = team.getTeamNumber();

			List<TeamReferenceWrapper> hostTeams = team.getHostTeams();
			List<TeamReferenceWrapper> guestTeams = team.getGuestTeams();
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
			for (TeamMemberWrapper teamMember : team.getTeamMembers()) {
				HSSFRow subRow = subRows.get(cnt++);
				if (teamMember.isHost()) {
					String zipAddition = " (" + messages.getMessage("label.zip", null, userLocale) + ": " + teamMember.getZip() + ")";
					addBoldStringCell(subRow, teamMembersCol, teamMember.getFullname() + zipAddition);
				}
				else {
					addPlainStringCell(subRow, teamMembersCol, teamMember.getFullname());
				}
			}

			cnt = 0;
			for (TeamReferenceWrapper guestTeam : guestTeams) {
				HSSFRow subRow = subRows.get(cnt++);
				addPlainStringCell(subRow, guestTeamsCol, generateTeamNrWithName(guestTeam, teamMemberNameMappings));
			}

			cnt = 0;
			for (TeamReferenceWrapper hostTeam : hostTeams) {
				HSSFRow subRow = subRows.get(cnt++);
				addPlainStringCell(subRow, hostTeamsCol, generateTeamNrWithName(hostTeam, teamMemberNameMappings));
			}

			cnt = 0;
			for (TeamReferenceWrapper hostTeam : hostTeams) {
				HSSFRow subRow = subRows.get(cnt++);
				addPlainStringCell(subRow, hostTeamMealsCol, hostTeam.getMealClass().getLabel());
			}

		}

		if (!CoreUtil.isEmpty(notAssignedParticipants)) {
			// TODO
		}
	}

	private String generateTeamNrWithName(final TeamReferenceWrapper team, final Map<Integer,String> teamMemberNameMappings) {
		String postfix = StringUtils.EMPTY;
		
		String teamMemberNames = teamMemberNameMappings.get(team.getTeamNumber());
		if (!StringUtils.isEmpty(teamMemberNames)) {
			postfix = " - (" + teamMemberNames + ")";
		}
		
		return team.getTeamNumber() + postfix;
	}

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

	private Map<Integer, String> generateTeamMemberNames(List<TeamWrapper> teams) {
		Map<Integer, String> result = new HashMap<Integer, String>();

		for (TeamWrapper team : teams) {

			StringBuilder teamMemberNamesCommaSeparated = new StringBuilder();

			List<TeamMemberWrapper> teamMembers = team.getTeamMembers();
			int cnt = 0;
			for (TeamMemberWrapper teamMember : teamMembers) {
				if (cnt++ > 0) {
					teamMemberNamesCommaSeparated.append(", ");
				}
				teamMemberNamesCommaSeparated.append(teamMember.getFullname());
			}

			result.put(team.getTeamNumber(), teamMemberNamesCommaSeparated.toString());
		}

		return result;
	}

	@Autowired
	public void setMessages(MessageSource messages) {
		this.messages = messages;
	}
}