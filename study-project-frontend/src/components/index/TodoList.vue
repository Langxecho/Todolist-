<script setup>
import {ref,watch} from "vue";
import {computed} from "vue";
import {get, post} from "@/net";

//--------------------------------------------------
const typevalue = ref("按关键词")
const types = [{
  value: '按日期',
  label: 0
},
  {
    value: '按关键词',
    label: 1
  }]
const timeSearch = ref('')
const value2 = ref('')
const shortcuts = [
  {
    text: 'Last week',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
      return [start, end]
    },
  },
  {
    text: 'Last month',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
      return [start, end]
    },
  },
  {
    text: 'Last 3 months',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
      return [start, end]
    },
  },
]
//饿了吗代码部分↑↑↑↑↑↑↑↑
import {useStore} from "@/stores";
import {ElMessage} from "element-plus";


const store = useStore();
let missionList = [];
const loading = ref(0)
//getmission重置missionList

const getMissions = async () => {
    const user = await new Promise((resolve) => {
      get('/api/user/me',(message) => {
        resolve(message);
      },() => {
        ElMessage.warning("用户名获取失败")
      })
    })
    const list = new Promise((resolve) => {
      post("/todolist/get-missions",{
        username: user.username
      },(message) => {

        resolve(message)
      },(message) => {
        ElMessage.warning("元数据获取失败")
      })
    })
  return list;

}

missionList = await getMissions();

const getCreateMission = async () => {
  const data = new Promise((resolve) => {
    post("/todolist/get-create-mission",{
      username: store.auth.user.username
    },async (message) => {

      resolve(message);
    },() => {
      ElMessage.error("日历数据获取失败")
      resolve(null);
    })
  })

  return data;
}
const createMissions = ref(await getCreateMission())
console.log(createMissions.value)



//任务分流
const flag = ref(true)
//任务分流的组件
const missionsShunt = (missionSource) => {
  let count = []
  let index = 0
  if(flag.value){
    for (let mission of missionSource) {
      if (mission.status === 0)
        count[index] = mission
      index++;
    }
    return count;
  }else{
    for (let mission of missionSource) {
      if (mission.status === 1)
        count[index] = mission
      index++;
    }
    return count;
  }
}
//最右侧任务列表输出
const missions = ref(missionsShunt(await getMissions()))
console.log("输出" + JSON.stringify(missions.value));

const setProcess = (starttime,endtime) => {
  const startDate = new Date(starttime).getTime();
  const endDate = new Date(endtime).getTime();
  const currentDate = new Date().getTime();
  let process = ((currentDate - startDate) / (endDate - startDate) * 100).toFixed(2);
  if (process > 100) {
    process = 100 + "%";
  }else{
    process = process + "%";
  }
  return process
}
const getTags = (mission) => {
  let tagString = mission.tag.substring(1,mission.tag.length - 1);
  let tags = tagString.split(";")
  // console.log(tags)
  return tags;
}
//任务一览区域
const missionOne = ref({
  headline: "",
  content: "",
  timeRange: "",
})



const tagValue = ref(["+","+","+","+"])
const changeStatus = (index) => {

  if(tagValue.value[index] === "+"){

    tagValue.value[index] = ""
  }
}
const changeStatus1 = (index) => {

  if(tagValue.value[index] === ""){

    tagValue.value[index] = "+"
  }
}
const generateTags = () => {
  let tags = "#";
  tagValue.value.forEach((item) => {
    if(item != "+") {
     tags = tags + item + ";"
    }
  })
  return tags;
}

const formatDateTime = (dateObject) => {
  const year = dateObject.getFullYear();
  const month = (dateObject.getMonth() + 1).toString().padStart(2, '0');
  const day = dateObject.getDate().toString().padStart(2, '0');
  const hours = dateObject.getHours().toString().padStart(2, '0');
  const minutes = dateObject.getMinutes().toString().padStart(2, '0');
  const seconds = dateObject.getSeconds().toString().padStart(2, '0');

  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}
const ValidHeadline = (headline) => {
  for(let i = 0;i < missions.value.length; i++){
    if (missions.value[i].headline === headline) return true;
  }

  return false;
}
const validMission = () => {
  if(missionOne.value.headline === ""){
    ElMessage.error("任务名不能为空！")
    return true;
  }else if(missionOne.value.timeRange[0] === undefined || missionOne.value.timeRange[1] === undefined)
  {
    ElMessage.error("请输入时间范围！")
    return true;
  }else {
    return false;
  }
}
const submitMission = () => {
  if(validMission()) return;
  console.log("我被触发了，validmission的值为" + validMission())
  const starttime = formatDateTime(new Date(missionOne.value.timeRange[0]))
  const endtime = formatDateTime(new Date(missionOne.value.timeRange[1]))
  const nowTime = new Date().getTime()
  const nowtime = formatDateTime(new Date())
  const endTime = new Date(missionOne.value.timeRange[1]).getTime()
  let status;

  if(nowTime >= endTime ) status = 1;
  else status = 0;
  if(ValidHeadline(missionOne.value.headline)){
    const p2 = new Promise((resolve) => {
      post("/todolist/update-mission", {
            username: store.auth.user.username,
            headline: missionOne.value.headline,
            content: missionOne.value.content,
            starttime: starttime,
            endtime: endtime,
            tag: generateTags(),
            status: status
          }, async (message) => {
            ElMessage.success("任务更新成功");
            missions.value = missionsShunt(await getMissions())

            console.log("我被执行了")
            resolve();
          },
          () => {
            ElMessage.error("任务更新失败，错误码：" + message)
          })
    })
  }
  else {
    const p1 = new Promise((resolve) => {
      post("/todolist/new-mission", {
            username: store.auth.user.username,
            headline: missionOne.value.headline,
            content: missionOne.value.content,
            starttime: starttime,
            endtime: endtime,
            tag: generateTags(),
            status: status
          }, async (message) => {
            ElMessage.success("任务添加成功");
            missions.value = missionsShunt(await getMissions())
            resolve();
          },
          () => {
            ElMessage.error("任务添加失败，错误码：" + message)
          })
    })
    p1.then(() => {
      post("/todolist/create-new-mission", {
        username: store.auth.user.username,
        createtime: nowtime,
        headline: missionOne.value.headline
      }, async () => {
        createMissions.value = await getCreateMission();
      }, () => {
        ElMessage.error("日历时间加入失败（日历上不会显示该任务的创建）")
      })
    })
  }

  loading.value = loading.value + 1
}

const mainProcess = ref("0%")
watch(() => missionOne.value.timeRange,() => {
  mainProcess.value = setProcess(formatDateTime(new Date(missionOne.value.timeRange[0])),formatDateTime(new Date(missionOne.value.timeRange[1])))
},{ deep: true })
//中间与右侧栏联动部分
//记录当前锁定的任务
const tar = ref(-1)
const addMission = () => {
  tar.value = -1;
  missionOne.value.timeRange = "";
  missionOne.value.content = "";
  missionOne.value.headline = "";
 tagValue.value = tagValue.value.map(item => item = "+")
  loading.value = loading.value + 1

}

//这个变量用来控制右侧任务列表里面的
const selectflag = ref(new Array(100).fill(true));
const selectMission = (index) => {
  selectflag.value = new Array(100).fill(true)
  selectflag.value[index] = !selectflag.value[index]

  tar.value = index;
  const getTimeRange = (starttime,endtime) => {
    return [new Date(starttime),new Date(endtime)]
  }
  missionOne.value.timeRange = getTimeRange(missions.value[index].starttime,missions.value[index].endtime);
  missionOne.value.content = missions.value[index].content;
  missionOne.value.headline = missions.value[index].headline;
  for (let i = 0;i < tagValue.value.length;i++){
    tagValue.value[i] = getTags(missions.value[index])[i];
    if (tagValue.value[i] === undefined || tagValue.value[i] === "#") tagValue.value[i] = "+";

  }
}
const deleteMission = async () => {
  if(tar.value === -1) return;
  const deleteHead = missions.value[tar.value].headline
  const p1 = new Promise((resolve) => {
    post("/todolist/del-mission",{
      headline: deleteHead
    },() => {
      ElMessage.success("删除成功")
      resolve();
    },(message) => {
      ElMessage.error("删除失败，错误码" + message)
    })
  })
  p1.then(() => {
    post("/todolist/delete-create-mission",{
      headline: deleteHead
    },async () => {  createMissions.value = await getCreateMission();},
        () => {
      ElMessage.error("日历信息更新失败")
        })
  })
  missions.value = missionsShunt(await getMissions())
  tar.value = -1;
  missionOne.value.timeRange = "";
  missionOne.value.content = "";
  missionOne.value.headline = "";
  tagValue.value = tagValue.value.map(item => item = "+")
  loading.value = loading.value + 1
}
//用来记录日历的数值
const calendar = ref(new Date())

// setInterval(() => {
//   console.log(formatDateTime(calendar.value).substring(0,10))
// },1000)
const validDate = (data) => {
  // console.log("valid被执行了")
  // console.log( createMissions.value)
  if (createMissions.value === null) return ;
  for (let i = 0;i < createMissions.value.length;i++){
    if (data.day === createMissions.value[i].createTime.substring(0,10)){
      return true
    }
    return false
  }
}
//搜索模块
const searchKey = ref("")
const searchMissions = ref()
const getSearchByKey = async () => {
  return await new Promise((resolve) => {
    post("/todolist/search-by-key",{
      key: searchKey.value,
      user: store.auth.user.username
    },(data) => {
      resolve(data)
      // console.log("这里是搜索到的数据" + JSON.stringify(data))
    })
  })
}
const updateSearchMissions = async () => {
  if(searchKey.value === "") {
    searchMissions.value = ""
    return;
  }
  searchMissions.value = await getSearchByKey()
}


const searchborder = computed(() => {
  if(typevalue.value === "按关键词"){
    return searchKey.value === "" ? 'none' : ''
  }
  else if(typevalue.value === "按日期"){
    return searchMissions.value === undefined ? 'none' : ''
  }

})
//如果想把搜索改成即时的，那就解放下面这一坨，虽然这么做很傻逼
watch(searchKey,() => {
  updateSearchMissions();
})
const selectSearchMission = async (mission) => {
  let index = -1;
 for(let i = 0;i < missions.value.length;i++){
   if(missions.value[i].headline === mission.headline){
     index = i;
     break;
   }
 }
 if(index === -1){
   flag.value = !flag.value
   missions.value = missionsShunt(await getMissions())
   selectSearchMission(mission)
   return;
 }

 selectMission(index);
}
const searchType = computed(() => {
  if(typevalue.value === "按关键词"){
    return [false,"请输入..."];
  }else if(typevalue.value === "按日期"){
    searchKey.value = ""
    return [true,"请在上方日期表选择日期"];
  }
})
watch(calendar,async (newValue,oldValue) => {
    if(typevalue.value === "按日期"){
      const searchDate = formatDateTime(newValue).substring(0,10)
      const getSearchMissions = async () => {
        return new Promise((resolve) => {
          post("/todolist/search-by-time",{
            datekey: searchDate,
            user: store.auth.user.username
          },(data) => {
            resolve(data)
          })
        })
      }
      searchMissions.value = await getSearchMissions();

    }
})
//已完成和未完成状态

const changeView = async () => {
  flag.value = !flag.value
  missions.value = missionsShunt(await getMissions())
}
const statusShow = computed(() => {
  return flag.value ? "未完成任务" : "已完成任务"
})
//任务列表分流

</script>

<template>
  <div class="flex ">
    <div class="round-radius ml-2 mr-2 white">
      <div class="ffg" style="width: 400px;">
      <el-calendar class="round-radius" v-model="calendar">
        <template #date-cell="{ data }">
          <p style="position: relative;" :class="data.isSelected ? 'is-selected' : ''">
            {{ data.day.split('-').slice(2).join('-') }}
            <p style="position: absolute;top:-40px;right: -6px">{{ data.isSelected ? '✔️' : '' }}</p>
            <p  :key="loading"
                style="position: absolute;top:-38px;right: 20px;font-size: 14px">{{ validDate(data) ? '⭐' : '' }}</p>
          </p>
        </template>
      </el-calendar>
    </div>
      <div class="flex" style="padding-left: 15px;">
        <div style="width: 110px">
          <el-select v-model="typevalue" placeholder="筛选">
            <el-option
                v-for="item in types"
                :key="item.label"

                :value="item.value"
            >
              <span style="float: left">{{}}</span>
              <span
                  style="
          float: left;
          color: var(--el-text-color-secondary);
          font-size: 13px;
        "
              >{{ item.value }}</span
              >
            </el-option>
          </el-select>
        </div>
        <div style="width: 255px">
          <el-input v-model="searchKey" :disabled="searchType[0]" v-on:blur="updateSearchMissions" :placeholder="searchType[1]" />
        </div>

      </div>
      <div :style="{display: searchborder}" style="width: 363px;height: 150px;margin-left: 15px;margin-top: 2px;overflow: auto" class="border">
        <ul>
          <li v-for="mission in searchMissions" @click="selectSearchMission(mission)" style="height: 40px;" class="border">
           <p style="font-size: 16px;margin: 0 0 0 8px;">{{mission.headline}}</p>
            <p style="font-size: 10px;margin: 0 0 0 8px">{{ mission.tag }}
              <span style="font-size: 10px;margin: 0">{{ formatDateTime(new Date(mission.starttime)) }} -</span>
              <span style="font-size: 10px;margin: 0">{{ formatDateTime(new Date(mission.endtime)) }} </span>
            </p>
          </li>
        </ul>
      </div>
    </div>
    <div class="round-radius mr-2 white" style="height: 600px;width: 470px">
    <h3 class="ml-2">当前代办： {{missions.length}}</h3>
      <el-input class="ml-2 mb-1" placeholder="任务名" style="width: 430px" v-model="missionOne.headline"></el-input>
      <el-input type="textarea"
                placeholder="开始你的任务!"
                :rows="7"
                v-model="missionOne.content"
                class="ml-2 large-radius "
                style="width: 430px;" ></el-input>
      <div class="border ml-2 main-process"
           here-set=""
           :style="{'--widthhere': mainProcess}"
           >
      </div>
      <div class="flex ml-2 vertical-center" style="height: 30px;width: 430px;border-top: none;margin-top: 1px;">
        <div class="block" style="margin-left: 5px;margin-top: 8px">
          <el-date-picker
              v-model="missionOne.timeRange"
              type="datetimerange"
              :clearable="false"
              :shortcuts="shortcuts"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
          />
        </div>
      </div>
      <div class="border ml-1 mt-2 flex" style="width: 435px;height: 40px;padding-left: 15px;" >
        <input type="text"
               maxlength="4"
               v-model="tagValue[0]"
               v-on:blur="changeStatus1(0)"
               v-on:focus="changeStatus(0)"

               class="border ml-2 round-radius text-vertical-center set-tag"
               style="height: 30px; margin-top: 4px;width: 60px; padding:0px 10px; ">
        <input type="text"
               maxlength="4"
               v-model="tagValue[1]"
               v-on:blur="changeStatus1(1)"
               v-on:focus="changeStatus(1)"

               class="border ml-2 round-radius text-vertical-center set-tag"
               style="height: 30px; margin-top: 4px;width: 60px; padding:0px 10px; ">
        <input type="text"
               maxlength="4"
               v-model="tagValue[2]"
               v-on:blur="changeStatus1(2)"
               v-on:focus="changeStatus(2)"

               class="border ml-2 round-radius text-vertical-center set-tag"
               style="height: 30px; margin-top: 4px;width: 60px; padding:0px 10px; ">
        <input type="text"
               maxlength="4"
               v-model="tagValue[3]"
               v-on:blur="changeStatus1(3)"
               v-on:focus="changeStatus(3)"

               class="border ml-2 round-radius text-vertical-center set-tag"
               style="height: 30px; margin-top: 4px;width: 60px; padding:0px 10px; ">

      </div>

      <el-button @click="submitMission()" class="mt-1 ml-2" style="width: 430px;" type="success" round>保存</el-button>
    </div>
    <div class="round-radius white" style="width: 350px">
      <div class="border mt-2" style="width: 300px;height: 560px;margin-left: 24px">
        <div class="border flex" style="height: 30px;border-left: none;border-top: none;border-right: none">
            <div class="border text-vertical-center" style="cursor: pointer;width: 50px;height: 100%;border-left: none;border-top: none;" @click="addMission()">＋</div>
          <div class="border text-vertical-center"
               style="cursor: pointer;width: 50px;height: 100%;border-left: none;border-top: none;"
               @click="deleteMission()"
          >
            -
          </div>
          <div @click="changeView" class="border text-vertical-center" style="width: 150px;height: 100%;border-left: none;border-top: none;cursor: pointer">
            {{ statusShow }}
          </div>
          <div class="border text-vertical-center" style="width: 50px;height: 100%;border-left: none;border-top: none;">
            {{missions.length}}
          </div>

        </div>
        <div class="mission-display" style="width: 300px;height: 530px;overflow-x: hidden;overflow-y: auto;border-left: none;">
<!--          //任务条-->
          <div v-for="(mission,index) in missions"
               @click="selectMission(index)"
               :style="{'border-color': selectflag[index] ? '#dadde4' : '#4ea48d',
               'border-width': selectflag[index] ? '1px' : '2px',
               'width': selectflag[index] ? '300px' : '296px'
               }"
               style="height: 170px;" class="border">
            <div onload="" self-set="" :style="{'--width': setProcess(mission.starttime,mission.endtime)}" class="index">

            </div>
            <div style="width: 300px;height: 130px;border-top:1px solid var(--el-border-color) ;border-bottom:1px solid var(--el-border-color);" class="">
              <span style="font-size: 18px;font-weight: bold;font-family: 幼圆">{{ mission.headline + "" }}</span>
              <br>
              {{mission.content}}
            </div>
            <div style="width: 300px;height: 20px;display: flex">
              <div v-for="tag in getTags(mission)" class="border ml-1 round-radius text-vertical-center" style="height: 17px; margin-top: 1px;min-width: 30px; padding:0px 10px; font-size: 14px">
                {{ tag }}
              </div>
            </div>
          </div>


        </div>

      </div>

    </div>

  </div>

</template>

<style scoped>

</style>
<style>
ul {
  list-style-type: none;
  margin: 0;
  padding: 0;
}
.main-process{
  height: 15px;
  width: 430px;
  margin-top: 3px;
  font-size: 8px;
  position:relative;
}
.main-process::before{
  content: attr(here-set);
  position: absolute;
  height: 80%;
  background-color: #fdcc66;
  border-radius: 8px;
  width: var(--widthhere);
  top:1px;
  left: 3px;
  min-width: 14px;
  max-width: 425px;
}
.index{
  width: 300px;
  height: 15px;
  border-top:1px solid var(--el-border-color) ;
  border-bottom:1px solid var(--el-border-color);
  border-radius: 8px;
  position: relative;
}
.index::after{
  content: attr(self-set);
  box-sizing: border-box;
  top: 1.5px;
  left: 4px;
  position: absolute;
  border-radius: 8px;
  background-color: #ffce67;
  min-width: 15px;
  max-width: 292px;
  width: var(--width);
  height: 80%;
}
::-webkit-scrollbar {
  width: 0px;
}

.ffg .el-calendar {
  --el-calendar-border: var(--el-table-border, 1px solid var(--el-border-color-lighter));
  --el-calendar-header-border-bottom: var(--el-calendar-border);
  --el-calendar-selected-bg-color: var(--el-color-primary-light-9);
  --el-calendar-cell-width: 45px;
  background-color: var(--el-fill-color-blank);
}
</style>