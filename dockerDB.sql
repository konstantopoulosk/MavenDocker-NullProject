create database dockerDB;
use dockerDB;

/*drop table Measurements
  drop table DockerInstance*/
  
create table Measurements(
id int not null auto_increment primary key,
date date not null
);
create table DockerInstance(
id varchar(64) not null primary key,
name varchar(100) not null,
image varchar(100),
state varchar(10)not null,
command varchar(100) not null,
created varchar(10),
ports varchar(8) not null
);
