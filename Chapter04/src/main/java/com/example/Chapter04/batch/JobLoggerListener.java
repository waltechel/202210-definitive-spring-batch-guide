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

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;

/**
 * 잡 리스너를 작성하는 두 가지 방법이 있다. 첫번째는 JobExecutionListener 인터페이스를 구현하는 방법이다.
 */
/**
 * @author Michael Minella
 */
//예제 4-22
//public class JobLoggerListener implements JobExecutionListener {
//
//	private static String START_MESSAGE = "%s is beginning execution";
//	private static String END_MESSAGE =
//			"%s has completed with the status %s";
//
//	@Override
//	public void beforeJob(JobExecution jobExecution) {
//		System.out.println(String.format(START_MESSAGE,
//				jobExecution.getJobInstance().getJobName()));
//	}
//
//	@Override
//	public void afterJob(JobExecution jobExecution) {
//		System.out.println(String.format(END_MESSAGE,
//				jobExecution.getJobInstance().getJobName(),
//				jobExecution.getStatus()));
//	}
//}


// 예제 4-25
public class JobLoggerListener {

	private static String START_MESSAGE = "%s is beginning execution";
	private static String END_MESSAGE =
			"%s has completed with the status %s";

	@BeforeJob
	public void beforeJob(JobExecution jobExecution) {
		System.out.println(String.format(START_MESSAGE,
				jobExecution.getJobInstance().getJobName()));
	}

	@AfterJob
	public void afterJob(JobExecution jobExecution) {
		System.out.println(String.format(END_MESSAGE,
				jobExecution.getJobInstance().getJobName(),
				jobExecution.getStatus()));
	}
}
