create table boundless_offices
(
  id serial not null primary key,
  geom geometry(Point,4326),
  city varchar not null unique
);
