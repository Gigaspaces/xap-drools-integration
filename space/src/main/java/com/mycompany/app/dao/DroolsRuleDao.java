package com.mycompany.app.dao;

import org.openspaces.core.GigaSpace;
import org.openspaces.core.context.GigaSpaceContext;
import org.springframework.stereotype.Component;

import com.mycompany.app.model.drools.DroolsRule;

@Component
public class DroolsRuleDao {
	
	@GigaSpaceContext(name = "gigaSpace")
	private GigaSpace gigaSpace;
	
	public void clear(String ruleName) {
		gigaSpace.clear(new DroolsRule(ruleName));
	}
	
	public void write(DroolsRule droolsRule) {
		gigaSpace.write(droolsRule);	
	}
	
}