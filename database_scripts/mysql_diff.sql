    drop table MailAddressStatusMapping;
    drop table MailReport;

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