#!/bin/bash
set -e
psql -U postgres << EOSQL
CREATE DATABASE bbs;
CREATE DATABASE test;
EOSQL