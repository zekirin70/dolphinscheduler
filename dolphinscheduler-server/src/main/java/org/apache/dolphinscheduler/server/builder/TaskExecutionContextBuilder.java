/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.dolphinscheduler.server.builder;

import static org.apache.dolphinscheduler.common.Constants.SEC_2_MINUTES_TIME_UNIT;

import org.apache.dolphinscheduler.common.enums.TaskTimeoutStrategy;
import org.apache.dolphinscheduler.common.enums.TimeoutFlag;
import org.apache.dolphinscheduler.dao.entity.ProcessDefinition;
import org.apache.dolphinscheduler.dao.entity.ProcessInstance;
import org.apache.dolphinscheduler.dao.entity.TaskDefinition;
import org.apache.dolphinscheduler.dao.entity.TaskInstance;
import org.apache.dolphinscheduler.spi.task.request.ProcedureTaskExecutionContext;
import org.apache.dolphinscheduler.server.entity.TaskExecutionContext;
import org.apache.dolphinscheduler.spi.task.request.DataxTaskExecutionContext;
import org.apache.dolphinscheduler.spi.task.request.SQLTaskExecutionContext;
import org.apache.dolphinscheduler.spi.task.request.SqoopTaskExecutionContext;

/**
 *  TaskExecutionContext builder
 */
public class TaskExecutionContextBuilder {

    public static TaskExecutionContextBuilder get() {
        return new TaskExecutionContextBuilder();
    }

    private TaskExecutionContext taskExecutionContext =  new TaskExecutionContext();

    /**
     * build taskInstance related info
     *
     * @param taskInstance taskInstance
     * @return TaskExecutionContextBuilder
     */
    public TaskExecutionContextBuilder buildTaskInstanceRelatedInfo(TaskInstance taskInstance) {
        taskExecutionContext.setTaskInstanceId(taskInstance.getId());
        taskExecutionContext.setTaskName(taskInstance.getName());
        taskExecutionContext.setFirstSubmitTime(taskInstance.getFirstSubmitTime());
        taskExecutionContext.setStartTime(taskInstance.getStartTime());
        taskExecutionContext.setTaskType(taskInstance.getTaskType());
        taskExecutionContext.setLogPath(taskInstance.getLogPath());
        taskExecutionContext.setWorkerGroup(taskInstance.getWorkerGroup());
        taskExecutionContext.setEnvironmentConfig(taskInstance.getEnvironmentConfig());
        taskExecutionContext.setHost(taskInstance.getHost());
        taskExecutionContext.setResources(taskInstance.getResources());
        taskExecutionContext.setDelayTime(taskInstance.getDelayTime());
        taskExecutionContext.setVarPool(taskInstance.getVarPool());
        return this;
    }

    public TaskExecutionContextBuilder buildTaskDefinitionRelatedInfo(TaskDefinition taskDefinition) {
        taskExecutionContext.setTaskTimeout(Integer.MAX_VALUE);
        if (taskDefinition.getTimeoutFlag() == TimeoutFlag.OPEN) {
            taskExecutionContext.setTaskTimeoutStrategy(taskDefinition.getTimeoutNotifyStrategy());
            if (taskDefinition.getTimeoutNotifyStrategy() == TaskTimeoutStrategy.FAILED
                    || taskDefinition.getTimeoutNotifyStrategy() == TaskTimeoutStrategy.WARNFAILED) {
                taskExecutionContext.setTaskTimeout(Math.min(taskDefinition.getTimeout() * SEC_2_MINUTES_TIME_UNIT, Integer.MAX_VALUE));
            }
        }
        taskExecutionContext.setTaskParams(taskDefinition.getTaskParams());
        return this;
    }

    /**
     * build processInstance related info
     *
     * @param processInstance processInstance
     * @return TaskExecutionContextBuilder
     */
    public TaskExecutionContextBuilder buildProcessInstanceRelatedInfo(ProcessInstance processInstance) {
        taskExecutionContext.setProcessInstanceId(processInstance.getId());
        taskExecutionContext.setScheduleTime(processInstance.getScheduleTime());
        taskExecutionContext.setGlobalParams(processInstance.getGlobalParams());
        taskExecutionContext.setExecutorId(processInstance.getExecutorId());
        taskExecutionContext.setCmdTypeIfComplement(processInstance.getCmdTypeIfComplement().getCode());
        taskExecutionContext.setTenantCode(processInstance.getTenantCode());
        taskExecutionContext.setQueue(processInstance.getQueue());
        return this;
    }

    /**
     * build processDefinition related info
     *
     * @param processDefinition processDefinition
     * @return TaskExecutionContextBuilder
     */
    public TaskExecutionContextBuilder buildProcessDefinitionRelatedInfo(ProcessDefinition processDefinition) {
        taskExecutionContext.setProcessDefineCode(processDefinition.getCode());
        taskExecutionContext.setProcessDefineVersion(processDefinition.getVersion());
        taskExecutionContext.setProjectCode(processDefinition.getProjectCode());
        return this;
    }

    /**
     * build SQLTask related info
     *
     * @param sqlTaskExecutionContext sqlTaskExecutionContext
     * @return TaskExecutionContextBuilder
     */
    public TaskExecutionContextBuilder buildSQLTaskRelatedInfo(SQLTaskExecutionContext sqlTaskExecutionContext) {
        taskExecutionContext.setSqlTaskExecutionContext(sqlTaskExecutionContext);
        return this;
    }

    /**
     * build DataxTask related info
     *
     * @param dataxTaskExecutionContext dataxTaskExecutionContext
     * @return TaskExecutionContextBuilder
     */
    public TaskExecutionContextBuilder buildDataxTaskRelatedInfo(DataxTaskExecutionContext dataxTaskExecutionContext) {
        taskExecutionContext.setDataxTaskExecutionContext(dataxTaskExecutionContext);
        return this;
    }

    /**
     * build procedureTask related info
     *
     * @param procedureTaskExecutionContext procedureTaskExecutionContext
     * @return TaskExecutionContextBuilder
     */
    public TaskExecutionContextBuilder buildProcedureTaskRelatedInfo(ProcedureTaskExecutionContext procedureTaskExecutionContext) {
        taskExecutionContext.setProcedureTaskExecutionContext(procedureTaskExecutionContext);
        return this;
    }

    /**
     * build sqoopTask related info
     *
     * @param sqoopTaskExecutionContext sqoopTaskExecutionContext
     * @return TaskExecutionContextBuilder
     */
    public TaskExecutionContextBuilder buildSqoopTaskRelatedInfo(SqoopTaskExecutionContext sqoopTaskExecutionContext) {
        taskExecutionContext.setSqoopTaskExecutionContext(sqoopTaskExecutionContext);
        return this;
    }

    /**
     * create
     *
     * @return taskExecutionContext
     */
    public TaskExecutionContext create() {
        return taskExecutionContext;
    }

}
