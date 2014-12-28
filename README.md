Run Your Dinner
=============

Web layer (persistence, service, UI) for runningdinner scenarios:<br/>
<a href="http://runyourdinner-clemens.rhcloud.com/" target="_blank">Hosted Application on Openshift (currently only German)</a>

## Running locally with standalone database / tomcat

1) Check out the core project on which this project depends.<br/>
Core project can be found on <a href="https://github.com/Clemens85/runningdinner-core" target="_blank">https://github.com/Clemens85/runningdinner-core</a>.
The artefact should be installed in local maven repository:
```
git clone https://github.com/Clemens85/runningdinner-core.git
mvn install
or for skipping tests:
mvn install -DskipTests=true
```

2) Clone this project and import into Eclipse
```
git clone https://github.com/Clemens85/runningdinner.git
```

3) Configurations

Pass following environment variables to Tomcat
```
-Dspring.profiles.active="dev"
-DMYLOGDIR="YOUR DIRECTORY FOR LOG FILES" 
```

4) Edit config_dev.properties
* Put your own folder for temporary files
* If you don't want to use Derby, adapt the hibernate settings
* If you want to test with an SMTP server put the settings into it (compare with config.properties) and change mail-context.xml ('dev' profile must use same mailsender as 'prod' profile) . Otherwise the application will use a mocked in-memory mail-sender simulation

5) Edit server.xml

Add datasource for JNDI-lookup, e.g:
```
<Resource auth="Container" description="RunYourDinner DB" driverClassName="org.apache.derby.jdbc.ClientDriver" 
factory="org.apache.tomcat.dbcp.dbcp.BasicDataSourceFactory" logAbandoned="true" maxActive="5" maxIdle="2" maxWait="3000" name="jdbc/MysqlDS" 
password="rd" removeAbandoned="true" type="javax.sql.DataSource" url="jdbc:derby://localhost:1527/runningdinner;create=true" username="rd" validationQueryTimeout="500"/>
```
Note: I chose the name "jdbc/MysqlDS" for simplicity reasons because of the openshift hosting. It should therefore not be changed.

6) Put Derby-JDBC driver (or the appropriate database driver) into tomcat server lib directory

7) Start Tomcat (standalone derby database server must be started before Tomcat).<br/>
Application can be accessed on http://localhost:8080/runningdinner


## Running embedded (quick start)

TODO

## Author
**Clemens Stich**
+ ClemensStich at web.de