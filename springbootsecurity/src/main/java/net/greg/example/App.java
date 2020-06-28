package net.greg.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;


@SpringBootApplication
public class App extends SpringBootServletInitializer {

  public static void main(String[] args) {
System.err.println("\n\nOk SpringBootApplication.main(String[])");

    SpringApplication.run(App.class, args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder app) {

System.err.println("\n\nOk " + getClass() + ".configure(SpringApplicationBuilder)");

    return app.sources(App.class);
  }
}
