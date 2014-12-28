create table DbGeocoderResult (
    id bigint not null auto_increment,
    createdAt datetime,
    modifiedAt datetime,
    naturalKey varchar(32) not null unique,
    VERSION_NR bigint not null,
    cityName varchar(255),
    exact bit not null,
    formattedAddressString varchar(255),
    lastAccess datetime,
    lat double precision not null,
    lng double precision not null,
    normalizedAddressString varchar(255),
    street varchar(255),
    streetNr varchar(255),
    zip integer not null,
    primary key (id)
);