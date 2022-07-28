package ru.netology.graphics.image;

//imports

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TextGraphicsConventerImpl implements TextGraphicsConverter {
    protected int imgWidth; //изначальная ширина картинки
    protected int imgHeight; //изначальная высота картинки
    protected int maxWidth; //максимальная ширина
    protected int maxHeight; //максимальная высота
    protected int newWidth;  //в дальнейшем используемая ширина
    protected int newHeight; //в дальнейшем используемая высота
    protected double maxRatio; //максимальное соотношение сторон
    protected TextColorSchema schema; //цветовая схема

    @Override
    public String convert(String url) throws IOException, BadImageSizeException {
        //скачаем картинку из интернета
        BufferedImage img = ImageIO.read(new URL(url));

        // Проверка на верное соотношение сторон, если нам ввели ограничения, если неверно, ты выкидываем исключение BadImageSizeException
        imgWidth = img.getWidth();
        imgHeight = img.getHeight();
        if (maxRatio < (float) (imgWidth / imgHeight) && maxRatio > 0) { //ОШИБКА о неправильном размере картинки
            throw new BadImageSizeException((float) (imgWidth / imgHeight), maxRatio);
        }

        //Если нам ввели ограничения по размерам картинки. Здесь мы это проверяем и исправляем.
        double scale;
        if ((maxHeight <imgHeight) || maxWidth < imgWidth) {
            scale = Math.min(((double) maxHeight / imgHeight), ((double) maxWidth / imgWidth));
        } else {
            scale = 1;
        }
        // Вычисляем новые размеры
        int newWidth = (int) (imgWidth * scale);
        int newHeight = (int) (imgHeight * scale);

        // Теперь нам надо попросить картинку изменить свои размеры на новые.
        //                                     просим картинку аккуратно уменьшиться - \/
        Image scaledImage = img.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOTH);

        // Теперь сделаем её чёрно-белой.
        // Создадим новую пустую картинку нужных размеров, заранее указав последним
        //                                   параметром чёрно-белую цветовую палитру - \/
        BufferedImage bwImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);
        // Попросим у этой картинки инструмент для рисования на ней:
        Graphics2D graphics = bwImg.createGraphics();
        // А этому инструменту скажем, чтобы он скопировал содержимое из нашей суженной картинки:
        graphics.drawImage(scaledImage, 0, 0, null);
        // Теперь в bwImg у нас лежит чёрно-белая картинка нужных нам размеров.

        // Теперь давайте пройдёмся по пикселям нашего изображения:
        WritableRaster bwRaster = bwImg.getRaster();

        if (schema == null) { //Задаём схему, если нам её не задали ранее
            schema = new TextColorSchemaImpl();
        }

        //Переписываем картинку спец. символами
        char[][] pixels = new char[newWidth][newHeight];
        for (int w = 0; w < newWidth; w++) {
            for (int h = 0; h < newHeight; h++) {
                int color = bwRaster.getPixel(w, h, new int[3])[0];
                char c = schema.convert(color);
                pixels[w][h] = c; //запоминаем символ c в двумерный массив
            }
        }

        // чисто в теории, можно было нижний цикл запихнуть сразу в верхний
        // Собираем все символы в один текст

        StringBuilder output = new StringBuilder();
        for (int h = 0; h < newHeight; h++) {
            for (int w = 0; w < newWidth; w++) {
                output.append(pixels[w][h]);
                output.append(" ");
            }
            output.append("\n");
        }

        //сохранение картинки на комп ImageIO.write(bwImg, "png", new File(LocalDateTime.now() + "_out.png"));
        return output.toString(); // Возвращаем собранный текст
    }

    @Override
    public void setMaxWidth(int width) {
        this.maxWidth = width;
    }

    @Override
    public void setMaxHeight(int height) {
        this.maxHeight = height;
    }

    @Override
    public void setMaxRatio(double maxRatio) {
        this.maxRatio = maxRatio;
    }

    @Override
    public void setTextColorSchema(TextColorSchema schema) {
        this.schema = schema;
    }
}
