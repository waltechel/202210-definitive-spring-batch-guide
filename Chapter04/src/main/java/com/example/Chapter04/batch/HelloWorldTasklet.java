/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.Chapter04.batch;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author Michael Minella
 */
public class HelloWorldTasklet implements Tasklet {

	private static final String HELLO_WORLD = "Hello, %s";

	@Value("#{jobParameters['name']}")
	private String name;

	/**
	 * contribution : 아직 커밋되지 않은 현재 트랜잭션에 대한 정보
	 * chunkContext : 실행 시점의 잡 상태
	 */
	@Override
	public RepeatStatus execute(StepContribution contribution,
			ChunkContext chunkContext) {

		ExecutionContext jobExecutionContext =
				chunkContext.getStepContext()
						.getStepExecution()
//						.getJobExecution()
						.getExecutionContext();

		jobExecutionContext.put("user.name", name);

		System.out.println(String.format(HELLO_WORLD, name));

		return RepeatStatus.FINISHED;
	}
}
