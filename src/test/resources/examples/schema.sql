create table author (
  name                      varchar(255) not null,
  age                       bigint,
  constraint pk_author primary key (name))
;

create table book (
  name                      varchar(255) not null,
  title                     varchar(255),
  pages                     bigint,
  author_name               varchar(255),
  constraint pk_book primary key (name))
;

alter table book add constraint fk_book_author_1 foreign key (author_name) references author (name) on delete restrict on update restrict;
create index ix_book_author_1 on book (author_name);


