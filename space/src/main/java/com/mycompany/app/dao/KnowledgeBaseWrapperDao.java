package com.mycompany.app.dao;

import org.openspaces.core.GigaSpace;
import org.openspaces.core.context.GigaSpaceContext;
import org.springframework.stereotype.Component;

import com.mycompany.app.model.drools.KnowledgeBaseWrapper;

@Component
public class KnowledgeBaseWrapperDao {

	@GigaSpaceContext(name = "gigaSpace")
	private GigaSpace gigaSpace;
	
	public KnowledgeBaseWrapper readByRuleSet(String ruleSet) {
		return gigaSpace.read(new KnowledgeBaseWrapper(ruleSet));
	}
	
	public void write(KnowledgeBaseWrapper knowledgeBaseWrapper) {
		gigaSpace.write(knowledgeBaseWrapper);
	}
	
}