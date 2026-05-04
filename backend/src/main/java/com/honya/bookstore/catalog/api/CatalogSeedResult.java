package com.honya.bookstore.catalog.api;

import java.util.UUID;

public record CatalogSeedResult(boolean seeded, UUID narutoBookId, UUID duneBookId) {
    public static CatalogSeedResult skipped() {
        return new CatalogSeedResult(false, null, null);
    }

    public static CatalogSeedResult seeded(UUID narutoBookId, UUID duneBookId) {
        return new CatalogSeedResult(true, narutoBookId, duneBookId);
    }
}
