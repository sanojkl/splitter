create table gruppe
(
    id     serial primary key,
    "name" varchar(300),
    offen  boolean
);

create table person
(
    id           serial primary key,
    git_hub_name varchar(40),
    gruppe       integer,
    constraint person_gruppe_fk foreign key (gruppe) references gruppe (id)
);

create table ausgabe
(
    id           serial primary key,
    beschreibung text,
    betrag       bigint,
    gruppe       integer,
    glaeubiger   integer,
    constraint ausgabe_gruppe_fk foreign key (gruppe) references gruppe (id),
    constraint ausgabe_glaeubiger_fk foreign key (glaeubiger) references person (id)
);

create table schuldner
(
    id      serial primary key,
    person  integer,
    ausgabe integer,
    constraint schuldner_person_fk foreign key (person) references person (id),
    constraint schuldner_ausgabe_fk foreign key (ausgabe) references ausgabe (id)
)