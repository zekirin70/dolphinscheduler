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
<template>
  <div class="list-model" style="position: relative;">
    <div class="table-box">
      <table>
        <tr>
          <th>
            <span>{{$t('#')}}</span>
          </th>
          <th>
            <span>{{$t('Process Name')}}</span>
          </th>
          <th>
            <span>{{$t('Start Time')}}</span>
          </th>
          <th>
            <span>{{$t('End Time')}}</span>
          </th>
          <th>
            <span>{{$t('crontab')}}</span>
          </th>
          <th>
            <span>{{$t('Failure Strategy')}}</span>
          </th>
          <th>
            <span>{{$t('State')}}</span>
          </th>
          <th>
            <span>{{$t('Create Time')}}</span>
          </th>
          <th>
            <span>{{$t('Update Time')}}</span>
          </th>
          <th width="120">
            <span>{{$t('Operation')}}</span>
          </th>
        </tr>
        <tr v-for="(item, $index) in list" :key="item.id">
          <td>
            <span>{{parseInt(pageNo === 1 ? ($index + 1) : (($index + 1) + (pageSize * (pageNo - 1))))}}</span>
          </td>
          <td>
            <span><a href="javascript:">{{item.processDefinitionName}}</a></span>
          </td>
          <td>
            <span>{{item.startTime | formatDate}}</span>
          </td>
          <td>
            <span>{{item.endTime | formatDate}}</span>
          </td>
          <td>
            <span>{{item.crontab}}</span>
          </td>
          <td>
            <span>{{item.failureStrategy}}</span>
          </td>
          <td>
            <span>{{_rtReleaseState(item.releaseState)}}</span>
          </td>
          <td>
            <span>{{item.createTime | formatDate}}</span>
          </td>
          <td>
            <span>{{item.updateTime | formatDate}}</span>
          </td>
          <td>
            <x-button
              type="info"
              shape="circle"
              size="xsmall"
              data-toggle="tooltip"
              :title="$t('Edit')"
              @click="_editTiming(item)"
              icon="ans-icon-edit"
              :disabled="item.releaseState === 'ONLINE'" >
            </x-button>
            <x-button
              type="warning"
              shape="circle"
              size="xsmall"
              data-toggle="tooltip"
              :title="$t('online')"
              @click="_online(item)"
              icon="ans-icon-upward"
              v-if="item.releaseState === 'OFFLINE'">
            </x-button>
            <x-button
              type="error"
              shape="circle"
              size="xsmall"
              data-toggle="tooltip"
              :title="$t('offline')"
              icon="ans-icon-downward"
              @click="_offline(item)"
              v-if="item.releaseState === 'ONLINE'">
            </x-button>
            <x-poptip
              :ref="'poptip-delete-' + $index"
              placement="bottom-end"
              width="90">
              <p>{{$t('Delete?')}}</p>
              <div style="text-align: right; margin: 0;padding-top: 4px;">
                <x-button type="text" size="xsmall" shape="circle" @click="_closeDelete($index)">{{$t('Cancel')}}</x-button>
                <x-button type="primary" size="xsmall" shape="circle" @click="_delete(item,$index)">{{$t('Confirm')}}</x-button>
              </div>
              <template slot="reference">
                <x-button
                  icon="ans-icon-trash"
                  type="error"
                  shape="circle"
                  size="xsmall"
                  :disabled="item.releaseState === 'ONLINE'"
                  data-toggle="tooltip"
                  :title="$t('delete')">
                </x-button>
              </template>
            </x-poptip>
          </td>
        </tr>
      </table>
    </div>
  </div>
</template>
<script>
  import _ from 'lodash'
  import mTiming from './timing'
  import { mapActions } from 'vuex'
  import { publishStatus } from '@/conf/home/pages/dag/_source/config'

  export default {
    name: 'timing-list',
    data () {
      return {
        isLoading: false,
        list: []
      }
    },
    props: {
      scheduleList: Array,
      pageNo: Number,
      pageSize: Number
    },
    methods: {
      ...mapActions('dag', ['getScheduleList', 'scheduleOffline', 'scheduleOnline', 'getReceiver','deleteTiming']),
      /**
       * Close the delete layer
       */
      _closeDelete (i) {
        this.$refs[`poptip-delete-${i}`][0].doClose()
      },
      /**
       * delete
       */
      _delete (item, i) {
        this.deleteTiming({
          scheduleId: item.id
        }).then(res => {
          this.$refs[`poptip-delete-${i}`][0].doClose()
          this.$message.success(res.msg)
          //this.$router.push({ name: 'projects-definition-list' })
          this._onUpdate()
        }).catch(e => {
          this.$refs[`poptip-delete-${i}`][0].doClose()
          this.$message.error(e.msg || '')
        })
      },
      /**
       * Update
       */
      _onUpdate () {
        this.$emit('on-update')
      },
      /**
       * return state
       */
      _rtReleaseState (code) {
        return _.filter(publishStatus, v => v.code === code)[0].desc
      },
      /**
       * online
       */
      _online (item) {
        this.pageNo = 1
        this.scheduleOnline({
          id: item.id
        }).then(res => {
          this.$message.success(res.msg)
          this._onUpdate()
        }).catch(e => {
          this.$message.error(e.msg || '')
        })
      },
      /**
       * offline
       */
      _offline (item) {
        this.pageNo = 1
        this.scheduleOffline({
          id: item.id
        }).then(res => {
          this.$message.success(res.msg)
          this._onUpdate()
        }).catch(e => {
          this.$message.error(e.msg || '')
        })
      },
      /**
       * get email
       */
      _getReceiver (id) {
        return new Promise((resolve, reject) => {
          this.getReceiver({ processDefinitionId: id }).then(res => {
            resolve({
              receivers: res.receivers && res.receivers.split(',') || [],
              receiversCc: res.receiversCc && res.receiversCc.split(',') || []
            })
          })
        })
      },
      /**
       * timing
       */
      _editTiming (item) {
        let self = this
        this._getReceiver(item.processDefinitionId).then(res => {
          let modal = this.$modal.dialog({
            closable: false,
            showMask: true,
            escClose: true,
            className: 'v-modal-custom',
            transitionName: 'opacityp',
            render (h) {
              return h(mTiming, {
                on: {
                  onUpdate () {
                    self.pageNo = 1
                    self._onUpdate()
                    modal.remove()
                  },
                  close () {
                    modal.remove()
                  }
                },
                props: {
                  item: item,
                  receiversD: res.receivers,
                  receiversCcD: res.receiversCc
                }
              })
            }
          })
        })
      }
    },
    watch: {
      scheduleList: {
        handler (a) {
          this.list = []
          setTimeout(() => {
            this.list = _.cloneDeep(a)
          })
        },
        immediate: true,
        deep: true
      },
    },
    created () {
    },
    mounted () {
    },
    components: { }
  }
</script>
