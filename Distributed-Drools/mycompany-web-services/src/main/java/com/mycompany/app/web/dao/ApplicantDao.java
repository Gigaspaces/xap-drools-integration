package com.mycompany.app.web.dao;

import org.openspaces.core.GigaSpace;
import org.openspaces.core.context.GigaSpaceContext;
import org.springframework.stereotype.Component;

import com.mycompany.app.model.facts.Applicant;

@Component
public class ApplicantDao {

	@GigaSpaceContext(name = "gigaSpace")
	private GigaSpace gigaSpace;
	
	public Applicant readById(Integer id) {
		return gigaSpace.readById(Applicant.class, id);
	}
	
}