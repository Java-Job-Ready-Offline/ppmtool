package com.hostmdy.ppm.service;

import java.util.List;
import java.util.Optional;

import com.hostmdy.ppm.domain.ProjectTask;

public interface ProjectTaskService {
	
	List<ProjectTask> findAll(String identifier,String username);
	
	Optional<ProjectTask> findByProjectSequence(String identifier,String sequence,String username);
	
	ProjectTask createProjectTask(String identifier,ProjectTask projectTask,String username);
	
	ProjectTask updateProjectTask(String identifier, String sequence, ProjectTask projectTask, String username);
	
	void deleteProjectTask(String identifier,String sequence,String username);
}
