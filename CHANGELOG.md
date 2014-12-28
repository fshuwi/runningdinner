# Version 0.9.5 (2014-12-28)
 - Enhanced Google Maps Integration (improvded error handling & added database cache for geocoding)
 - Use CDN for JS files in production
 - Fixed datepicker locale bug
 
# Version 0.9.4 (2014-12-09)
 - Added first google maps integration for dinner route view
 - Introduced grunt for minifying and putting all JS and CSS files together
 - Many improvements and bugfixes

# Version 0.9.3 (2014-11-16)
 - Added tooltip for showing all teams that a single team sees on this evening 

# Version 0.9.2 (2014-11-11)
 - Unbalanced (based upon number of seats) teams are now highlighted in team view   

# Version 0.9.1 (2014-11-09)
 - Added possibility to use own mail server for sending mails (mail server credentials are stored encrypted in user's browser)

# Version 0.9.0 (2014-10-17)
 - Core logic: Modified dinner visitation plan alogirthm => Now the computation is not any longer restricted to 9er teams.
 - Core logic: Implemented distribution algorithms (Gender distribution, Number of Seats distribution)
 - Enhanced excel parsing validation
 - Switched to Java 7
 - Some minor Bugfixes

# Version 0.7.4 (2014-03-24)
 - Added Excel export for teams
 - Added Mail Previews
 - Many bugfixes and improvements (UI)

# Version 0.7.3 (2014-02-19)
 - Added helper texts on start wizard page
 - Added predefined mail templates
 - Added mail sending reports
 - Many bugfixes

# Version 0.7.2 (2014-02-17)
 - Added highlighting to currently selected menu entry in admin view
 - Added scheduled jobs for deletion of old entries (Db, filesystem)
 - Added Drag&Drop in team view
 - Bugfix for switching teams

# Version 0.7.1 (2014-02-14)
 - Added jquery datepicker

# Version 0.7.0 (2014-01-29)
### Initial Version for first productive usage
MVP which can compute a dinner and mails can be sent to participants. Contains lots of  hardcoded logic and copy&paste to get version running for first productive usage.
