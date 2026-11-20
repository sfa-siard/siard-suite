package ch.admin.bar.siardsuite.localwebapp.connections;

import java.util.List;

/**
 * Persistence of saved connection profiles, ordered by name. The SQLite implementation is the
 * only one for now; the interface keeps the detail swappable (design D2).
 */
public interface ConnectionStore {

    List<SavedConnection> list();

    /** @throws DuplicateConnectionNameException if a profile with the same name exists */
    SavedConnection create(NewConnection connection);

    /** @return whether a profile with the id existed */
    boolean delete(long id);

    boolean existsByName(String name);
}
