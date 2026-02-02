# RDBMS AND SQL

## Introduction

This project is a hands-on SQL practice project designed to strengthen core relational database and SQL querying skills using PostgreSQL. The project is based on a realistic club management scenario that includes members, facilities, and booking records.

The primary goal is to practice writing clean SQL Data Definition Language (DDL) statements, querying relational data using joins, aggregation, subqueries, and window functions, and documenting solutions clearly in a professional GitHub repository.

The target users of this project are data analysts, data engineers, and software developers who want to improve their SQL fundamentals and interview readiness.

The database is created and queried using Docker PostgreSQL, with **DBeaver** as the primary database client for schema visualization, query execution, and result validation. SQL scripts are version-controlled using Git.


## SQL Queries

###### Table Setup (DDL)

```sql
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
```

The database schema is designed using three core tables: members, facilities, and bookings. Primary keys uniquely identify each record, while foreign keys enforce relationships between tables and maintain referential integrity.

The design supports one-to-many relationships between members and bookings, as well as facilities and bookings. A self-referencing foreign key is used in the members table to represent member referrals. This schema follows normalization principles to reduce redundancy and ensure data consistency.

###### Question 1: Insert some data into facilities table

```sql
INSERT INTO cd.facilities
VALUES (9, 'Spa', 20, 30, 100000, 800);
```

###### Question 2: Insert calculated data into facilities table

```sql
INSERT INTO cd.facilities
VALUES ((SELECT max(facid) FROM cd.facilities)+1, 'Spa', 20, 30, 100000, 800);
```

###### Question 3 : Update some existing data in facilities table

```sql
UPDATE cd.facilities
SET initialoutlay = 10000
WHERE name = 'Tennis Court 2';
```

###### Question 4: Update a row based on the contents of another row

```sql
UPDATE cd.facilities
SET membercost = 1.1 * (SELECT membercost FROM cd.facilities WHERE name = 'Tennis Court 1'),
    guestcost = 1.1 * (SELECT guestcost FROM cd.facilities WHERE name = 'Tennis Court 1')
WHERE name = 'Tennis Court 2';
```

###### Question 5: Delete all bookings

```sql
DELETE FROM cd.bookings;
```

###### Question 6: Delete a member from the cd.members table

```sql
DELETE FROM cd.members
WHERE memid = 37;
```

###### Question 7: Control which rows are retrieved

```sql
SELECT facid,
       name,
       membercost,
       monthlymaintenance
FROM cd.facilities
WHERE membercost < monthlymaintenance/50 and membercost > 0;
```

###### Question 8: Basic string searches

```sql
SELECT *
FROM cd.facilities
WHERE name LIKE '%Tennis%';
```

###### Question 9: Matching against multiple possible values

```sql
SELECT *
FROM cd.facilities
WHERE facid in (1,5);
```

###### Question 10: Working with dates

```sql
SELECT memid,
       surname,
       firstname,
       joindate
FROM cd.members
WHERE joindate >= '2012-09-01';
```

###### Question 11: Combining results from multiple queries

```sql
SELECT surname
FROM cd.members
UNION
SELECT name
FROM cd.facilities;
```

###### Question 12: Retrieve the start times of members' bookings

```sql
SELECT b.starttime
FROM cd.bookings b
         JOIN cd.members m
              ON b.memid = m.memid
WHERE m.surname = 'Farrell' and m.firstname = 'David';
```

###### Question 13: Work out the start times of bookings for tennis courts

```sql
SELECT b.starttime as start,
       f.name
FROM cd.bookings b
         JOIN cd.facilities f
              ON b.facid = f.facid
WHERE  f.name LIKE 'Tennis Court%' and b.starttime >= '2012-09-21' and b.starttime < '2012-09-22'
ORDER BY b.starttime;
```

###### Question 14: Produce a list of all members, along with their recommender

```sql
SELECT
    m1.firstname as memfname,
    m1.surname as memsname,
    m2.firstname as recfname,
    m2.surname as recname
FROM cd.members m1
         LEFT JOIN cd.members m2
                   ON m1.recommendedby = m2.memid
ORDER BY m1.surname, m1.firstname;
```

###### Question 15: Produce a list of all members who have recommended another member

```sql
SELECT DISTINCT m2.firstname,
                m2.surname
FROM cd.members m1
         JOIN cd.members m2
              ON m1.recommendedby = m2.memid
ORDER BY m2.surname, m2.firstname;
```

###### Question 16: Produce a list of all members, along with their recommender, using no joins

```sql
SELECT distinct m1.firstname || ' ' ||  m1.surname as member,
                (SELECT firstname || ' '||  surname as recommender FROM cd.members m2 WHERE m1.recommendedby = m2.memid)
FROM cd.members m1
ORDER BY member;
```

###### Question 17: Count the number of recommendations each member makes

```sql
SELECT recommendedby,
       count(*) as count
FROM cd.members
GROUP BY recommendedby
HAVING recommendedby is NOT NULL
ORDER BY recommendedby;
```

###### Question 18: List the total slots booked per facility

```sql
SELECT facid,
       sum(slots) as "Total Slots"
FROM cd.bookings
GROUP BY facid
ORDER BY facid;
```

###### Question 19: List the total slots booked per facility in a given month

```sql
SELECT facid,
       sum(slots) AS "Total Slots"
FROM cd.bookings
WHERE starttime >= '2012-09-01' AND starttime < '2012-10-01'
GROUP BY facid
ORDER BY sum(slots);
```

###### Question 20:  List the total slots booked per facility per month

```sql
SELECT facid,
       extract(month from starttime) AS month,
       sum(slots) AS "Total Slots"
FROM cd.bookings
WHERE extract(year from starttime) = 2012
GROUP BY facid, extract(month from starttime)
ORDER BY facid, month;
```

###### Question 21:Find the count of members who have made at least one booking

```sql
SELECT COUNT(DISTINCT memid)
FROM cd.bookings;
```

###### Question 22:  List each member's first booking after September 1st 2012

```sql
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
```

###### Question 23: Produce a list of member names, with each row containing the total member count

```sql
SELECT count(*) OVER () as count,
       firstname,
       surname
FROM cd.members
ORDER BY joindate;
```

###### Question 24: Produce a numbered list of members

```sql
SELECT row_number() OVER (ORDER BY joindate) as row_number,
       firstname,
       surname
FROM cd.members;
```

###### Question 25: Output the facility id that has the highest number of slots booked, again

```sql
SELECT facid,
       total
FROM
    (SELECT facid, sum(slots) as total, rank() OVER (ORDER BY sum(slots) DESC) as ranking
     FROM cd.bookings
     GROUP BY facid) AS rank_table
WHERE ranking = 1;
```

###### Question 26: Format the names of members

```sql
SELECT surname|| ', '||firstname as name
FROM cd.members;
```

###### Question 27: Find telephone numbers with parentheses

```sql
SELECT memid,
       telephone
FROM cd.members
WHERE telephone ~ '[()]';
```

###### Question 28: Count the number of members whose surname starts with each letter of the alphabet

```sql
SELECT substr (surname,1,1) AS letter,
       count(*) AS count
FROM cd.members
GROUP BY substr (surname,1,1)
ORDER BY letter;
```

## Deployment

This project is deployed **locally**.

### Environment
- PostgreSQL database run in Docker
- DBeaver as the SQL client

### Deployment Process
1. Start Docker PostgreSQL Container
2. Connect DBeaver to PostgreSQL
3. Execute `queries.sql` to create tables and run queries
4. Validate outputs using DBeavers result grid

## Improvement

Potential future improvements include:

- Adding indexes on frequently queried columns (e.g. `memid`, `facid`)
- Using `EXPLAIN ANALYZE` to evaluate query performance
- Extending the schema to support payments or membership plans