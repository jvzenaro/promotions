package com.zenaro.promotions;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;

import org.jboss.weld.environment.se.Weld;

/**
 * Hello world!
 *
 */
public class Application {
	
	public static void main(String[] args) throws Exception {
        try (CDI<Object> container = new Weld().initialize()) { 
        	Instance<ApplicationCrawler> instance = container.select(ApplicationCrawler.class);
        	instance.get().init();
        } 
	}
}
