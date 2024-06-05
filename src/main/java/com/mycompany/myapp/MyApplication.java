package com.mycompany.myapp;

import org.glassfish.jersey.server.ResourceConfig;
import jakarta.ws.rs.ApplicationPath;

@ApplicationPath("/api")
public class MyApplication extends ResourceConfig {
    public MyApplication() {
        // Scan the specified package for JAX-RS resources
        packages("com.mycompany.myapp.resources");
    }
}
