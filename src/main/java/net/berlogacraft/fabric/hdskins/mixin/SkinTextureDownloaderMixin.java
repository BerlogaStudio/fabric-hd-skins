package net.berlogacraft.fabric.hdskins.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SkinTextureDownloader;
import net.minecraft.util.ARGB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SkinTextureDownloader.class)
public class SkinTextureDownloaderMixin {

    private static final Logger LOGGER = LoggerFactory.getLogger("fabric-hd-skins");

    @Inject(method = "processLegacySkin", at = @At("HEAD"), cancellable = true)
    private static void onProcessLegacySkin(NativeImage image, String url,
                                             CallbackInfoReturnable<NativeImage> cir) {
        int width = image.getWidth();
        int height = image.getHeight();

        // Standard 64x32 or 64x64 — let vanilla handle it
        if (width == 64 && (height == 32 || height == 64)) return;

        // Must be power of 2
        if (!isPow2(width) || !isPow2(height) || width < 64) {
            image.close();
            LOGGER.warn("Invalid skin dimensions {}x{} from {}", width, height, url);
            cir.setReturnValue(null);
            return;
        }

        // Must be square (modern) or 2:1 (legacy)
        if (width != height && width != height * 2) {
            image.close();
            LOGGER.warn("Invalid skin ratio {}x{} from {}", width, height, url);
            cir.setReturnValue(null);
            return;
        }

        cir.setReturnValue(processHDSkin(image));
    }

    private static NativeImage processHDSkin(NativeImage image) {
        int width = image.getWidth();
        int s = width / 64;
        boolean isLegacy = image.getHeight() * 2 == width;

        if (isLegacy) {
            NativeImage modern = new NativeImage(width, width, true);
            modern.copyFrom(image);
            image.close();
            image = modern;

            image.fillRect(0, 32 * s, 64 * s, 32 * s, 0);

            image.copyRect(4 * s,  16 * s,  16 * s,  32 * s, 4 * s,  4 * s,  true, false);
            image.copyRect(8 * s,  16 * s,  16 * s,  32 * s, 4 * s,  4 * s,  true, false);
            image.copyRect(0,      20 * s,  24 * s,  32 * s, 4 * s,  12 * s, true, false);
            image.copyRect(4 * s,  20 * s,  16 * s,  32 * s, 4 * s,  12 * s, true, false);
            image.copyRect(8 * s,  20 * s,  8 * s,   32 * s, 4 * s,  12 * s, true, false);
            image.copyRect(12 * s, 20 * s,  16 * s,  32 * s, 4 * s,  12 * s, true, false);
            image.copyRect(44 * s, 16 * s, -8 * s,   32 * s, 4 * s,  4 * s,  true, false);
            image.copyRect(48 * s, 16 * s, -8 * s,   32 * s, 4 * s,  4 * s,  true, false);
            image.copyRect(40 * s, 20 * s,  0,       32 * s, 4 * s,  12 * s, true, false);
            image.copyRect(44 * s, 20 * s, -8 * s,   32 * s, 4 * s,  12 * s, true, false);
            image.copyRect(48 * s, 20 * s, -16 * s,  32 * s, 4 * s,  12 * s, true, false);
            image.copyRect(52 * s, 20 * s, -8 * s,   32 * s, 4 * s,  12 * s, true, false);
        }

        setNoAlpha(image, 0, 0, 32 * s, 16 * s);
        if (isLegacy) {
            doNotchTransparencyHack(image, 32 * s, 0, 64 * s, 32 * s);
        }
        setNoAlpha(image, 0, 16 * s, 64 * s, 32 * s);
        setNoAlpha(image, 16 * s, 48 * s, 48 * s, 64 * s);

        return image;
    }

    private static boolean isPow2(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

    private static void setNoAlpha(NativeImage image, int x1, int y1, int x2, int y2) {
        for (int x = x1; x < x2; x++) {
            for (int y = y1; y < y2; y++) {
                image.setPixel(x, y, ARGB.opaque(image.getPixel(x, y)));
            }
        }
    }

    private static void doNotchTransparencyHack(NativeImage image, int x1, int y1, int x2, int y2) {
        for (int x = x1; x < x2; x++) {
            for (int y = y1; y < y2; y++) {
                if (ARGB.alpha(image.getPixel(x, y)) < 128) return;
            }
        }
        for (int x = x1; x < x2; x++) {
            for (int y = y1; y < y2; y++) {
                image.setPixel(x, y, image.getPixel(x, y) | 0x00FFFFFF);
            }
        }
    }
}
