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
package plus.extvos.logging.service;

import org.springframework.scheduling.annotation.Async;
import plus.extvos.logging.annotation.type.LogAction;
import plus.extvos.logging.annotation.type.LogLevel;
import plus.extvos.logging.domain.LogObject;


/**
 * @author Mingcai SHEN
 */
public interface LogDispatchService {
    /**
     * 保存日志数据
     *
     * @param logObject 日志实体
     */
    @Async
    void dispatch(LogObject logObject);

    default void dispatch(LogAction action, LogLevel level, String model, String method, String comment) {
        LogObject log = new LogObject(action.name(), 0L);
        log.setLevel(level.name());
        log.setComment(comment);
        log.setModel(model);
        log.setMethod(method);
        dispatch(log);
    }

}
