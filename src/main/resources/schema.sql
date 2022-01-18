drop table if exists mcg_user cascade;
create table mcg_user
(
    id           SERIAL,
    username     VARCHAR(100) primary KEY,
    password     VARCHAR(1000) not null,
    display_name VARCHAR(100) null,
    bio          VARCHAR,
    image        VARCHAR,
    coins        smallint default 20,
    elo          decimal  default 100,
    win_count    integer  default 0,
    lose_count   integer  default 0,
    tie_count    integer  default 0
);

drop table if exists admin_package;
create table admin_package
(
    package_number SERIAL primary key,
    is_bought      boolean
);

drop table if exists card_name;
create table card_name
(
    name         varchar(100) primary key,
    display_name varchar(100)
);

insert into card_name(name, display_name)
values ('dark_bat', 'Dark Bat'),
       ('dark_ent', 'Dark Ent'),
       ('dragon', 'Dragon'),
       ('fire_elf', 'Fire Elf'),
       ('fire_wizard', 'Fire Wizard'),
       ('grey_goblin', 'Grey Goblin'),
       ('knight', 'Knight'),
       ('kraken', 'Kraken'),
       ('orc', 'Orc'),
       ('water_witch', 'Water Witch'),
       ('dark_spell', 'Dark Spell'),
       ('fire_spell', 'Fire Spell'),
       ('slowness_spell', 'Slowness Spell'),
       ('speed_spell', 'Speed Spell'),
       ('water_spell', 'Water Spell');

drop table if exists card cascade;
create table card
(
    id                   varchar PRIMARY KEY,
    name                 varchar(100),
    damage               decimal,
    admin_package_number integer references admin_package (package_number) on delete set null,
    involved_in_trade    boolean default false
);

drop table if exists stack;
create table stack
(
    card_id  varchar references card (id),
    username Varchar(100) references mcg_user (username)
);
ALTER TABLE public.stack
    ADD CONSTRAINT stack_pk PRIMARY KEY (card_id, username);


drop table if exists deck;
create table deck
(
    card_id  varchar references card (id),
    username Varchar(100) references mcg_user (username)
);
ALTER TABLE public.deck
    ADD CONSTRAINT deck_pk PRIMARY KEY (card_id, username);

drop table if exists trade cascade;
create table trade
(
    id                 varchar primary key,
    trade_card_id      varchar references card (id),
    trade_card_name    varchar(100) references card_name (name),
    trade_user         varchar(100) references mcg_user (username),
    desired_card_name  varchar(100) references card_name (name),
    desired_coins      integer,
    trade_finished     boolean default false,
    traded_to_user     varchar references mcg_user (username),
    traded_for_card_id varchar(100) references card (id),
    traded_at          timestamp
);

