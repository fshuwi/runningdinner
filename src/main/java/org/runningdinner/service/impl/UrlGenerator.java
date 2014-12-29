package org.runningdinner.service.impl;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.runningdinner.core.util.CoreUtil;
import org.runningdinner.ui.RequestMappings;
import org.springframework.beans.factory.annotation.Value;

public class UrlGenerator {

	@Value("${host.context.url}")
	private String hostUrlContext;

	/**
	 * Builds a valid URL for administrating the running dinner identified by the passed uuid.<br>
	 * Typically the URL is constructed out of a configured property which identifies host on which this app is running. If this property
	 * does not exist then it is tried to construct the host out of the passed request.
	 * 
	 * @param uuid
	 * @param request (Optional) Used when there is no configured host property for constructing the resulting URL
	 * @return
	 */
	public String constructAdministrationUrl(final String uuid, final HttpServletRequest request) {
		String hostUrlContextToUse = this.hostUrlContext;
		if (StringUtils.isEmpty(hostUrlContext)) {
			hostUrlContextToUse = generateHostContextFromRequest(request);
		}

		if (StringUtils.isEmpty(hostUrlContextToUse)) {
			throw new IllegalStateException("Host URL of current server could not be retrieved");
		}

		if (hostUrlContextToUse.endsWith("/")) {
			hostUrlContextToUse = StringUtils.chop(hostUrlContextToUse);
		}

		String adminUrlPart = RequestMappings.ADMIN_URL_PATTERN.replaceFirst("\\{" + RequestMappings.ADMIN_URL_UUID_MARKER + "\\}", uuid);
		return hostUrlContextToUse + adminUrlPart;
	}

	public String constructPrivateDinnerRouteUrl(final String teamKey, final String participantKey) {

		String hostUrlContextToUse = this.hostUrlContext;

		CoreUtil.assertNotEmpty(hostUrlContextToUse, "Host URL of current server could not be retrieved");
		CoreUtil.assertNotEmpty(teamKey, "Passed teamKey must not be empty!");
		CoreUtil.assertNotEmpty(participantKey, "Passed participantKey must not be empty!");

		if (hostUrlContextToUse.endsWith("/")) {
			hostUrlContextToUse = StringUtils.chop(hostUrlContextToUse);
		}

		String result = RequestMappings.TEAM_DINNER_ROUTE_FOR_PARTICIPANT.replaceFirst("\\{teamKey\\}", teamKey).replaceFirst(
				"\\{participantKey\\}", participantKey);
		return hostUrlContextToUse + result;
	}

	protected String generateHostContextFromRequest(final HttpServletRequest request) {
		throw new UnsupportedOperationException("not yet implemented");
	}

	public void setHostUrlContext(String hostUrlContext) {
		this.hostUrlContext = hostUrlContext;
	}
}
