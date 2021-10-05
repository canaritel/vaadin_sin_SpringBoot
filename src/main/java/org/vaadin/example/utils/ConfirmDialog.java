package org.vaadin.example.utils;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ConfirmDialog extends Dialog {

    //Para saber como usarlo mirar método AdminView de Bookstore-example
    public ConfirmDialog(String caption, String text, String confirmButtonText,
            Runnable confirmListener) {

        final VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        add(content);

        add(new H3(caption));
        add(new Span(text));

        final HorizontalLayout buttons = new HorizontalLayout();
        buttons.setPadding(false);
        add(buttons);

        final Button confirm = new Button(confirmButtonText, e -> {
            confirmListener.run();
            close();
        });
        confirm.addThemeVariants(ButtonVariant.LUMO_ERROR);
        buttons.add(confirm);

        final Button cancel = new Button("Cancelar", e -> close());
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        buttons.add(cancel);

    }

}
