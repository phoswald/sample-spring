#!/usr/bin/env bash
#
# Configures the local Keycloak instance (started via docker-compose.yml in this folder)
# with a realm and an OIDC client for the sample-spring application, plus a test user.
#
# Usage:
#   docker compose up -d
#   ./setup-keycloak.sh
#
set -euo pipefail

KEYCLOAK_URL="http://localhost:8091"
ADMIN_USER="admin"
ADMIN_PASSWORD="admin"

REALM_NAME="default"
CLIENT_ID="sample-spring"
CLIENT_SECRET="sample-spring-secret"

APP_URL="http://localhost:8080"
REDIRECT_URI="${APP_URL}/login/oauth2/code/keycloak"
POST_LOGOUT_REDIRECT_URI="${APP_URL}/"
WEB_ORIGIN="${APP_URL}"

TEST_USER="test"
TEST_USER_PASSWORD="test"
TEST_USER_EMAIL="test@example.com"

command -v curl >/dev/null || { echo "curl is required but not installed." >&2; exit 1; }
command -v jq >/dev/null || { echo "jq is required but not installed." >&2; exit 1; }

echo "Waiting for Keycloak at ${KEYCLOAK_URL} ..."
until curl -sf "${KEYCLOAK_URL}/realms/master/.well-known/openid-configuration" >/dev/null 2>&1; do
    sleep 2
done
echo "Keycloak is up."

echo "Fetching admin access token ..."
ADMIN_TOKEN=$(curl -sf -X POST "${KEYCLOAK_URL}/realms/master/protocol/openid-connect/token" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "grant_type=password" \
    -d "client_id=admin-cli" \
    -d "username=${ADMIN_USER}" \
    -d "password=${ADMIN_PASSWORD}" | jq -r '.access_token')

if [[ -z "${ADMIN_TOKEN}" || "${ADMIN_TOKEN}" == "null" ]]; then
    echo "Failed to obtain admin access token. Check ADMIN_USER/ADMIN_PASSWORD." >&2
    exit 1
fi

AUTH_HEADER="Authorization: Bearer ${ADMIN_TOKEN}"

echo "Creating realm '${REALM_NAME}' (if missing) ..."
if curl -sf -H "${AUTH_HEADER}" "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}" >/dev/null 2>&1; then
    echo "  realm already exists, skipping."
else
    curl -sf -X POST "${KEYCLOAK_URL}/admin/realms" \
        -H "${AUTH_HEADER}" -H "Content-Type: application/json" \
        -d "{
              \"realm\": \"${REALM_NAME}\",
              \"enabled\": true,
              \"sslRequired\": \"none\",
              \"registrationAllowed\": false
            }"
    echo "  realm created."
fi

echo "Creating client '${CLIENT_ID}' (if missing) ..."
EXISTING_CLIENT_UUID=$(curl -sf -H "${AUTH_HEADER}" \
    "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/clients?clientId=${CLIENT_ID}" | jq -r '.[0].id // empty')

if [[ -n "${EXISTING_CLIENT_UUID}" ]]; then
    echo "  client already exists, skipping."
else
    curl -sf -X POST "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/clients" \
        -H "${AUTH_HEADER}" -H "Content-Type: application/json" \
        -d "{
              \"clientId\": \"${CLIENT_ID}\",
              \"enabled\": true,
              \"protocol\": \"openid-connect\",
              \"publicClient\": false,
              \"clientAuthenticatorType\": \"client-secret\",
              \"secret\": \"${CLIENT_SECRET}\",
              \"standardFlowEnabled\": true,
              \"directAccessGrantsEnabled\": true,
              \"serviceAccountsEnabled\": false,
              \"redirectUris\": [\"${REDIRECT_URI}\"],
              \"webOrigins\": [\"${WEB_ORIGIN}\"],
              \"postLogoutRedirectUris\": [\"${POST_LOGOUT_REDIRECT_URI}\"]
            }"
    echo "  client created."
fi

echo "Creating test user '${TEST_USER}' (if missing) ..."
EXISTING_USER_ID=$(curl -sf -H "${AUTH_HEADER}" \
    "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/users?username=${TEST_USER}&exact=true" | jq -r '.[0].id // empty')

if [[ -n "${EXISTING_USER_ID}" ]]; then
    echo "  user already exists, skipping."
else
    curl -sf -X POST "${KEYCLOAK_URL}/admin/realms/${REALM_NAME}/users" \
        -H "${AUTH_HEADER}" -H "Content-Type: application/json" \
        -d "{
              \"username\": \"${TEST_USER}\",
              \"email\": \"${TEST_USER_EMAIL}\",
              \"enabled\": true,
              \"emailVerified\": true,
              \"credentials\": [{
                  \"type\": \"password\",
                  \"value\": \"${TEST_USER_PASSWORD}\",
                  \"temporary\": false
              }]
            }"
    echo "  user created."
fi

cat <<EOF

Keycloak is configured.

  Realm:         ${REALM_NAME}
  Issuer URI:    ${KEYCLOAK_URL}/realms/${REALM_NAME}
  Client ID:     ${CLIENT_ID}
  Client secret: ${CLIENT_SECRET}
  Test user:     ${TEST_USER} / ${TEST_USER_PASSWORD}

Admin console:   ${KEYCLOAK_URL}/admin/master/console/#/${REALM_NAME}
EOF
