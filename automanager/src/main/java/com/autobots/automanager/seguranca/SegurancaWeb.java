package com.autobots.automanager.seguranca;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SegurancaWeb implements WebMvcConfigurer {
  private final Acesso acesso;

  public SegurancaWeb(Acesso acesso) {
    this.acesso = acesso;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(acesso);
  }
}
