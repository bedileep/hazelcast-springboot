package com.cgi.hc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cgi.hc.data.EmployeeSerializer;
import com.cgi.hc.model.Employee;
import com.hazelcast.config.Config;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.ManagementCenterConfig;
import com.hazelcast.config.SerializerConfig;

@Configuration
public class CacheConfig {
@Bean
	public Config configure() {

		Config config = new Config();
		config.setInstanceName("HZ-1");
		
		GroupConfig grpConfig = new GroupConfig();
		grpConfig.setName("HZSpring");
		grpConfig.setPassword("dev-pass");
		config.setGroupConfig(grpConfig);
		
		ManagementCenterConfig mngmntConfig = new ManagementCenterConfig();
		mngmntConfig.setEnabled(true);
		mngmntConfig.setUrl("http://localhost:9090/hazelcast-mancenter");
		config.setManagementCenterConfig(mngmntConfig);

		SerializerConfig sc = new SerializerConfig();
		sc.setTypeClass(Employee.class);
		sc.setClass(EmployeeSerializer.class);
		
		config.getSerializationConfig().addSerializerConfig(sc);
		
		return config;
	}
	
	
}
