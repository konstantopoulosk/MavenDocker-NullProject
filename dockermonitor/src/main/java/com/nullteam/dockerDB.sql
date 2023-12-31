create database dockerDB;
use dockerDB;

/*drop table Measurements
  drop table DockerInstance
  drop table DockerImage*/
  
create table measurementsofcontainers(
idmc int not null primary key,
date date not null
);
create table measurementsofimages(
    idmi int not null primary key,
    date datetime not null
);
create table DockerInstance(
id varchar(64) not null primary key,
name varchar(100),
image varchar(100) not null,
state varchar(10) not null,
command varchar(100) not null,
created varchar(10) not null,
ports varchar(8) not null
idmc int foreign key references measurementsofcontainers
);
create table DockerImage(
id varchar(71) not null primary key,
repository varchar(100) not null,
tag varchar(100),
timesUsed varchar(5),
size varchar(10)
idmi int foreign key references measurementsofimages
);

create table Volumes(
name varchar(100) not null primary key,
driver varchar(100) not null,
created varchar(10) not null,
mountpoint varchar(255) not null,
size varchar(10)
);

create table Networks(
networkid varchar(64) not null primary key,
name varchar(128) not null,
driver varchar(100) not null,
scope varchar(100) not null
);

create table DockerSubnet(
CIDRnotation varchar(20) not null primary key, /*etsi legetai to ypotithemeno ID tou subnet*/
networkid varchar(64) foreign key references DockerNetwork
);

create table DockerLog (
    log_id int not null primary key,
    timestamp datetime not null,
    message text not null,
    id varchar(64) foreign key references DockerInstance
);




