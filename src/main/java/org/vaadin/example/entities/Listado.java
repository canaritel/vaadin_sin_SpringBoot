package org.vaadin.example.entities;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import javax.persistence.Id;

@Tag("listado-card")
public class Listado extends VerticalLayout {

    @Id
    private Image image = new Image();

    @Id
    private Span header = new Span();

    @Id
    private Span subtitle = new Span();

    @Id
    private Paragraph text = new Paragraph();

    @Id
    private Span badge = new Span();

    public Listado(String text, String url) {
        this.image.setSrc(url);
        this.image.setAlt(text);
        this.header.setText("Title");
        this.subtitle.setText("Card subtitle");
        this.text.setText("Texto que aparece como comentario");
        this.badge.setText("Label");
    }

    public Listado() {
    }

}
