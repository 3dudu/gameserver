#!/bin/bash
set -e
service ssh start
cd /app && ./startup.sh 
tail -f /app/logs/out.log
