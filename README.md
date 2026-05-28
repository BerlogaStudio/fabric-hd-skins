# fabric-hd-skins

Fabric mod that enables HD skin support for Minecraft 26.1.2+.

Vanilla Minecraft only accepts 64×32 and 64×64 skin textures. This mod patches `SkinTextureDownloader` to accept any power-of-2 skin where `width == height` (modern) or `width == height × 2` (legacy), with a minimum size of 64px — enabling 128×128, 256×256, and larger skins.

## Compatibility

| Minecraft | Fabric Loader | Status |
|-----------|--------------|--------|
| 26.1.2    | 0.19.2       | ✓      |

## Building

Requires JDK 21+. The build script compiles directly against the local Minecraft installation (no internet required after MC is installed).

```bash
chmod +x build.sh
./build.sh
# → build/fabric-hd-skins.jar
```

By default it looks for Minecraft at `~/minecraft/BerlogaCraft/updates/Vanilla`. Override with:

```bash
MC_DIR=/path/to/minecraft/vanilla ./build.sh
```

## License

[MIT](LICENSE)
