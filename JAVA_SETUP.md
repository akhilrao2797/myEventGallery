# Java 17 Setup Guide

This project requires Java 17 or higher. You currently have Java 8 installed.

## Install Java 17

### macOS

#### Using Homebrew (Recommended)
```bash
# Install Java 17
brew install openjdk@17

# Add to PATH
echo 'export PATH="/usr/local/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# Verify installation
java -version
```

#### Using SDKMAN (Alternative)
```bash
# Install SDKMAN
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# Install Java 17
sdk install java 17.0.10-tem

# Set as default
sdk default java 17.0.10-tem

# Verify
java -version
```

### Ubuntu/Debian Linux
```bash
# Update packages
sudo apt update

# Install Java 17
sudo apt install openjdk-17-jdk

# Verify installation
java -version

# Set JAVA_HOME
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc
source ~/.bashrc
```

### Windows

1. Download Java 17 from [Adoptium](https://adoptium.net/)
2. Run the installer
3. Add to PATH:
   - Open System Properties > Environment Variables
   - Add `JAVA_HOME` pointing to Java 17 installation
   - Add `%JAVA_HOME%\bin` to PATH

## Switch Between Java Versions (macOS/Linux)

If you need to keep Java 8 and use Java 17 for this project only:

### Using jEnv (macOS/Linux)
```bash
# Install jenv
brew install jenv  # macOS
# OR
git clone https://github.com/jenv/jenv.git ~/.jenv  # Linux

# Add to shell
echo 'export PATH="$HOME/.jenv/bin:$PATH"' >> ~/.zshrc
echo 'eval "$(jenv init -)"' >> ~/.zshrc
source ~/.zshrc

# Add Java versions
jenv add /usr/local/opt/openjdk@17
jenv add /usr/local/opt/openjdk@8

# Set Java 17 for this project
cd /Users/araop/dev/myEventGallery
jenv local 17
```

### Using JAVA_HOME Environment Variable
Create a script to switch Java versions:

```bash
# Create java_switch.sh
cat > ~/.java_switch.sh << 'EOF'
#!/bin/bash
if [ "$1" = "17" ]; then
    export JAVA_HOME=$(/usr/libexec/java_home -v 17)
elif [ "$1" = "8" ]; then
    export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)
fi
export PATH=$JAVA_HOME/bin:$PATH
java -version
EOF

chmod +x ~/.java_switch.sh

# Use it
source ~/.java_switch.sh 17  # Switch to Java 17
source ~/.java_switch.sh 8   # Switch to Java 8
```

## Project-Specific Java Version (Maven)

The project is configured to use Java 17. Even if you have Java 8 as default, you can use Maven Toolchains:

### Create toolchains.xml
```bash
mkdir -p ~/.m2
cat > ~/.m2/toolchains.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<toolchains>
    <toolchain>
        <type>jdk</type>
        <provides>
            <version>17</version>
        </provides>
        <configuration>
            <jdkHome>/usr/local/opt/openjdk@17</jdkHome>
        </configuration>
    </toolchain>
    <toolchain>
        <type>jdk</type>
        <provides>
            <version>1.8</version>
        </provides>
        <configuration>
            <jdkHome>/usr/local/opt/openjdk@8</jdkHome>
        </configuration>
    </toolchain>
</toolchains>
EOF
```

Adjust the `jdkHome` paths based on your Java installation locations.

## Verify Setup

After installing Java 17:

```bash
# Check Java version
java -version
# Should show: openjdk version "17.x.x"

# Check JAVA_HOME
echo $JAVA_HOME
# Should point to Java 17

# Verify Maven uses correct Java
./mvnw -version
# Should show Java version: 17.x.x

# Clean and rebuild
./mvnw clean install
```

## Troubleshooting

### Maven still uses Java 8
```bash
# Set JAVA_HOME explicitly for Maven
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
./mvnw clean install
```

### Cannot find java executable
```bash
# Ensure Java is in PATH
export PATH=$JAVA_HOME/bin:$PATH
```

### Wrong Java version in IDE
- **IntelliJ IDEA**: File > Project Structure > Project SDK > Select Java 17
- **Eclipse**: Window > Preferences > Java > Installed JREs > Add Java 17
- **VS Code**: Update `java.configuration.runtimes` in settings.json

## Docker Alternative

If you prefer not to install Java 17 locally, use Docker:

```bash
# Build with Docker
docker run --rm -v "$PWD":/app -w /app maven:3.9-eclipse-temurin-17 mvn clean install

# Run with Docker
docker-compose up
```

## Quick Test

Once Java 17 is installed:

```bash
cd /Users/araop/dev/myEventGallery
./mvnw clean compile
```

If successful, you should see:
```
BUILD SUCCESS
```
