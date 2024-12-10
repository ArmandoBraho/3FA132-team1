use gm1;
SET NAMES utf8;

/* FK-/UNIQUE-Check ausschalten */
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;

/* Tabellen löschen */
DROP TABLE IF EXISTS ort;
DROP TABLE IF EXISTS abteilung;
DROP TABLE IF EXISTS gehalt;
DROP TABLE IF EXISTS mitarbeiter;
/* Tabellen - nächste Aufgaben - auch löschen, wenn vorhanden */ 
DROP TABLE IF EXISTS landkreis;
DROP TABLE IF EXISTS bundesland;
DROP TABLE IF EXISTS funktion;

CREATE TABLE ort (
  id   INT         PRIMARY KEY AUTO_INCREMENT,
  plz  VARCHAR(10),
  name VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE abteilung (
  id        INT         PRIMARY KEY AUTO_INCREMENT,
  abteilung VARCHAR(50),
  lid       INT
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE gehalt (
  id     INT          PRIMARY KEY AUTO_INCREMENT,
  gehalt DECIMAL(9,2),
  iban   VARCHAR(25)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE mitarbeiter (
  id      INT         PRIMARY KEY AUTO_INCREMENT,
  name    VARCHAR(50),
  vorname VARCHAR(50),
  gebdat  DATE,
  strasse VARCHAR(50),
  oid     INT,
  aid     INT
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER table gehalt ADD CONSTRAINT fk_gehalt_mitarbeiter 
      Foreign key (id) References mitarbeiter (id);

ALTER table mitarbeiter ADD CONSTRAINT fk_mitarbeiter_ort 
      Foreign key (oid) References ort (id);

ALTER table mitarbeiter ADD CONSTRAINT fk_mitarbeiter_abteilung 
      Foreign key (aid) References abteilung (id);

ALTER table abteilung ADD CONSTRAINT fk_abteilung_mitarbeiter 
      Foreign key (lid) References mitarbeiter (id);

      
insert into ort (plz,name) values ('85609','Aschheim');
insert into ort (plz,name) values ('85653','Aying');
insert into ort (plz,name) values ('85521','Hohenbrunn');
insert into ort (plz,name) values ('85662','Hohenbrunn');
insert into ort (plz,name) values ('82061','Neuried');
insert into ort (plz,name) values ('82131','Neuried');

insert into abteilung (abteilung,lid) values ('Geschäftsleitung',1);
insert into abteilung (abteilung,lid) values ('Fahrdienst',2);
insert into abteilung (abteilung,lid) values ('Verkauf',8);
insert into abteilung (abteilung,lid) values ('Buchhaltung',5);

insert into mitarbeiter (name,vorname,gebdat,strasse,oid,aid) values ('Walker','Jonny','1970-11-04','Blumenstr. 1', 1,1);
insert into mitarbeiter (name,vorname,gebdat,strasse,oid,aid) values ('Schlau','Susi', '1999-10-14','Dorfplatz 3', 2,2);
insert into mitarbeiter (name,vorname,gebdat,strasse,oid,aid) values ('Ratlos','Rudi', '1980-01-09','Landweg 15', 3,3);
insert into mitarbeiter (name,vorname,gebdat,strasse,oid,aid) values ('Keller','Brigitte', '1985-11-24','Zwergelstr. 12', 5,4);
insert into mitarbeiter (name,vorname,gebdat,strasse,oid,aid) values ('Keller','Josef', '1985-10-13','Zwergelstr. 12', 5,3);
insert into mitarbeiter (name,vorname,gebdat,strasse,oid,aid) values ('Huber','Sepp', '2001-04-04','Kaltenbachstr. 23', 2,1);
insert into mitarbeiter (name,vorname,gebdat,strasse,oid,aid) values ('Meier','Siglinde', '1991-05-01','Waldweg 12', 2,2);
insert into mitarbeiter (name,vorname,gebdat,strasse,oid,aid) values ('Mair','Hans', '1995-10-15','Treppenweg 3', 4,3);
insert into mitarbeiter (name,vorname,gebdat,strasse,oid,aid) values ('Maier','Peter', '1994-11-15','Bgm Huber Straße 2', 4,3);

insert into gehalt (id,gehalt,iban) values (1,11200.78,'DE23399010200021456987');
insert into gehalt (id,gehalt,iban) values (2,3200.11,'DE10245200007849635148');
insert into gehalt (id,gehalt,iban) values (3,4312.00,'DE47290500000000874596');
insert into gehalt (id,gehalt,iban) values (4,5423.56,'DE98590500000096857412');
insert into gehalt (id,gehalt,iban) values (5,4900.12,'DE98590500000096857412');
insert into gehalt (id,gehalt,iban) values (6,9234.99,'DE47660500000000811596');
insert into gehalt (id,gehalt,iban) values (7,2900.98,'DE47660500000000877786');
insert into gehalt (id,gehalt,iban) values (8,3200.48,'DE23343010200021567881');
insert into gehalt (id,gehalt,iban) values (9,3223.48,'DE23343010200021567113');
      
/* FK-/UNIQUE-Check einschalten */
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
	
