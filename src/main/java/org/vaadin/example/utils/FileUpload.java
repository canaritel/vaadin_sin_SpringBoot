package org.vaadin.example.utils;

public class FileUpload {

    /*
    
    public static void createSimpleUpload() {
        Div output = new Div();

        //@formatter:off
        // begin-source-example
        // source-example-heading: Simple in memory receiver for single file upload
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);

        upload.addSucceededListener(event -> {
            Component component = createComponent(event.getMIMEType(),
                                                  event.getFileName(),
                                                  buffer.getInputStream());
            showOutput(event.getFileName(), component, output);
        });
        // end-source-example
        //@formatter:on
        upload.setMaxFileSize(500 * 1024);
        upload.setId("test-upload");
        output.setId("test-output");

        //    addCard("Simple in memory receiver for single file upload", upload, output);
    }
     
    public static void createSimpleUploadImage(MemoryBuffer buffer, Upload upload, byte[] imageByte, Image image) {
        Div output = new Div();

        //@formatter:off
        // begin-source-example
        // source-example-heading: Simple in memory receiver for single file upload
        //MemoryBuffer buffer = new MemoryBuffer();
        // Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");

        upload.addSucceededListener(event -> {
            Component component = createComponent(event.getMIMEType(),
                                                  event.getFileName(),
                                                  buffer.getInputStream(), imageByte, image);
            output.removeAll(); ////////////////
            showOutput(event.getFileName(), component, output);
        });
        // end-source-example
        //@formatter:on
        upload.setMaxFileSize(600 * 1024);
        upload.setMaxFileSize(65000);
        upload.setId("test-upload");
        output.setId("test-output");

        upload.addFileRejectedListener(event -> {
            Paragraph component = new Paragraph();
            output.removeAll();
            showOutput(event.getErrorMessage(), component, output);
        });

        upload.getElement().addEventListener("file-remove", event -> {
                                         output.removeAll();
                                     });

        //    addCard("Simple in memory receiver for single file upload", upload, output);
    }

    
    public static void createUploadWithFileConstraints() {
        Div output = new Div();

        //@formatter:off
        // begin-source-example
        // source-example-heading: Simple single file upload showing messages when file rejected
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setMaxFiles(1);
        upload.setDropLabel(new Label("Upload a 300 bytes file in .csv format"));
        upload.setAcceptedFileTypes("text/csv");
        upload.setMaxFileSize(300);

        upload.addFileRejectedListener(event -> {
            Paragraph component = new Paragraph();
            showOutput(event.getErrorMessage(), component, output);
        });
        // end-source-example
        //@formatter:on
        upload.setId("test-upload");
        output.setId("test-output");

        //    addCard("Simple single file upload showing messages when file rejected", upload, output);
    }
     
 
    public static void createSimpleMultiFileUpload() {
        Div output = new Div();

        //@formatter:off
        // begin-source-example
        // source-example-heading: Simple in memory receiver for multi file upload
        MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
        Upload upload = new Upload(buffer);

        upload.addSucceededListener(event -> {
            Component component = createComponent(event.getMIMEType(),
                                                  event.getFileName(),
                                                  buffer.getInputStream(event.getFileName()));
            showOutput(event.getFileName(), component, output);
        });
        // end-source-example
        //@formatter:on
        upload.setMaxFileSize(200 * 1024);

        //  addCard("Simple in memory receiver for multi file upload", upload, output);
    }
     
 
    public static void createFilteredMultiFileUpload() {
        Div output = new Div();

        // begin-source-example
        // source-example-heading: Filtered multi file upload for images
        MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");

        upload.addSucceededListener(event -> {
            Component component = createComponent(event.getMIMEType(),
                                                  event.getFileName(),
                                                  buffer.getInputStream(event.getFileName()));
            showOutput(event.getFileName(), component, output);
        });
        // end-source-example
        upload.setMaxFileSize(200 * 1024);

        //   addCard("Filtered multi file upload for images", upload, output);
    }
     
 
    public static void createNonImmediateUpload() {
        Div output = new Div();

        // begin-source-example
        // source-example-heading: Non immediate upload
        MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAutoUpload(false);

        upload.addSucceededListener(event -> {
            Component component = createComponent(event.getMIMEType(),
                                                  event.getFileName(),
                                                  buffer.getInputStream(event.getFileName()));
            showOutput(event.getFileName(), component, output);
        });
        // end-source-example
        upload.setMaxFileSize(200 * 1024);

        //   addCard("Non immediate upload", upload, output);
    }
     
 
    public static void changeDefaultComponents() {
        Div output = new Div();

        // begin-source-example
        // source-example-heading: Custom components upload demo
        MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
        Upload upload = new Upload(buffer);

        NativeButton uploadButton = new NativeButton("Upload");
        upload.setUploadButton(uploadButton);

        Span dropLabel = new Span("Drag and drop things here!");
        upload.setDropLabel(dropLabel);

        Span dropIcon = new Span("¸¸.•*¨*•♫♪");
        upload.setDropLabelIcon(dropIcon);

        upload.addSucceededListener(event -> {
            Component component = createComponent(event.getMIMEType(),
                                                  event.getFileName(),
                                                  buffer.getInputStream(event.getFileName()));
            showOutput(event.getFileName(), component, output);
        });
        // end-source-example
        upload.setMaxFileSize(200 * 1024);

        //  addCard("Custom components upload demo", upload, output);
    }
     
 
    public static void i18nSampleUpload() {
        Div output = new Div();

        // begin-source-example
        // source-example-heading: i18n translations example
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setId("i18n-upload");

        upload.addSucceededListener(event -> {
            Component component = createComponent(event.getMIMEType(),
                                                  event.getFileName(), buffer.getInputStream());
            showOutput(event.getFileName(), component, output);
        });

        UploadI18N i18n = new UploadI18N();
        i18n.setDropFiles(
                new UploadI18N.DropFiles().setOne("Перетащите файл сюда...")
                        .setMany("Перетащите файлы сюда..."))
                .setAddFiles(new UploadI18N.AddFiles()
                        .setOne("Выбрать файл").setMany("Добавить файлы"))
                .setCancel("Отменить")
                .setError(new UploadI18N.Error()
                        .setTooManyFiles("Слишком много файлов.")
                        .setFileIsTooBig("Слишком большой файл.")
                        .setIncorrectFileType("Некорректный тип файла."))
                .setUploading(new UploadI18N.Uploading()
                        .setStatus(new UploadI18N.Uploading.Status()
                                .setConnecting("Соединение...")
                                .setStalled("Загрузка застопорилась.")
                                .setProcessing("Обработка файла..."))
                        .setRemainingTime(
                                new UploadI18N.Uploading.RemainingTime()
                                        .setPrefix("оставшееся время: ")
                                        .setUnknown(
                                                "оставшееся время неизвестно"))
                        .setError(new UploadI18N.Uploading.Error()
                                .setServerUnavailable("Сервер недоступен")
                                .setUnexpectedServerError(
                                        "Неожиданная ошибка сервера")
                                .setForbidden("Загрузка запрещена")))
                .setUnits(Stream
                        .of("Б", "Кбайт", "Мбайт", "Гбайт", "Тбайт", "Пбайт",
                            "Эбайт", "Збайт", "Ибайт")
                        .collect(Collectors.toList()));

        upload.setI18n(i18n);
        // end-source-example
        upload.setMaxFileSize(200 * 1024);

        //   addCard("i18n translations example", upload, output);
    }
     
    public static Component createComponent(String mimeType, String fileName, InputStream stream, byte[] imageByte, Image image) {
        if (mimeType.startsWith("text")) {
            return createTextComponent(stream);
        } else if (mimeType.startsWith("image")) {
            //Image image = new Image();
            try {

                byte[] bytes = IOUtils.toByteArray(stream);
                image.getElement().setAttribute("src", new StreamResource(
                                                fileName, () -> new ByteArrayInputStream(bytes)));
                try (ImageInputStream in = ImageIO.createImageInputStream(
                        new ByteArrayInputStream(bytes))) {
                    final Iterator<ImageReader> readers = ImageIO
                            .getImageReaders(in);
                    if (readers.hasNext()) {
                        ImageReader reader = readers.next();
                        try {
                            reader.setInput(in);
                            //image.setWidth(reader.getWidth(0) + "px");
                            //image.setHeight(reader.getHeight(0) + "px");
                            image.setHeight("70px");
                        } finally {
                            reader.dispose();
                        }
                    }
                }
                //Pasamos la imagen a tipo byte
                imageByte = bytes;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return image;
        }
        Div content = new Div();
        String text = String.format("Mime type: '%s'\nSHA-256 hash: '%s'",
                                    mimeType, MessageDigestUtil.sha256(stream.toString()));
        content.setText(text);
        return content;

    }

    public static Component createTextComponent(InputStream stream) {
        String text;
        try {
            text = IOUtils.toString(stream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            text = "exception reading stream";
        }
        return new Text(text);
    }

    public static void showOutput(String text, Component content, HasComponents outputContainer) {
        HtmlComponent p = new HtmlComponent(Tag.P);
        p.getElement().setText(text);
        outputContainer.add(p);
        outputContainer.add(content);
    }
     */
}
