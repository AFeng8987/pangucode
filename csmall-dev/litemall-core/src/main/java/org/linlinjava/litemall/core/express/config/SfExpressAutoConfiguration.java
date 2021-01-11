package org.linlinjava.litemall.core.express.config;

import org.linlinjava.litemall.core.express.ExpressService;
import org.linlinjava.litemall.core.express.SfExpressService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SfExpressProperties.class)
public class SfExpressAutoConfiguration {

    private final SfExpressProperties sfProperties;

    public SfExpressAutoConfiguration(SfExpressProperties sfProperties) {
        this.sfProperties = sfProperties;
    }

    @Bean
    public SfExpressService sfExpressService() {
        SfExpressService sfExpressService = new SfExpressService();
        sfExpressService.setSfProperties(sfProperties);
        return sfExpressService;
    }

}
