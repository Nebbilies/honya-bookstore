package com.honya.bookstore.config;

import com.honya.bookstore.cart.api.CartApi;
import com.honya.bookstore.catalog.api.CatalogSeedApi;
import com.honya.bookstore.catalog.api.CatalogSeedResult;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DataSeederTest {

    @Test
    void skipsCartSeedWhenCatalogAlreadyHasData() throws Exception {
        CatalogSeedApi catalogSeedApi = mock(CatalogSeedApi.class);
        CartApi cartApi = mock(CartApi.class);
        when(catalogSeedApi.seedDefaultCatalogIfEmpty()).thenReturn(CatalogSeedResult.skipped());

        new DataSeeder(catalogSeedApi, cartApi).run();

        verify(cartApi, never()).addItemToCart(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyInt());
    }

    @Test
    void seedsDummyCartWhenCatalogWasSeeded() throws Exception {
        CatalogSeedApi catalogSeedApi = mock(CatalogSeedApi.class);
        CartApi cartApi = mock(CartApi.class);
        UUID narutoId = UUID.randomUUID();
        UUID duneId = UUID.randomUUID();
        when(catalogSeedApi.seedDefaultCatalogIfEmpty()).thenReturn(CatalogSeedResult.seeded(narutoId, duneId));

        new DataSeeder(catalogSeedApi, cartApi).run();

        verify(cartApi).addItemToCart(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.eq(narutoId), org.mockito.ArgumentMatchers.eq(2));
        verify(cartApi).addItemToCart(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.eq(duneId), org.mockito.ArgumentMatchers.eq(1));
    }
}
