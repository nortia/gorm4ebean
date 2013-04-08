package es.nortia_in.orm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import es.nortia_in.orm.annotations.Domain;

@Domain(REQUIRED={"static foo", "bar"})
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MockAnnotation1 {

}