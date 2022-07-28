package ru.netology.graphics.image;

public class TextColorSchemaImpl implements TextColorSchema {

    @Override
    public char convert(int color) {
        if (color < 32) {
            return "#".charAt(0);
        } else if (color < 64) {
            return "$".charAt(0);
        } else if (color < 95) {
            return "@".charAt(0);
        } else if (color < 127) {
            return "%".charAt(0);
        } else if (color < 159) {
            return "*".charAt(0);
        } else if (color < 191) {
            return "+".charAt(0);
        } else if (color < 223) {
            return "-".charAt(0);
        } else {
            return "#".charAt(0);
        }
    }
}
