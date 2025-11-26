# Minecraft 1.21.11 Pre-Release 2 Evaluation Copy Decompilation Kit

```
================================================================================
              CONFIDENTIAL - EVALUATION BUILD - DO NOT DISTRIBUTE
================================================================================
                    Property of Mojang AB, a Microsoft Company
================================================================================
```

## Official Notice

This package is a **Minecraft Evaluation Copy Decompilation Kit** containing
decompiled Java source code derived from Mojang's officially distributed
unobfuscated JAR files. This is NOT original source code but rather a
machine-generated reconstruction for review and evaluation purposes.

## What This Is

Mojang provides unobfuscated (human-readable) JAR builds for select purposes
including press evaluation, mod development support, and security research.
This kit processes those official JARs through industry-standard decompilation
tools to produce reviewable Java source code.

### Key Points

- This is **decompiled output**, not original authored source code
- Derived from official Mojang unobfuscated distribution JARs
- Variable names, comments, and structure may differ from internal sources
- Intended for evaluation, education, and mod development reference only

## Build Information

| Property | Value |
|----------|-------|
| Game Version | 1.21.11 Pre-Release 2 |
| Build Type | Evaluation / Review Copy |
| Distribution | Unobfuscated JAR Decompilation |
| Java Version | 21+ |

---

## Requirements

- Java Development Kit (JDK) 21 or later
- Gradle (wrapper included)

## Setup

```bash
./gradlew setup
```

This decompiles both client and server, applies patches, and sets up the source code.

## Build

```bash
./gradlew :server:compileJava
./gradlew :client:compileJava
```

## Run

```bash
./gradlew runServer    # starts the server
./gradlew runClient    # starts the client (needs assets first)
```

For the client, download assets first:
```bash
./gradlew downloadAssets
```

## Project Structure

```
MCP-Reborn/
├── jars/           # vanilla jars (server.jar, client.jar)
├── libs/           # libraries for decompiler linking
├── patches/        # compilation fixes applied after decompilation
├── mods/           # your modifications (server.patch, client.patch)
├── decompSrc/      # raw decompiled source (generated)
├── patchSrc/       # base snapshots for mod diffing (generated)
├── server/         # server subproject
│   └── src/main/java/
└── client/         # client subproject
    └── src/main/java/
```

## Modding Workflow

The project uses a two-layer patch system:

1. **Vanilla patches** (`patches/`) - Compilation fixes for the decompiled code. Don't modify these.
2. **Mod patches** (`mods/`) - Your custom modifications on top of the working source.

### Creating Mods

1. Run `./gradlew setup` to get fresh source with vanilla patches applied
2. Edit files in `server/src/main/java/` or `client/src/main/java/`
3. Test with `./gradlew runServer` or `./gradlew runClient`
4. Generate your mod patches:
   ```bash
   ./gradlew genServerMods   # generates mods/server.patch
   ./gradlew genClientMods   # generates mods/client.patch
   ./gradlew genMods         # generates both
   ```

### How It Works

```
decompile -> apply vanilla patches -> snapshot -> apply mod patches
                                        ^              |
                                   (base for diff)  (your code)
```

When you run `setup`:
1. Decompiles vanilla JARs to `decompSrc/`
2. Applies `patches/*.patch` (compilation fixes)
3. Creates snapshots in `patchSrc/` (zip of patched source)
4. Applies `mods/*.patch` (your modifications)

When you run `genMods`:
- Diffs current source against the snapshot to generate your mod patches

This keeps vanilla fixes separate from your modifications.

## Maintaining Vanilla Patches

If you need to fix decompilation issues (not mods):

1. Edit the source files
2. Run `./gradlew genServerPatch` or `./gradlew genClientPatch`

## Decompilation Notes

Uses [Vineflower](https://github.com/Vineflower/vineflower) decompiler with these flags:
- `-din=1` - decompile inner classes
- `-rbr=1` - remove bridge methods
- `-dgs=1` - decompile generic signatures
- `-asc=1` - ascii strings
- `-rsy=1` - remove synthetic methods

Due to the nature of decompilation:
- Some generic type information may be lost or inferred incorrectly
- Lambda expressions may appear differently than originally authored
- Local variable names are reconstructed and may not match original names
- Code structure is functionally equivalent but may differ stylistically

Patches in `patches/` correct compilation errors introduced by the decompilation
process while preserving functional equivalence.

---

## Legal

See [LICENSE](LICENSE) for complete terms.

**This software is proprietary to Mojang AB. Unauthorized distribution,
modification for commercial use, or creation of derivative works for
distribution is strictly prohibited.**

```
================================================================================
        Mojang AB | Stockholm, Sweden | https://www.minecraft.net
================================================================================
```
