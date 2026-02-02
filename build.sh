#!/bin/bash

# Event Gallery Build Script
# Automatically uses Java 17 for building

set -e

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}===============================================${NC}"
echo -e "${BLUE} Event Gallery - Build Script${NC}"
echo -e "${BLUE}===============================================${NC}"
echo ""

# Set Java 17
export JAVA_HOME=/Users/araop/Library/Java/JavaVirtualMachines/corretto-17.0.18/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH

echo -e "${GREEN}✓ Using Java 17${NC}"
java -version
echo ""

# Parse command line argument
COMMAND=${1:-compile}

case $COMMAND in
  compile)
    echo -e "${BLUE}Compiling project...${NC}"
    ./mvnw clean compile
    ;;
  test)
    echo -e "${BLUE}Running unit tests...${NC}"
    ./mvnw test -Dtest="*ServiceTest,JwtUtilTest,EventTest"
    ;;
  package)
    echo -e "${BLUE}Building JAR package...${NC}"
    ./mvnw clean package -DskipTests
    ;;
  install)
    echo -e "${BLUE}Installing to local repository...${NC}"
    ./mvnw clean install -DskipTests
    ;;
  run)
    echo -e "${BLUE}Running application...${NC}"
    ./mvnw spring-boot:run
    ;;
  full)
    echo -e "${BLUE}Full build with tests...${NC}"
    ./mvnw clean compile
    ./mvnw test -Dtest="*ServiceTest,JwtUtilTest,EventTest"
    ./mvnw package -DskipTests
    ;;
  *)
    echo -e "${RED}Unknown command: $COMMAND${NC}"
    echo "Usage: ./build.sh [compile|test|package|install|run|full]"
    echo ""
    echo "Commands:"
    echo "  compile - Compile the project"
    echo "  test    - Run unit tests"
    echo "  package - Create JAR file"
    echo "  install - Install to local Maven repository"
    echo "  run     - Run the application"
    echo "  full    - Complete build with tests and package"
    exit 1
    ;;
esac

echo ""
echo -e "${GREEN}===============================================${NC}"
echo -e "${GREEN} ✓ Build completed successfully!${NC}"
echo -e "${GREEN}===============================================${NC}"
