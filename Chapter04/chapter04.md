---
theme: default
_class: lead
paginate: true
color: black
backgroundColor: #EEEEEE
style: |
  section {
    font-family: 'Gowun Batang', serif !important;
  }
backgroundImage: url("images/template_content.jpg")
marp: true
---

![bg](images/template_title.jpg)

# Chapter 04 잡과 스텝 이해하기

---

#### 잡 파라미터에 접근하기

- `ChunkContext`
- Late binding : 이 스코프 각각의 기능은 스텝의 실행 범위나 잡의 실행 범위에 들어갈 때까지 빈 생성을 지연시키는 것이다.

#### 잡 파라미터 유효성 검증하기

이 재정의된 반환 타입이 `void`이므로 `JobParametersInvalidException`이 발생하지 않는다면 유효성 검증이 통과됐다고 판단한다. 

`CompositeJobParametersValidator`를 사용해 원하는 두 개의 유효성 검증기를 사용할 수도 있다.

#### 잡 파라미터 증가시키기

`JobParameterIncrementer`를 사용하면 잡에서 사용할 파라미터를 고유하게 생성할 수 있도록 해준다. 이를 구현하면 기본적으로 파라미터 이름이 `run.id`인 long 파라미터의 값을 증가시킨다. 

```java
@Bean
public Job job(){
    return this.jobBuilderFactory.get("basicJob")
                .start(step1())
                .validator(validator())
                .incrementer(new RunIdIncrementer())
                .build();
}
```

날짜를 주게 할수도 있다.

---

### 4.2.3 잡 리스너 적용하기

잡 리스너를 작성하는 두 가지 방법이 있다.
- 첫 번째는 org.springframework.batch.core.JobExecutionListener 인터페이스를 구현하는 방법

```java
public class JobLoggerListener implements JobExecutionListener {
    @Override
    public void beforeJob(JobExecution jobExecution) {}
    @Override
    public void afterJob(JobExecution jobExecution) {}
}
```

```java
@Bean
public Job job() {
    return this.jobBuilderFactory.get("basicJob")
                .start(step1())
                .listener(new JobLoggerListener())
                .build();
}
```

---

- 두 번째는 어노테이션을 사용하는 방법이다

```java
public class JobLoggerListener {
    @BeforeJob
    public void beforeJob(JobExecution jobExecution) {}
    @AfterJob
    public void afterJob(JobExecution jobExecution) {}
}
```

```java
@Bean
public Job job() {
    return this.jobBuilderFactory.get("basicJob")
                .start(step1())
                .listener(JobListenerFactoryBean.getListener(
                    new JobLoggerListener()
                ))
                .build();
}
```

---

### 4.2.4 ExecutionContext

EcecutionCOntext는 기본저긍로 배치 잡의 세션이다. 한 가지 특이한 점은ㅇ 잡을 다루는 과정에서 여러 개으ㅏㅣExecutionContext가 생성될 수 있다는 점이다. 

### 4.2.5 EcecutionConetext 조작하기

---

## 4.3 스텝 알아보기

잡이 전체적인 처리를 정의한다면 스텝은 잡의 구성요소를 담당하낟. 

스베은 모든 단위 조각이ㅡ 조각이다. 자체적으로 입력을 처리하고, 자체적인 처리기를 가질 수 있으며 자체적으로 출력을 처리한다. 

---

### 4.3.1 태스크릿 처리와 청크 처리 비교. 

스프링 배치는 두 가지 유형의 처리 모델을 모두 지원한다.

- 태스크릿 모델은 Tasklet 인터페이스를 사용해 개발자는 Tasklet.execute 메서드가 RepeatStatus.FINISHED를 반환할 때까지 트랜잭션 범위 내에서 반복적으로 실행되는 코드 블록을 만들 수 있다.
- 청크 모델은 최소한 2~3개의 주요 컴포넌트(ItemReader, ItemProcessor, ItemWriter)로 구성된다.

---

### 4.3.2 스텝 구성

스프링 배치는 기본적으로 각 스텝이 상태와 다음 상태로 이어지는 전이의 모음을 나타내는 상태 머신이다. 

#### 태스크릿 스텝

태스크릿 스텝을 구현하는 방법에는 두 가지 유형이 있따. 하나는 스프링 배치가 제공하는 MethodInvokingTaskletAdapter를 사용해서 사용자 코드를 태스크릿 스텝으로 정의하는 것이다.
다른 하나는 Tasklet 인터페이스를 구현하는 것이다. 

---

### 4.3.3 그 밖의 여러 다른 유형의 태스크릿 이해하기

스프링 배치는 CallableTaskletAdapter, MethodInvokingTaskletAdapter, SystemCommandTasklet의 세 가지의 서로 다른 Tasklet 구현체를 제공한다.

#### CallableTaskletAdapter

Callable<V> 인터페이스를 구현하게 해주는 어댑터다.

```java
@Bean
public Step methodInvokingStep(){
    return this.stepBuilderFactory.get("methodInvokingStep")
                .tasklet(methodInvokingTasklet(null))
                .build();
}

@StepScope
@Bean
public MethodInvokingTaskletAdapter methodInvokingTasklet(
    @value("#{jobParameters['message']}") String message
){
    MethodInvokingTaskletAdapter methodInvokingTaskletAdapter = 
        new MethodInvokingTaskletAdapter();

        methodInvokingTaskletAdapter.setTargetObject(service());
        methodInvokingTaskletAdapter.setTargetMethod("serviceMethod");
        methodInvokingTaskletAdapter.setArguments(new String[] {message});

        return methodInvokingTaskletAdapter;
}
```

---

#### SystemCommandTasklet

지정한 시스템 명령은 비동기로 실행된다. 

```java
@Bean
public SystemCommandTasklet systemCommandTasklet(){
    SystemCommandTasklet systemCommandTasklet = new SystemCommandTasklet();

    systemCommandTasklet.setCommand("rm -rf /tmp.txt");
    systemCommandTasklet.setTimeout(5000);
    systemCommandTasklet.setInterruptOnCancel(true);
    
    return systemCommandTasklet;
}
```

---

#### 청크 기반 스텝

```java
@Bean
public Step step1(){
    return this.stepBUilderFactory.get("step1")
                .<String, String>chunk(10)
                .reader(itemReader(null))
                .writer(itemWriter(null))
                .build();
}
```

```java
@Bean
@StepScope
public FlatFileItemReader<String> itemReader(
    @Value("#{jobParameters['inputFile']}") Resource inputFile
){
    return new FlatFileItemReaderBuilder<String>()
                .name("itemReader")
                .resource(inputFile)
                .lineMapper(new PassThroughLineMapper())
                .build();
}
```

```java
@Bean
@StepScope
public FlatFileItemWriter<String> itemWriter(
    @Value("#{jobParameters['outputFile']}") Resource outputFile
){
    return new FlatFileItemWriterBuilder<String>()
                .name("itemWriter")
                .resource(outputFile)
                .lineAggregator(new PassThroughLineAggregator<>())
                .build();
}
```

---

CompletionPolicy 인터페이스는 청크의 완료 여부를 결정할 수 있는 결정 로직을 구현할 수 있게 해준다. 기본저긍로 SImpleCompletionPolicy를 사용한다. 이는 처리된 아이템 개수를 세는데, 이 개수가 미리 구성해둔 임곗값에 도달하면 청크 완료로 표시한다. 그 외에는 TimeoutTermiationPolicy이다. 이 구현체를 사용해 타임아웃 값을 구성한다. 

```java
@Bean
public CompletionPolicy completionPolicy(){
    CompositeCompletionPolicy policy = new CompositeCompletionPolicy();
    policy.setPolicies(
        new CompletionPolicy[]{
            new TimeoutTerminationPolicy(3),
            new SimpleCompletionPolicy(1000)
        }
    );
    return policy;
}
```

- 랜덤하게 청크 크기를 지정하도록 작성한 CompletionPolicy 구현체

```java
public class RamdomChunkSizePolicy implements CompletionPolicy {
    
    @Override
    public boolean isComplete(RepeatContext context, RepeatStatus result){
        if(RepeatStatus.FINISHED == result){
            return true;
        }else{
            return isComplete(context);
        }
    }

    @Override
    public boolean isComplete(RepeatContext context){
        return this.totalProcessed >= chunksize;
    }

    @Override
    public RepeatContext start(RepeatContext parent){
        this.chunksize = random.nextInt(20);
        this.totalProcessed = 0;
        System.out.println("the chunk size has been set to " + this.chunksize());
        return parent;
    }

    @Override
    public void update(RepeatContext context){
        this.totalProcessed++;
    }
}
```

---

```java
@Bean
public Step chunkStep(){
    return this.stepBuilderFactory.get("chunkstep")
                .<String, String>chunk(randomCompletionPolicy())
                .reader(itemReader())
                .writer(itemWriter())
                .build();
}
```

---

#### 스텝 리스너

```java
public class LoggingStepStartStopListener {
    @BeforeStep
    public void beforeStep(StepExecution stepExcecution){}
    @AfterStep
    public ExitStatus afterStep(StepExecution stepExcecution){
        return stepExcecution.getExitStatus();
    }
}
```

---

### 4.3.4 스텝 플로우

이런 상황에서는 스프링 배치의 조건 로직을 사용한다.

#### 조건 로직

스텝을 다른 순서로 실행하는 것은 전이를 구성하면 된다.

```java
@Bean
public Job job(){
    return this.jobBuilderFactory.get("conditionalJob")
                .start()
                .on("FAILED").to(failureStep())
                .from(firstStep()).on("*").to(successStep())
                .end()
                .build();
}
```

`ExitStatus`의 값은 문자열이기 때문에 와일드 카드를 사용하면 모든 경우의 수를 다 받을 수 있다.

---

스프링 배치는 `JobExecutionDecider` 인터페이스를 구현하여 `JobExecution`과 `StepExecution`을 아규먼트로 전달받고 `FlowExecutionStatus`를 반환한다. 

---

#### 잡 종료하기

스프링 배치에서 프로그래밍 방식으로 잡을 종료할 때 세 가지 상태로 종료할 수 있다.
- Completed : 성공적으로 종료됐음을 의미한다.
- Failed : 성공적으로 완료되지 않았음을 의미한다.
- Stopped : 다시 시작할 수 있다.

---

![bg](images/template_title.jpg)

# 감사합니다!
