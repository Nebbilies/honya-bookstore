package com.honya.bookstore.media.api;

import java.util.UUID;

public interface MediaApi {
    MediaView getMediaById(UUID id);
}
