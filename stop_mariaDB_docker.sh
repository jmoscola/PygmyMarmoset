#!/usr/bin/env bash

set -e

########################################
# Stop MariaDB container
########################################
if ! command -v docker >/dev/null 2>&1; then
  echo "Docker is not installed. Nothing to stop."
else
  if docker ps --format '{{.Names}}' | grep -q '^pygmy-mariadb$'; then
    echo "Stopping MariaDB container..."
    docker stop pygmy-mariadb
    echo "Container stopped."
  else
    echo "MariaDB container is not running. Nothing to stop."
  fi
fi

########################################
# Stop Colima
########################################
if ! command -v colima >/dev/null 2>&1; then
  echo "Colima is not installed. Nothing to stop."
else
  if colima status >/dev/null 2>&1; then
    echo "Stopping Colima..."
    colima stop
    echo "Colima stopped."
  else
    echo "Colima is not running. Nothing to stop."
  fi
fi

echo "Done."