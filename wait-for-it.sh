#!/usr/bin/env bash
# wait-for-it.sh

host="$1"
shift
cmd="$@"

until curl -sSf "http://$host/ready"; do
  >&2 echo "Loki is unavailable - waiting..."
  sleep 5
done

>&2 echo "Loki is up - executing command"
exec $cmd
