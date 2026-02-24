set -e

mkdir -p bin
cp -r src/main/resources/config.txt bin/
javac -cp "lib/gson-2.13.1.jar" -d bin src/main/java/*/*.java