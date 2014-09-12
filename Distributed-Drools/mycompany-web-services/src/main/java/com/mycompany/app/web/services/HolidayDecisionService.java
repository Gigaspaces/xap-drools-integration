package com.mycompany.app.web.services;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.openspaces.remoting.ExecutorProxy;
import org.springframework.stereotype.Component;

import com.mycompany.app.model.facts.Holiday;
import com.mycompany.app.service.drools.IRulesExecutionService;
import com.mycompany.app.util.RulesConstants;

@Component
public class HolidayDecisionService {

	private static Logger log = Logger.getLogger(HolidayDecisionService.class);

    @ExecutorProxy(gigaSpace = "gigaSpace")
    private IRulesExecutionService rulesExecutionService;
	
	public HolidayDecisionService() {}
	
	public void checkIfItsJuly(String name, String month) {
		log.info("Starting HolidayDecisionService Execution");

		Collection<Object> facts = new ArrayList<Object>(1);
        facts.add(new Holiday(name, month));

        try {
            rulesExecutionService.executeRules(RulesConstants.RULE_SET_HOLIDAY, facts);
        }catch(Exception e) {
        	System.out.println(e);
            log.error(e.getMessage(), e);
        }

        log.info("End HolidayDecisionService Execution");
	}
	
}