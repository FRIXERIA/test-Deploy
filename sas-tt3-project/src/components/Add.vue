<script setup>
import { ref, onMounted, computed, onBeforeUpdate, onBeforeMount, VueElement, onUpdated } from 'vue'
import { fetchId,fetchCategory} from '../code/get.js';
import { useRoute } from 'vue-router'
import { useRouter } from 'vue-router'
const { params } = useRoute()
const router = useRouter();
let useButton = ref(true)
let category =ref({})
onMounted(async () => {

    category.value=await fetchCategory()
    console.log(category.value)

    // if(params.id){

        // fetchId(params.id)
    // }
    //     if(props.data===undefined){
    // data.value ={
    //     announcementTitle:"",
    //     announcementDescription:"",
    //     publishDate:"",
    //     closeDate:"",
    //     announcementDisplay:"",
    //     announcementCategory:""
    // }
    //     }


    data.value = {
        announcementTitle: "",
        announcementDescription: "",
        publishDate: "",
        closeDate: "",
        announcementDisplay: "",
        announcementCategory: "",
        categoryId:"1"
    }
    console.log(data.value)




})

onUpdated(() => {
    if(params.id){
    if (data.value.announcementTitle.trim() !== '') {
        useButton.value = false
    }

    if (data.value.announcementCategory.trim() !== '') {
        useButton.value = false
    }
    if (data.value.announcementDescription.trim() !== '') {
        useButton.value = false
    }
    if (data.value.publishDate.trim() !== '') {
        useButton.value = false
    }
    if (data.value.closeDate.trim() !== '') {
        useButton.value = false
    }
    if (data.value.announcementDisplay==true) {
        useButton.value = false
    }

    data.value.categoryId=1
}
})
let data = ref({})
const props = defineProps({
    data: { type: Object }
})
function cancelAction() {
    router.go(-1)
}


// let submitEdit =()=>{

// }
defineEmits(['edit', 'add'])

let test = () => {
    console.log(data.value)
}

// let addData = ()=>{
//     provide('newAdd', data.value)

// }

let addNew=( async(newQuestion)=>{
  const newAdd = inject('newAdd')
  console.log('porchaa')
console.log(newQuestion)
try{
  console.log(newQuestion.categoryId)
const res=await fetch(`http://localhost:8080/api/announcements/${newQuestion.categoryId}`,{
  method:"POST",
  headers:{
    'content-type':'application/json'
  },
  body:JSON.stringify({
    id:5,
    announcementTitle:newQuestion.announcementTitle,
    announcementCategory:newQuestion.announcementCategory,
    announcementDescription:newQuestion.announcementDescription,
    publishDate:newQuestion.publishDate,
    closeDate:newQuestion.closeDate,
    announcementDisplay:newQuestion.announcementDisplay
  })
})

if(res.status===201){
  const addNewQuestions=await res.json()
announcement.value.push(addNewQuestions)
console.log(announcement.value)
// setCurrentComponent('QuestionManagement')
}
else if(res.status===400){
alert('cannot add')
}
else{
  throw Error('cannot add!!')
}
}
catch (err){
console.log(err)
}
})

</script>

<template>
    <div class="flex flex-col ml-16 mr-16">
        <div>
            <p class="mb-3 font-bold text-lg pt-5">Announcement Detail:</p>
        </div>
        <div class="font-medium">Title</div>
        <div class="pt-1">
            <input class="ann-title w-full border border-teal-500 rounded-lg" v-model="data.announcementTitle" type="text"
                maxlength="200">
        </div>
        <div class="pt-3 font-medium">Category</div>
        <div class="pt-1" id="selectCategory">
            <select v-model="data.announcementCategory" class="block appearance-none w-1/6  bg-white border-gray-400 hover:border-gray-500 px-4 py-2 pr-8 rounded-full shadow">
                <option v-for="(categorys, index) in category" :key="index">{{ categorys.categoryName }}</option>
            </select>
        </div>
        <div class="pt-3 font-medium">Description</div>
        <div class="h-48 pt-1">
            <textarea type="textarea" class="ann-description w-full h-48 border border-teal-500 rounded-lg"
                maxlength="10000" v-model="data.announcementDescription"></textarea>
        </div>
        <div class="pt-3 font-medium">Publish Date</div>
        <div class="flex flex-row">
            <input class="ann-publish-date w-40 border border-teal-500 rounded-lg" type="date" v-model="data.publishDate">
            <input class="ann-publish-time ml-3 w-40 border border-teal-500 rounded-lg" type="time" data.publishDate>
        </div>
        <div class="pt-3 font-medium">Close Date</div>
        <div class="flex flex-row">
            <input class="ann-close-date w-40 border border-teal-500 rounded-lg" type="date" v-model="data.closeDate">
            <input class="ann-close-time ml-3 w-40 border border-teal-500 rounded-lg" type="time" v-model="data.closeDate">
        </div>
        <div class="pt-3 font-medium">Display</div>
        <div>
            <input class="ann-display mr-1 border border-teal-500 rounded-lg" type="checkbox"
                v-model="data.announcementDisplay">Check to show this announcement
        </div>
        <div class="pt-5 flex flex-row">
            <button type="submit" class="btn btn-outline btn-success ann-button" id="submit" v-if="data.id">Edit</button>
            <RouterLink :to="{ name: 'Data' }">
            <button type="submit" class="btn btn-outline btn-success ann-button" id="submit"
                @click=addData :disabled=useButton>Add</button>
            </RouterLink>
            <!-- <button type="submit" class="btn btn-outline btn-success ann-button" id="submit" @click="test">Add</button> -->
            <button type="button" @click="cancelAction()"
            class="btn btn-outline btn-error ann-button ml-2">Cancel</button>
    </div>
</div></template>