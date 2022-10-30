---
theme: default
_class: lead
paginate: true
color: black
backgroundColor: #EEEEEE
style: |
  section {
    font-family: 'Gowun Batang', serif !important;
    font-size: 26px
  }
  img[alt~="center"] {
    display: block;
    margin: 0 auto;
  }
backgroundImage: url("images/template_content.jpg")
marp: true
author: dongjun lee
class: |
  .center{
    display: inline-block;
  }
size: 16:9
title: 잡과 스텝 이해하기
---

![bg](images/template_title.jpg)

# Chapter 06 잡 실행하기

---

## 6.1 스프링 부트로 배치 잡 시작시키기

스프링 부트는 CommandLineRunner와 ApplicationRunner라는 두 가지 메커니즘을 이용해 실행 시 로직을 수행한다. 스프링 부트를 스프링 배치와 함께 사용할 때는 JobLauncherCommandLineRunner라는 특별한 CommandLineRunner가 사용된다.

스프링 부트가 ApplicationContext 내에 구성된 모든 CommandLineRunner를 실행할 때, 클래스패스에 spring-boot-starter-batch가 존재한다면 JobLauncherCommandLineRunner는 컨텍스트 내에서 찾아낸 모든 잡을 실행한다. 

spring.batch.job.enabled 를 false로 설정하고 해당 코드를 실행하면 기동 시에 어떤 잡도 실행되지 않는다. 컨텍스트는 생성된 후 즉시 종료된다. 

---

```java
public static void main(String[] args){
  SpringApplication application = new SpringApplication(NoRunJob.class);
  Properties properties = new Properties();
  properties.put("spring.batch.job.enabled", false);
  application.setDefaultProperties(properties);
  application.run(args);
}

```

---

컨텍스트에 여러 잡이 정의돼 있는 상태에서 기동 시에 특정한 잡만 실행하고 싶은 경우 부모 잡이 자식 잡을 실행하므로 스프링 부트를 통해 기동할 때는 부모 잡만 실행하면 된다. 이럴 때는 spring.batch.job.names 프로퍼티를 이용해 애플리케이션 기동 시에 실행할 잡을 구성할 수 있다.

---

## 6.2 REST 방식으로 잡 실행하기

JobLauncher 인터페이스는 잡을 실행하는 인터페이스이다. JobLauncher 인터페이스에는 run 메서드 하나만 존재한다. 

스프링 배치는 기본저긍로 유일한 JobLauncher 관련 구현체인 SimpleJobLauncher를 제공한다. 

jobLauncher 인터페이스는 잡을 동기 방식으로 실행할지 비동기 방식으로 실행할지와 관련된 메커니즘을 제공하진 않으며 특별히 권장하는 방법이 있는 것도 아니다. 하지만 JobLauncher가 사용하는 TaskExecutor를 적절하게 구현해 실행 방식을 결정할 수는 있다. 기본적으로 SimpleJobLauncher는 동기식 TaskExecutor를 사용해 잡을 동기식으로 실행한다. 

---

```java
@EnableBatchProcessing
@RestController
public class JobLaunchingController {
  
  @Autowired
  private JobLauncher jobLauncher;
  
  @Autowired
  private ApplicationContext context;
  
  @PostMapping("/run")
  public ExitStatus runJob(@RequestBody JobLaunchRequest request) throws Exception {
    Job job this.context.getBean(request.getName(), Job.class);
    return this.jobLauncher.run(job, request.getJobParameters()).getExitStatus();
  }
}
```

---

```powershell
$ curl -H "Content-Type: application/json" -X POST -d "{
  "name": "job",
  "jobParmaters":{
    "foo":"bar",
    "bas":"quix"
  }
}" http://localhost:8080/run {
  "exitCode":"COMPLETED",
  "exitDescription":"",
  "running":false
}
```

---

아예 다르게 표현할 수도 있다.

```java
@EnableBatchProcessing
@RestController
public class JobLaunchingController {
  
  @Autowired
  private JobLauncher jobLauncher;
  
  @Autowired
  private ApplicationContext context;
  
  @PostMapping("/run")
  public ExitStatus runJob(@RequestBody JobLaunchRequest request) throws Exception {
    Job job this.context.getBean(request.getName(), Job.class);
    JobParameters jobParameters = new JobParametersBuilder(request.getJobParameters(), this.jobExplorer)
                                      .getNextJobParameters(job)
                                      .toJobParameters();
    return this.jobLauncher.run(job, jobParameters).getExitStatus();
  }
}
```
---

### 6.2.1 쿼츠를 이용해 스케줄링하기

쿼츠 오픈소스 스케줄러는 Scheduler, Trigger, Job 이라는 세 가지 주요 컴포넌트를 가진다. 

다음 절차를 수행해서 쿼츠를 스프링 배치와 통합해보도록 한다. 
- 올바른 스타터를 사용해 프로젝트를 생성한다. 
- 스프링 배치 잡을 작성한다.
- QuartzJobBean을 사용해 스프링 배치 잡을 기동하는 쿼츠 잡을 작성한다. 
- 쿼츠 JobDetail을 생성하도록 스프링이 제공하는 JobDetailBean을 구성한다.
- 잡 실행 시점을 정의하도록 트리거를 구성한다. 

---

```java
public class BatchScheduledJob extends QuartzJobBean{
  @Autowired
  private Job job;

  @Autowired
  private JobExplorer jobExplorer;

  @Autowired
  private JobLauncher jobLauncher;

  @Override
  protected void executeInternal(JobExecutionContext context){
    JobParameters jobParameters = new JobParametersBuilder(this.jobExplorer)
                                      .getNextJobParameters(this.job)
                                      .toJobParameters();
    try{
      this.jobLauncher.run(this.job, jobParameters);
    }catch(Exception e){
      e.printstacktrace();
    }
  }
}
```

---

```java
@Configuration
public class QuartzConfiguration{
  @Bean
  public JobDetail quartzJobDetail(){
    return JobBuilder.newJob(BatchScheduledJob.class)
                    .storeDurably()
                    .build();
  }
  @Bean
  public Trigger jobTrigger(){
    SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                                            .withIntervalInSeconds(5).withRepeatCount(4);
    return TriggerBuilder.newTrigger()
                        .forJob(quartzJobDetail())
                        .withSchedule(scheduleBuilder)
                        .build();
  }
}
```

---

## 6.3 잡 중지하기

### 6.3.1 자연스러운 완료

### 6.3.2 프로그래밍적으로 중지하기

#### 중지 트랜지션 사용하기

#### StepExecution을 사용해 중지하기

좀 더 효율적인 접근 방싱이 있다. AfterStep 대신에 BeforeStep을 사용하도록 변경해 StepExecution을 가져온다.

### 6.3.3 오류 처리

#### 잡 실패

---

## 6.4 재시작 제어하기

### 6.4.1 잡의 재시작 방지하기

```java
@Bean
public Job transactionJob() {
    return this.jobBuilderFactory.get("transactionJob")
                .preventRestart()
                .start(importTransactionFileStep())
                .next(applyTransactionStep())
                .next(generateAccountSummaryStep())
                .build();
}
```

---

### 6.4.2 재시작 횟수를 제한하도록 구성하기

```java
@Bean
public Step importTransactionFileStep(){
    return this.stepBuilderFactory.get("importTransactionFileStep")
                .startLimit(2)
                .<Transaction, Transaction>chunk(100)
                .reader(transactionReader())
                .writer(transactionWriter(null))
                .allowStartIfComplete(true)
                .listener(transactionReader())
                .build();
}
```

---

### 6.4.3 완료된 스텝 재실행하기

스프링 배치 특징 중 하나는 프레임워크를 사용하면 동일한 파라미터로 잡을 한 번만 성공적으로 실행할 수 있다는 점이다.

```java
@Bean
public Step importTransactionFileStep(){
    return this.stepBuilderFactory.get("importTransactionFileStep")
                .allowStartIfComplete(true)
                .<Transaction, Transaction>chunk(100)
                .reader(transactionReader())
                .writer(transactionWriter(null))
                .allowStartIfComplete(true)
                .listener(transactionReader())
                .build();
}
```

---

![bg](images/template_title.jpg)

# 감사합니다!
