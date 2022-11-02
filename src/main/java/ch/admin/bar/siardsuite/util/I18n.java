package ch.admin.bar.siardsuite.util;

import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.model.database.DatabaseObject;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class I18n {

  private static final ObjectProperty<Locale> locale;

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
    return List.of(Locale.ENGLISH, Locale.GERMAN, Locale.FRENCH, Locale.ITALIAN);
  }

  private static final String BASE_NAME = "ch/admin/bar/siardsuite/i18n/messages";

  public static String get(final String key, final Object... args) {
    ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, getLocale());
    return String.format(bundle.getString(key), args);
  }

  public static StringBinding createStringBinding(final String key, final Object... args) {
    return Bindings.createStringBinding(() -> get(key, args), locale);
  }

  public static ObjectBinding<TreeAttributeWrapper> createTreeAtributeWrapperBinding(final String key, final int id, final TreeContentView type, DatabaseObject databaseObject, final Object... args) {
    return Bindings.createObjectBinding(() -> new TreeAttributeWrapper(get(key, args), id, type, databaseObject), locale);
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
