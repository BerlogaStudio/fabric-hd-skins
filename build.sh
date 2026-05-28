#!/bin/bash
set -euo pipefail

# Builds fabric-hd-skins against the local Minecraft installation.
# Requires Minecraft 26.1.2 with Fabric to be installed at $MC_DIR.

MC_DIR="${MC_DIR:-$HOME/minecraft/BerlogaCraft/updates/Vanilla}"
MC_JAR="$MC_DIR/minecraft.jar"
LOADER_JAR="$MC_DIR/libraries/net/fabricmc/fabric-loader/0.19.2/fabric-loader-0.19.2.jar"
MIXIN_JAR="$MC_DIR/libraries/net/fabricmc/sponge-mixin/0.17.2+mixin.0.8.7/sponge-mixin-0.17.2+mixin.0.8.7.jar"
SLF4J_JAR="$MC_DIR/libraries/org/slf4j/slf4j-api/2.0.17/slf4j-api-2.0.17.jar"

SRC="src/main/java/net/berlogacraft/fabric/hdskins/mixin/SkinTextureDownloaderMixin.java"
OUT="build/classes"
JAR="build/fabric-hd-skins.jar"

echo "[build] Compiling..."
mkdir -p "$OUT"
javac --release 21 \
  -cp "$MC_JAR:$LOADER_JAR:$MIXIN_JAR:$SLF4J_JAR" \
  -d "$OUT" "$SRC"

echo "[build] Packaging..."
cp -r src/main/resources/. "$OUT/"
jar cf "$JAR" -C "$OUT" .

echo "[build] Done: $JAR ($(du -sh "$JAR" | cut -f1))"
