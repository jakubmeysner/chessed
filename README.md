# Chessed

A Minecraft Spigot multiplayer server plugin, allowing users to play Chess.

## Usage

### Installation

A Spigot or Spigot-compatible server must be set up. The compiled `.jar` file
must be placed in the `plugins` directory.

### Configuration

Arenas must be added using the in-game command `/arena add`.

### Gameplay

A player can challenge another player to a game using `/play`.

## Building

JDK 17 is required. To build the plugin, run `gradlew shadowJar`. The compiled
.jar file will be placed in `build/libs`.
