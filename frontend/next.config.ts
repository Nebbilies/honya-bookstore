import type { NextConfig } from "next";

const nextConfig: NextConfig = {
    output: "standalone",
    images: {
        remotePatterns: [
            {
                protocol: "https",
                hostname: "example.com",
            },
            {
                protocol: "http",
                hostname: 'host.docker.internal',
            }
        ]
    }
};

export default nextConfig;
