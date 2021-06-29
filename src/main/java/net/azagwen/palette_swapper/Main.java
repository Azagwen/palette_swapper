package net.azagwen.palette_swapper;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {
    public static final boolean printDebug = true;

    public static void main(String[] args) {
        var workingDir = System.getProperty("user.dir");
        System.out.println(workingDir);

        var inputImage = (BufferedImage) null;
        var paletteImage = (BufferedImage) null;
        var inputImageFile = new File(workingDir, "palswap_input.png");
        var paletteImageFile = new File(workingDir, "palswap_palette.png");

        try {
            inputImage = ImageIO.read(inputImageFile);
        } catch (IOException e) {
            System.out.println("Unable to open input image");
        }
        try {
            paletteImage = ImageIO.read(paletteImageFile);
        } catch (IOException e) {
            System.out.println("Unable to open palette image");
        }

        var inputPixels = getPixels(inputImage);
        var outputPixels = new int[inputPixels.length];
        var palettes = getPixelArray(paletteImage);
        var originalPalette = palettes[0];

        for (int y = 1; y < paletteImage.getHeight(); y++) {
            for (int i = 0; i < inputPixels.length; i++) {
                for (int x = 0; x < originalPalette.length; x++) {
                    if (matchColors(inputPixels[i], originalPalette[x], 6)) {
                        outputPixels[i] = palettes[y][x];
                    }
                }
            }
            var outputImageFile = new File(workingDir, "palswap_output_" + y + ".png");
            var outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
            outputImage.setRGB(0, 0, inputImage.getWidth(), inputImage.getHeight(), outputPixels, 0, inputImage.getWidth());

            try {
                ImageIO.write(outputImage, "png", outputImageFile);
            } catch (IOException e) {
                System.out.println("Could not write Output image for palette " + y + ".");
            }
        }
    }

    //Could be done better ?
    public static boolean matchColors(int input, int target, int threshold) {
        var inputRGB = new Color(input);
        var targetRGB = new Color(target);

        for (int i = 0; i < threshold; i++) {
            var shiftedRedInputPositive = new Color(shiftChannel(inputRGB.getRed(), i, true), inputRGB.getGreen(), inputRGB.getBlue(), inputRGB.getAlpha());
            var shiftedGreenInputPositive = new Color(inputRGB.getRed(), shiftChannel(inputRGB.getGreen(), i, true), inputRGB.getBlue(), inputRGB.getAlpha());
            var shiftedBlueInputPositive = new Color(inputRGB.getRed(), inputRGB.getGreen(), shiftChannel(inputRGB.getBlue(), i, true), inputRGB.getAlpha());
            var shiftedAlphaInputPositive = new Color(inputRGB.getRed(), inputRGB.getGreen(), inputRGB.getBlue(), shiftChannel(inputRGB.getAlpha(), i, true));

            var shiftedRedInputNegative = new Color(shiftChannel(inputRGB.getRed(), i, false), inputRGB.getGreen(), inputRGB.getBlue(), inputRGB.getAlpha());
            var shiftedGreenInputNegative = new Color(inputRGB.getRed(), shiftChannel(inputRGB.getGreen(), i, false), inputRGB.getBlue(), inputRGB.getAlpha());
            var shiftedBlueInputNegative = new Color(inputRGB.getRed(), inputRGB.getGreen(), shiftChannel(inputRGB.getBlue(), i, false), inputRGB.getAlpha());
            var shiftedAlphaInputNegative = new Color(inputRGB.getRed(), inputRGB.getGreen(), inputRGB.getBlue(), shiftChannel(inputRGB.getAlpha(), i, false));

            if (shiftedRedInputPositive.equals(targetRGB) || shiftedGreenInputPositive.equals(targetRGB) || shiftedBlueInputPositive.equals(targetRGB) || shiftedAlphaInputPositive.equals(targetRGB)) {
                return true;
            } else if (shiftedRedInputNegative.equals(targetRGB) || shiftedGreenInputNegative.equals(targetRGB) || shiftedBlueInputNegative.equals(targetRGB) || shiftedAlphaInputNegative.equals(targetRGB)) {
                return true;
            }
        }
        return false;
    }

    public static int shiftChannel(int channel, int shift, boolean positive) {
        if (positive) {
            return (channel + shift) > 255 ? (channel - shift) : (channel + shift);
        } else {
            return (channel - shift) < 0 ? (channel + shift) : (channel - shift);
        }
    }

    public static int[][] getPixelArray(BufferedImage inputImage) {
        if (inputImage != null) {
            var tempArray = new int[inputImage.getHeight()][inputImage.getWidth()];

            for(int y = 0; y < inputImage.getHeight(); y++) {
                for (int x = 0; x < inputImage.getWidth(); x++) {
                    tempArray[y][x] = inputImage.getRGB(x, y);
                }
            }
            if (printDebug) {
                for (var color : tempArray) {
                    System.out.println(Arrays.toString(color));
                }
            }

            return tempArray;
        } else {
            System.out.println("Could not fill Pixel Array as the image provided is null");
            return null;
        }
    }

    public static int[] getPixels(BufferedImage image) {
        if (image != null) {
            return image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        } else {
            System.out.println("Could not gather Pixels as the image provided is null");
            return null;
        }
    }
}
