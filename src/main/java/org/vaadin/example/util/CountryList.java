package org.vaadin.example.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CountryList {

    static private String[] locales;
    static private final List<String> paises = new ArrayList<>();

    public static List<String> listadoPaises() {
        locales = Locale.getISOCountries();

        for (String countryCode : locales) {
            Locale obj = new Locale("", countryCode);
            paises.add(obj.getDisplayCountry());
        }
        return paises;
    }

}
