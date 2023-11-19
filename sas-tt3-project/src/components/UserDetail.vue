<script setup>
import { ref, onMounted, computed, inject, onUpdated, onBeforeUpdate, VueElement, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useRoute } from 'vue-router';
import { useAnnounce, useMode } from '../stores/announcement'
import { fetchId, fetchFile, fetchFileName } from '../code/get.js';
// import { switchMode } from '../stores/closeMode'

// let myMode = switchMode()
const { params } = useRoute()
const router = useRouter();
let detail = ref({})
let newDate = ref()
let showAlert = ref(true)
let myMode = useMode()
const fileName = ref({})

onMounted(async () => {
    if (params.id !== undefined) {
        detail.value = await fetchId(params.id, true)
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
    if (myMode.mode === undefined || myMode.mode === null) {
        if (detail.value.publishDate === convertDate(detail.value.publishDate)) {
            myMode.mode = 'active'
        }
        if (detail.value.closeDate === convertDate(detail.value.closeDate)) {
            myMode.mode = 'close'
        }
    }
    if (myMode.mode === "close") {
        showClosed.value = true
        showActive.value = false
    }

    if (myMode.mode === "active") {
        showClosed.value = false
        showActive.value = true
    }
    fileName.value = await fetchFileName(params.id)
    fileName.value.forEach(async (file) => {
        const blob = await fetchFile(file.fileName);
        // fileData.value = await fetchFile(file.fileName)
        displayFile(blob, file.fileName);
    })
    console.log(fileName.value);
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

function displayFile(blob, fileName) {
    const fileListContainer = document.getElementById('fileList');

    // Determine the appropriate MIME type based on the file extension
    const blobType = getBlobType(fileName);

    // Create or find a container for the specific file type
    let fileTypeContainer = document.getElementById(blobType);
    if (!fileTypeContainer) {
        // Create a container for the file type if it doesn't exist
        fileTypeContainer = document.createElement('div');
        fileTypeContainer.id = blobType;

        // Create a heading for the file type group
        const fileTypeHeading = document.createElement('h2');
        getFriendlyFileType(blobType); // Display a friendly file type name
        fileTypeHeading.style.padding = '8px'; // Add padding for better appearance
        fileTypeContainer.appendChild(fileTypeHeading);

        // Append the file type container to the main list container
        fileListContainer.appendChild(fileTypeContainer);
    }

    // Create a container for each file
    const fileContainer = document.createElement('div');
    fileContainer.style.marginBottom = '10px'; // Add some spacing between files

    // Check the MIME type to determine the file type
    if (isImageType(blobType)) {
        // Display image
        const imgElement = document.createElement('a');
        imgElement.href = URL.createObjectURL(blob);
        imgElement.target = '_blank'
        imgElement.textContent = fileName
        imgElement.alt = fileName; // Set the alt attribute if needed
        imgElement.style.color = 'blue'
        imgElement.style.textDecorationLine = "underline"
        imgElement.style.width = '200px'; // Set the width or other styles as needed
        fileContainer.appendChild(imgElement);
    } else {
        // Display download link for non-image files
        const fileLink = document.createElement('a');
        fileLink.href = URL.createObjectURL(blob);
        fileLink.download = fileName; // Set the download attribute with the file name
        fileLink.textContent = fileName; // Display the file name
        fileLink.style.color = 'blue'
        fileLink.style.textDecorationLine = "underline"
        //fileLink.style.padding = '3px 8px 3px 8px'; // Add some padding for better appearance
        // fileLink.style.border = '1px solid #000000'; // Gray border
        // fileLink.style.borderRadius = '8px'; // Rounded corners
        fileContainer.appendChild(fileLink);
    }

    // Append the file container to the specific file type container
    fileTypeContainer.appendChild(fileContainer);
}

function getFriendlyFileType(mimeType) {
    // Provide friendly names for different file types
    switch (mimeType) {
        case 'image/jpeg':
            return 'JPEG Images';
        case 'image/png':
            return 'PNG Images';
        case 'application/pdf':
            return 'PDF Files';
        case 'application/msword':
            return 'Word Documents';
        default:
            return 'Other Files';
    }
}

function getBlobType(fileName) {
    const extension = fileName.split('.').pop().toLowerCase();

    switch (extension) {
        case 'jpg':
        case 'jpeg':
            return 'image/jpeg';
        case 'png':
            return 'image/png';
        case 'pdf':
            return 'application/pdf';
        case 'doc':
        case 'docx':
            return 'application/msword';
        default:
            return 'application/octet-stream'; // Default MIME type for unknown files
    }
}

function isImageType(mimeType) {
    const supportedImageTypes = ['image/jpeg', 'image/png', 'image/gif'];
    return supportedImageTypes.includes(mimeType);
}
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
            }">{{ detail.announcementCategory }}
             <RouterLink to="/verify" class="ann-button absolute top-26 right-56">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="0.5"
                stroke="none" class="w-14 h-12 stroke-success hover:fill-success">
                <path stroke-linecap="round" stroke-linejoin="round"
                  d="M17.593 3.322c1.1.128 1.907 1.077 1.907 2.185V21L12 17.25 4.5 21V5.507c0-1.108.806-2.057 1.907-2.185a48.507 48.507 0 0111.186 0z" />
              </svg>
            </RouterLink>
        </div>

            <!-- <div v-if="showClosed">
                <div class="ann-close-date pt-2 ml-96 pl-48"><span class="text-red-500 font-semibold">Closed on :
                    </span>{{
                        detail.closeDate }}</div>
            </div> -->
            <div class="border-b-2 border-slate-400"></div>
            <!-- <div class="mx-5 my-5 ann-description font-semibold">{{ detail.announcementDescription }}</div> -->
            <p class="font-semibold text-3xl mx-9 mt-5">Description:</p>
            <div class="mx-5 mb-5 ann-description ql-editor text-xl" v-html="detail.announcementDescription"></div>
            <div class="mx-9 my-5">
                <p class="font-semibold text-3xl mt-5" v-show="fileName.length !== 0">File(s):</p>
                <div id="fileList" class="flex flex-row space-x-5 text-xl">
                </div>
            </div>
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
                 <RouterLink to="/verify" class="ann-button absolute top-26 right-56">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="0.5"
                stroke="none" class="w-14 h-12 stroke-success hover:fill-success">
                <path stroke-linecap="round" stroke-linejoin="round"
                  d="M17.593 3.322c1.1.128 1.907 1.077 1.907 2.185V21L12 17.25 4.5 21V5.507c0-1.108.806-2.057 1.907-2.185a48.507 48.507 0 0111.186 0z" />
              </svg>
            </RouterLink>
                <div class="flex flex-row ml-96 pl-80">
                    <div class="text-red-500 font-semibold pt-2 pr-1">Closed on :</div>
                    <div class="ann-close-date pt-2">{{ detail.closeDate }}</div>
                </div>
            </div>
            <div class="border-b-2 border-slate-400"></div>
            <!-- <div class="mx-5 my-5 ann-description font-semibold">{{ detail.announcementDescription }}</div> -->
            <p class="font-semibold text-3xl mx-9 mt-5">Description:</p>
            <div class="mx-5 mb-5 ann-description ql-editor" v-html="detail.announcementDescription"></div>
            <div class="mx-9 my-5">
                <p class="font-semibold text-3xl mt-5" v-show="fileName.length !== 0">File(s):</p>
                <div id="fileList" class="flex flex-row space-x-5 text-xl">
                </div>
            </div>
            <RouterLink :to="{ name: 'UserAll' }" @click="myMode.setMode('close')"><button
                    class="btn btn-outline btn-success mx-5 ann-button" id="back">Back</button>
            </RouterLink>
        </div>
    </div>
</template>
 
<style scoped></style>