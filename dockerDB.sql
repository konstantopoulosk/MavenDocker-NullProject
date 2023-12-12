create database dockerDB;
use dockerDB;

/*drop table Measurements
  drop table DockerInstance
  drom table DockerImage*/
  
create table Measurements(
id int not null auto_increment primary key,
date date not null
);
create table DockerInstance(
id varchar(64) not null primary key,
name varchar(100),
image varchar(100) not null,
state varchar(10) not null,
command varchar(100) not null,
created varchar(10) not null,
ports varchar(8) not null
);
create table DockerImage(
id varchar(71) not null primary key,
repository varchar(100) not null,
tag varchar(100),
timesUsed varchar(5),
size varchar(10)
)

