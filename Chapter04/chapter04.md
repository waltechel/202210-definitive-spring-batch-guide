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

# Chapter 04 잡과 스텝 이해하기

---

## 4.1 잡 소개하기

- 유일하다 : 스프링 배치의 잡은 코어 스프링 프레임워크를 사용한 빈 구성 방식과 동일하다.
- 순서를 가진 여러 스텝의 목록이다 : 잡에서 스텝의 순서가 매우 중요하다
- 처음부터 끝까지 실행 가능하다 : 능동적으로 수행한다.
- 독립적이다 : 잡의 실행은 스케줄러와 같은 것이 책임진다. 대신 잡은 자신이 처리하기로 정의한 모든 요소를 제어한다.

---

### 4.1.1 잡의 생명주기 따라가보기

잡의 정의는 스프링 배치가 잡의 인스턴스를 생성하는 데 필요한 청사진이다. 스프링 배치는 두 가지 잡 러너를 제공한다.

- CommandLineJobRunner
- JobRegistryBackgroundJobRunner
- JobLauncherCommandLineRunner : 스프링 부트에서 수행

실제 진입점은 잡 러너가 아닌 org.springframework.batch.core.launch.JobLauncher 인터페이스의 구현체다.

---

> JobInstance는 한번 성공적으로 완료되면 다시 실행시킬 수 없다. JobInstance는 잡 이름과 전달된 식별 파라미터로 식별되므로, 동일한 식별 파라미터를 사용하는 잡은 한번만 실행할 수 있다.

JobExecution

- 잡 실행의 실제 시도
- 각 JobExcecution은 BATCH_JOB_EXECUTION 테이블의 레코드로 저장된다.
- 잡이 처음부터 끝까지 단번에 실행 완료됐다면 해당 JobInstance와 JobExecution은 단 하나씩만 존재한다.
- 만약 첫 번째 잡 실행 후 오류 상태로 종료됐다면 해당 JobInstance를 실행하려고 시도할 때마다 새로운 JobExecution이 생성된다. 

---

## 4.2 잡 구성하기

### 4.2.1 잡의 기본 구성

배치 스키마가 존재하지 않는다면 자동으로 배치 스키마를 생성하도록 어플리케이션을 구성했다.

```properties
spring.batch.initialize-schema=always
```

### 4.2.2 잡 파라미터

동일한 식별 파라미터를 사용해 동일한 잡을 두 번 이상 실행할 수 없다.

#### 잡 파라미터에 접근하기

`@StepScope`, `@JobScope` 각각의 기능은 스텝의 실행 범위나 잡의 실행 범위에 들어갈 때까지 빈 생성을 지연시키는 것이다.

#### 잡 파라미터 유효성 검증하기

스프링 배치는 `org.springframework.batch.core.JobParameterValidator` 인터페이스를 구현하고 해당 구현체를 잡 내에 구현하면 된다. 

---

### 4.2.3 잡 리스너 적용하기

리스너가 하는 일
- 알림
- 초기화
- 정리 : 잡이 실행 이후에 정리 작업을 수행하는 경우

#### 플로우 외부화하기

스텁을 외부화하는 다음 방법은 플로우 스텝을 사용하는 것이다.

플로우를 사용하게 되면 추가적인 흐름을 파악할 수 있다.

---

![bg](images/template_title.jpg)

# 감사합니다!
