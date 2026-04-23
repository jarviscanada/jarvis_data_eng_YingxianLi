# Introduction

This project demonstrates how Databricks can be used to build end-to-end data pipelines for both batch ETL and declarative pipeline processing. The business context is to transform raw data from multiple sources into clean, analysis-ready datasets that support reporting and decision-making through dashboards.

The project contains two implementations under the same overall data engineering project. The first implementation is a traditional ETL pipeline in Databricks for financial transaction fraud analysis. It focuses on ingesting raw customer, card, transaction, merchant category, and fraud label data from different sources, transforming the data through a medallion architecture, and producing curated gold tables for fraud analytics dashboards.

The second implementation is a DLT pipeline in Databricks for stock market trend analysis. It focuses on ingesting daily stock market data from the Alpha Vantage API, processing the data through bronze, silver, and gold layers, and creating dashboard-ready tables that track price and volume trends across multiple stock symbols.

Across both implementations, I used Databricks, PySpark, Azure services, APIs, medallion architecture, dashboards, workflow orchestration, and structured data engineering design. This project shows my ability to build both notebook-based ETL pipelines and DLT/Lakeflow-style declarative pipelines in Databricks.

# Implementation

## 1. ETL in Databricks

### Business Goal

The ETL pipeline was built to support fraud analytics on financial transaction data. The goal was to integrate data from multiple storage systems, clean and standardize the raw records, enrich them with reference data, and create gold tables that answer business questions related to fraud behavior, risky users, suspicious merchants, fraud timing, and fraud loss.

### Dataset

The ETL implementation uses the following files:

- **transactions_data.csv**: detailed transaction records including timestamp, amount, merchant, and card/user relationships
- **cards_data.csv**: card-level account information
- **users_data.csv**: customer demographic and profile information
- **mcc_codes.json**: merchant category code lookup data
- **train_fraud_labels.json**: fraud labels for identifying fraudulent transactions

### Pipeline Development
- ingested CSV and JSON data from different Azure-based sources into Databricks
- loaded transaction and card data through Azure SQL Database connections
- used JDBC to bring structured data into Databricks
- used Azure Storage and external locations for JSON ingestion
- created bronze, silver, and gold notebooks following medallion architecture
- cleaned schemas, cast data types, and standardized raw fields
- enriched the transaction table with merchant category descriptions and fraud labels
- built gold tables for fraud analysis and dashboard reporting
- created a Databricks dashboard using gold tables
- orchestrated the workflow using Databricks Jobs

### Technologies Used

- Azure Databricks
- PySpark
- Azure SQL Database
- Azure Storage Account / ADLS Gen2
- JDBC
- Unity Catalog
- Databricks Jobs
- Databricks Dashboard

### ETL Architecture

The ETL architecture follows a batch-oriented medallion design:

**Ingestion**
- `transactions_data.csv` and `cards_data.csv` were uploaded into Azure SQL Database
- Databricks connected to Azure SQL using JDBC and Lakeflow Connect
- `users_data.csv`, `mcc_codes.json`, and `train_fraud_labels.json` were loaded from Azure Storage / external location

**Bronze Layer**
- stored raw copies of source data with minimal transformation

**Silver Layer**
- cleaned data types
- standardized columns
- resolved formatting issues
- enriched transactions with MCC descriptions and fraud labels

**Gold Layer**
- created analytical tables to answer fraud questions such as:
    - highest fraudulent transaction count by day of week
    - fraud rate trend over time
    - top users by flagged fraud transactions
    - merchant categories with highest fraud rate
    - merchants with unusually high fraud volume
    - fraud loss by day
    - fraud by time of day
    - fraud patterns by transaction amount

**Dashboard**
- used gold tables as the dashboard source
- added filters and visual components for interactive exploration

**Orchestration**
- built a Databricks job with tasks sequenced as:
    - Bronze notebook
    - Silver notebook
    - Gold notebook
    - Dashboard refresh

## 2. DLT in Databricks

### Business Goal

The DLT pipeline was built to support stock trend analytics using daily stock market data. The goal was to automatically ingest stock data for four companies from an external API, organize the pipeline using a medallion architecture, compute business-friendly trend metrics, and make the data available for dashboards and scheduled refresh.

### Dataset

The DLT implementation uses stock market data from the Alpha Vantage API.

The pipeline focuses on four stock symbols(Apple, Google, Microsoft, Meta) and ingests daily market data including:

- trading date
- open price
- high price
- low price
- close price
- volume

### Pipeline Development

In this DLT implementation:

- used the Alpha Vantage API as the raw data source
- landed and processed daily stock data for four ticker symbols
- created a DLT pipeline in Databricks using medallion architecture
- designed bronze tables for raw API data
- created silver tables for cleaned and standardized stock records
- created gold tables for price trend and volume trend analysis
- built a Databricks dashboard on top of gold tables
- orchestrated a daily workflow for ingestion, pipeline refresh, and dashboard refresh

### Technologies Used
- Databricks
- DLT / Lakeflow Declarative Pipelines
- PySpark
- Alpha Vantage API
- Unity Catalog
- Databricks Jobs
- Databricks Dashboard

### DLT Architecture

The DLT implementation follows a declarative pipeline design:

**Ingestion**

Alpha Vantage API provides daily stock market data
the API is rate-limited, so ingestion must be designed carefully

**Bronze Layer**

stores raw API output for each stock symbol
keeps the original daily records for traceability

**Silver Layer**

standardizes schema
reformats fields
cleans dates and numeric columns
prepares stock data for analytics

**Gold Layer**

produces aggregate analytics tables such as:
price change over 7, 30, and 90 days
percentage price change over 7, 30, and 90 days
volume trend over 7, 30, and 90 days

**Dashboard**

built from aggregate gold tables
supports analysis of stock movement and trading activity

**Orchestration**

created a scheduled workflow for:
ingestion
DLT pipeline update
dashboard refresh

# Future Improvement
**Add Data Quality Validation**

- Implement automated checks for null values, duplicate records, schema drift, and invalid business logic before promoting data from bronze to silver and silver to gold.

**Add Monitoring and Alerting**

- Introduce operational monitoring, job failure alerts, and logging dashboards so pipeline issues can be identified quickly.

**Add Real-Time Capabilities**

- For fraud analytics especially, future versions could support streaming ingestion and near-real-time detection workflows.
