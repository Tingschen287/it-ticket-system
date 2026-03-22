#!/bin/bash

# IT Ticket Management System - Startup Script
# This script starts both frontend and backend development servers

set -e

PROJECT_ROOT="$(cd "$(dirname "$0")" && pwd)"
FRONTEND_DIR="$PROJECT_ROOT/frontend"
BACKEND_DIR="$PROJECT_ROOT/backend"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}=== IT Ticket Management System ===${NC}"
echo ""

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check Java
echo -e "${YELLOW}Checking Java...${NC}"
if command_exists java; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -ge 17 ]; then
        echo -e "${GREEN}✓ Java $(java -version 2>&1 | head -n 1)${NC}"
    else
        echo -e "${RED}✗ Java 17 or higher is required. Current version: $JAVA_VERSION${NC}"
        exit 1
    fi
else
    echo -e "${RED}✗ Java is not installed. Please install OpenJDK 17+${NC}"
    exit 1
fi

# Check Maven
echo -e "${YELLOW}Checking Maven...${NC}"
if command_exists mvn; then
    echo -e "${GREEN}✓ Maven $(mvn -version 2>&1 | head -n 1 | cut -d' ' -f3)${NC}"
else
    echo -e "${RED}✗ Maven is not installed. Please install Maven${NC}"
    exit 1
fi

# Check Node.js
echo -e "${YELLOW}Checking Node.js...${NC}"
if command_exists node; then
    echo -e "${GREEN}✓ Node.js $(node -v)${NC}"
else
    echo -e "${RED}✗ Node.js is not installed. Please install Node.js${NC}"
    exit 1
fi

# Check PostgreSQL
echo -e "${YELLOW}Checking PostgreSQL...${NC}"
if command_exists psql; then
    echo -e "${GREEN}✓ PostgreSQL $(psql --version)${NC}"
else
    echo -e "${YELLOW}⚠ PostgreSQL client not found. Make sure PostgreSQL is running.${NC}"
fi

echo ""
echo -e "${GREEN}All prerequisites checked!${NC}"
echo ""

# Start backend
start_backend() {
    echo -e "${YELLOW}Starting backend server...${NC}"
    if [ -f "$BACKEND_DIR/pom.xml" ]; then
        cd "$BACKEND_DIR"
        mvn spring-boot:run &
        BACKEND_PID=$!
        echo -e "${GREEN}Backend started (PID: $BACKEND_PID)${NC}"
        cd "$PROJECT_ROOT"
    else
        echo -e "${RED}Backend not initialized. Run setup first.${NC}"
    fi
}

# Start frontend
start_frontend() {
    echo -e "${YELLOW}Starting frontend server...${NC}"
    if [ -f "$FRONTEND_DIR/package.json" ]; then
        cd "$FRONTEND_DIR"
        if [ ! -d "node_modules" ]; then
            echo -e "${YELLOW}Installing frontend dependencies...${NC}"
            npm install
        fi
        npm run dev &
        FRONTEND_PID=$!
        echo -e "${GREEN}Frontend started (PID: $FRONTEND_PID)${NC}"
        cd "$PROJECT_ROOT"
    else
        echo -e "${RED}Frontend not initialized. Run setup first.${NC}"
    fi
}

# Handle command line arguments
case "${1:-all}" in
    backend)
        start_backend
        wait $BACKEND_PID
        ;;
    frontend)
        start_frontend
        wait $FRONTEND_PID
        ;;
    all)
        start_backend
        start_frontend
        echo ""
        echo -e "${GREEN}=== Servers Running ===${NC}"
        echo -e "  Backend:  ${GREEN}http://localhost:8080${NC}"
        echo -e "  Frontend: ${GREEN}http://localhost:5173${NC}"
        echo ""
        echo -e "${YELLOW}Press Ctrl+C to stop all servers${NC}"
        wait
        ;;
    *)
        echo "Usage: $0 {backend|frontend|all}"
        exit 1
        ;;
esac
