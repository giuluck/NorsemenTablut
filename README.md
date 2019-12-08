# Tablut Competition

Project of Fundamentals of Artificial Intelligence course at the University of Bologna.

## Approach

We decided to use **Minimax algorithm** with **alpha-beta pruning** to explore the search space of reachable game states.  
Not being possible to perform the search exhaustively we used a weighted combination of **four heuristic functions** to determine which states are more advantageous for each player.  
Since white and black player have **distinct objectives** in order to win the game, we opted for giving **different weights** to each heuristic based on the role.

## Prerequisites

The project is entirely written using **Java** and **Kotlin** so, in order to run it, you need at least to have `JDK 8` installed in your system but I advise you to upgrade it to `JDK 11` if possible.  
Just as an example, I provide the instructions to install it for Ubuntu/Debian.

```[bash]
sudo apt-get install default-jdk
```

## Development

The project has been developed using **IntelliJ IDEA** and it's highly recommended to use it since most of the code is written in Kotlin, which is natively supported in the IDE.  
You can rely on **Gradle** to build the project. It will take care of testing, compiling and creating the artifact of the Tablut AI player.

## Usage

The game is executed using a client-server model.  
First you need to execute the server, which is already available inside the `Executables` folder.

```[bash]
cd Executables
java -jar Server.jar
```

Then two clients must be launched: first the one representing the white player, after the one representing the black player.  
There are different types of player available, specifically random, human and artificially intelligent.  
In order to run this last kind of client, which is located inside `build/libs` folder after executing

```[bash]
./gradlew fatJar
```

you must run the following command specifying the role (black/white), the time limit for each move and the IP address at which the server previously launched is running.

```[bash]
java -jar TablutAIClient.jar [role] [time] [server]
```
