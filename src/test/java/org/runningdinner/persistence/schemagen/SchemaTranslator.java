package org.runningdinner.persistence.schemagen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.jdbc.util.FormatStyle;
import org.hibernate.jdbc.util.Formatter;
import org.runningdinner.core.MealClass;
import org.runningdinner.core.Participant;
import org.runningdinner.core.Team;
import org.runningdinner.core.VisitationPlan;
import org.runningdinner.model.DinnerRouteMailReport;
import org.runningdinner.model.RunningDinner;
import org.runningdinner.model.RunningDinnerPreference;
import org.runningdinner.model.TeamMailReport;

public class SchemaTranslator {
	private Configuration config = null;

	public SchemaTranslator() {
		config = new Configuration();
	}

	public SchemaTranslator setDialect(String dialect) {
		config.setProperty("hibernate.dialect", dialect);
		return this;
	}

	/**
	 * Method determines classes which will be used for DDL generation.
	 * 
	 * @param annotatedClasses - entities annotated with Hibernate annotations.
	 */
	public SchemaTranslator addAnnotatedClasses(Class<?>[] annotatedClasses) {
		for (Class<?> clazz : annotatedClasses)
			config.addAnnotatedClass(clazz);
		return this;
	}

	/**
	 * Method performs translation of entities in table schemas.
	 * It generates 'CREATE' and 'DELETE' scripts for the Hibernate entities.
	 * Current implementation involves usage of {@link #write(FileOutputStream, String[], Formatter)} method.
	 * 
	 * @param outputStream - stream will be used for *.sql file creation.
	 * @throws IOException
	 */
	public SchemaTranslator translate(FileOutputStream outputStream) throws IOException {
		Dialect requiredDialect = Dialect.getDialect(config.getProperties());
		String[] query = null;

		query = config.generateDropSchemaScript(requiredDialect);
		write(outputStream, query, FormatStyle.DDL.getFormatter());

		query = config.generateSchemaCreationScript(requiredDialect);
		write(outputStream, query, FormatStyle.DDL.getFormatter());

		return this;
	}

	/**
	 * Method writes line by line DDL scripts in the output stream.
	 * Also each line logs in the console.
	 * 
	 * @throws IOException
	 */
	private void write(FileOutputStream outputStream, String[] lines, Formatter formatter) throws IOException {
		String tempStr = null;

		for (String line : lines) {
			tempStr = formatter.format(line) + ";";
			System.out.println(tempStr);
			outputStream.write(tempStr.getBytes());
		}
	}

	public static void main(String[] args) throws IOException {
		SchemaTranslator translator = new SchemaTranslator();
		Class<?>[] entityClasses = { MealClass.class, Participant.class, Team.class, VisitationPlan.class, RunningDinner.class,
				TeamMailReport.class, DinnerRouteMailReport.class, RunningDinnerPreference.class };

		 String dialect = "org.hibernate.dialect.MySQLDialect";
//		String dialect = "org.hibernate.dialect.DerbyDialect";
		translator.setDialect(dialect).addAnnotatedClasses(entityClasses).translate(new FileOutputStream(new File("db-schema.sql")));

	}

}