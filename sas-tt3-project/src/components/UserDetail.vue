<script setup>
import { ref, onMounted, computed, inject, onUpdated, onBeforeUpdate, VueElement, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useRoute } from 'vue-router';
import { useAnnounce, useMode } from '../stores/announcement'
import { fetchId } from '../code/get.js';
// import { switchMode } from '../stores/closeMode'

// let myMode = switchMode()
const { params } = useRoute()
const router = useRouter();
let detail = ref({})
let newDate = ref()
let showAlert = ref(true)
let myMode = useMode()

onMounted(async () => {
    if (params.id !== undefined) {
        detail.value = await fetchId(params.id)
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

    if (myMode.mode === "close") {
        showClosed.value = true
        showActive.value = false
    }

    if (myMode.mode === "active") {
        showClosed.value = false
        showActive.value = true
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

let showClosed = ref()
let showActive = ref()
// function showClosedVal() {
//     showClosed.value = !showClosed.value
// }
</script>
 
<template>
    <div class="flex flex-col mt-16 mx-56 border-2 border-slate-400 rounded-md" v-if="showActive">
        <div class="my-5">
            <div class="mx-5 font-black text-3xl ann-title ">{{ detail.announcementTitle }}</div>
            <div class="mx-5 mb-3 text-gray-500 ann-category mt-2" :class="{
                'badge badge-info badge-lg text-white': detail.announcementCategory === 'ทั่วไป',
                'badge badge-success badge-lg text-white': detail.announcementCategory === 'ทุนการศึกษา',
                'badge badge-error badge-lg text-white': detail.announcementCategory === 'หางาน',
                'badge badge-warning badge-lg text-white': detail.announcementCategory === 'ฝึกงาน'
            }">{{ detail.announcementCategory }}</div>

            <!-- <div v-if="showClosed">
                <div class="ann-close-date pt-2 ml-96 pl-48"><span class="text-red-500 font-semibold">Closed on :
                    </span>{{
                        detail.closeDate }}</div>
            </div> -->
            <div class="border-b-2 border-slate-400"></div>
            <!-- <div class="mx-5 my-5 ann-description font-semibold">{{ detail.announcementDescription }}</div> -->
            <div class="mx-5 my-5 ann-description ql-editor" v-html="detail.announcementDescription"></div>
            <RouterLink :to="{ name: 'UserAll' }" @click="myMode.setMode('active')"><button
                    class="btn btn-outline btn-success mx-5 ann-button" id="back">Back</button>
            </RouterLink>
        </div>
    </div>

    <!-- close -->
    <div class="flex flex-col mt-16 mx-56 border-2 border-slate-400 rounded-md" v-if="showClosed">
        <div class="my-5">
            <div class="mx-5 font-black text-3xl ann-title">{{ detail.announcementTitle }}</div>
            <div class="flex flex-row">
                <div class="mx-5 mb-3 text-gray-500 ann-category mt-2 " :class="{
                    'badge badge-info badge-lg text-white': detail.announcementCategory === 'ทั่วไป',
                    'badge badge-success badge-lg text-white': detail.announcementCategory === 'ทุนการศึกษา',
                    'badge badge-error badge-lg text-white': detail.announcementCategory === 'หางาน',
                    'badge badge-warning badge-lg text-white': detail.announcementCategory === 'ฝึกงาน'
                }">{{ detail.announcementCategory }}</div>
                <div class="flex flex-row ml-96 pl-80">
                    <div class="text-red-500 font-semibold pt-2 pr-1">Closed on :</div>
                    <div class="ann-close-date pt-2">{{ detail.closeDate }}</div>
                </div>
            </div>
            <div class="border-b-2 border-slate-400"></div>
            <!-- <div class="mx-5 my-5 ann-description font-semibold">{{ detail.announcementDescription }}</div> -->
            <div class="mx-5 my-5 ann-description ql-editor" v-html="detail.announcementDescription"></div>
            <RouterLink :to="{ name: 'UserAll' }" @click="myMode.setMode('close')"><button
                    class="btn btn-outline btn-success mx-5 ann-button" id="back">Back</button>
            </RouterLink>
        </div>
    </div>
</template>
 
<style scoped></style>