package ch.admin.bar.siardsuite.localwebapp.connections;

import java.util.List;
import java.util.Optional;

/**
 * Persistence of saved connection profiles, ordered by name. The SQLite implementation is the
 * only one for now; the interface keeps the detail swappable (design D2).
 */
public interface ConnectionStore {

    List<SavedConnection> list();

    /** @throws DuplicateConnectionNameException if a profile with the same name exists */
    SavedConnection create(NewConnection connection);

    /**
     * Replaces the fields of an existing profile, keeping its id and creation time.
     *
     * @return the updated profile, or empty if no profile with the id exists
     * @throws DuplicateConnectionNameException if another profile has the same name
     */
    Optional<SavedConnection> update(long id, NewConnection connection);

    /** @return whether a profile with the id existed */
    boolean delete(long id);
}
