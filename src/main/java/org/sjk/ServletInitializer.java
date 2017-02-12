package org.sjk;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer {
//    @Bean
//    public ServletRegistrationBean loginServletRegistrationBean(){
//        return new ServletRegistrationBean(new LoginPageServlet(),"/login.do");
//    }
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(TabProjektApplication.class);
	}

}
