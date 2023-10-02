package ch.admin.bar.siardsuite.util;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Labeled;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class I18n {

    public static final ObjectProperty<Locale> locale;

    public static Locale getLocale() {
        return locale.get();
    }

    public static void setLocale(final Locale locale) {
        localeProperty().set(locale);
        Locale.setDefault(locale);
    }

    public static ObjectProperty<Locale> localeProperty() {
        return locale;
    }

    static {
        locale = new SimpleObjectProperty<>(getDefaultLocale());
        locale.addListener((observable, oldValue, newValue) -> Locale.setDefault(newValue));
    }

    public static Locale getDefaultLocale() {
        Locale defaultLocale = Locale.getDefault();
        return getSupportedLocales().contains(defaultLocale) ? defaultLocale : Locale.ENGLISH;
    }

    public static List<Locale> getSupportedLocales() {
        List<Locale> locales = new ArrayList<>();
        locales.add(Locale.ENGLISH);
        locales.add(Locale.GERMAN);
        locales.add(Locale.FRENCH);
        locales.add(Locale.ITALIAN);
        return locales;
    }

    private static final String BASE_NAME = "ch/admin/bar/siardsuite/i18n/messages";

    public static String get(final I18nKey key, final Object... args) {
        return get(key.getValue(), args);
    }

    /**
     * Deprecation: Please use {@link #get(I18nKey, Object...)} instead.
     */
    @Deprecated
    public static String get(final String key, final Object... args) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, getLocale());
        String translation = bundle.containsKey(key) ? bundle.getString(key) : key;
        return String.format(translation, args);
    }

    @Deprecated
    public static StringBinding createStringBinding(final String key, final Object... args) {
        return Bindings.createStringBinding(() -> get(key, args), locale);
    }

    public static void bind(StringProperty stringProperty, final String key, final Object... args) {
        stringProperty.bind(I18n.createStringBinding(key, args));
    }

    public static void bind(Labeled labeled, final String key, final Object... args) {
        bind(labeled.textProperty(), key, args);
    }

    public static void bind(Text text, String key, final Object... args) {
        bind(text.textProperty(), key, args);
    }

    public static void bind(TextFlow textFlow, String prefix, String orientation) {
        for (int i = 0; i < textFlow.getChildren().size(); i++) {
            Text text = (Text) textFlow.getChildren().get(i);
            bind(text.textProperty(), prefix + orientation + ".text" + i);
        }
    }

    public static String getLocaleDate(final LocalDate date) {
        final String pattern;
        if (getLocale().equals(Locale.ENGLISH)) {
            pattern = "dd MMM yyyy";
        } else {
            pattern = "dd. MMM yyyy";
        }
        return date.format(DateTimeFormatter.ofPattern(pattern, getLocale()));
    }

    public static String getLocaleDate(final String yyyy_mm_dd) {
        final LocalDate date = LocalDate.parse(yyyy_mm_dd);
        return getLocaleDate(date);
    }
}
