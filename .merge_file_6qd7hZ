package com.mycompany.app.event.listener;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.io.ResourceFactory;
import org.openspaces.events.EventDriven;
import org.openspaces.events.EventTemplate;
import org.openspaces.events.TransactionalEvent;
import org.openspaces.events.adapter.SpaceDataEvent;
import org.openspaces.events.polling.Polling;
import org.springframework.beans.factory.annotation.Autowired;

import com.j_spaces.core.client.SQLQuery;
import com.mycompany.app.dao.DroolsRuleDao;
import com.mycompany.app.dao.KnowledgeBaseWrapperDao;
import com.mycompany.app.dao.KnowledgePackageDao;
import com.mycompany.app.model.drools.DroolsRule;
import com.mycompany.app.model.drools.DroolsRuleAddEvent;
import com.mycompany.app.model.drools.KnowledgeBaseWrapper;
import com.mycompany.app.model.drools.KnowledgePackage;

@EventDriven
@TransactionalEvent(timeout=3000)
@Polling(gigaSpace = "gigaSpace", concurrentConsumers = 1, maxConcurrentConsumers = 1)
public class DroolsRuleAddEventListener  {

    private final Logger log = Logger.getLogger(DroolsRuleAddEventListener.class);
    
    @Autowired
    private DroolsRuleDao droolsRuleDao;
    
    @Autowired
    private KnowledgeBaseWrapperDao knowledgeBaseWrapperDao;
    
    @Autowired
    private KnowledgePackageDao knowledgePackageDao;
    
    private Properties properties;

    @PostConstruct
    public void initializeProperties() {
    	properties = new Properties();
        properties.setProperty("drools.dialect.java.compiler.lnglevel", "1.6");
    }
    
    @EventTemplate
    public static SQLQuery<DroolsRuleAddEvent> findUnprocessedRule() {
        return new SQLQuery<DroolsRuleAddEvent>(DroolsRuleAddEvent.class, "processed = ?", Boolean.FALSE);
    }

    @SpaceDataEvent
    public DroolsRuleAddEvent addRule(DroolsRuleAddEvent droolsRuleEvent) {
    	String ruleSet = droolsRuleEvent.getRuleSet();
		String knowledgePackageName = droolsRuleEvent.getPackageName();
		String ruleName = droolsRuleEvent.getRuleName();
    	
		try {
    		KnowledgeBaseWrapper knowledgeBaseWrapper = knowledgeBaseWrapperDao.readByRuleSet(ruleSet);
            if(knowledgeBaseWrapper == null) {
            	knowledgeBaseWrapper = new KnowledgeBaseWrapper(ruleSet);
            	
                knowledgeBaseWrapper.setKnowledgeBase(KnowledgeBaseFactory.newKnowledgeBase());
                knowledgeBaseWrapper.setTotalKnowledgePackages(new Integer(0));
                knowledgeBaseWrapper.setTotalRules(new Integer(0));
            }
        	
            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(new PackageBuilderConfiguration(properties));
            kbuilder.add(ResourceFactory.newByteArrayResource(droolsRuleEvent.getRuleBytes()), ResourceType.getResourceType(droolsRuleEvent.getOriginalResourceType()));

            if(kbuilder.hasErrors()) {
                for(KnowledgeBuilderError error : kbuilder.getErrors()) {
                    log.error(error.getMessage() + " on lines " + Arrays.toString(error.getLines()));
                }
                throw new IllegalArgumentException("Could not parse Rule: " + ruleName + " type: " + droolsRuleEvent.getOriginalResourceType());
            }

            KnowledgeBase knowledgeBase = knowledgeBaseWrapper.getKnowledgeBase();
            knowledgeBase.addKnowledgePackages(kbuilder.getKnowledgePackages());
            
            KnowledgePackage knowledgePackage = lookupKnowledgePackage(ruleSet, knowledgePackageName);
            if(!knowledgePackage.getRules().containsKey(ruleName)) {
            	updateKnowledgePackageAndDroolsRule(droolsRuleEvent, knowledgePackage);
            	
            	knowledgeBaseWrapper.setTotalKnowledgePackages(knowledgeBaseWrapper.getTotalKnowledgePackages() + 1);
            	knowledgeBaseWrapper.setTotalRules(knowledgeBaseWrapper.getTotalRules() + 1);

                knowledgeBaseWrapperDao.write(knowledgeBaseWrapper);
                log.info(String.format("Rule '%s' compiled successfully", ruleName));
            }else {
                log.info(String.format("Rule '%s' already exists in knowledgePackage '%s'", ruleName, knowledgePackageName));
            }
        }catch(Exception e) {
            log.info(String.format("Rule '%s' failed compilation for ruleset", ruleName));
            log.error(e.getMessage(), e);
        }

        droolsRuleEvent.setProcessed(Boolean.TRUE);
        return null;
    }
    
    private KnowledgePackage lookupKnowledgePackage(String ruleSet, String knowledgePackageName) {
    	KnowledgePackage knowledgePackage = null;
    	
    	KnowledgePackage knowledgePackageTemplate = new KnowledgePackage();
    	knowledgePackageTemplate.setRuleSet(ruleSet);
    	knowledgePackageTemplate.setPackageName(knowledgePackageName);
        	
        knowledgePackage = knowledgePackageDao.read(knowledgePackageTemplate);
        if(knowledgePackage == null) {
        	knowledgePackage = new KnowledgePackage(knowledgePackageName);
        	knowledgePackage.setRuleSet(ruleSet);
        	knowledgePackage.setCreateDate(new Date(System.currentTimeMillis()));
        	knowledgePackage.setRules(new HashMap<String, DroolsRule>());
        	knowledgePackage.setTotalRules(new Integer(0));
        }
    	knowledgePackage.setLastUpdateDate(new Date(System.currentTimeMillis()));

        return knowledgePackage;
    }
    
    private void updateKnowledgePackageAndDroolsRule(DroolsRuleAddEvent droolsRuleEvent, KnowledgePackage knowledgePackage) {
    	try {
    		DroolsRule droolsRule = new DroolsRule();
    		
    		droolsRule.setRuleName(droolsRuleEvent.getRuleName());
    		droolsRule.setRuleSet(droolsRuleEvent.getRuleSet());
    		droolsRule.setCreateDate(new Date(System.currentTimeMillis()));
    		droolsRule.setOriginalResourceType(droolsRuleEvent.getOriginalResourceType());
    		
    		knowledgePackage.getRules().put(droolsRuleEvent.getRuleName(), droolsRule);
    		knowledgePackage.setTotalRules(knowledgePackage.getTotalRules() + 1);
    		knowledgePackageDao.write(knowledgePackage);
    		
            log.info(String.format("Rule '%s' wrote to the space successfully", droolsRuleEvent.getRuleName()));
        }catch(Exception e) {
            log.info(String.format("Rule '%s' failed to write to the space", droolsRuleEvent.getRuleName()));
            log.error(e.getMessage(), e);
        }
    }
    
}