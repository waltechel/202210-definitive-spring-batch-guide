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
title: ItemReader
---

![bg](images/template_title.jpg)

# Chapter 07 ItemReader

---

## 7.1 ItemReader 인터페이스

ItemReader 인터페이스는 전략 인터페이스이다.

## 7.2 파일 입력

### 7.2.1 플랫 파일

한 개 또는 그 이상의 레코드가 포함된 특정 파일 을 말한다.

#### 고정 너비 파일

- customerItemReader 빈 선언

```java
@Bean
@StepScope
public FlatFileItemReader<Customer> custemerItemReader(
    @Value("#(jobParameters['customerFile']}") Resource inputFile
) {
    return new FlatFileItemReaderBuilder<Customer>()
            .name("customerItemReader")
            .resource(inputFile)
            .fixedLength()
        .columns(new Range[]{
            new Range(1, 11),
            new Range(12, 12),
            new Range(13, 22),
            new Range(23, 26),
            new Range(27, 46),
            new Range(47, 62),
            new Range(63, 64),
            new Range(65, 69)
        })
        .names(new String[]{
            "firstName",
            "middleInitial",
            "lastName",
            "addressNumber",
            "street",
            "city",
            "state",
            "zipCode"
        })
        .targetType(Customer.class)
        .build();
}
```

---

#### 필드가 구분자로 구분된 파일

```java
@Bean
@StepScope
public FlatFileItemReader<Customer> customerItemReader(
    @Value("#{jobParameters['customerFile']}") Resource inputFile) {
    return new FlatFileItemReaderBuilder<Customer>()
                .name("customerItemReader")
                .delimited()
                .names(new String[]{
                    "firstName",
                    "middleInitial",
                    "lastName",
                    "addressNumber",
                    "street",
                    "city",
                    "state",
                    "zipCode"
                })
                .targetType(Customer.class)
                .resource(inputFile)
                .build();
}

```

---

## 7.3 JSON

---

## 7.7 에러 처리

### 7.7.1 레코드 건너뛰기

- ParseException을 10개까지 건너뛰는 구성

```Java
@Bean
public Step copyFileStep(){
    return this.stepBuilderFactory.get("copyFileStep")
                .<Customer, Customer>chunk(10)
                .reader(customerItemReader())
                .writer(itemWriter())
                .faultTolerant()
                .skip(ParseException.class)
                .skipLimit(10)
                .build();
}
```

---

### 7.7.2 잘못된 레코드 로그 남기기

- CustomerItemListener

```Java
@OnReadError
public void onReadError(Exception e) {
    if(e instanceof FlatFileParseException) {
        FlatFileParseException ffpe = (FlatFileParseException) e;

        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("An error occured while processing the "
        + ffpe.getLineNumber() +
        " line of the file. Below was the faulty input. ");
        errorMessage.append(ffpe.getInput() + "\n");
        logger.error(errorMessage.toString(), ffpe);
    }else{
        logger.error("An error has occured", e);
    }
}
```

---



---

![bg](images/template_title.jpg)

# 감사합니다!
