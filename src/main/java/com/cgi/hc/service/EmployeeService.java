package com.cgi.hc.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cgi.hc.data.EmployeeRepository;
import com.cgi.hc.model.Employee;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;

@Service
public class EmployeeService {

	private Logger logger = Logger.getLogger(EmployeeService.class.getName());
	
	@Autowired
	EmployeeRepository repository;
	
	@Autowired
	HazelcastInstance instance;
	
	IMap<Integer, Employee> map;
	
	@PostConstruct
	public void init() {
		
		
		map = instance.getMap("employee");
		map.addIndex("company", true);
		logger.info("Employees cache: " + map.size());

	}
	
	@SuppressWarnings("rawtypes")
	public Employee findByPersonId(Integer personId) {
		Predicate predicate = Predicates.equal("personId", personId);
		logger.info("========= trying to find Employee in cache ===========");
		Collection<Employee> ps = map.values(predicate);
		Optional<Employee> e = ps.stream().findFirst();
		if (e.isPresent()) {
		logger.info("========= found the Employee in cache ===========");
			return e.get();
			}
		logger.info("+++++++++ trying to find Employee in DB +++++++++++");
		Employee emp = repository.findByPersonId(personId);
		logger.info("Employee: " + emp);
		map.put(emp.getId(), emp);
		return emp;
	}
	
	@SuppressWarnings("rawtypes")
	public List<Employee> findByCompany(String company) {
		
		Predicate prdeicate = Predicates.equal("company", company);
		logger.info("========= trying to find Employee in cache ===========");
		Collection<Employee> ps = map.values(prdeicate);
		if (ps.size() > 0) {
			logger.info("========= found the Employees in cache ===========");
			   return ps.stream().collect(Collectors.toList());
			  }
		logger.info("+++++++++ trying to find Employee in DB +++++++++++");
		 List<Employee> e = repository.findByCompany(company);
		 logger.info("Employees size: " + e.size());
		 e.parallelStream().forEach(it -> {
			   map.putIfAbsent(it.getId(), it);
			  });
		 return e;
	}
	
	public Employee findById(Integer id) {
		Employee e = map.get(id);
		if (e != null) {
			return e;
		}
		Optional<Employee> emp = repository.findById(id);
		if(emp.isPresent()) {
		map.put(id, e);
		}
		return e;
	}
	
	public Employee add(Employee e) {
		e = repository.save(e);
		map.put(e.getId(), e);
		return e;
	}
	
}
