package org.vaadin.example.utils;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;
import java.io.ByteArrayInputStream;

public class ConvertToImage {

    static private Image imagenes;
/*
    public static Image convertToImage(final byte[] imageData) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
            BufferedImage imagen = ImageIO.read(bis);

            return (Image) imagen.getSource();
        } catch (IOException ex) {
            Logger.getLogger(ConvertToImage.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
*/
    public static StreamResource convertToStreamImage(byte[] imageData) {
        StreamResource streamResource = new StreamResource("isr", () -> new ByteArrayInputStream(imageData));

        return streamResource;
    }

}
