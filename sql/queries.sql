-- Create the database if not exists
CREATE DATABASE IF NOT EXISTS exercises;

-- Switch to database 'exercises'
\c exercises

-- Create schema if not exists
CREATE SCHEMA IF NOT EXISTS cd;

-- Create table cd.members
CREATE TABLE cd.members (
                            memid           INTEGER PRIMARY KEY,
                            surname         VARCHAR(200) NOT NULL,
                            firstname       VARCHAR(200) NOT NULL,
                            address         VARCHAR(300) NOT NULL,
                            zipcode         INTEGER NOT NULL,
                            telephone       VARCHAR(20) NOT NULL,
                            recommendedby   INTEGER,
                            joindate        TIMESTAMP NOT NULL,
                            CONSTRAINT fk_members_recommendedby
                                FOREIGN KEY (recommendedby)
                                    REFERENCES cd.members(memid)
                                    ON DELETE SET NULL
);

-- Create table cd.facilities
CREATE TABLE cd.facilities (
                               facid               INTEGER PRIMARY KEY,
                               name                VARCHAR(100) NOT NULL,
                               membercost          NUMERIC NOT NULL,
                               guestcost           NUMERIC NOT NULL,
                               initialoutlay       NUMERIC NOT NULL,
                               monthlymaintenance  NUMERIC NOT NULL
);

-- Create table cd.bookings
CREATE TABLE cd.bookings (
                             bookid      INTEGER PRIMARY KEY,
                             facid       INTEGER NOT NULL,
                             memid       INTEGER NOT NULL,
                             starttime   TIMESTAMP NOT NULL,
                             slots       INTEGER NOT NULL,
                             CONSTRAINT fk_bookings_facid
                                 FOREIGN KEY (facid)
                                     REFERENCES cd.facilities(facid),
                             CONSTRAINT fk_bookings_memid
                                 FOREIGN KEY (memid)
                                     REFERENCES cd.members(memid)
);




----------------------
-- Practice SQL Queries
-----------------------

-- Modifying Data

-- Q1 Solution
INSERT INTO cd.facilities
VALUES (9, 'Spa', 20, 30, 100000, 800);

-- Q2 Solution
INSERT INTO cd.facilities
VALUES ((SELECT max(facid) FROM cd.facilities)+1, 'Spa', 20, 30, 100000, 800);

-- Q3 Solution
UPDATE cd.facilities
SET initialoutlay = 10000
WHERE name = 'Tennis Court 2';

-- Q4 Solution
UPDATE cd.facilities
SET membercost = 1.1 * (SELECT membercost FROM cd.facilities WHERE name = 'Tennis Court 1'),
    guestcost = 1.1 * (SELECT guestcost FROM cd.facilities WHERE name = 'Tennis Court 1')
WHERE name = 'Tennis Court 2';

-- Q5 Solution
DELETE FROM cd.bookings;

-- Q6 Solution
DELETE FROM cd.members
WHERE memid = 37;



-- Basics

-- Q1 Solution
SELECT facid,
       name,
       membercost,
       monthlymaintenance
FROM cd.facilities
WHERE membercost < monthlymaintenance/50 and membercost > 0;

-- Q2 Solution
SELECT *
FROM cd.facilities
WHERE name LIKE '%Tennis%';

-- Q3 Solution
SELECT *
FROM cd.facilities
WHERE facid in (1,5);

-- Q4 Solution
SELECT memid,
       surname,
       firstname,
       joindate
FROM cd.members
WHERE joindate >= '2012-09-01';

-- Q5 Solution
SELECT surname
FROM cd.members
UNION
SELECT name
FROM cd.facilities;



-- Join

-- Q1 Solution
SELECT b.starttime
FROM cd.bookings b
         JOIN cd.members m
              ON b.memid = m.memid
WHERE m.surname = 'Farrell' and m.firstname = 'David';

-- Q2 Solution
SELECT b.starttime as start,
       f.name
FROM cd.bookings b
         JOIN cd.facilities f
              ON b.facid = f.facid
WHERE  f.name LIKE 'Tennis Court%' and b.starttime >= '2012-09-21' and b.starttime < '2012-09-22'
ORDER BY b.starttime;

-- Q3 Solution
SELECT
    m1.firstname as memfname,
    m1.surname as memsname,
    m2.firstname as recfname,
    m2.surname as recname
FROM cd.members m1
         LEFT JOIN cd.members m2
                   ON m1.recommendedby = m2.memid
ORDER BY m1.surname, m1.firstname;

-- Q4 Solution
SELECT DISTINCT m2.firstname,
                m2.surname
FROM cd.members m1
         JOIN cd.members m2
              ON m1.recommendedby = m2.memid
ORDER BY m2.surname, m2.firstname;

-- Q5 Solution
SELECT distinct m1.firstname || ' ' ||  m1.surname as member,
                (SELECT firstname || ' '||  surname as recommender FROM cd.members m2 WHERE m1.recommendedby = m2.memid)
FROM cd.members m1
ORDER BY member;



-- Aggregation

-- Q1 Solution
SELECT recommendedby,
       count(*) as count
FROM cd.members
GROUP BY recommendedby
HAVING recommendedby is NOT NULL
ORDER BY recommendedby;

-- Q2 Solution
SELECT facid,
       sum(slots) as "Total Slots"
FROM cd.bookings
GROUP BY facid
ORDER BY facid;

-- Q3 Solution
SELECT facid,
       sum(slots) AS "Total Slots"
FROM cd.bookings
WHERE starttime >= '2012-09-01' AND starttime < '2012-10-01'
GROUP BY facid
ORDER BY sum(slots);

-- Q4 Solution
SELECT facid,
       extract(month from starttime) AS month,
       sum(slots) AS "Total Slots"
FROM cd.bookings
WHERE extract(year from starttime) = 2012
GROUP BY facid, extract(month from starttime)
ORDER BY facid, month;

-- Q5 Solution
SELECT COUNT(DISTINCT memid)
FROM cd.bookings;

-- Q6 Solution
SELECT m.surname,
       m.firstname,
       m.memid,
       min(b.starttime) AS starttime
FROM cd.members m
         LEFT JOIN cd.bookings b
                   ON m.memid = b.memid
WHERE b.starttime >= '2012-09-01'
GROUP BY m.memid
ORDER BY m.memid;

-- Q7 Solution
SELECT count(*) OVER () as count,
       firstname,
       surname
FROM cd.members
ORDER BY joindate;

-- Q8 Solution
SELECT row_number() OVER (ORDER BY joindate) as row_number,
       firstname,
       surname
FROM cd.members;

-- Q9 Solution
SELECT facid,
       total
FROM
    (SELECT facid, sum(slots) as total, rank() OVER (ORDER BY sum(slots) DESC) as ranking
     FROM cd.bookings
     GROUP BY facid) AS rank_table
WHERE ranking = 1;



-- String

-- Q1 Solution
SELECT surname|| ', '||firstname as name
FROM cd.members;

-- Q2 Solution
SELECT memid,
       telephone
FROM cd.members
WHERE telephone ~ '[()]';

-- Q3 Solution
SELECT substr (surname,1,1) AS letter,
       count(*) AS count
FROM cd.members
GROUP BY substr (surname,1,1)
ORDER BY letter;




