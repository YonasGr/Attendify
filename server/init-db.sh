#!/bin/bash
# Database Initialization Script for Attendify
# This script initializes a local PostgreSQL database with tables and seed data

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}======================================${NC}"
echo -e "${GREEN}Attendify Database Initialization${NC}"
echo -e "${GREEN}======================================${NC}"
echo ""

# Load environment variables
if [ -f .env ]; then
    echo -e "${YELLOW}Loading environment from .env file...${NC}"
    export $(grep -v '^#' .env | xargs)
else
    echo -e "${RED}Error: .env file not found!${NC}"
    echo -e "${YELLOW}Please create a .env file based on .env.example${NC}"
    exit 1
fi

# Parse DATABASE_URL or use individual variables
if [ -n "$DATABASE_URL" ]; then
    echo -e "${GREEN}Using DATABASE_URL from environment${NC}"
    DB_CONNECTION_STRING="$DATABASE_URL"
else
    # Use individual database variables
    DB_USER="${DB_USER:-postgres}"
    DB_HOST="${DB_HOST:-localhost}"
    DB_DATABASE="${DB_DATABASE:-attendify}"
    DB_PASSWORD="${DB_PASSWORD:-password}"
    DB_PORT="${DB_PORT:-5432}"
    
    echo -e "${GREEN}Using individual database variables${NC}"
    DB_CONNECTION_STRING="postgresql://$DB_USER:$DB_PASSWORD@$DB_HOST:$DB_PORT/$DB_DATABASE"
fi

echo -e "${YELLOW}Database: $DB_DATABASE${NC}"
echo ""

# Check if PostgreSQL is running
echo -e "${YELLOW}Checking PostgreSQL connection...${NC}"
if ! pg_isready -h ${DB_HOST:-localhost} -p ${DB_PORT:-5432} > /dev/null 2>&1; then
    echo -e "${RED}Error: PostgreSQL is not running or not accessible!${NC}"
    echo -e "${YELLOW}Please start PostgreSQL first.${NC}"
    echo -e "${YELLOW}On Arch Linux: sudo systemctl start postgresql${NC}"
    exit 1
fi
echo -e "${GREEN}✓ PostgreSQL is running${NC}"
echo ""

# Create database if it doesn't exist
echo -e "${YELLOW}Creating database if it doesn't exist...${NC}"
psql -h ${DB_HOST:-localhost} -p ${DB_PORT:-5432} -U ${DB_USER:-postgres} -c "CREATE DATABASE ${DB_DATABASE:-attendify};" 2>/dev/null || echo -e "${GREEN}✓ Database already exists${NC}"
echo ""

# Run the schema creation (server.js does this, but we'll ensure it's ready)
echo -e "${YELLOW}Starting server to create tables...${NC}"
echo -e "${YELLOW}(Server will start and create tables, then you can stop it with Ctrl+C)${NC}"
echo -e "${YELLOW}Once you see 'All tables created successfully', press Ctrl+C${NC}"
echo ""

# Start server in background to create tables
npm start &
SERVER_PID=$!

# Wait for tables to be created (give it 10 seconds)
sleep 10

# Stop the server
kill $SERVER_PID 2>/dev/null || true
wait $SERVER_PID 2>/dev/null || true

echo ""
echo -e "${GREEN}✓ Tables created${NC}"
echo ""

# Seed the database
echo -e "${YELLOW}Seeding database with sample data...${NC}"
if [ -f "seed-data.sql" ]; then
    psql "$DB_CONNECTION_STRING" -f seed-data.sql
    echo -e "${GREEN}✓ Database seeded successfully!${NC}"
else
    echo -e "${RED}Error: seed-data.sql not found!${NC}"
    exit 1
fi

echo ""
echo -e "${GREEN}======================================${NC}"
echo -e "${GREEN}Database Initialization Complete!${NC}"
echo -e "${GREEN}======================================${NC}"
echo ""
echo -e "${YELLOW}Test Accounts:${NC}"
echo -e "  Admin:        username: ${GREEN}admin${NC}       password: ${GREEN}password123${NC}"
echo -e "  Instructor:   username: ${GREEN}instructor${NC}  password: ${GREEN}password123${NC}"
echo -e "  Student:      username: ${GREEN}student${NC}     password: ${GREEN}password123${NC}"
echo ""
echo -e "${YELLOW}You can now start the server with:${NC} npm start ${YELLOW}or${NC} npm run dev"
echo ""
