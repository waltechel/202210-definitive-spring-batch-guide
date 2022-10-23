package com.example.Chapter04;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.Chapter04.jobs.ChunkJob;
import com.example.Chapter04.jobs.ConditionalJob;
import com.example.Chapter04.jobs.FlowJob;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        // ChunkJob 수행
//        SpringApplication.run(ChunkJob.class, args);
        // FunctionalJob 수행
//        SpringApplication.run(ConditionalJob.class, args);
        // FlowJob 수행
          SpringApplication.run(FlowJob.class, args);

    }

}
