# Proyecto Vaadin
Este proyecto se basa en una aplicación web con acceso a BD MySQL hosteada, mediante el ORM JPA y EclipseLink.
A medida vaya ampliando este proyecto se irá documentando.

Mi forma de crear este proyecto y restantes es simplificar el código, no abusar de métodos o sentencias complejas, logrando un código fácil de entender por estudiantes de Vaadin.
Este proyecto no usa Spring Boot, he visto necesario para un primer acercamiento un desarrollo como he explicado antes lo más sencillo posible, por lo que se ha usado unicamente una base del Vaadin con servidor web Jetty. En el fichero pom.xml pueden ver las distintas librerías que se están usando.

El mismo proyecto está creado en JavaFX dentro de mis repositorios públicos. https://github.com/canaritel/proyecto_JPA

# ¿Porqué Vaadin?

Vaadin es un marco basado en componentes (como Grid, TextField, ButtonField, FormLayout, HorizontalLayout y VerticalLayout). Pero, el verdadero poder de la arquitectura basada en componentes está en la capacidad de crear sus propios componentes.

En lugar de crear una vista completa en una sola clase, su vista puede estar compuesta por componentes más pequeños que manejan diferentes partes de la vista. La ventaja de este enfoque es que los componentes individuales son más fáciles de comprender y probar. La vista de nivel superior se utiliza principalmente para dirigir los componentes.

# Skeleton Starter for Vaadin

This project can be used as a starting point to create your own Vaadin application.
It has the necessary dependencies and files to help you get started.

The best way to use it is via [vaadin.com/start](https://vaadin.com/start) - you can get only the necessary parts and choose the package naming you want to use.
There is also a [getting started tutorial](https://vaadin.com/tutorials/getting-started-with-flow) based on this project.

To access it directly from github, clone the repository and import the project to the IDE of your choice as a Maven project. You need to have Java 8 or 11 installed.

Run using `mvn jetty:run` and open [http://localhost:8080](http://localhost:8080) in the browser.

If you want to run your app locally in the production mode, run `mvn jetty:run -Pproduction`.

### Running Integration Tests

Integration tests are implemented using [Vaadin TestBench](https://vaadin.com/testbench). The tests take a few minutes to run and are therefore included in a separate Maven profile. We recommend running tests with a production build to minimize the chance of development time toolchains affecting test stability. To run the tests using Google Chrome, execute

`mvn verify -Pit,production`

and make sure you have a valid TestBench license installed.

Profile `it` adds the following parameters to run integration tests:
```sh
-Dwebdriver.chrome.driver=path_to_driver
-Dcom.vaadin.testbench.Parameters.runLocally=chrome
```

For a full Vaadin application example, there are more choices available also from [vaadin.com/start](https://vaadin.com/start) page.


