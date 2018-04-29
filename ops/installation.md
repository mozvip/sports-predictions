# Initial installation    

## install the required packages

    sudo apt-get install postgresql

## Create the PostGreSQL databases

    sudo -i -u postgres

Run the psql command

    psql

The psql prompt should display

    psql (9.5.12)
    Type "help" for help.
    
    postgres=#

Create the required user and its database

    CREATE USER sports_predictions;
    ALTER ROLE sports_predictions WITH CREATEDB;
    CREATE DATABASE sports_predictions OWNER sports_predictions;
    ALTER USER sports_predictions WITH ENCRYPTED PASSWORD 'sports_predictions';
    
# Installation of the Server app
    
Create a user to execute the process

    sudo adduser --system --shell /bin/bash --gecos 'User for managing the Sports Prediction app' --group --disabled-password --home /home/sports-predictions sports-predictions
    
Should complete with no errors

    Adding system user `sports-predictions' (UID 113) ...
    Adding new group `sports-predictions' (GID 119) ...
    Adding new user `sports-predictions' (UID 113) with group `sports-predictions' ...
    Creating home directory `/home/sports-predictions' ...
    
Creating the ssl certificate

    certbot --nginx -d wc2018.soccer -d test.wc2018.soccer -d euromaster.wc2018.soccer -d michelin-solutions.wc2018.soccer -d grand-est.wc2018.soccer    
    
Restart the application with 

    sudo systemctl start sports-predictions.service

