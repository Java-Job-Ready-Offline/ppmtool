package com.hostmdy.ppm.service.serviceimpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hostmdy.ppm.domain.Backlog;
import com.hostmdy.ppm.domain.Project;
import com.hostmdy.ppm.domain.User;
import com.hostmdy.ppm.exception.ProjectIdException;
import com.hostmdy.ppm.exception.ProjectNotFoundException;
import com.hostmdy.ppm.repository.BacklogRepository;
import com.hostmdy.ppm.repository.ProjectRepository;
import com.hostmdy.ppm.repository.UserRepository;
import com.hostmdy.ppm.service.ProjectService;

@Service
public class ProjectServiceImpl implements ProjectService{
	
	private final ProjectRepository projectRepository;
	private final UserRepository userRepository;
	private final BacklogRepository backlogRepository;

	@Autowired
	public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository, BacklogRepository backlogRepository) {
		super();
		this.projectRepository = projectRepository;
		this.userRepository = userRepository;
		this.backlogRepository = backlogRepository;
	}

	@Override
	public Project saveOrUpdate(Project project,String username) {
		// TODO Auto-generated method stub
		
		User user = userRepository.findByUsername(username).get();
		String projectIdentifier = project.getProjectIdentifier().toUpperCase();
		Long projectId = project.getId();
		
		if(projectId != null) {
			Optional<Project> existedProjectOpt = projectRepository.findById(projectId);
			
			if(existedProjectOpt.isPresent() && 
				(!existedProjectOpt.get().getProjectLeader().equals(username)))
					throw new ProjectNotFoundException("Project not found in your account");
			
			if(existedProjectOpt.isEmpty())
				throw new ProjectNotFoundException("Project with id="+projectId
						+" does not exist.Therefore,update can be undone");
			
			//project - user
			project.setUser(user);
			user.getProjects().add(project);
			
			//project - backlog
			Backlog backlog = backlogRepository.findByProjectIdentifier(projectIdentifier).get();
			project.setBacklog(backlog);
			backlog.setProject(project);
			
			project.setProjectLeader(username);
			project.setProjectIdentifier(projectIdentifier);
			
			return projectRepository.save(project);
			
		}
		
		Backlog backlog = new Backlog();
		backlog.setProjectIdentifier(projectIdentifier);
		
		//project - user
		project.setUser(user);
		user.getProjects().add(project);
		
		//project backlog
		project.setBacklog(backlog);
	    backlog.setProject(project);
	    
	    project.setProjectLeader(username);
	    project.setProjectIdentifier(projectIdentifier);
		
		return projectRepository.save(project);
	}

	@Override
	public List<Project> findAll(String username) {
		// TODO Auto-generated method stub
		return projectRepository.findByProjectLeader(username);
	}

	@Override
	public Optional<Project> findByIdentifier(String identifier,String username) {
		// TODO Auto-generated method stub
		Optional<Project> projectOptional = projectRepository.findByProjectIdentifier(identifier);
		
		if(projectOptional.isEmpty())
			throw new ProjectIdException("Project with id="+identifier+" does not exist");
		
		if(projectOptional.isPresent() && (!projectOptional.get().getProjectLeader().equals(username)))
			throw new ProjectNotFoundException("Project is not found in your account");
		
		return projectOptional;
	}

	@Override
	public void flashDelete(String identifier,String username) {
		// TODO Auto-generated method stub
		Optional<Project> projectOptional = projectRepository.findByProjectIdentifier(identifier);
		
		if (projectOptional.isEmpty())
			throw new ProjectIdException("Project with id=" + identifier + " does not exist");

		Project project = projectOptional.get();
		
		if (projectOptional.isPresent() && (!projectOptional.get().getProjectLeader().equals(username)))
			throw new ProjectNotFoundException("Project is not found in your account");
		
		project.setStatus("deleted");
		project.getBacklog().setStatus("deleted");
		
		projectRepository.save(project);
	}
	
	
	
	

}
