package com.artoftesting.base;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface TestDetail {
   public String testCaseID() default "";
   public String testCaseName() default "";
   public String author() default "";
   public String module() default "";
   public String category() default "";
  
}


