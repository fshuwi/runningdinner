create table RunningDinnerPreference (
    id bigint not null auto_increment,
    createdAt datetime,
    modifiedAt datetime,
    naturalKey varchar(32) not null unique,
    VERSION_NR bigint not null,
    preferenceName varchar(255) not null,
    preferenceValue longtext not null,
    dinner_id bigint,
    primary key (id)
);

alter table RunningDinnerPreference 
    add index FK3C41F2ACF916B1ED (dinner_id), 
    add constraint FK3C41F2ACF916B1ED 
    foreign key (dinner_id) 
    references RunningDinner (id);