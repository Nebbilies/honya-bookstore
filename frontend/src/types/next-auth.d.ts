import { UserRole } from "@/types/roles";
import "next-auth";
import "next-auth/jwt";

declare module "next-auth" {
    export interface Profile {
        realm_access?: {
            roles: string[];
        };
        address?: {
            street_address: string;
            locality: string;
        };
    }

    export interface Session {
        userId: string;
        firstName: string;
        lastName: string;
        accessToken: string;
        role: UserRole | null;
        address: {
            street_address: string;
            locality: string;
        };
        phoneNumber: string;
    }
}

declare module "next-auth/jwt" {
    export interface JWT {
        userId: string;
        accessToken: string;
        role: UserRole | null;
        address: {
            street_address: string;
            locality: string;
        };
        phoneNumber: string;
        firstName: string;
        lastName: string;
    }
}
