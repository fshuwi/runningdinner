package org.runningdinner.repository.jpa;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.runningdinner.core.Participant;
import org.runningdinner.core.Team;
import org.runningdinner.core.model.AbstractEntity;
import org.runningdinner.model.RunningDinner;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class RunningDinnerRepositoryJpa extends AbstractJpaRepository {

	@PersistenceContext
	private EntityManager em;

	/**
	 * Returns a running dinner instance without resolving the lazy associations
	 * 
	 * @param uuid
	 * @return
	 */
	public RunningDinner findDinnerWithBasicDetailsByUuid(final String uuid) {
		TypedQuery<RunningDinner> query = em.createQuery("SELECT r FROM RunningDinner r WHERE r.uuid=:uuid", RunningDinner.class);
		query.setParameter("uuid", uuid);
		return getSingleResult(query);
	}

	/**
	 * Returns a running dinner instance and also loads all participants of this dinner
	 * 
	 * @param uuid
	 * @return
	 */
	public RunningDinner findDinnerByUuidWithParticipants(final String uuid) {
		TypedQuery<RunningDinner> query = em.createQuery("SELECT r FROM RunningDinner r LEFT JOIN FETCH r.participants WHERE r.uuid=:uuid",
				RunningDinner.class);
		query.setParameter("uuid", uuid);
		return getSingleResultMandatory(query);
	}

	/**
	 * Fetches all participants of a running dinner
	 * 
	 * @param uuid The identifier of the running dinner
	 * @return
	 */
	public List<Participant> loadAllParticipantsOfDinner(final String uuid) {
		TypedQuery<Participant> query = em.createQuery(
				"SELECT p FROM RunningDinner r LEFT JOIN r.participants p WHERE r.uuid=:uuid ORDER BY p.participantNumber",
				Participant.class);
		query.setParameter("uuid", uuid);
		return query.getResultList();
	}

	/**
	 * Fetches only those participants of a running dinner that could not be assigned into teams.<br>
	 * This call makes only sense after there have been performed a (persistent) team assignment
	 * 
	 * @param uuid
	 * @return
	 */
	public List<Participant> loadNotAssignableParticipantsFromDinner(String uuid) {
		TypedQuery<Participant> query = em.createQuery(
				"SELECT p FROM RunningDinner r LEFT JOIN r.notAssignedParticipants p WHERE r.uuid=:uuid ORDER BY p.participantNumber",
				Participant.class);
		query.setParameter("uuid", uuid);
		return query.getResultList();
	}

	/**
	 * Returns the number of regular teams of a running dinner
	 * 
	 * @param uuid
	 * @return
	 */
	public int loadNumberOfTeamsForDinner(final String uuid) {
		TypedQuery<Number> query = em.createQuery("SELECT DISTINCT COUNT(t) FROM RunningDinner r JOIN r.teams t WHERE r.uuid=:uuid",
				Number.class);
		query.setParameter("uuid", uuid);
		return query.getSingleResult().intValue();
	}

	/**
	 * Loads all regular teams of a running dinner (including their meals to cook and also their team-members).<br>
	 * Note: This method doesn't load the dinner-routes (Visitation-Plans)
	 * 
	 * @param uuid
	 * @return
	 */
	public List<Team> loadRegularTeamsFromDinner(String uuid) {
		TypedQuery<Team> query = em.createQuery(
				"SELECT DISTINCT t FROM RunningDinner r JOIN r.teams t LEFT JOIN FETCH t.teamMembers LEFT JOIN FETCH t.mealClass WHERE r.uuid=:uuid ORDER BY t.teamNumber",
				Team.class);
		query.setParameter("uuid", uuid);
		return query.getResultList();
	}

	/**
	 * Loads all regular teams of a running dinner, including also their Visitation-Plans (dinner routes).<br>
	 * Currently it is possible to access also the team-members of fetched host- and/or guest-teams. But this is only possible because
	 * we load all teams from database.<br>
	 * If we would not load all teams (e.g. when using paging), this would not be possible and would result in
	 * LazyInitializationExceptions!
	 * 
	 * @param uuid
	 * @return
	 */
	public List<Team> loadRegularTeamsWithArrangementsFromDinner(String uuid) {
		TypedQuery<Team> query = em.createQuery(
				"SELECT DISTINCT t FROM RunningDinner r JOIN r.teams t LEFT JOIN FETCH t.teamMembers LEFT JOIN FETCH t.mealClass LEFT JOIN FETCH t.visitationPlan.hostTeams LEFT JOIN FETCH t.visitationPlan.guestTeams WHERE r.uuid=:uuid ORDER BY t.teamNumber",
				Team.class);
		query.setParameter("uuid", uuid);
		List<Team> teamResults = query.getResultList();
		return teamResults;
	}

	/**
	 * Loads all teams identified by the passed natural keys.
	 * 
	 * @param uuid
	 * @param teamKeys
	 * @return
	 */
	public List<Team> loadRegularTeamsFromDinnerByKeys(String uuid, Set<String> teamKeys) {
		TypedQuery<Team> query = em.createQuery(
				"SELECT DISTINCT t FROM RunningDinner r JOIN r.teams t LEFT JOIN FETCH t.teamMembers LEFT JOIN FETCH t.mealClass WHERE r.uuid=:uuid AND t.naturalKey IN :teamKeys ORDER BY t.teamNumber",
				Team.class);
		query.setParameter("uuid", uuid);
		query.setParameter("teamKeys", teamKeys);
		return query.getResultList();
	}

	/**
	 * Loads the teams identified by the passed naturalKeys.
	 * 
	 * @param teamKeys The naturalKeys that identify the teams to load
	 * @param fetchTeamMembersOfReferencedTeams If set to true each team is also loaded with the complete dinner-routes
	 * @return
	 */
	@Transactional
	public List<Team> loadTeamsWithVisitationPlan(Set<String> teamKeys, boolean fetchTeamMembersOfReferencedTeams) {
		TypedQuery<Team> query = em.createQuery(
				"SELECT t FROM Team t LEFT JOIN FETCH t.teamMembers LEFT JOIN FETCH t.mealClass LEFT JOIN FETCH t.visitationPlan.hostTeams LEFT JOIN FETCH t.visitationPlan.guestTeams WHERE t.naturalKey IN :teamKeys",
				Team.class);
		query.setParameter("teamKeys", teamKeys);
		List<Team> result = query.getResultList();

		// Load also teamMembers of referenced teams):
		// Not very nice, but currently this is the easiest way, and should also not slow down performance due to @BatchSize in
		// Team.teamMembers
		if (fetchTeamMembersOfReferencedTeams) {

			for (Team resultTeam : result) {
				Set<Team> hostTeams = resultTeam.getVisitationPlan().getHostTeams();
				Set<Team> guestTeams = resultTeam.getVisitationPlan().getGuestTeams();
				Set<Team> allReferencedTeams = new HashSet<Team>(hostTeams);
				allReferencedTeams.addAll(guestTeams);
				for (Team referencedTeam : allReferencedTeams) {
					referencedTeam.getTeamMembers().size();
				}
			}

		}

		return result;
	}

	/**
	 * Loads a team from database with his complete dinner-route (if fetchTeamMembersOfReferencedTeams is set to true), his assigned
	 * team-members and his assigned meal.<br>
	 * Furthermore the team members of all referenced teams in this dinner-route are also loaded
	 * 
	 * @param teamKey The naturalKey which identifies the team to load
	 * @param fetchTeamMembersOfReferencedTeams
	 * @return The found team or an exception will be thrown
	 * @throws EntityNotFoundException when no team is found
	 */

	@Transactional
	public Team loadSingleTeamWithVisitationPlan(String teamKey, boolean fetchTeamMembersOfReferencedTeams) {
		TypedQuery<Team> query = em.createQuery(
				"SELECT t FROM Team t LEFT JOIN FETCH t.teamMembers LEFT JOIN FETCH t.mealClass LEFT JOIN FETCH t.visitationPlan.hostTeams LEFT JOIN FETCH t.visitationPlan.guestTeams WHERE t.naturalKey=:teamKey ",
				Team.class);
		query.setParameter("teamKey", teamKey);
		Team result = getSingleResultMandatory(query);

		// Load also teamMembers of referenced teams):
		// Not very nice, but currently this is the easiest way, and should also not slow down performance due to @BatchSize in
		// Team.teamMembers
		if (fetchTeamMembersOfReferencedTeams) {
			Set<Team> hostTeams = result.getVisitationPlan().getHostTeams();
			Set<Team> guestTeams = result.getVisitationPlan().getGuestTeams();
			Set<Team> allReferencedTeams = new HashSet<Team>(hostTeams);
			allReferencedTeams.addAll(guestTeams);
			for (Team referencedTeam : allReferencedTeams) {
				referencedTeam.getTeamMembers().size();
			}
		}

		return result;
	}

	public List<Team> loadTeamsForParticipants(final String uuid, Set<String> participantKeys) {
		TypedQuery<Team> query = em.createQuery(
				"SELECT DISTINCT t FROM RunningDinner r JOIN r.teams t LEFT JOIN FETCH t.teamMembers members WHERE members.naturalKey IN :participantKeys AND r.uuid=:uuid",
				Team.class);
		query.setParameter("participantKeys", participantKeys);
		query.setParameter("uuid", uuid);
		return query.getResultList();
	}

	/**
	 * Loads the participant identified by the passed naturalKey
	 * 
	 * @param participantKey
	 * @throws EntityNotFoundException If the participant is not found
	 * @return
	 */
	public Participant loadParticipant(final String participantKey) {
		TypedQuery<Participant> query = em.createQuery("SELECT p FROM Participant p WHERE p.naturalKey=:participantKey", Participant.class);
		query.setParameter("participantKey", participantKey);
		return getSingleResultMandatory(query);
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

// public List<Participant> loadTeamMembersOfTeams(Set<Team> allTeams) {
// Set<Long> teamIds = getEntityIds(allTeams);
// TypedQuery<Participant> query = em.createQuery("SELECT DISTINCT p FROM Team t JOIN t.teamMembers p WHERE t.id IN :teamIds",
// Participant.class);
// query.setParameter("teamIds", teamIds);
// return query.getResultList();
// }

// public List<Team> loadTeamsById(Set<Long> teamIds) {
// TypedQuery<Team> query = em.createQuery(
// "SELECT DISTINCT t FROM Team t LEFT JOIN FETCH t.teamMembers LEFT JOIN FETCH t.mealClass WHERE t.id IN :teamIds",
// Team.class);
// query.setParameter("teamIds", teamIds);
// return query.getResultList();
// }

// public List<Team> loadTeamsForParticipants(final String uuid, Set<String> participantKeys) {
// TypedQuery<Team> query = em.createQuery(
// "SELECT DISTINCT t FROM RunningDinner r JOIN r.teams t LEFT JOIN FETCH t.teamMembers members WHERE members.naturalKey IN :participantKeys AND r.uuid=:uuid",
// Team.class);
// query.setParameter("participantKeys", participantKeys);
// query.setParameter("uuid", uuid);
// return query.getResultList();
// }