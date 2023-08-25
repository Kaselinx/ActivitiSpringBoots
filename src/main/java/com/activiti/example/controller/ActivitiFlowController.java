package com.activiti.example.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import com.activiti.example.config.ActivitiConfiguration;

@RestController
@RequestMapping("/Activiti")
public class ActivitiFlowController {
	private Logger logger = LoggerFactory.getLogger(ActivitiConfiguration.class);

	// create the instance of RepositoryService
	@Autowired
	private RepositoryService repositoryService;
	
	@Autowired
	private RuntimeService runTimeService;

	
	/**
	 * Deploy a new process 
     * @param Resource path.. eg: "process/Process1.bpmn"
     * @param Process unique name
     */
	@RequestMapping(value = "/deployment", method = RequestMethod.POST)
	public String deployment(String resourceFullPath, String flowUniqueName) {
		try {

			// create deploy builder service for deploy flow
			DeploymentBuilder builder = repositoryService.createDeployment();
			// add resource path.. eg: process/Process1.bpmn
			builder.addClasspathResource(resourceFullPath);
			// eg first_activiti_process
			builder.name(flowUniqueName);
			Deployment deployment = builder.deploy();
			
			// get the flow Id
			String flowId = deployment.getId();
			return flowId;
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			return ex.toString();
		}
	}
	
	/**
     * remove a deployed process
     *
     * @param DeploymentId to remove a process
     */
	@RequestMapping(value = "/removeDeployment", method = RequestMethod.POST)
	public boolean removeDeployment(String deploymentId) {
		try {
			// create deploy builder service for deploy flow
			DeploymentBuilder builder = repositoryService.createDeployment();
			repositoryService.deleteDeployment(deploymentId);
			return true;
		}
		
		catch (Exception ex) {
			logger.error(ex.getMessage());
			return false;
		}
	}
	/**
     * query list of deployed processe by unique name.
     * 
     * @param Process unique name
     */
	@RequestMapping(value = "/queryProcessAllDefinition", method = RequestMethod.POST)
	public List<ProcessDefinition> queryProcessAllDefinition(String flowUniqueName) {
		
		 // object for query the definition of processes
		 ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
		 
		 //all definitions for deployed processes.
		 List<ProcessDefinition> definitionList = processDefinitionQuery.processDefinitionKey(flowUniqueName)
	                .orderByProcessDefinitionVersion()
	                .desc()
	                .list();

		return definitionList;
	}
	
	/**
     * start a new process by process name. 
     * 
     * @param Process unique name
     */
	public String startProcess(String flowUniqueName) {
		
		try {
			//start a new process instance
			ProcessInstance processInstance = runTimeService.startProcessInstanceById(flowUniqueName);
		    
		    logger.info("ProcessDefinition ID:" + processInstance.getProcessDefinitionId());
		    logger.info("processInstance ID:" + processInstance.getId());
		    logger.info("Activity ID:" + processInstance.getActivityId());
		        
		    return processInstance.getId();

		}
		catch (Exception ex) {
			logger.error(ex.getMessage());
		}
		return null;
	}
		

}
