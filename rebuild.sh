#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
NC='\033[0m' # No Color

echo -e "${GREEN}Step 1: Cleaning and building the Maven project...${NC}"
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
  echo "❌ Maven build failed. Aborting."
  exit 1
fi

echo -e "${GREEN}Step 2: Stopping existing containers...${NC}"
docker compose down

echo -e "${GREEN}Step 3: Rebuilding Docker images...${NC}"
docker compose build

echo -e "${GREEN}Step 4: Starting containers...${NC}"
docker compose up -d

echo -e "${GREEN}✅ Done. The application is now running at http://localhost:8081${NC}"
