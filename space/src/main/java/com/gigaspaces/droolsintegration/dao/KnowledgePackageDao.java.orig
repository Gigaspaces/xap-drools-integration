package com.gigaspaces.droolsintegration.dao;

import org.openspaces.core.GigaSpace;
import org.openspaces.core.context.GigaSpaceContext;
import org.springframework.stereotype.Component;

import com.gigaspaces.droolsintegration.model.drools.KnowledgePackage;

@Component
public class KnowledgePackageDao {
	
	@GigaSpaceContext(name = "gigaSpace")
	private GigaSpace gigaSpace;
	
	public void write(KnowledgePackage knowledgePackage) {
		gigaSpace.write(knowledgePackage);	
	}
	
	public KnowledgePackage read(KnowledgePackage knowledgePackage) {
		return gigaSpace.read(knowledgePackage);
	}
	
	public void clear(KnowledgePackage knowledgePackage) {
		gigaSpace.clear(knowledgePackage);
	}
	
}