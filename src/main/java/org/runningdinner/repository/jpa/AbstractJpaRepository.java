package org.runningdinner.repository.jpa;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.runningdinner.core.model.AbstractEntity;
import org.runningdinner.core.util.CoreUtil;

/**
 * Contains some common methods for a JPA repository implementation class
 * 
 * @author Clemens Stich
 * 
 */
public class AbstractJpaRepository {

	/**
	 * Helper method for retrieving a single result from a JPA query.
	 * 
	 * @param query
	 * @throws NonUniqueResultException
	 * @return The result or null if there exist no result or NonUniqueResultException if there exist multiple results
	 */
	protected <T> T getSingleResult(final TypedQuery<T> query) {
		List<T> resultList = query.getResultList();

		if (CoreUtil.isEmpty(resultList)) {
			return null;
		}

		return resultList.get(0);
	}
	
	@SuppressWarnings("unchecked")
	protected <T> T getSingleResult(final Query query, final Class<T> clazz) {
		List<T> resultList = query.getResultList();

		if (CoreUtil.isEmpty(resultList)) {
			return null;
		}

		return resultList.get(0);
	}
	

	/**
	 * Helper method for retrieving a single result from a JPA query.
	 * 
	 * @param query
	 * @throws NonUniqueResultException
	 * @throws EntityNotFoundException If no single result was found
	 * @return The result which is never null or NonUniqueResultException if there exist multiple results
	 */
	protected <T> T getSingleResultMandatory(final TypedQuery<T> query) {
		T result = getSingleResult(query);
		if (result == null) {
			throw new EntityNotFoundException("Could not find entity");
		}
		return result;
	}

	/**
	 * Gathers all IDs of the passed entities and returns them as a set
	 * 
	 * @param entities
	 * @return
	 */
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
