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

import java.util.Arrays;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.listener.JobListenerFactoryBean;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.Chapter04.batch.DailyJobTimestamper;
import com.example.Chapter04.batch.JobLoggerListener;
import com.example.Chapter04.batch.ParameterValidator;

/**
 * @author Michael Minella
 */
@EnableBatchProcessing
//@SpringBootApplication
public class HelloWorldJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

//    public static void main(String[] args) {
//        SpringApplication.run(HelloWorldJob.class, args);
//    }

    // ?????? 4-13 JobParameters??? ????????? ????????? ??????????????? ????????? ???
    @Bean
    public CompositeJobParametersValidator validator() {
        CompositeJobParametersValidator validator 
                = new CompositeJobParametersValidator();

        DefaultJobParametersValidator defaultJobParametersValidator = 
                new DefaultJobParametersValidator(
                        new String[] { "fileName" },
                        // ?????? 4-16 ??? ????????? JobParameterIncrementer ????????????
                        // new String[] { "name", "run.id" }
                        // ?????? 4-20 DailyJobTimeStamper??? ??????????????? ????????? ???
                        new String[] { "name", "currentDate" }
                        );

        defaultJobParametersValidator.afterPropertiesSet();

        validator.setValidators(
                Arrays.asList(new ParameterValidator(),
                        defaultJobParametersValidator));

        return validator;
    }

    @Bean
    public Job job() {
        return this.jobBuilderFactory.get("basicJob")
                .start(step1())
                .validator(validator())
                // ?????? 4-16 ??? ????????? JobParameterIncrementer ????????????
                // .incrementer(new RunIdIncrementer())
                // ?????? 4-20 DailyJobTimeStamper??? ??????????????? ????????? ???
                .incrementer(new DailyJobTimestamper())
                // ?????? 4-23 JobLoggerListener??? ???????????? ???
                // .listener(new JobLoggerListener())
                // ?????? 4-26 
                .listener(JobListenerFactoryBean.getListener(new JobLoggerListener()))
                .build();
    }

    @Bean
    public Step step1() {
        return this.stepBuilderFactory.get("step1")
                .tasklet(helloWorldTasklet(null, null))
                .build();
    }

    @StepScope
    @Bean
    public Tasklet helloWorldTasklet(
            @Value("#{jobParameters['name']}") String name,
            @Value("#{jobParameters['fileName']}") String fileName) {

        return (contribution, chunkContext) -> {

            System.out.println(
                    String.format("Hello, %s!", name));
            System.out.println(
                    String.format("fileName = %s", fileName));

            return RepeatStatus.FINISHED;
        };
    }

    // ?????? ???????????? ??????????????? ?????? ???????????? ????????? ??????
//    @StepScope
//    @Bean
//    public Tasklet helloWorldTasklet() {
//
//        return (contribution, chunkContext) -> {
//            String name = (String) chunkContext.getStepContext()
//                    .getJobParameters()
//                    .get("name");
//
//            System.out.println(String.format("Hello, %s!", name));
//            return RepeatStatus.FINISHED;
//        };
//    }

    @Bean
    public Tasklet TasklethelloWorldTasklet(
            @Value("#{jobParameters['name']}") String name) {
        return (Contribution, chunkContext) -> {
            System.out.println(String.format("Hello, %s!", name));
            return RepeatStatus.FINISHED;
        };
    }

//    @Bean
//    public Step step1() {
//        return this.stepBuilderFactory.get("step1")
//                .tasklet((contribution, chunkContext) -> {
//                    System.out.println("Hello world!");
//                    return RepeatStatus.FINISHED;
//                }).build();
//    }

//	@Bean
//	public Job job() {
//
//		return this.jobBuilderFactory.get("basicJob")
//				.start(step1())
//				.validator(validator())
//				.incrementer(new DailyJobTimestamper())
////				.listener(new JobLoggerListener())
//				.listener(JobListenerFactoryBean.getListener(new JobLoggerListener()))
//				.build();
//	}
//

//    @Bean
//    public Step step1() {
//        return this.stepBuilderFactory.get("step1")
//                .tasklet(helloWorldTasklet(null, null))
//                .build();
//    }
//

}
