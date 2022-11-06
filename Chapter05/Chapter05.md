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

# Chapter 05 JobRepository와 메타데이터

---

## 5.1 JobRepository란?

스프링 배치 내의 JobRepository는 둘 중 하나를 말한다. 첫 번째는 JobRepository 인터페이스이며, 두 번째는 JobRepository 인터페이스를 구현해 데이터를 저장하는 데 사용하는 데이터 저장소다.

---

### 5.1.1 관계형 데이터베이스 사용하기

JobRepository 내에는 여섯 개의 테이블이 존재한다.

- BATCH_JOB_INSTANCE : 잡의 논리적 실행
- BATCH_JOB_EXECUTION : 배치 잡의 실제 실행 기록
  - 여기에서 CREATE_TIME, START_TIME, END_TIME, STATUS를 찾아볼 수 있따.
- BATCH_JOB_EXECUTION_CONTEXT : JobExecution의 ExecutionContext를 저장한다.
- BATCH_JOB_EXECUTION_PARAMS : 잡에 전달된 모든 파라미터가 테이블에 저장된다.
- BATCH_STEP_EXECUTION 
  - 여기에서 START_TIME, END_TIME, STATUS를 찾아볼 수 있다.
  - COMMIT_COUNT, READ_COUNT, WRITE_COUNT, READ_SKIP_COUNT, PROCESS_SKIP_COUNT, WRITE_SKIP_COUNT, ROLLBACK_COUNT
- BATCH_STEP_EXECUTION_CONTEXT

---

### 5.1.2 인메모리 JobRepository

---

## 5.2 배치 인프라스트럭처 구성하기

@EnableBatchProcessing 어노테이션을 구성하면 추가적인 구성 없이 스프링 배치가 제공하는 JobRepository를 사용할 수 있다.

### 5.2.2 JobRepository 커스터마이징하기

### 5.2.3 TransactionManager 커스터마이징하기

### 5.2.4 JobExplorer 커스터마이징하기

### 5.2.5 JobLauncher 커스터마이징하기

JobLauncher는 스프링 배치 잡을 실행하는 진입점이다. 스프링 부트는 기본적으로 스프링 배치가 제공하는 SimpleJobLauncher를 사용한다. 그러므로 스프링 부트의 기본 메커니즘으로 잡을 실행할 때는 대부분 JobLauncher를 커스터마이징할 필요가 없다. 그러나 가령 어떤 잡이 스프링 MVC 애플리케이션의 일부분으로 존재하며, 컨트롤러를 통해 해당 잡을 실행하려 한다고 생각해보자.

### 5.2.6 데이터베이스 구성하기

```yml
spring:
  batch:
    initalize-schema: always
```

- always : 애플리케이션을 실행할 때마다 스크립트가 실행된다.
- never : 스크립트를 실행하지 않는다.
- embedded : 내장 데이터베이스를 사용하는 것으로 한다.

---

## 5.3 잡 메타데이터 사용하기 

### 5.3.1 JobExplorer

JobExplorer 인터페이스가 노출하는 메서드를 통해 JobRepository 전체에 접근이 가능하다. 

---

## 5.4 요약

사용하려는 데이터에 안전한 방법으로 접근하고 싶다면 JobExplorer와 같은 API를 사용하는 것이 좋다.
5장에서 활용되는 메타데이터에 대한 내용이 더욱 적극적으로 활용될 수 있을 것이다.

---

![bg](images/template_title.jpg)

# 감사합니다!
