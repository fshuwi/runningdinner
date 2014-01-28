package org.runningdinner.repository;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.runningdinner.core.Participant;
import org.runningdinner.core.Team;
import org.runningdinner.core.model.AbstractEntity;
import org.runningdinner.model.RunningDinner;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class RunningDinnerRepository extends AbstractRepository {

	@PersistenceContext
	private EntityManager em;

	public RunningDinner findDinnerByUuid(final String uuid) {
		TypedQuery<RunningDinner> query = em.createQuery("SELECT r FROM RunningDinner r WHERE r.uuid=:uuid", RunningDinner.class);
		query.setParameter("uuid", uuid);
		return getSingleResult(query);
	}

	public RunningDinner findDinnerByUuidWithParticipants(final String uuid) {
		TypedQuery<RunningDinner> query = em.createQuery("SELECT r FROM RunningDinner r LEFT JOIN FETCH r.participants WHERE r.uuid=:uuid",
				RunningDinner.class);
		query.setParameter("uuid", uuid);
		return getSingleResultMandatory(query);
	}

	public List<Participant> loadAllParticipantsOfDinner(final String uuid) {
		TypedQuery<Participant> query = em.createQuery(
				"SELECT p FROM RunningDinner r LEFT JOIN r.participants p WHERE r.uuid=:uuid ORDER BY p.participantNumber",
				Participant.class);
		query.setParameter("uuid", uuid);
		return query.getResultList();
	}

	public List<Participant> loadNotAssignableParticipantsFromDinner(String uuid) {
		TypedQuery<Participant> query = em.createQuery(
				"SELECT p FROM RunningDinner r LEFT JOIN r.notAssignedParticipants p WHERE r.uuid=:uuid ORDER BY p.participantNumber",
				Participant.class);
		query.setParameter("uuid", uuid);
		return query.getResultList();
	}

	public int loadNumberOfTeamsForDinner(final String uuid) {
		TypedQuery<Number> query = em.createQuery("SELECT DISTINCT COUNT(t) FROM RunningDinner r JOIN r.teams t WHERE r.uuid=:uuid",
				Number.class);
		query.setParameter("uuid", uuid);
		return query.getSingleResult().intValue();
	}

	public List<Team> loadRegularTeamsFromDinner(String uuid) {
		TypedQuery<Team> query = em.createQuery(
				"SELECT DISTINCT t FROM RunningDinner r JOIN r.teams t LEFT JOIN FETCH t.teamMembers LEFT JOIN FETCH t.mealClass WHERE r.uuid=:uuid ORDER BY t.teamNumber",
				Team.class);
		query.setParameter("uuid", uuid);
		return query.getResultList();
	}

	public List<Team> loadRegularTeamsWithArrangementsFromDinner(String uuid) {
		TypedQuery<Team> query = em.createQuery(
				"SELECT DISTINCT t FROM RunningDinner r JOIN r.teams t LEFT JOIN FETCH t.teamMembers LEFT JOIN FETCH t.mealClass LEFT JOIN FETCH t.visitationPlan.hostTeams LEFT JOIN FETCH t.visitationPlan.guestTeams WHERE r.uuid=:uuid ORDER BY t.teamNumber",
				Team.class);
		query.setParameter("uuid", uuid);
		List<Team> teamResults = query.getResultList();
		return teamResults;
	}

	public List<Team> loadRegularTeamsFromDinnerByKeys(String uuid, Set<String> teamKeys) {
		TypedQuery<Team> query = em.createQuery(
				"SELECT DISTINCT t FROM RunningDinner r JOIN r.teams t LEFT JOIN FETCH t.teamMembers LEFT JOIN FETCH t.mealClass WHERE r.uuid=:uuid AND t.naturalKey IN :teamKeys ORDER BY t.teamNumber",
				Team.class);
		query.setParameter("uuid", uuid);
		query.setParameter("teamKeys", teamKeys);
		return query.getResultList();
	}

	public Team loadSingleTeamWithVisitationPlan(String teamKey) {
		TypedQuery<Team> query = em.createQuery(
				"SELECT DISTINCT t FROM Team t LEFT JOIN FETCH t.teamMembers LEFT JOIN FETCH t.mealClass LEFT JOIN FETCH t.visitationPlan.hostTeams LEFT JOIN FETCH t.visitationPlan.guestTeams WHERE t.naturalKey=:teamKey ",
				Team.class);
		query.setParameter("teamKey", teamKey);
		return getSingleResult(query);
	}

	public List<Team> loadTeamsById(Set<Long> teamIds) {
		TypedQuery<Team> query = em.createQuery(
				"SELECT DISTINCT t FROM Team t LEFT JOIN FETCH t.teamMembers LEFT JOIN FETCH t.mealClass WHERE t.id IN :teamIds",
				Team.class);
		query.setParameter("teamIds", teamIds);
		return query.getResultList();
	}

	public List<Team> loadTeamsForParticipants(final String uuid, Set<String> participantKeys) {
		TypedQuery<Team> query = em.createQuery(
				"SELECT DISTINCT t FROM RunningDinner r JOIN r.teams t LEFT JOIN FETCH t.teamMembers members WHERE members.naturalKey IN :participantKeys AND r.uuid=:uuid",
				Team.class);
		query.setParameter("participantKeys", participantKeys);
		query.setParameter("uuid", uuid);
		return query.getResultList();
	}

	public Participant loadParticipant(String participantKey) {
		TypedQuery<Participant> query = em.createQuery("SELECT p FROM Participant p WHERE p.naturalKey=:participantKey", Participant.class);
		query.setParameter("participantKey", participantKey);
		return getSingleResult(query);
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
