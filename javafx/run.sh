#!/bin/bash

# Script to run the JavaFX application
# Make sure JavaFX is in your classpath or use --module-path

echo "Compiling JavaFX application..."

# Create output directory
mkdir -p target/classes

# Find all Java files
find src/main/java -name "*.java" > sources.txt

# Compile (you may need to adjust JavaFX path)
javac -d target/classes \
    --module-path /usr/local/lib/javafx-sdk-17.0.2/lib \
    --add-modules javafx.controls,javafx.fxml \
    @sources.txt

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo "Running application..."
    java --module-path /usr/local/lib/javafx-sdk-17.0.2/lib \
         --add-modules javafx.controls,javafx.fxml \
         -cp target/classes com.mazesolver.Main
else
    echo "Compilation failed. Trying alternative method..."
    echo "Please install Maven or download JavaFX SDK"
    echo ""
    echo "To install Maven:"
    echo "  brew install maven"
    echo ""
    echo "Or download JavaFX from: https://openjfx.io/"
fi

rm -f sources.txt

