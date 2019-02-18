#!/usr/bin/env bash
set +x
curl -s localhost:5432 -m 10 >/dev/null && echo Success. || echo Fail.
set -x
