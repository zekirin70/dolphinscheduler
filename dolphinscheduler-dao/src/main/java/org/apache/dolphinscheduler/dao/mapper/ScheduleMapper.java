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
package org.apache.dolphinscheduler.dao.mapper;

import org.apache.dolphinscheduler.dao.entity.Schedule;

import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * scheduler mapper interface
 */
public interface ScheduleMapper extends BaseMapper<Schedule> {

    /**
     * scheduler page
     * @param page page
     * @param processDefinitionCode processDefinitionCode
     * @param searchVal searchVal
     * @param projectId projectId
     * @param statusArray statusArray
     * @param startTime startTime
     * @param endTime endTime
     * @return scheduler IPage
     */
    IPage<Schedule> queryByProcessDefineCodePaging(IPage<Schedule> page,
                                                   @Param("processDefinitionCode") long processDefinitionCode,
                                                   @Param("searchVal") String searchVal,
                                                   @Param("projectId") int projectId,
                                                   @Param("states") int[] statusArray,
                                                   @Param("startTime") Date startTime,
                                                   @Param("endTime") Date endTime
    );


    /**
     * query schedule list by project name
     * @param projectName projectName
     * @return schedule list
     */
    List<Schedule> querySchedulerListByProjectName(@Param("projectName") String projectName);

    /**
     * query schedule list by process definition codes
     * @param processDefineCodes processDefineCodes
     * @return schedule list
     */
    List<Schedule> selectAllByProcessDefineArray(@Param("processDefineCodes") long[] processDefineCodes);

    /**
     * query schedule list by process definition code
     * @param processDefinitionCode processDefinitionCode
     * @return schedule list
     */
    List<Schedule> queryByProcessDefinitionCode(@Param("processDefinitionCode") long processDefinitionCode);

    /**
     * query schedule list by process definition code
     * @param processDefinitionCode processDefinitionCode
     * @return schedule list
     */
    List<Schedule> queryReleaseSchedulerListByProcessDefinitionCode(@Param("processDefinitionCode") long processDefinitionCode);

}
