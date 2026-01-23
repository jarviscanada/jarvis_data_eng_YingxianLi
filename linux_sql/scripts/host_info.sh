#!/bin/bash

psql_host=$1
psql_port=$2
db_name=$3
psql_user=$4
psql_password=$5

# Check argument count
if [ "$#" -ne 5 ]; then
  echo "Illegal number of parameters"
  exit 1
fi

# Collect the host information
hostname=$(hostname -f)
lscpu_out=$(lscpu)

cpu_number=$(echo "$lscpu_out" | egrep "^CPU\(s\):" | awk '{print $2}' | xargs)
cpu_architecture=$(echo "$lscpu_out" | egrep "^Architecture:" | awk '{print $2}' | xargs)
cpu_model=$(echo "$lscpu_out" | egrep "^Model name:" | awk -F: '{print $2}' | xargs)

cpu_mhz=$(echo "$lscpu_out" | awk -F: '/^CPU MHz:/ {gsub(/^[ \t]+/, "", $2); print $2}' | xargs)
if [ -z "$cpu_mhz" ]; then
  cpu_mhz=$(echo "$lscpu_out" | awk -F: '/^CPU max MHz:/ {gsub(/^[ \t]+/, "", $2); print $2}' | xargs)
fi
cpu_mhz=${cpu_mhz:-0}

l2_cache=$(echo "$lscpu_out" | egrep "^L2 cache:" | awk '{print $3}' | sed 's/K//' | xargs)
total_mem=$(grep -w "MemTotal" /proc/meminfo | awk '{print $2}' | xargs)

# Make sure the current time in UTC format
timestamp=$(date -u "+%Y-%m-%d %H:%M:%S")

# Insert the data in to host_info table
insert_stmt="INSERT INTO host_info(hostname, cpu_number, cpu_architecture, cpu_model, cpu_mhz, l2_cache, \"timestamp\", total_mem)
VALUES ('$hostname', $cpu_number, '$cpu_architecture', '$cpu_model', $cpu_mhz, $l2_cache, '$timestamp', $total_mem)
ON CONFLICT (hostname) DO NOTHING;
"

# Execute the psql command
export PGPASSWORD="$psql_password"
psql -h $psql_host -p $psql_port -d $db_name -U $psql_user -c "$insert_stmt"
exit $?
