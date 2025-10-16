#!/bin/bash

# Attendify Backend API Test Script
# Tests core endpoints to verify functionality

echo "======================================"
echo "Attendify API Test Suite"
echo "======================================"
echo ""

# Set the base URL (change for production testing)
BASE_URL="${1:-http://localhost:3000}"
echo "Testing API at: $BASE_URL"
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test counter
PASSED=0
FAILED=0

# Function to test endpoint
test_endpoint() {
    local name="$1"
    local method="$2"
    local endpoint="$3"
    local data="$4"
    local expected_status="$5"
    local token="$6"
    
    echo -n "Testing $name... "
    
    # Build curl command
    if [ -n "$data" ]; then
        if [ -n "$token" ]; then
            response=$(curl -s -w "\n%{http_code}" -X "$method" "$BASE_URL$endpoint" \
                -H "Content-Type: application/json" \
                -H "Authorization: Bearer $token" \
                -d "$data")
        else
            response=$(curl -s -w "\n%{http_code}" -X "$method" "$BASE_URL$endpoint" \
                -H "Content-Type: application/json" \
                -d "$data")
        fi
    else
        if [ -n "$token" ]; then
            response=$(curl -s -w "\n%{http_code}" -X "$method" "$BASE_URL$endpoint" \
                -H "Authorization: Bearer $token")
        else
            response=$(curl -s -w "\n%{http_code}" -X "$method" "$BASE_URL$endpoint")
        fi
    fi
    
    # Extract status code and body
    status_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')
    
    # Check status code
    if [ "$status_code" = "$expected_status" ]; then
        echo -e "${GREEN}✓ PASSED${NC} (Status: $status_code)"
        PASSED=$((PASSED + 1))
        
        # Store response if needed (e.g., for token)
        echo "$body"
        return 0
    else
        echo -e "${RED}✗ FAILED${NC} (Expected: $expected_status, Got: $status_code)"
        echo "Response: $body"
        FAILED=$((FAILED + 1))
        return 1
    fi
}

echo "======================================"
echo "1. Health Check"
echo "======================================"
test_endpoint "Health Check" "GET" "/api/health" "" "200"
echo ""

echo "======================================"
echo "2. Authentication Tests"
echo "======================================"

# Test registration
echo "Testing user registration..."
REGISTER_RESPONSE=$(test_endpoint "Register User" "POST" "/api/auth/register" \
    '{"username":"testuser_'"$RANDOM"'","password":"test123","role":"student","firstName":"Test","lastName":"User","email":"test@example.com"}' \
    "201")
echo ""

# Test login with invalid credentials
echo "Testing login with invalid credentials..."
test_endpoint "Login (Invalid)" "POST" "/api/auth/login" \
    '{"username":"nonexistent","password":"wrong"}' \
    "401"
echo ""

# Try to access protected route without token
echo "Testing protected route without authentication..."
test_endpoint "Get Courses (No Auth)" "GET" "/api/courses" "" "401"
echo ""

echo "======================================"
echo "3. Test with Valid Authentication"
echo "======================================"
echo "${YELLOW}Note: The following tests require a valid user account.${NC}"
echo "${YELLOW}If you have a test account, you can manually test with:${NC}"
echo ""
echo "# Login and get token:"
echo "TOKEN=\$(curl -s -X POST $BASE_URL/api/auth/login \\"
echo "  -H 'Content-Type: application/json' \\"
echo "  -d '{\"username\":\"admin\",\"password\":\"admin123\"}' | \\"
echo "  grep -o '\"token\":\"[^\"]*' | cut -d'\"' -f4)"
echo ""
echo "# Get courses with token:"
echo "curl -X GET $BASE_URL/api/courses \\"
echo "  -H \"Authorization: Bearer \$TOKEN\""
echo ""

echo "======================================"
echo "Test Summary"
echo "======================================"
echo -e "${GREEN}Passed:${NC} $PASSED"
echo -e "${RED}Failed:${NC} $FAILED"
echo -e "Total: $((PASSED + FAILED))"
echo ""

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}All tests passed!${NC}"
    exit 0
else
    echo -e "${RED}Some tests failed. Please check the output above.${NC}"
    exit 1
fi
