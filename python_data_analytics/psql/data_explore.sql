-- Show table schema 
\d+ retail;

-- Show first 10 rows
SELECT * FROM retail limit 10;

-- Check # of records
select count(*) from retail;

-- number of clients (e.g. unique client ID)
select count(distinct customer_id) from retail

-- invoice date range
select max(invoice_date), min(invoice_date) from retail

-- number of SKU/merchants (e.g. unique stock code)
select count(distinct stock_code) from retail

-- Calculate average invoice amount excluding invoices with a negative amount (e.g. canceled orders have negative amount)
WITH invoice_amounts AS (
  select
    invoice_no,
    SUM(quantity * unit_price) AS invoice_amount
  from retail
  group by invoice_no
  having SUM(quantity * unit_price) > 0
)
select
    AVG(invoice_amount) AS avg_invoice_amount
from invoice_amounts

-- Calculate total revenue (e.g. sum of unit_price * quantity)
select SUM(quantity * unit_price) ASinvoice_amount from retail

-- Calculate total revenue by YYYYMM
select
    (EXTRACT(YEAR from invoice_date)::INT * 100
   + EXTRACT(MONTH from invoice_date)::INT) AS yyyymm,
    SUM(quantity * unit_price) AS total_revenue
from retail
group by yyyymm
group by yyyymm



