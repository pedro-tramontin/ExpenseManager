# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table category (
  id                        bigint not null,
  name                      varchar(255),
  constraint pk_category primary key (id))
;

create table earn (
  id                        bigint not null,
  datetime                  timestamp,
  bank_description          varchar(255),
  description               varchar(255),
  value                     double,
  category_id               bigint,
  period_id                 bigint,
  constraint pk_earn primary key (id))
;

create table expense (
  id                        bigint not null,
  datetime                  timestamp,
  bank_description          varchar(255),
  description               varchar(255),
  value                     double,
  category_id               bigint,
  period_id                 bigint,
  constraint pk_expense primary key (id))
;

create table period (
  id                        bigint not null,
  value                     varchar(255),
  constraint pk_period primary key (id))
;

create sequence category_seq;

create sequence earn_seq;

create sequence expense_seq;

create sequence period_seq;

alter table earn add constraint fk_earn_category_1 foreign key (category_id) references category (id) on delete restrict on update restrict;
create index ix_earn_category_1 on earn (category_id);
alter table earn add constraint fk_earn_period_2 foreign key (period_id) references period (id) on delete restrict on update restrict;
create index ix_earn_period_2 on earn (period_id);
alter table expense add constraint fk_expense_category_3 foreign key (category_id) references category (id) on delete restrict on update restrict;
create index ix_expense_category_3 on expense (category_id);
alter table expense add constraint fk_expense_period_4 foreign key (period_id) references period (id) on delete restrict on update restrict;
create index ix_expense_period_4 on expense (period_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists category;

drop table if exists earn;

drop table if exists expense;

drop table if exists period;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists category_seq;

drop sequence if exists earn_seq;

drop sequence if exists expense_seq;

drop sequence if exists period_seq;

