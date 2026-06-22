package com.honya.platform.security;

import java.util.List;

@FunctionalInterface
public interface PublicEndpointsContributor {
    List<PublicEndpoint> publicEndpoints();
}
