#!/bin/bash

# Test script voor de Blog REST API
# Start eerst de applicatie met: mvn spring-boot:run

BASE_URL="http://localhost:8080/posts"

echo "==================================="
echo "Blog REST API Test Script"
echo "==================================="
echo ""

# Test 1: GET alle posts
echo "1. GET alle posts"
echo "   URL: GET $BASE_URL"
curl -s -X GET $BASE_URL | jq .
echo ""
echo ""

# Test 2: GET specifieke post
echo "2. GET specifieke post (id=1)"
echo "   URL: GET $BASE_URL/1"
curl -s -X GET $BASE_URL/1 | jq .
echo ""
echo ""

# Test 3: POST nieuwe post
echo "3. POST nieuwe post"
echo "   URL: POST $BASE_URL"
RESPONSE=$(curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "titel": "Mijn tweede blogpost",
    "content": "Dit is de inhoud van mijn tweede blogpost. Hier kan ik alles schrijven wat ik wil!"
  }' \
  -w "\nLocation: %{header{Location}}" \
  -i)
echo "$RESPONSE"
echo ""
echo ""

# Test 4: GET alle posts (nu met 2 posts)
echo "4. GET alle posts (nu met 2 posts)"
echo "   URL: GET $BASE_URL"
curl -s -X GET $BASE_URL | jq .
echo ""
echo ""

# Test 5: PUT update post
echo "5. PUT update post (id=1)"
echo "   URL: PUT $BASE_URL/1"
curl -s -X PUT $BASE_URL/1 \
  -H "Content-Type: application/json" \
  -d '{
    "titel": "Hello World (Geüpdatet)",
    "content": "Dit is de geüpdatete inhoud van de eerste post!"
  }' | jq .
echo ""
echo ""

# Test 6: GET geüpdatete post
echo "6. GET geüpdatete post (id=1)"
echo "   URL: GET $BASE_URL/1"
curl -s -X GET $BASE_URL/1 | jq .
echo ""
echo ""

# Test 7: DELETE post
echo "7. DELETE post (id=2)"
echo "   URL: DELETE $BASE_URL/2"
curl -s -X DELETE $BASE_URL/2 -w "Status: %{http_code}\n"
echo ""
echo ""

# Test 8: GET alle posts (nu weer met 1 post)
echo "8. GET alle posts (nu weer met 1 post)"
echo "   URL: GET $BASE_URL"
curl -s -X GET $BASE_URL | jq .
echo ""
echo ""

# Test 9: Error handling - niet bestaande post
echo "9. Error handling - GET niet bestaande post (id=999)"
echo "   URL: GET $BASE_URL/999"
curl -s -X GET $BASE_URL/999 -w "\nStatus: %{http_code}\n"
echo ""
echo ""

# Test 10: Error handling - id mismatch
echo "10. Error handling - PUT met id mismatch"
echo "    URL: PUT $BASE_URL/1"
curl -s -X PUT $BASE_URL/1 \
  -H "Content-Type: application/json" \
  -d '{
    "id": 5,
    "titel": "Test",
    "content": "Test"
  }' -w "\nStatus: %{http_code}\n"
echo ""

echo "==================================="
echo "Test voltooide!"
echo "==================================="
