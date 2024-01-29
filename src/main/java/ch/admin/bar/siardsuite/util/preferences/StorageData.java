package ch.admin.bar.siardsuite.util.preferences;

import ch.admin.bar.siardsuite.util.I18n;
import lombok.Builder;
import lombok.Value;

import java.time.ZonedDateTime;

@Value
@Builder
public class StorageData<T> {
    ZonedDateTime storedAt;
    T storedData;

    /**
     * Returns the storage date as formatted string (for compatibility reasons)
     */
    public String getStoredAtDate() {
        return I18n.getLocaleDate(storedAt.toLocalDate());
    }

    /**
     * Returns the storage time in epoch milliseconds as string (for compatibility reasons)
     */
    public String getStoredAtTime() {
        return storedAt.toInstant().toEpochMilli() + "";
    }
}