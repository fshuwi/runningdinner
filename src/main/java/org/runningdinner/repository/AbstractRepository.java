package org.runningdinner.repository;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityNotFoundException;
import javax.persistence.TypedQuery;

import org.runningdinner.core.CoreUtil;
import org.runningdinner.core.model.AbstractEntity;

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
		if (result == null) { // TODO: Use Spring's exception?
			throw new EntityNotFoundException("ASDf");
			// TODO: throw excpetion
		}
		return result;
	}

	public <T extends AbstractEntity> Set<Long> getEntityIds(final Collection<T> entities) {
		if (CoreUtil.isEmpty(entities)) {
			return Collections.emptySet();
		}

		Set<Long> result = new HashSet<Long>(entities.size());
		for (T entity : entities) {
			result.add(entity.getId());
		}
		return result;
	}
}
