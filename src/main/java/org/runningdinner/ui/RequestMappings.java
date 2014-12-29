package org.runningdinner.ui;

public class RequestMappings {

	// *************** Admin request mappings ********************** //
	public static final String ADMIN_URL_UUID_MARKER = "uuid";
	public static final String ADMIN_URL_PATTERN = "/event/{" + ADMIN_URL_UUID_MARKER + "}/admin";

	public static final String ADMIN_OVERVIEW = ADMIN_URL_PATTERN;
	public static final String SHOW_TEAMS = ADMIN_URL_PATTERN + "/teams";
	public static final String SEND_TEAM_MAILS = ADMIN_URL_PATTERN + "/teams/mail";
	public static final String EXPORT_TEAMS = SHOW_TEAMS + ".xls"; // Use Spring content negotiation
	public static final String SEND_PARTICIPANT_MAILS = ADMIN_URL_PATTERN + "/participants/mail";
	public static final String SEND_DINNERROUTES_MAIL = ADMIN_URL_PATTERN + "/dinnerroute/mail";
	public static final String SHOW_PARTICIPANTS = ADMIN_URL_PATTERN + "/participants";
	public static final String EDIT_MEALTIMES = ADMIN_URL_PATTERN + "/mealtimes";
	public static final String EDIT_PARTICIPANT = ADMIN_URL_PATTERN + "/participant/{key}/edit";

	public static final String TEAM_DINNER_ROUTE_FOR_ADMINS = "/team/{teamKey}/route";
	
	public static final String AJAX_CHECK_MAIL_CONNECTION = ADMIN_URL_PATTERN + "/checkMailConnection";
	public static final String AJAX_SAVE_MAIL_SETTINGS = ADMIN_URL_PATTERN + "/saveCustomMailSettings";

	public static final String AJAX_SAVE_HOSTS = ADMIN_URL_PATTERN + "/teams/savehosts";
	public static final String AJAX_SWITCH_TEAMMEMBERS = ADMIN_URL_PATTERN + "/teams/switchmembers";

	public static final String AJAX_GET_CROSSING_TEAMS_PREFIX = ADMIN_URL_PATTERN + "/teams";
	public static final String AJAX_GET_CROSSING_TEAMS = AJAX_GET_CROSSING_TEAMS_PREFIX + "/{teamKey}/crossingteams";

	// *************** Wizard request mappings *********************** //
	public static final String WIZARD_STEP = "/wizard";
	public static final String WIZARD_UPLOAD = "/wizard-upload";
	public static final String WIZARD_FINISH = "/finish";

	// *************** Team / Participant request mappings ****************** //
	public static final String TEAM_DINNER_ROUTE_FOR_PARTICIPANT = "/team/{teamKey}/participant/{participantKey}/route";

	// *************** Request Parameters ********************* //
	public static final String SELECT_ALL_TEAMS_PARAMETER = "selectAll";

	// ************** Static request mappings **************** //
	public static final String PRIVACY = "/privacy";
	public static final String IMPRESSUM = "/impressum";
	public static final String ABOUT = "/about";

}
