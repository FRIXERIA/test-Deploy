<script setup>
import { ref, onMounted, computed, onBeforeUpdate, onBeforeMount, defineEmits } from 'vue'
import { fetchId, fetchFile, fetchFileName } from '../code/get.js';
import { useRoute } from 'vue-router'
import { useRouter } from 'vue-router'
// import Edit from './Edit.vue'
const { params } = useRoute()
const router = useRouter();

let detail = ref({})
let newDate = ref()
let showAlert = ref(true)
const haveNoRight = ref(false)
const fileData = ref(new Blob([], { type: 'image/jpeg' }))
const fileName = ref({})
const myImage = ref('')
const previewImageFile = ref('')

const previewBinaryFile = (binaryFileObject) => {
  return URL.createObjectURL(binaryFileObject)
}

onMounted(async () => {
  // fileData.value = fetchFile()
  // console.log(fileData.value);
  fileName.value = await fetchFileName(params.id)
  fileName.value.forEach(async (file) => {
    const blob = await fetchFile(file.fileName);
    // fileData.value = await fetchFile(file.fileName)
    displayFile(blob, file.fileName);
  })
  console.log(fileName.value);
  if (params.id !== undefined) {
    detail.value = await fetchId(params.id)
    if (detail.value == 403) {
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

// function displayImages(array, blob) {
//   const container = document.getElementById('imageContainer');

//   // Iterate over the array and create image elements
//   array.forEach(item => {
//     // Check if your blobData has the appropriate data before creating the URL
//     if (blob.size > 0) {
//       const imageUrl = URL.createObjectURL(blob);

//       // Create image element
//       const imgElement = document.createElement('img');
//       imgElement.src = imageUrl;
//       imgElement.alt = item.fileName; // Set the alt attribute if needed
//       imgElement.style.width = '200px'; // Set the width or other styles as needed

//       // Append image element to the container
//       container.appendChild(imgElement);
//     }
//   });
// }

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
              <p class="ann-counter item mb-3 text-2xl font-bold  inline-flex right-element ml-4">{{ detail.viewCount }}
              </p>
              <p class="mr-14 font-bold -mt-4">VIEWS</p>
            </div>
            <!-- <p class="ann-counter item mb-3 text-2xl font-bold  inline-flex right-element mr-5">{{ detail.viewCount }} views</p> -->
          </div>
          <div class="flex flex-row border ">
            <div class="flex flex-col pl-10">
              <table>
                <tr>
                  <td>
                    <div class="py-4 font-bold">Title:</div>
                  </td>
                  <td>
                    <div class="py-4 ann-title ml-10">{{ detail.announcementTitle }}</div>
                  </td>
                </tr>
                <tr>
                  <td>
                    <div class="py-4 font-bold">Category:</div>
                  </td>
                  <td>
                    <div class="py-4 ann-category ml-10">{{ detail.announcementCategory }}</div>
                  </td>
                </tr>
                <tr>
                  <td>
                    <div class="py-4 font-bold">Description:</div>
                  </td>
                  <td>
                    <div class="py-4 ann-description ml-6 ql-editor" v-html="detail.announcementDescription"></div>
                  </td>
                </tr>
                <tr>
                  <td>
                    <div class="py-4 font-bold">Publish Date:</div>
                  </td>
                  <td>
                    <div class="py-4 ann-publish-date ml-10">{{ detail.publishDate ? detail.publishDate : '-' }}</div>
                  </td>
                </tr>
                <tr>
                  <td>
                    <div class="py-4 font-bold">Close Date:</div>
                  </td>
                  <td>
                    <div class="py-4 ann-close-date ml-10">{{ detail.closeDate ? detail.closeDate : '-' }}</div>
                  </td>
                </tr>
                <tr>
                  <td>
                    <div class="py-4 font-bold">Display:</div>
                  </td>
                  <td>
                    <div class="py-4 ann-display ml-10">{{ detail.announcementDisplay }}</div>
                  </td>
                </tr>
                <tr>
                  <td class="py-4 font-bold">File(s):</td>
                  <td>
                    <div id="fileList" class="flex flex-row space-x-5">
                    </div>
                  </td>
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

td {
  vertical-align: top;
}
</style>
