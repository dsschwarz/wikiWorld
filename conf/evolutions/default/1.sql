# Node schema
 
# --- !Ups

CREATE SEQUENCE node_id_seq;
CREATE SEQUENCE link_id_seq;
CREATE TABLE node (
    id integer NOT NULL DEFAULT nextval('node_id_seq'),
    url varchar(255)
);
CREATE TABLE link (
	id integer NOT NULL DEFAULT nextval('link_id_seq'),
	nodeA integer,
	nodeB integer
)
 
# --- !Downs
 
DROP TABLE node;
DROP SEQUENCE node_id_seq;
DROP SEQUENCE link_id_seq;