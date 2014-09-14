package com.mycompany.app.web.controller;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mycompany.app.model.facts.Application;
import com.mycompany.app.web.services.delegate.DecisionServiceDelegate;


@Controller
@RequestMapping(value = "/mycompany/rest/decision/*")
public class DecisionRESTfulServiceController {
	
    private static final Logger logger = Logger.getLogger(DecisionRESTfulServiceController.class.getName());
	
    @Autowired
    private DecisionServiceDelegate decisionServiceDelegate;
	
	@RequestMapping(value = "/checkHolidayService/{name}/{month}", method = RequestMethod.GET)
	public @ResponseBody void checkHolidayService(@PathVariable String name, @PathVariable String month) {		
	    logger.info("Attempting to execute checkHolidayService service with name: " + name + " month: " + month);
		decisionServiceDelegate.holidayDecisionService(name, month);
	}
	
	@RequestMapping(value = "/processApplicationService/{id}", method = RequestMethod.GET)
	public @ResponseBody Application processApplicationService(@PathVariable Integer id) {
		logger.info("Attempting to execute processApplicationService with id: " + id);
		return decisionServiceDelegate.processApplicationService(id);
	}
	
}