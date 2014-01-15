package org.runningdinner.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.runningdinner.core.Participant;
import org.runningdinner.core.model.AbstractEntity;
import org.runningdinner.model.RunningDinner;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class RunningDinnerRepository extends AbstractRepository {

	@PersistenceContext
	private EntityManager em;

	public RunningDinner findRunningDinnerByUuid(final String uuid) {
		TypedQuery<RunningDinner> query = em.createQuery("SELECT r FROM RunningDinner r WHERE r.uuid=:uuid", RunningDinner.class);
		query.setParameter("uuid", uuid);
		return getSingleResult(query);
	}

	public RunningDinner findRunningDinnerByUuidWithParticipants(final String uuid) {
		TypedQuery<RunningDinner> query = em.createQuery("SELECT r FROM RunningDinner r LEFT JOIN FETCH r.participants WHERE r.uuid=:uuid",
				RunningDinner.class);
		query.setParameter("uuid", uuid);
		return getSingleResultMandatory(query);
	}

	public List<Participant> getParticipantsFromRunningDinner(final String runningDinnerUuid) {
		TypedQuery<Participant> query = em.createQuery("SELECT p FROM RunningDinner r LEFT JOIN r.participants p WHERE r.uuid=:uuid",
				Participant.class);
		query.setParameter("uuid", runningDinnerUuid);
		return query.getResultList();
	}

	public int getNumberOfTeamsForDinner(final String runningDinnerUuid) {
		TypedQuery<Number> query = em.createQuery("SELECT DISTINCT COUNT (t) FROM RunningDinner r JOIN r.teams t WHERE r.uuid=:uuid ",
				Number.class);
		query.setParameter("uuid", runningDinnerUuid);
		return query.getSingleResult().intValue();
	}

	@Transactional
	public <T extends AbstractEntity> T save(final T entity) {
		if (entity.isNew()) {
			em.persist(entity);
			return entity;
		}
		else {
			return em.merge(entity);
		}
	}
}
