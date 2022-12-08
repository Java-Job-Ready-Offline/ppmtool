package com.hostmdy.ppm.resource;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hostmdy.ppm.domain.ProjectTask;
import com.hostmdy.ppm.service.MapValidationErrorService;
import com.hostmdy.ppm.service.ProjectTaskService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/task")
public class ProjectTaskController {
	private final ProjectTaskService projectTaskService;
	private final MapValidationErrorService mapErrorService;

	public ProjectTaskController(ProjectTaskService projectTaskService, MapValidationErrorService mapErrorService) {
		super();
		this.projectTaskService = projectTaskService;
		this.mapErrorService = mapErrorService;
	}
	
	@PostMapping("/create/{identifier}")
	public ResponseEntity<?> createProjectTask(@PathVariable String identifier,
			@Valid @RequestBody ProjectTask projectTask,
			BindingResult result,Principal principal){
		ResponseEntity<?> errorResponse = mapErrorService.validate(result);
		
		if(errorResponse != null)
			return errorResponse;
		
		ProjectTask createdProjectTask = projectTaskService.createProjectTask(identifier, projectTask,principal.getName());
	    return new ResponseEntity<ProjectTask>(createdProjectTask,HttpStatus.CREATED);
	}
	
	@PatchMapping("/update/{identifier}/{sequence}")
	public ResponseEntity<?> updateProjectTask(@PathVariable String identifier,
			@PathVariable String sequence,@Valid @RequestBody ProjectTask projectTask,
			BindingResult result,Principal principal){
		ResponseEntity<?> errorResponse = mapErrorService.validate(result);
		
		if(errorResponse != null)
			return errorResponse;
		
		ProjectTask updatedProjectTask = projectTaskService.updateProjectTask(identifier, sequence, projectTask,principal.getName());
		return new ResponseEntity<ProjectTask>(updatedProjectTask,HttpStatus.OK);
		
	}
	
	@GetMapping("/all/{identifier}")
	public ResponseEntity<?> findAll(@PathVariable String identifier,Principal principal){
		List<ProjectTask> pTList = projectTaskService.findAll(identifier, principal.getName());
		
		if(pTList.isEmpty())
			return new ResponseEntity<String>("No projects are found in your account",HttpStatus.NOT_FOUND);
		
		return new ResponseEntity<List<ProjectTask>>(pTList,HttpStatus.OK);
		
	}
	
	@GetMapping("/{identifier}/{sequence}")
	public ResponseEntity<?> findByProjectSequence(@PathVariable String identifier,
			@PathVariable String sequence,Principal principal){
		Optional<ProjectTask> pTOptional = projectTaskService.findByProjectSequence(identifier, sequence, principal.getName());
		
		if(pTOptional.isEmpty())
			return new ResponseEntity<String>("projectTask with id="+sequence+" is not found",HttpStatus.NOT_FOUND);
		
		return new ResponseEntity<ProjectTask>(pTOptional.get(),HttpStatus.OK);
		
	}
	
	@DeleteMapping("/delete/{identifier}/{sequence}")
	public ResponseEntity<String> deleteProjectTask(@PathVariable String identifier,
			@PathVariable String sequence,Principal principal){
		projectTaskService.deleteProjectTask(identifier, sequence, principal.getName());
		
		return new ResponseEntity<String>("projectTask with id="+sequence+" is deleted",HttpStatus.OK);
		
	}
	
	
}
