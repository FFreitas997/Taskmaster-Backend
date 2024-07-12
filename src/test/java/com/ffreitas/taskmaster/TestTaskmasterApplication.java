package com.ffreitas.taskmaster;

import org.springframework.boot.SpringApplication;

public class TestTaskmasterApplication {

	public static void main(String[] args) {
		SpringApplication.from(TaskmasterApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
