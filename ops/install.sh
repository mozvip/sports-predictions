#!/usr/bin/env bash
cp sports-predictions.service /etc/systemd/system/
systemctl enable sports-predictions.service
