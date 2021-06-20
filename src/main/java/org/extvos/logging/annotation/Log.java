/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.extvos.logging.annotation;

import org.extvos.logging.annotation.type.LogAction;
import org.extvos.logging.annotation.type.LogLevel;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Mingcai SHEN
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {
    LogAction value() default LogAction.SELECT;

    @AliasFor("value")
    LogAction action() default LogAction.SELECT;

    LogLevel level() default LogLevel.NORMAL;

    /**
     * 是否启用
     *
     * @return default true
     */
    boolean enable() default true;

    /**
     * Comments
     *
     * @return default ""
     */
    String comment() default "";


}
