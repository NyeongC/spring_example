package com.example.spring_example.config.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER) // 파라미터 안에서만 사용한다
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginUser {
}
