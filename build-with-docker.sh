#!/bin/bash

# Build script using Docker (no local Java 17 installation required)
# This script allows you to build and test the project using Docker

set -e

echo "==============================================="
echo " Event Gallery - Docker Build Script"
echo "==============================================="
echo ""

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "Error: Docker is not installed."
    echo "Please install Docker from: https://www.docker.com/get-started"
    exit 1
fi

echo "✓ Docker is installed"
echo ""

# Build using Maven Docker image
echo "Building project with Maven 3.9 + Java 17..."
echo ""

docker run --rm \
    -v "$PWD":/app \
    -v "$HOME/.m2":/root/.m2 \
    -w /app \
    maven:3.9-eclipse-temurin-17-alpine \
    mvn clean install -DskipTests

echo ""
echo "==============================================="
echo " Build completed successfully!"
echo "==============================================="
echo ""
echo "To run tests:"
echo "  ./build-with-docker.sh test"
echo ""
echo "To run the application:"
echo "  docker-compose up"
echo ""
