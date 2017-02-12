package org.sjk;

import org.sjk.dao.ActionDao;
import org.sjk.dao.IpDao;
import org.sjk.dao.PasswordDao;
import org.sjk.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@SpringBootApplication
@ComponentScan
public class TabProjektApplication{
	@Autowired
	private UserDao userDao;
	@Autowired
	private PasswordDao passwordDao;
	@Autowired
	private IpDao ipDao;
	@Autowired
	private ActionDao actionDao;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		SpringApplication.run(TabProjektApplication.class, args);
	}

	@PostConstruct
	public void initApplication(){
		userDao.updateRelations();
		passwordDao.updateRelations();
		actionDao.updateRelations();
	}




}
