package com.hostmdy.ppm.service.serviceimpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.hostmdy.ppm.domain.Backlog;
import com.hostmdy.ppm.domain.ProjectTask;
import com.hostmdy.ppm.domain.Status;
import com.hostmdy.ppm.exception.ProjectIdException;
import com.hostmdy.ppm.exception.ProjectNotFoundException;
import com.hostmdy.ppm.repository.BacklogRepository;
import com.hostmdy.ppm.repository.ProjectRepository;
import com.hostmdy.ppm.repository.ProjectTaskRepository;
import com.hostmdy.ppm.service.ProjectTaskService;

@Service
public class ProjectTaskServiceImpl implements ProjectTaskService{

	private final ProjectTaskRepository projectTaskRepository;
	private final BacklogRepository backlogRepository;
	
	public ProjectTaskServiceImpl(ProjectTaskRepository projectTaskRepository, ProjectRepository projectRepository, BacklogRepository backlogRepository) {
		super();
		this.projectTaskRepository = projectTaskRepository;
		this.backlogRepository = backlogRepository;
	}

	@Override
	public List<ProjectTask> findAll(String identifier,String username) {
		// TODO Auto-generated method stub
		Optional<Backlog> backlogOpt = backlogRepository.findByProjectIdentifier(identifier);

		if (backlogOpt.isEmpty())
			throw new ProjectIdException("backlog with id=" + identifier + " does not exist");

		Backlog backlog = backlogOpt.get();

		if (!backlog.getProject().getProjectLeader().equals(username))
			throw new ProjectNotFoundException("backlog is not found in your account");
		
		return projectTaskRepository.findByProjectIdentifierOrderByPriority(identifier);
	}

	@Override
	public Optional<ProjectTask> findByProjectSequence(String identifier,String sequence,String username) {
		// TODO Auto-generated method stub

		Optional<Backlog> backlogOpt = backlogRepository.findByProjectIdentifier(identifier);

		if (backlogOpt.isEmpty())
			throw new ProjectIdException("backlog with id=" + identifier + " does not exist");

		Backlog backlog = backlogOpt.get();

		if (!backlog.getProject().getProjectLeader().equals(username))
			throw new ProjectNotFoundException("backlog is not found in your account");
		
		return projectTaskRepository.findByProjectSequence(sequence);
	}

	@Override
	public ProjectTask createProjectTask(String identifier, ProjectTask projectTask,String username) {
		// TODO Auto-generated method stub
		Optional<Backlog> backlogOpt = backlogRepository.findByProjectIdentifier(identifier);
		
		if(backlogOpt.isEmpty())
			throw new ProjectIdException("backlog with id="+identifier+" does not exist");
		
		Backlog backlog = backlogOpt.get();
		
		if(!backlog.getProject().getProjectLeader().equals(username))
			throw new ProjectNotFoundException("backlog is not found in your account");
		
		projectTask.setProjectIdentifier(identifier.toUpperCase());
		
		Integer pTSequence =  backlog.getPTSequence();
		pTSequence++;
		backlog.setPTSequence(pTSequence);
		
		projectTask.setProjectSequence(identifier+"-"+pTSequence);
			
		
		if(projectTask.getPriority()==0)
			projectTask.setPriority(5);
		
		if(projectTask.getStatus()==null)
			projectTask.setStatus(Status.TODO);
		
		projectTask.setBacklog(backlog);
		backlog.getProjectTasks().add(projectTask);
		
		return projectTaskRepository.save(projectTask);
	}

	@Override
	public ProjectTask updateProjectTask(String identifier, String sequence, ProjectTask projectTask, String username) {
		// TODO Auto-generated method stub
		Optional<Backlog> backlogOpt = backlogRepository.findByProjectIdentifier(identifier);

		if (backlogOpt.isEmpty())
			throw new ProjectIdException("backlog with id=" + identifier + " does not exist");

		Backlog backlog = backlogOpt.get();

		if (!backlog.getProject().getProjectLeader().equals(username))
			throw new ProjectNotFoundException("backlog is not found in your account");

		Optional<ProjectTask> pTOptional = projectTaskRepository.findByProjectSequence(sequence);
		
		if (pTOptional.isEmpty())
				throw new ProjectIdException("projectTask with id="+sequence+" does not exist");
		
		//project - backlog
		projectTask.setId(pTOptional.get().getId());
		projectTask.setBacklog(backlog);
		backlog.getProjectTasks().add(projectTask);
		
		return projectTaskRepository.save(projectTask);
	}

	@Override
	public void deleteProjectTask(String identifier, String sequence, String username) {
		// TODO Auto-generated method stub
		Optional<Backlog> backlogOpt = backlogRepository.findByProjectIdentifier(identifier);

		if (backlogOpt.isEmpty())
			throw new ProjectIdException("backlog with id=" + identifier + " does not exist");

		Backlog backlog = backlogOpt.get();

		if (!backlog.getProject().getProjectLeader().equals(username))
			throw new ProjectNotFoundException("backlog is not found in your account");

		Optional<ProjectTask> pTOptional = projectTaskRepository.findByProjectSequence(sequence);
		
		if (pTOptional.isEmpty())
				throw new ProjectIdException("projectTask with id="+sequence+" does not exist");
		
		projectTaskRepository.deleteById(pTOptional.get().getId());
	}

}
