#!/bin/bash
mkdir /var/log/sports-predictions
chown sports-predictions:sports-predictions /var/log/sports-predictions
mkdir /var/lib/sports-predictions
chown sports-predictions:sports-predictions /var/lib/sports-predictions
mkdir /var/run/sports-predictions
chown sports-predictions:sports-predictions /var/run/sports-predictions
update-rc.d sports-predictions defaults