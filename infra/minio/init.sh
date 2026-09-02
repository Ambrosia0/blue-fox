#!/bin/sh
MINIO_ALIAS="s3-storage"
MINIO_ENDPOINT="${MINIO_ENDPOINT:-http://minio:9000}"
MINIO_ROOT_USER="${MINIO_ROOT_USER:-minioadmin}"
MINIO_ROOT_PASSWORD="${MINIO_ROOT_PASSWORD:-minioadmin}"
until mc alias set "$MINIO_ALIAS" "$MINIO_ENDPOINT" \
    "$MINIO_ROOT_USER" "$MINIO_ROOT_PASSWORD" >/dev/null 2>&1; do
    echo "Waiting minio"
    sleep 2
done
echo "Minio init"
mc mb --ignore-existing "$MINIO_ALIAS/$BUCKET"