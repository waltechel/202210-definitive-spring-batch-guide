package com.example.Chapter04.batch;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.lang.Nullable;

// 예제 4-27
public class HelloWorld implements Tasklet {

    private static final String HELLO_WORLD = "Hello, %s";

    @Override
    @Nullable
    public RepeatStatus execute(StepContribution step, ChunkContext context) throws Exception {

        String name = (String) context.getStepContext()
                .getJobParameters()
                .get("name");

        ExecutionContext jobContext = context.getStepContext()
                                            .getStepExecution()
                                            // 예제 4-27 잡의 ExecutionContext에 name 데이터 추가하기
                                            // .getJobExecution()
                                            // .getExecutionContext();
                                            // 예제 4-28 스텝의 ExecutionContext에 name 데이터 추가하기
                                            .getExecutionContext();
        jobContext.put("user.name", name);
        
        System.out.println(String.format(HELLO_WORLD, name));

        return RepeatStatus.FINISHED;
    }

}
