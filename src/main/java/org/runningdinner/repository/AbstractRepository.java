package org.runningdinner.repository;

import java.util.List;

import javax.persistence.TypedQuery;

import org.runningdinner.core.CoreUtil;

public class AbstractRepository {

	protected <T> T getSingleResult(final TypedQuery<T> query) {
		List<T> resultList = query.getResultList();

		if (CoreUtil.isEmpty(resultList)) {
			return null;
		}
		if (resultList.size() != 1) {

		}
		return resultList.get(0);
	}

	protected <T> T getSingleResultMandatory(final TypedQuery<T> query) {
		T result = getSingleResult(query);
		if (result == null) {
			// TODO: throw excpetion
		}
		return result;
	}
}
