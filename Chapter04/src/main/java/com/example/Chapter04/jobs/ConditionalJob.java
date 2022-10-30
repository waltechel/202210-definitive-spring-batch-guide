/**
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
package com.example.Chapter04.jobs;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import com.example.Chapter04.batch.RandomDecider;

/**
 * @author Michael Minella
 */
@EnableBatchProcessing
public class ConditionalJob {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	public Tasklet passTasklet() {
		return (contribution, chunkContext) -> {
			return RepeatStatus.FINISHED;
//			throw new RuntimeException("Causing a failure");
		};
	}

	@Bean
	public Tasklet successTasklet() {
		return (contribution, context) -> {
			System.out.println("Success!");
			return RepeatStatus.FINISHED;
		};
	}

	@Bean
	public Tasklet failTasklet() {
		return (contribution, context) -> {
			System.out.println("Failure!");
			return RepeatStatus.FINISHED;
		};
	}

	@Bean
	public Job job() {
		return this.jobBuilderFactory.get("conditionalJob")
		        // 예제 4-49 Completed 상태로 잡 종료하기
		        .start(firstStep())
				.on("FAILED").end()
				.from(firstStep()).on("*").to(successStep())
				.end()
				.build();
				// 예제 4-48 RandomDecider 구성 사례
//		        .start(firstStep())
//				.next(decider())
//				.from(decider())
//				.on("FAILED").to(failureStep())
//				.from(decider())
//				.on("*").to(successStep())
//				.end()
//				.build();
	}

	@Bean
	public Step firstStep() {
		return this.stepBuilderFactory.get("firstStep")
				.tasklet(passTasklet())
				.build();
	}

	@Bean
	public Step successStep() {
		return this.stepBuilderFactory.get("successStep")
				.tasklet(successTasklet())
				.build();
	}

	@Bean
	public Step failureStep() {
		return this.stepBuilderFactory.get("failureStep")
				.tasklet(failTasklet())
				.build();
	}

	@Bean
	public JobExecutionDecider decider() {
		return new RandomDecider();
	}
}
