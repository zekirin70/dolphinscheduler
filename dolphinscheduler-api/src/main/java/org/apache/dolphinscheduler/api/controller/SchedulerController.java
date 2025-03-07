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

package org.apache.dolphinscheduler.api.controller;

import static org.apache.dolphinscheduler.api.enums.Status.CREATE_SCHEDULE_ERROR;
import static org.apache.dolphinscheduler.api.enums.Status.DELETE_SCHEDULE_CRON_BY_ID_ERROR;
import static org.apache.dolphinscheduler.api.enums.Status.OFFLINE_SCHEDULE_ERROR;
import static org.apache.dolphinscheduler.api.enums.Status.PREVIEW_SCHEDULE_ERROR;
import static org.apache.dolphinscheduler.api.enums.Status.PUBLISH_SCHEDULE_ONLINE_ERROR;
import static org.apache.dolphinscheduler.api.enums.Status.QUERY_SCHEDULE_LIST_ERROR;
import static org.apache.dolphinscheduler.api.enums.Status.QUERY_SCHEDULE_LIST_PAGING_ERROR;
import static org.apache.dolphinscheduler.api.enums.Status.UPDATE_SCHEDULE_ERROR;
import static org.apache.dolphinscheduler.common.Constants.SESSION_USER;

import org.apache.dolphinscheduler.api.aspect.AccessLogAnnotation;
import org.apache.dolphinscheduler.api.exceptions.ApiException;
import org.apache.dolphinscheduler.api.service.SchedulerService;
import org.apache.dolphinscheduler.api.utils.Result;
import org.apache.dolphinscheduler.common.enums.FailureStrategy;
import org.apache.dolphinscheduler.common.enums.Priority;
import org.apache.dolphinscheduler.common.enums.ReleaseState;
import org.apache.dolphinscheduler.common.enums.WarningType;
import org.apache.dolphinscheduler.common.utils.ParameterUtils;
import org.apache.dolphinscheduler.dao.entity.User;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * scheduler controller
 */
@Api(tags = "SCHEDULER_TAG")
@RestController
@RequestMapping("/projects/{projectCode}/schedules")
public class SchedulerController extends BaseController {

    public static final String DEFAULT_WARNING_TYPE = "NONE";
    public static final String DEFAULT_NOTIFY_GROUP_ID = "1";
    public static final String DEFAULT_FAILURE_POLICY = "CONTINUE";
    public static final String DEFAULT_PROCESS_INSTANCE_PRIORITY = "MEDIUM";

    @Autowired
    private SchedulerService schedulerService;

    /**
     * create schedule
     *
     * @param loginUser login user
     * @param projectCode project code
     * @param processDefinitionCode process definition code
     * @param schedule scheduler
     * @param warningType warning type
     * @param warningGroupId warning group id
     * @param failureStrategy failure strategy
     * @param processInstancePriority process instance priority
     * @param workerGroup worker group
     * @return create result code
     */
    @ApiOperation(value = "createSchedule", notes = "CREATE_SCHEDULE_NOTES")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "processDefinitionCode", value = "PROCESS_DEFINITION_CODE", required = true, dataType = "Long", example = "100"),
        @ApiImplicitParam(name = "schedule", value = "SCHEDULE", dataType = "String",
            example = "{'startTime':'2019-06-10 00:00:00','endTime':'2019-06-13 00:00:00','timezoneId':'America/Phoenix','crontab':'0 0 3/6 * * ? *'}"),
        @ApiImplicitParam(name = "warningType", value = "WARNING_TYPE", type = "WarningType"),
        @ApiImplicitParam(name = "warningGroupId", value = "WARNING_GROUP_ID", dataType = "Int", example = "100"),
        @ApiImplicitParam(name = "failureStrategy", value = "FAILURE_STRATEGY", type = "FailureStrategy"),
        @ApiImplicitParam(name = "workerGroupId", value = "WORKER_GROUP_ID", dataType = "Int", example = "100"),
        @ApiImplicitParam(name = "environmentCode", value = "ENVIRONMENT_CODE", dataType = "Long"),
        @ApiImplicitParam(name = "processInstancePriority", value = "PROCESS_INSTANCE_PRIORITY", type = "Priority"),
    })
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @ApiException(CREATE_SCHEDULE_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result createSchedule(@ApiIgnore @RequestAttribute(value = SESSION_USER) User loginUser,
                                 @ApiParam(name = "projectCode", value = "PROJECT_CODE", required = true) @PathVariable long projectCode,
                                 @RequestParam(value = "processDefinitionCode") long processDefinitionCode,
                                 @RequestParam(value = "schedule") String schedule,
                                 @RequestParam(value = "warningType", required = false, defaultValue = DEFAULT_WARNING_TYPE) WarningType warningType,
                                 @RequestParam(value = "warningGroupId", required = false, defaultValue = DEFAULT_NOTIFY_GROUP_ID) int warningGroupId,
                                 @RequestParam(value = "failureStrategy", required = false, defaultValue = DEFAULT_FAILURE_POLICY) FailureStrategy failureStrategy,
                                 @RequestParam(value = "workerGroup", required = false, defaultValue = "default") String workerGroup,
                                 @RequestParam(value = "environmentCode", required = false, defaultValue = "-1") Long environmentCode,
                                 @RequestParam(value = "processInstancePriority", required = false, defaultValue = DEFAULT_PROCESS_INSTANCE_PRIORITY) Priority processInstancePriority) {
        Map<String, Object> result = schedulerService.insertSchedule(loginUser, projectCode, processDefinitionCode, schedule,
            warningType, warningGroupId, failureStrategy, processInstancePriority, workerGroup, environmentCode);

        return returnDataList(result);
    }

    /**
     * updateProcessInstance schedule
     *
     * @param loginUser login user
     * @param projectCode project code
     * @param id scheduler id
     * @param schedule scheduler
     * @param warningType warning type
     * @param warningGroupId warning group id
     * @param failureStrategy failure strategy
     * @param workerGroup worker group
     * @param processInstancePriority process instance priority
     * @return update result code
     */
    @ApiOperation(value = "updateSchedule", notes = "UPDATE_SCHEDULE_NOTES")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "SCHEDULE_ID", required = true, dataType = "Int", example = "100"),
        @ApiImplicitParam(name = "schedule", value = "SCHEDULE", dataType = "String", example = "{'startTime':'2019-06-10 00:00:00','endTime':'2019-06-13 00:00:00','crontab':'0 0 3/6 * * ? *'}"),
        @ApiImplicitParam(name = "warningType", value = "WARNING_TYPE", type = "WarningType"),
        @ApiImplicitParam(name = "warningGroupId", value = "WARNING_GROUP_ID", dataType = "Int", example = "100"),
        @ApiImplicitParam(name = "failureStrategy", value = "FAILURE_STRATEGY", type = "FailureStrategy"),
        @ApiImplicitParam(name = "workerGroupId", value = "WORKER_GROUP_ID", dataType = "Int", example = "100"),
        @ApiImplicitParam(name = "processInstancePriority", value = "PROCESS_INSTANCE_PRIORITY", type = "Priority"),
        @ApiImplicitParam(name = "environmentCode", value = "ENVIRONMENT_CODE", dataType = "Long"),
    })
    @PutMapping("/{id}")
    @ApiException(UPDATE_SCHEDULE_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result updateSchedule(@ApiIgnore @RequestAttribute(value = SESSION_USER) User loginUser,
                                 @ApiParam(name = "projectCode", value = "PROJECT_CODE", required = true) @PathVariable long projectCode,
                                 @PathVariable(value = "id") Integer id,
                                 @RequestParam(value = "schedule") String schedule,
                                 @RequestParam(value = "warningType", required = false, defaultValue = DEFAULT_WARNING_TYPE) WarningType warningType,
                                 @RequestParam(value = "warningGroupId", required = false) int warningGroupId,
                                 @RequestParam(value = "failureStrategy", required = false, defaultValue = "END") FailureStrategy failureStrategy,
                                 @RequestParam(value = "workerGroup", required = false, defaultValue = "default") String workerGroup,
                                 @RequestParam(value = "environmentCode", required = false, defaultValue = "-1") Long environmentCode,
                                 @RequestParam(value = "processInstancePriority", required = false) Priority processInstancePriority) {

        Map<String, Object> result = schedulerService.updateSchedule(loginUser, projectCode, id, schedule,
            warningType, warningGroupId, failureStrategy, processInstancePriority, workerGroup, environmentCode);
        return returnDataList(result);
    }

    /**
     * publish schedule setScheduleState
     *
     * @param loginUser login user
     * @param projectCode project code
     * @param id scheduler id
     * @return publish result code
     */
    @ApiOperation(value = "online", notes = "ONLINE_SCHEDULE_NOTES")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "SCHEDULE_ID", required = true, dataType = "Int", example = "100")
    })
    @PostMapping("/{id}/online")
    @ApiException(PUBLISH_SCHEDULE_ONLINE_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result online(@ApiIgnore @RequestAttribute(value = SESSION_USER) User loginUser,
                         @ApiParam(name = "projectCode", value = "PROJECT_CODE", required = true) @PathVariable long projectCode,
                         @PathVariable("id") Integer id) {
        Map<String, Object> result = schedulerService.setScheduleState(loginUser, projectCode, id, ReleaseState.ONLINE);
        return returnDataList(result);
    }

    /**
     * offline schedule
     *
     * @param loginUser login user
     * @param projectCode project code
     * @param id schedule id
     * @return operation result code
     */
    @ApiOperation(value = "offline", notes = "OFFLINE_SCHEDULE_NOTES")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "SCHEDULE_ID", required = true, dataType = "Int", example = "100")
    })
    @PostMapping("/{id}/offline")
    @ApiException(OFFLINE_SCHEDULE_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result offline(@ApiIgnore @RequestAttribute(value = SESSION_USER) User loginUser,
                          @ApiParam(name = "projectCode", value = "PROJECT_CODE", required = true) @PathVariable long projectCode,
                          @PathVariable("id") Integer id) {

        Map<String, Object> result = schedulerService.setScheduleState(loginUser, projectCode, id, ReleaseState.OFFLINE);
        return returnDataList(result);
    }

    /**
     * query schedule list paging
     *
     * @param loginUser login user
     * @param projectCode project code
     * @param processDefinitionCode process definition code
     * @param pageNo page number
     * @param pageSize page size
     * @param searchVal search value
     * @return schedule list page
     */
    @ApiOperation(value = "queryScheduleListPaging", notes = "QUERY_SCHEDULE_LIST_PAGING_NOTES")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "processDefinitionId", value = "PROCESS_DEFINITION_ID", required = true, dataType = "Int", example = "100"),
        @ApiImplicitParam(name = "searchVal", value = "SEARCH_VAL", type = "String"),
        @ApiImplicitParam(name = "pageNo", value = "PAGE_NO", dataType = "Int", example = "1"),
        @ApiImplicitParam(name = "pageSize", value = "PAGE_SIZE", dataType = "Int", example = "20"),
        @ApiImplicitParam(name = "stateType", value = "RELEASE_STATE", type = "ReleaseState"),
        @ApiImplicitParam(name = "startDate", value = "START_DATE", type = "String"),
        @ApiImplicitParam(name = "endDate", value = "END_DATE", type = "String")
    })
    @GetMapping()
    @ApiException(QUERY_SCHEDULE_LIST_PAGING_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result queryScheduleListPaging(@ApiIgnore @RequestAttribute(value = SESSION_USER) User loginUser,
                                          @ApiParam(name = "projectCode", value = "PROJECT_CODE", required = true) @PathVariable long projectCode,
                                          @RequestParam(value = "processDefinitionCode") long processDefinitionCode,
                                          @RequestParam(value = "searchVal", required = false) String searchVal,
                                          @RequestParam("pageNo") Integer pageNo,
                                          @RequestParam("pageSize") Integer pageSize,
                                          @RequestParam(value = "stateType", required = false) ReleaseState stateType,
                                          @RequestParam(value = "startDate", required = false) String startTime,
                                          @RequestParam(value = "endDate", required = false) String endTime) {
        Result result = checkPageParams(pageNo, pageSize);
        if (!result.checkResult()) {
            return result;
        }
        searchVal = ParameterUtils.handleEscapes(searchVal);
        result = schedulerService.querySchedule(loginUser, projectCode, processDefinitionCode, searchVal, pageNo, pageSize, stateType, startTime, endTime);
        return result;

    }

    /**
     * delete schedule by id
     *
     * @param loginUser login user
     * @param projectCode project code
     * @param id scheule id
     * @return delete result code
     */
    @ApiOperation(value = "deleteScheduleById", notes = "OFFLINE_SCHEDULE_NOTES")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "SCHEDULE_ID", required = true, dataType = "Int", example = "100")
    })
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(DELETE_SCHEDULE_CRON_BY_ID_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result deleteScheduleById(@RequestAttribute(value = SESSION_USER) User loginUser,
                                     @ApiParam(name = "projectCode", value = "PROJECT_CODE", required = true) @PathVariable long projectCode,
                                     @PathVariable("id") Integer id) {
        Map<String, Object> result = schedulerService.deleteScheduleById(loginUser, projectCode, id);
        return returnDataList(result);
    }

    /**
     * query schedule list
     *
     * @param loginUser login user
     * @param projectCode project code
     * @return schedule list
     */
    @ApiOperation(value = "queryScheduleList", notes = "QUERY_SCHEDULE_LIST_NOTES")
    @PostMapping("/list")
    @ApiException(QUERY_SCHEDULE_LIST_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result queryScheduleList(@ApiIgnore @RequestAttribute(value = SESSION_USER) User loginUser,
                                    @ApiParam(name = "projectCode", value = "PROJECT_CODE", required = true) @PathVariable long projectCode) {
        Map<String, Object> result = schedulerService.queryScheduleList(loginUser, projectCode);
        return returnDataList(result);
    }

    /**
     * preview schedule
     *
     * @param loginUser login user
     * @param schedule schedule expression
     * @return the next five fire time
     */
    @ApiOperation(value = "previewSchedule", notes = "PREVIEW_SCHEDULE_NOTES")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "schedule", value = "SCHEDULE", dataType = "String", example = "{'startTime':'2019-06-10 00:00:00','endTime':'2019-06-13 00:00:00','crontab':'0 0 3/6 * * ? *'}"),
    })
    @PostMapping("/preview")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiException(PREVIEW_SCHEDULE_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result previewSchedule(@ApiIgnore @RequestAttribute(value = SESSION_USER) User loginUser,
                                  @RequestParam(value = "schedule") String schedule) {
        Map<String, Object> result = schedulerService.previewSchedule(loginUser, schedule);
        return returnDataList(result);
    }
}
