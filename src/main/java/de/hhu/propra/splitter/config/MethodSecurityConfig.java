package de.hhu.propra.splitter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class MethodSecurityConfig {

}
