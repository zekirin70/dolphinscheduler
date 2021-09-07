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
  <div class="wrap-definition">
    <m-list-construction :title="$t('Cron Manage')">
      <template slot="conditions">
        <m-timing-list-conditions @on-query="_onQuery"></m-timing-list-conditions>
      </template>
      <template slot="content">
        <template v-if="scheduleListP.length || total>0">
          <m-list :schedule-list="scheduleListP" @on-update="_onUpdate" :page-no="searchParams.pageNo" :page-size="searchParams.pageSize"></m-list>
          <div class="page-box">
            <x-page :current="parseInt(searchParams.pageNo)" :total="total" show-elevator @on-change="_page" show-sizer :page-size-options="[10,30,50]" @on-size-change="_pageSize"></x-page>
          </div>
        </template>
        <template v-if="!scheduleListP.length && total<=0">
          <m-no-data></m-no-data>
        </template>
        <m-spin :is-spin="isLoading" :is-left="isLeft"></m-spin>
      </template>
    </m-list-construction>
  </div>
</template>
<script>
  import _ from 'lodash'
  import { mapActions } from 'vuex'
  import mList from './_source/list'
  import mSpin from '@/module/components/spin/spin'
  import localStore from '@/module/util/localStorage'
  import mNoData from '@/module/components/noData/noData'
  import listUrlParamHandle from '@/module/mixin/listUrlParamHandle'
  import mTimingListConditions from '@/conf/home/pages/projects/pages/_source/timingListConditions'
  import { setUrlParams } from '@/module/util/routerUtil'
  import mSecondaryMenu from '@/module/components/secondaryMenu/secondaryMenu'
  import mListConstruction from '@/module/components/listConstruction/listConstruction'
  import { findComponentDownward } from '@/module/util/'

  export default {
    name: 'timing-list-index',
    data () {
      return {
        total: null,
        scheduleListP: [],
        isLoading: true,
        searchParams: {
          pageSize: 10,
          pageNo: 1,
          searchVal: '',
          userId: '',
          stateType: '',
          startDate: '',
          endDate: ''
        },
        isLeft: true
      }
    },
    mixins: [listUrlParamHandle],
    props: {
    },
    methods: {
      ...mapActions('dag', ['getScheduleListP']),

      /**
       * File Upload
       */
      _uploading () {
        findComponentDownward(this.$root, 'roof-nav')._fileUpdate('DEFINITION')
      },
      /**
       * page
       */
      _page (val) {
        this.searchParams.pageNo = val
      },
      _pageSize (val) {
        this.searchParams.pageSize = val
      },
      /**
       * conditions
       */
      _onQuery (o) {
        // this.searchParams.searchVal = o.searchVal
        this.searchParams = _.assign(this.searchParams, o)
        this.searchParams.pageNo = 1
        setUrlParams(this.searchParams)
        console.log("searchParams1:", this.searchParams)
        this._debounceGET()
      },
      /**
       * get data list
       */
      _getList (flag) {
        if(sessionStorage.getItem('isLeft')==0) {
          this.isLeft = false
        } else {
          this.isLeft = true
        }
        this.isLoading = !flag
        this.getScheduleListP(this.searchParams).then(res => {
          if(this.searchParams.pageNo>1 && res.data.totalList.length == 0) {
            this.searchParams.pageNo = this.searchParams.pageNo -1
          } else {
            this.scheduleListP = []
            this.scheduleListP = res.data.totalList
            this.total = res.data.total
            this.isLoading = false
          }
        }).catch(e => {
          this.isLoading = false
        })
      },
      _onUpdate () {
        this._debounceGET('false')
      },
      _updateList () {
        this.searchParams.pageNo = 1
        this.searchParams.searchVal = ''
        this._debounceGET()
      }
    },
    watch: {
      '$route' (a) {
        // url no params get instance list
        this.searchParams.pageNo = _.isEmpty(a.query) ? 1 : a.query.pageNo
      },
      'searchParams': {
        deep: true,
        handler () {
          this._debounceGET()
        }
      }
    },
    created () {
      localStore.removeItem('subProcessId')
    },
    mounted() {
      this.$modal.destroy()
    },
    beforeDestroy () {
      sessionStorage.setItem('isLeft',1)
    },
    components: { mList, mTimingListConditions, mSpin, mListConstruction, mSecondaryMenu, mNoData }
  }
</script>

<style lang="scss" rel="stylesheet/scss">
  .wrap-definition {
    .table-box {
      overflow-y: scroll;
    }
    .table-box {
      .fixed {
        table-layout: auto;
        tr {
          th:last-child,td:last-child {
            background: inherit;
            width: 300px;
            height: 40px;
            line-height: 40px;
            border-left:1px solid #ecf3ff;
            position: absolute;
            right: 0;
            z-index: 2;
          }
          td:last-child {
            border-bottom:1px solid #ecf3ff;
          }
          th:nth-last-child(2) {
            padding-right: 330px;
          }
        }
      }
    }
  }
</style>
