package ch.admin.bar.siardsuite.util;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.text.MessageFormat;
import java.util.*
        ;


public class I18n {

  private static final ObjectProperty<Locale> locale;

  public static Locale getLocale() {
    return locale.get();
  }

  public static void setLocale(Locale locale) {
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
    return List.of(Locale.ENGLISH, Locale.GERMAN, Locale.FRENCH, Locale.ITALIAN);
  }

  private static final String BASE_NAME = "ch/admin/bar/siardsuite/i18n/messages";

  public static String get(final String key, final Object... args) {
    ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, getLocale());
    return MessageFormat.format(bundle.getString(key), args);
  }

  public static StringBinding createStringBinding(final String key, Object... args) {
    return Bindings.createStringBinding(() -> get(key, args), locale);
  }
}
