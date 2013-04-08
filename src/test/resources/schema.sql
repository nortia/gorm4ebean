
create table e_almacen (
  dtype                     varchar(10) not null,
  codigo_interno            varchar(255) not null,
  abreviada                 varchar(255),
  descripcion               varchar(255),
  seccion_seccion           varchar(255),
  version                   integer not null,
  price                     integer,
  constraint pk_e_almacen primary key (codigo_interno))
;

create table e_seccion (
  seccion                   varchar(255) not null,
  descripcion               varchar(255),
  version                   integer not null,
  constraint pk_e_seccion primary key (seccion))
;

alter table e_almacen add constraint fk_e_almacen_seccion_1 foreign key (seccion_seccion) references e_seccion (seccion) on delete restrict on update restrict;
create index ix_e_almacen_seccion_1 on e_almacen (seccion_seccion);

create table e_cliente (
  nif                   	varchar(10) not null,
  nombre               		varchar(255),
  apellido1               	varchar(255),
  apellido2               	varchar(255),
  version                   integer not null,
  constraint pk_e_cliente primary key (nif))
;

create table e_familia (
  dtype                     varchar(10) not null,
  familia                   varchar(255) not null,
  version                   timestamp not null,
  constraint pk_e_familia primary key (familia))
;
