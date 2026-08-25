package com.lab.taskmanager.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lab.taskmanager.model.Task;

@RestController
@RequestMapping("api/tasks")
public class TaskController {

	@GetMapping
	public List<Task> getAllTasks() {
		return List.of(
				new Task(1L, "Set up development environment", "COMPLETED"),
				new Task(2L, "Build first REST endpoint", "IN_PROGRESS"),
				new Task(3L, "Integrate Spring Data JPA", "PENDING")
				
				);

	}
}
