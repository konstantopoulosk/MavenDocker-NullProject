create database dockerDB;
use dockerDB;

/*drop table Measurements
  drop table DockerInstance*/
  
create table Measurements(
id int not null auto_increment primary key,
date date not null
);
create table DockerInstance(
id varchar(12) not null primary key,
image varchar(100) not null,
command varchar(100),
timestamp varchar(100)not null,
status varchar(100) not null,
port varchar(100),
name varchar(100) not null
);
