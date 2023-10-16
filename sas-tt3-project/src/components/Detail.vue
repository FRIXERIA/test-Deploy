<script setup>
import { ref, onMounted, computed, onBeforeUpdate, onBeforeMount, defineEmits } from 'vue'
import { fetchId } from '../code/get.js';
import { useRoute } from 'vue-router'
import { useRouter } from 'vue-router'
// import Edit from './Edit.vue'
const { params } = useRoute()
const router = useRouter();

let detail = ref({})
let newDate = ref()
let showAlert = ref(true)
const haveNoRight = ref(false)

onMounted(async () => {
  if (params.id !== undefined) {
    detail.value = await fetchId(params.id)
    if(detail.value == 403){
      haveNoRight.value = true
    }
  }
  if (detail.value === undefined) {
    showAlert.value = false
    alert('The request page is not available')
    router.go(-1)
  }
  if (detail.value.publishDate !== null) {
    detail.value.publishDate = convertDate(detail.value.publishDate)
  }
  if (detail.value.closeDate !== null) {
    detail.value.closeDate = convertDate(detail.value.closeDate)
  }
  if (detail.value.publishDate !== null && detail.value.closeDate !== null) {
    detail.value.publishDate = convertDate(detail.value.publishDate)
    detail.value.closeDate = convertDate(detail.value.closeDate)
  }
})


const options = ref({
  year: 'numeric',
  month: 'short',
  day: 'numeric',
  hour: 'numeric',
  minute: 'numeric'
})
const convertDate = (date) => {
  newDate.value = new Date(date)
  newDate.value = newDate.value.toLocaleString("en-GB", options.value)
  return newDate.value
}
defineEmits(['edit'])


let currentComponent = ref('view')
function setCurrentComponent(currentCom) {
  currentComponent.value = currentCom
}

let editData = ref()
let status = ref()
let setData = () => {
  editData.value = detail.value
  setCurrentComponent('addEdit')
}
</script>
 
<template>
  <div>
    <div v-if="currentComponent === 'view'">
      <div class="text-center py-10">
        <h1 class="text-5xl font-bold">SIT Announcement System (SAS)</h1>
      </div>
      <div>
        <div class="display items-center">
          <div class="flex-between">
            <p class="ml-5 mb-3 font-bold text-lg inline-flex left-element ">Announcement Detail: </p>
            <div>
              <p class="ann-counter item mb-3 text-2xl font-bold  inline-flex right-element ml-4">{{ detail.viewCount }}</p>
              <p class="mr-14 font-bold -mt-4">VIEWS</p>
            </div>
            <!-- <p class="ann-counter item mb-3 text-2xl font-bold  inline-flex right-element mr-5">{{ detail.viewCount }} views</p> -->
          </div>
          <div class="flex flex-row border ">
            <div class="flex flex-col pl-10">
              <table>
                <tr>
                  <td><div class="py-4 font-bold">Title:</div></td>
                  <td><div class="py-4 ann-title ml-10">{{ detail.announcementTitle }}</div></td>
                </tr>
                <tr>
                  <td><div class="py-4 font-bold">Category:</div></td>
                  <td><div class="py-4 ann-category ml-10">{{ detail.announcementCategory }}</div></td>
                </tr>
                <tr>
                  <td><div class="py-4 font-bold">Description:</div></td>
                  <td ><div class="py-4 ann-description ml-6 ql-editor" v-html="detail.announcementDescription"></div></td>
                </tr>
                <tr>
                  <td><div class="py-4 font-bold">Publish Date:</div></td>
                  <td><div class="py-4 ann-publish-date ml-10">{{ detail.publishDate ? detail.publishDate : '-' }}</div></td>
                </tr>
                <tr>
                  <td><div class="py-4 font-bold">Close Date:</div></td>
                  <td> <div class="py-4 ann-close-date ml-10">{{ detail.closeDate ? detail.closeDate : '-' }}</div></td>
                </tr>
                <tr>
                  <td><div class="py-4 font-bold">Display:</div></td>
                  <td> <div class="py-4 ann-display ml-10">{{ detail.announcementDisplay }}</div></td>
                </tr>
              </table>
            </div>
            </div>
            </div>
        <div class="flex flex-row">
          <RouterLink :to="{ name: 'Data' }"><button class="btn btn-outline btn-success mt-6 ml-6 ann-button"
              id="back">Back</button>
          </RouterLink>
          <RouterLink :to="{ name: 'Edit', params: { id: params.id } }">
            <button class="btn btn-outline btn-info mt-6 ml-6 ann-button" id="edit">Edit</button>
          </RouterLink>
          <!-- <button class="btn btn-outline btn-success mt-6 ml-6 ann-button" id="edit">Edit</button> -->
        </div>
      </div>
    </div>
    <!-- <Edit v-else :data="editData" @add="addNew"/> -->
    <div v-show="haveNoRight">
      <div class="popup">
        <div class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-screen h-screen bg-black opacity-60"></div>
      </div>
      <div class="popup">
        <div
          class="p-5 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-1/7 h-2/7 bg-gray-200 border-4 border-gray-700 rounded-xl">
          <div class="relative text-right">
            <RouterLink :to="{ name: 'Data' }">
              <button class="font-bold text-xl hover:bg-red-700 hover:text-white pl-2 pr-2 pb-1 rounded-md">x</button>
            </RouterLink>
          </div>
          <div class="relative flex flex-col justify-center items-center pl-5 pr-5">
            <p class="text-black text-center text-4xl p-6 ">You don't have ACCESS</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
 
<style scoped>
/* .ann-right-angle {
  margin-left: 41cm;
} */
.flex-between {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}
.left-element {
  align-self: flex-start;
}

.right-element {
  align-self: flex-end;
}
td{
  vertical-align: top;
}
</style>
