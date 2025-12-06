# Minecraft Decompiled

Decompiled source from Mojang's unobfuscated JARs. No Loom, no Forge toolchains, just Vineflower and Gradle.

## Requirements

- JDK 21+
- Gradle (wrapper included)

## Setup

```bash
./gradlew setup
```

Decompiles both client and server, applies patches, done.

## Build

```bash
./gradlew :server:compileJava
./gradlew :client:compileJava
```

## Run

```bash
./gradlew runServer
./gradlew runClient
```

For client, grab assets first:
```bash
./gradlew downloadAssets
```

## Structure

```
├── jars/           # vanilla jars
├── libs/           # decompiler linking libs
├── patches/        # decompiler fixes
├── mods/           # your modifications
├── server/src/     # server source
└── client/src/     # client source
```

## Modding

Two-layer patch system:
1. `patches/` - fixes for decompiler output (don't touch)
2. `mods/` - your changes

Edit source, test, then generate patches:
```bash
./gradlew genServerMods
./gradlew genClientMods
```

## Decompiler

Uses Vineflower with standard flags. Patches fix type inference issues, lambda captures, and other decompiler artifacts.

## Legal

Mojang owns this code. See LICENSE.
