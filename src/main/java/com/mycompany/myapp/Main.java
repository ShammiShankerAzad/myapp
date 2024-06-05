package com.mycompany.myapp;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

public class Main {
    public static void main(String[] args) {
        // Create a basic Jetty server object that will listen on port 8080
        Server server = new Server(8080);

        // The ServletContextHandler is a place where we register servlets
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");

        // Add the ServletContextHandler to the server
        server.setHandler(context);

        // Create a ServletHolder and set it to be a Jersey ServletContainer
        ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, "/api/*");
        jerseyServlet.setInitOrder(0);

        // Configure Jersey Servlet to use the application class with package scanning
        jerseyServlet.setInitParameter("jakarta.ws.rs.Application", MyApplication.class.getCanonicalName());

        try {
            // Start the Jetty server
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.destroy();
        }
    }
}
