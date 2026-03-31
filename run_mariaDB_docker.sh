#!/usr/bin/env bash

set -e
PROPS_FILE="pygmymarmoset.properties"

########################################
# Check for Homebrew (required)
########################################
if ! command -v brew >/dev/null 2>&1; then
  echo "Error: Homebrew is required but not installed."
  echo "Install it from https://brew.sh and rerun this script."
  exit 1
fi

########################################
# Ensure Docker is installed
########################################
if ! command -v docker >/dev/null 2>&1; then
  echo "Docker not found. Installing..."
  brew install docker
else
  echo "Docker is already installed."
fi

########################################
# Ensure Colima is installed
########################################
if ! command -v colima >/dev/null 2>&1; then
  echo "Colima not found. Installing..."
  brew install colima
else
  echo "Colima is already installed."
fi

########################################
# Ensure Colima is running
########################################
if ! colima status >/dev/null 2>&1; then
  echo "Colima is not running. Starting..."
  colima start
else
  # Check if it's actually running (status command can succeed but not be running)
  if ! colima status | grep -q "Running"; then
    echo "Colima is installed but not running. Starting..."
    colima start
  else
    echo "Colima is already running."
  fi
fi

########################################
# Verify properties file exists
########################################
if [[ ! -f "$PROPS_FILE" ]]; then
  echo "Error: Properties file '$PROPS_FILE' not found."
  exit 1
fi

########################################
# Extract DB name and credentials from properties file
########################################
DB_NAME=$(grep '^pm.db.name=' "$PROPS_FILE" | cut -d'=' -f2-)
DB_USERNAME=$(grep '^pm.db.user=' "$PROPS_FILE" | cut -d'=' -f2-)
DB_PASSWORD=$(grep '^pm.db.passwd=' "$PROPS_FILE" | cut -d'=' -f2-)

# exit if username and password not available
if [[ -z "$DB_NAME" || -z "$DB_USERNAME" || -z "$DB_PASSWORD" ]]; then
  echo "Error: Could not read database name or credentials from $PROPS_FILE"
  exit 1
fi

########################################
# Run Docker container
########################################
echo "Starting MariaDB container..."

# Check if container already exists
if docker ps -a --format '{{.Names}}' | grep -q '^pygmy-mariadb$'; then
  echo "Container 'pygmy-mariadb' already exists."

  # Check if it's already running
  if docker ps --format '{{.Names}}' | grep -q '^pygmy-mariadb$'; then
    echo "Container is already running. Nothing to do."
  else
    echo "Starting existing container..."
    docker start pygmy-mariadb
  fi

  exit 0
fi

docker run --name pygmy-mariadb \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE="$DB_NAME" \
  -e MYSQL_USER="$DB_USERNAME" \
  -e MYSQL_PASSWORD="$DB_PASSWORD" \
  -p 3306:3306 \
  -d mariadb:latest

echo "Done."