#!/usr/bin/env bash
set PGPASSWORD=sports_predictions
pg_dump -h 127.0.0.1 -U sports_predictions sports_predictions > sports_predictions.backup
dropdb -h 127.0.0.1 -i --if-exists -U sports_predictions sports_predictions
createdb -h 127.0.0.1 -U sports_predictions -O sports_predictions sports_predictions
./db_migrate.sh
