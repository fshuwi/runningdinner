    create table GuestTeamMapping (
        guest_team_id bigint not null,
        parent_team_id bigint not null,
        primary key (guest_team_id, parent_team_id)
    );
    create table HostTeamMapping (
        host_team_id bigint not null,
        parent_team_id bigint not null,
        primary key (host_team_id, parent_team_id)
    );
    create table MailAddressStatusMapping (
        BaseMailReport_id bigint not null,
        mailAddressStatusMapping bit,
        mailAddressStatusMapping_KEY varchar(255),
        primary key (BaseMailReport_id, mailAddressStatusMapping_KEY)
    );
    create table MailReport (
        mailType varchar(31) not null,
        id bigint not null auto_increment,
        createdAt datetime,
        modifiedAt datetime,
        naturalKey varchar(32) not null unique,
        VERSION_NR bigint not null,
        interrupted bit not null,
        sending bit not null,
        sendingStartDate datetime,
        dinner_id bigint,
        primary key (id)
    );
    create table MealClass (
        id bigint not null auto_increment,
        createdAt datetime,
        modifiedAt datetime,
        naturalKey varchar(32) not null unique,
        VERSION_NR bigint not null,
        label varchar(255),
        time datetime,
        dinner_id bigint,
        primary key (id)
    );
    create table NotAssignedParticipant (
        dinner_id bigint not null,
        participant_id bigint not null,
        primary key (dinner_id, participant_id),
        unique (participant_id)
    );
    create table Participant (
        id bigint not null auto_increment,
        createdAt datetime,
        modifiedAt datetime,
        naturalKey varchar(32) not null unique,
        VERSION_NR bigint not null,
        addressName varchar(255),
        cityName varchar(255),
        remarks varchar(255),
        street varchar(255),
        streetNr varchar(255),
        zip integer not null,
        age integer not null,
        email varchar(255),
        gender integer,
        host bit not null,
        mobileNumber varchar(255),
        firstnamePart varchar(255),
        lastname varchar(255),
        numSeats integer not null,
        participantNumber integer not null,
        dinner_id bigint,
        team_id bigint,
        primary key (id)
    );
    create table RunningDinner (
        id bigint not null auto_increment,
        createdAt datetime,
        modifiedAt datetime,
        naturalKey varchar(32) not null unique,
        VERSION_NR bigint not null,
        city varchar(255),
        considerShortestPaths bit not null,
        forceEqualDistributedCapacityTeams bit not null,
        genderAspects integer,
        teamSize integer not null,
        date datetime not null,
        email varchar(255),
        title varchar(255) not null,
        uuid varchar(48) not null unique,
        primary key (id)
    );
    create table Team (
        id bigint not null auto_increment,
        createdAt datetime,
        modifiedAt datetime,
        naturalKey varchar(32) not null unique,
        VERSION_NR bigint not null,
        teamNumber integer not null,
        mealClass_id bigint,
        dinner_id bigint,
        primary key (id)
    );
    
    alter table GuestTeamMapping 
        add index FK988EA2D9F6BC474D (parent_team_id), 
        add constraint FK988EA2D9F6BC474D 
        foreign key (parent_team_id) 
        references Team (id);
    alter table GuestTeamMapping 
        add index FK988EA2D9994A5E7B (guest_team_id), 
        add constraint FK988EA2D9994A5E7B 
        foreign key (guest_team_id) 
        references Team (id);
    alter table HostTeamMapping 
        add index FK10324CC9F6BC474D (parent_team_id), 
        add constraint FK10324CC9F6BC474D 
        foreign key (parent_team_id) 
        references Team (id);
    alter table HostTeamMapping 
        add index FK10324CC9F7C564B (host_team_id), 
        add constraint FK10324CC9F7C564B 
        foreign key (host_team_id) 
        references Team (id);
    alter table MailAddressStatusMapping 
        add index FK425695FA840426 (BaseMailReport_id), 
        add constraint FK425695FA840426 
        foreign key (BaseMailReport_id) 
        references MailReport (id);
    alter table MailReport 
        add index FK2632BDEBF916B1ED (dinner_id), 
        add constraint FK2632BDEBF916B1ED 
        foreign key (dinner_id) 
        references RunningDinner (id);
    alter table MealClass 
        add index FK5BD6A755F916B1ED (dinner_id), 
        add constraint FK5BD6A755F916B1ED 
        foreign key (dinner_id) 
        references RunningDinner (id);
    alter table NotAssignedParticipant 
        add index FK9026B6D2F916B1ED (dinner_id), 
        add constraint FK9026B6D2F916B1ED 
        foreign key (dinner_id) 
        references RunningDinner (id);
    alter table NotAssignedParticipant 
        add index FK9026B6D276CF5692 (participant_id), 
        add constraint FK9026B6D276CF5692 
        foreign key (participant_id) 
        references Participant (id);
    alter table Participant 
        add index FK91279713F916B1ED (dinner_id), 
        add constraint FK91279713F916B1ED 
        foreign key (dinner_id) 
        references RunningDinner (id);
    alter table Participant 
        add index FK91279713E797E1E2 (team_id), 
        add constraint FK91279713E797E1E2 
        foreign key (team_id) 
        references Team (id);
    alter table Team 
        add index FK27B67DF916B1ED (dinner_id), 
        add constraint FK27B67DF916B1ED 
        foreign key (dinner_id) 
        references RunningDinner (id);
    alter table Team 
        add index FK27B67D1CA60132 (mealClass_id), 
        add constraint FK27B67D1CA60132 
        foreign key (mealClass_id) 
        references MealClass (id);