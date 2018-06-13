package com.cgi.hc.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cgi.hc.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Integer>{
	
	 	public Employee findByPersonId(Integer personId);
	    public List<Employee> findByCompany(String company);

}
