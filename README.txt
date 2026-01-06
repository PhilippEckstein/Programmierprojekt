Route Planning – Phase I
handed in by Philipp Eckstein and Fabian Wiese

This project implements Phase I of the route planning assignment.
It reads a graph, computes node heights from SRTM (.hgt) files,
and supports one-to-one and one-to-all Dijkstra queries as specified
in the assignment.

------------------------------------------------------------
Requirements
------------------------------------------------------------

- Java (tested with Java 21 (LTS) and OpenJDK 21 (Eclipse Temurin)
- Sufficient RAM (12 GB as specified)

------------------------------------------------------------
Project Structure
------------------------------------------------------------

phase1EcksteinWiese/
├── src/
│   └── main/
│       └── java/
│           ├── Benchmark.java
│           ├── CordToTile.java
│           ├── Dijkstra.java
│           ├── Graph.java
│           └── ...
├── build.sh
└── README.txt

------------------------------------------------------------
Compilation
------------------------------------------------------------

To compile the project, run:

    ./build.sh

All .class files will be generated in the directory src/main/java.

------------------------------------------------------------
Execution
------------------------------------------------------------

The main entry point of the program is the class 'Benchmark'.

After compilation, run the program using:

    java -cp src/main/java Benchmark \
      -graph <path-to-graph-directory> \
      -lon <longitude> \
      -lat <latitude> \
      -que <path-to-query-file> \
      -source <source-node-id>

Example:

    java -cp src/main/java Benchmark \
      -graph "C:\Users\MaxMuster\Documents\germany-bicycle.fmi" \
      -lon 9.098 \
      -lat 48.746 \
      -que "C:\Users\MaxMuster\Documents\germany-bicycle.que" \
      -source 7750003

------------------------------------------------------------
Input Data
------------------------------------------------------------

- The graph directory specified by '-graph' must lead to a valid .fmi file.
  -  This .fmi file must be in a directory also containing a subdirectory named 
     'srtm' which contains all required '.hgt' files
- '-lon' and '-lat' are values specifying the longitude and latitude that
  the closest node is being searched from.
- The .que file directory specified by '-que' must lead to a valid .que file.
- '-source' specifies the index of the node that one-to-all Dijkstra is being
  run from.