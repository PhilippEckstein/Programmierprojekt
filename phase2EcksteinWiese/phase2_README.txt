Route Planning – Phase 2
handed in by Philipp Eckstein and Fabian Wiese

This project implements Phase II of the route planning assignment.
It reads a graph, computes node heights from SRTM (.hgt) files,
and provides a frontend that allows for bicycle route planning using
OpenStreetMap, Dijstra's algorithm and AJAX requests.

------------------------------------------------------------
Requirements
------------------------------------------------------------

- Java (tested with Java 21 (LTS) and OpenJDK 21 (Eclipse Temurin))
- Sufficient RAM (12 GB as specified)

------------------------------------------------------------
Compilation
------------------------------------------------------------

!!! Before compilation, make sure to add the file path to the .fmi graph 
    file to the config.txt file that can be found at 
    src/main/resources.

To compile the project, run:

    ./phase2_build.sh

All .class files will be generated in the bin directory and the
config.txt file will be copied.

------------------------------------------------------------
Execution
------------------------------------------------------------

After compilation, run the program using:

Linux/macOS:    java -cp "lib/gson-2.13.1.jar:bin" frontend.Server
Windows:        java -cp "lib/gson-2.13.1.jar;bin" frontend.Server

Server will be started once the graph file has been read.
Frontend can be accessed at http://localhost:8080.