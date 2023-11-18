<script setup>
import {
  ref,
  onMounted,
  computed,
  inject,
  onUpdated,
  onBeforeUpdate,
  VueElement,
  watch,
} from "vue";
import { useRouter } from "vue-router";
import { useAnnounce } from "../stores/announcement.js";
import { fetchToken } from "../code/get.js";
const myAnnounce = useAnnounce();
const loginfirst = ref(false);
const payloadObject = ref();
const isAdmin = ref(false);
const session = ref(false);
const delSuccess = ref(false);
let showError = ref(false);
let error = ref("");
const check = ref(false)
// const announcement = ref([])
// const someData = ref([])
// let addNewData =ref()
// watch(addNewData,()=>{
//   announcement.value.push(addNewData.value)
// })
// const newAdd = ref()
onMounted(async () => {
  if (!jwtToken) {
    loginfirst.value = true;
    return;
  }
  console.log(jwtToken)
  const base64Payload = jwtToken.split(".")[1];
  const decodePayload = atob(base64Payload);
  payloadObject.value = JSON.parse(decodePayload);
  console.log(payloadObject.value);
  if (payloadObject.value.role == "announcer") {
    // await myAnnounce.fetchAnnounce();
    console.log("announcer");
  }
  if (payloadObject.value.role == "admin") {
    console.log("admin");
    isAdmin.value = true;
  }
  if (jwtToken) {
    let res = await fetchToken(refreshToken);
    console.log(res);
    if (res == 200) {
      await myAnnounce.fetchAnnounce();
      console.log("token 200");
    }
    if (res == 401) {
      session.value = true;
      console.log("token 401");
      return;
    }
    await myAnnounce.fetchAnnounce();
    if (myAnnounce.announcement.length == 0) {
      console.log("length = 0");
      error.value = "No Announcements";
      showError.value = true;
    }
    if (myAnnounce.announcement.length !== 0) {
      showError.value = false;
    }

    myAnnounce.announcement = myAnnounce.announcement.map((data) => {
      if (data.publishDate !== null) {
        data.publishDate = convertDate(data.publishDate);
      }
      if (data.closeDate !== null) {
        data.closeDate = convertDate(data.closeDate);
      }

      return data;
    });
  }
}
);

onBeforeUpdate(() => {
  myAnnounce.announcement = myAnnounce.announcement.map((data) => {
    if (data.publishDate !== null) {
      data.publishDate = convertDate(data.publishDate);
    }
    if (data.closeDate !== null) {
      data.closeDate = convertDate(data.closeDate);
    }

    return data;
  });
});

let showDate = ref(true);
let NoDate = ref();
const router = useRouter();
const date = ref({});
let newDate = ref();
//tokens
const jwtToken = localStorage.getItem("jwtToken");
const refreshToken = localStorage.getItem("refreshToken");
const options = ref({
  year: "numeric",
  month: "short",
  day: "numeric",
  hour: "numeric",
  minute: "numeric",
});
const convertDate = (date) => {
  newDate.value = new Date(date);
  newDate.value = newDate.value.toLocaleString("en-GB", options.value);
  return newDate.value;
};

let showDetail = ref(false);
// function showDetails() {
//   showDetail.value = !showDetail.value
// }

const timeZoneName = ref(Intl.DateTimeFormat().resolvedOptions().timeZone);

let currentComponent = ref("view");
// function setCurrentComponent(currentCom) {
//   currentComponent.value = currentCom
// }

const deleteAnnouncement = async (deleteId) => {
  try {
    const res = await fetch(
      `${import.meta.env.VITE_ROOT_API}/api/announcements/${deleteId}`,
      {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${jwtToken}`,
        },
      }
    );
    if (res.ok) {
      myAnnounce.announcement = myAnnounce.announcement.filter((a) => {
        return a.id !== deleteId;
      });
      deleteFiles(deleteId)
      delSuccess.value = true;
      setTimeout(() => {
        delSuccess.value = false;
      }, 2000);
      if (res.status === 401) {
        if ((await res.text()) == "JWT Token has expired") {
          await fetchToken(refreshToken);
        }
      }
    } else {
      const errorResponse = await res.json();
      alert(`There is an error : ${JSON.stringify(errorResponse)}`);
    }
  } catch (err) {
    alert(err);
  }
};

const annId = ref()

let deleteCheck = (id) => {
  check.value = true
  return annId.value = id
  //deleteAnnouncement(id)
  // if (confirm("Do you want to delete")) {
  //   deleteAnnouncement(id);
  // } else {
  //   doNothing();
  // }
};

const deleted = (id) => {
  deleteAnnouncement(id)
  check.value = false
}

const signOut = () => {
  localStorage.removeItem("jwtToken");
  localStorage.removeItem("refreshToken");
  //loginfirst.value = false
  router.push({ name: "UserLogin" });
};

const deleteFiles = async (deleteId) => {
  try {
    const res = await fetch(
      `${import.meta.env.VITE_ROOT_API}/api/files/fileName/${deleteId}`,
      {
        method: "DELETE",
      }
    );
    if (res.ok) {
      console.log('Files deleted successfully');
      const deletedFiles = await res.text();
      console.log(deletedFiles);
      // Perform any additional logic as needed
    } else {
      console.error('Failed to delete files');
    }
  } catch (error) {
    console.error('Error during file delete:', error);
  }
}
</script>

<template>
  <div>
    <div class="bg-teal-600 h-20">
      <div v-if="jwtToken !== null">
        <button class="btn btn-outline btn-error absolute top-3 right-6 h-13 w-28 my-1 bg-white" @click="signOut">
          SIGN OUT
        </button>
        <RouterLink to="/announcement">
          <button class="btn btn-outline btn-success absolute top-3 bg-white ml-2">
            Announcement (Viewer)
          </button>
        </RouterLink>
        <RouterLink to="/admin/announcement">
          <button class="btn btn-outline btn-success absolute top-3 left-52 bg-white ml-5">
            Announcement
          </button>
        </RouterLink>
        <div v-if="isAdmin">
          <RouterLink to="/admin/user">
            <button class="btn btn-outline btn-success absolute top-3 left-80 w-40 bg-white" style="margin-left: 65px">
              User
            </button>
          </RouterLink>
          <RouterLink :to="{ name: 'MatchPwd' }">
            <button class="btn btn-outline btn-success absolute top-3 left-80 w-40 bg-white" style="margin-left: 233px">
              Match Password
            </button>
          </RouterLink>
        </div>
      </div>
    </div>
    <div>
      <div class="w-full items-center">
        <div class="text-center py-10">
          <h1 class="text-5xl font-bold">SIT Announcement System (SAS)</h1>
        </div>
        <div v-if="!showDetail">
          <p class="ml-5 mr-96 font-bold text-lg place-items-left mb-3">
            Date/Time shown In Timezone : {{ timeZoneName }}
          </p>
          <RouterLink to="/admin/announcement/add">
            <button @click="" class="btn btn-outline btn-success ann-button absolute top-48 right-11 h-13 w-40">
              Add Announcement
            </button>
          </RouterLink>
        </div>
        <div class="inline-flex" v-if="!showDetail">
          <table class="table-fixed w-full">
            <thead class="border bg-teal-100 h-16">
              <tr>
                <th class="pl-20 pr-2 text-lg">No.</th>
                <th class="pl-20 pr-10 sm:pl-10 text-lg">Title</th>
                <th class="pl-5 text-lg">Category</th>
                <th class="text-lg">Publish Date</th>
                <th class="text-lg">Close Date</th>
                <th class="text-lg">Display</th>
                <th class="text-lg">Views</th>
                <th class="text-lg" v-if="isAdmin">Owner</th>
                <th class="pr-5 sm:pr-16 text-lg">Action</th>
              </tr>
            </thead>
          </table>
        </div>
        <h1 class="font-bold text-6xl flex justify-center py-72 text-red-500 sm:text-6xl" v-if="showError">
          {{ error }}
        </h1>
        <div v-for="(q, index) in myAnnounce.announcement" :key="index" class="w-full items-center border py-2"
          v-if="!showDetail">
          <!-- ยังไม่กด -->
          <div class="items-center text-center px-10" v-if="!showDetail">
            <div class="inline-flex">
              <table class="w-full table-fixed">
                <tbody>
                  <tr class="ann-item">
                    <td class="sm:pl-2 pr-4">{{ index + 1 }}</td>
                    <td class="text-left pl-8 ann-title">
                      {{ q.announcementTitle }}
                    </td>
                    <td class="pl-18 mr-4 ann-category" :class="{
                      'badge badge-info badge-lg text-white':
                        q.announcementCategory === 'ทั่วไป',
                      'badge badge-success badge-lg text-white':
                        q.announcementCategory === 'ทุนการศึกษา',
                      'badge badge-error badge-lg text-white':
                        q.announcementCategory === 'หางาน',
                      'badge badge-warning badge-lg text-white':
                        q.announcementCategory === 'ฝึกงาน',
                    }">
                      {{ q.announcementCategory }}
                    </td>
                    <td class="ann-publish-date">
                      {{ q.publishDate ? q.publishDate : "-" }}
                    </td>
                    <td class="pl-9 ann-close-date">
                      {{ q.closeDate ? q.closeDate : "-" }}
                    </td>
                    <td class="pl-10 ann-display">
                      {{ q.announcementDisplay }}
                    </td>
                    <td class="pl-10 ann-display ann-counter">
                      {{ q.viewCount }}
                    </td>
                    <td class="pl-14" v-if="isAdmin">
                      {{ q.announcementOwner }}
                    </td>
                    <td class="pl-14 sm:pl-12">
                      <div class="flex flex-row justify-center">
                        <router-link :to="{ name: 'Detail', params: { id: q.id } }">
                          <button class="btn btn-outline btn-info ann-button ann-button">
                            view
                          </button>
                        </router-link>
                        <button class="btn btn-outline btn-error ann-button ann-button ml-2" @click="deleteCheck(q.id)">
                          Delete
                        </button>
                        <div v-show="check">
                          <div class="popup">
                            <div
                              class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-screen h-screen bg-black opacity-20">
                            </div>
                          </div>
                          <div class="popup">
                            <div
                              class="p-5 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-1/7 h-2/7 bg-white border-4 border-gray-500 rounded-xl">
                              <div class="relative flex flex-col text-center justify-center items-center pl-5 pr-5">
                                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5"
                                  stroke="currentColor" class="w-24 h-24">
                                  <path stroke-linecap="round" stroke-linejoin="round"
                                    d="M9.879 7.519c1.171-1.025 3.071-1.025 4.242 0 1.172 1.025 1.172 2.687 0 3.712-.203.179-.43.326-.67.442-.745.361-1.45.999-1.45 1.827v.75M21 12a9 9 0 11-18 0 9 9 0 0118 0zm-9 5.25h.008v.008H12v-.008z" />
                                </svg>
                                <p class="text-black text-center text-3xl p-6 font-semibold">Are you sure?</p>
                                <p class="text-lg text-gray-600">If you confirm, This category will be deleted.</p>
                                <div class="flex flex-row space-x-5 mt-5 h-12">
                                  <button @click="deleted(annId)"
                                    class="w-24 rounded-lg bg-green-500 text-xl hover:bg-green-900 hover:text-white">YES</button>
                                  <button @click="check = false"
                                    class="w-24 rounded-lg bg-red-500 text-xl hover:bg-red-900 hover:text-white">NO</button>
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
      <!-- <div>
      <Edit @add=""/> 
    </div> -->
      <RouterView></RouterView>
      <!-- <div v-if="showDetail">
        <Detail/>
      </div> -->
    </div>
    <div v-show="loginfirst">
      <div class="popup">
        <div class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-screen h-screen bg-black opacity-60"></div>
      </div>
      <div class="popup">
        <div
          class="p-5 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-1/7 h-2/7 bg-gray-200 border-4 border-gray-700 rounded-xl">
          <div class="relative text-right">
            <RouterLink :to="{ name: 'UserLogin' }">
              <button class="font-bold text-xl hover:bg-red-700 hover:text-white pl-2 pr-2 pb-1 rounded-md">
                x
              </button>
            </RouterLink>
          </div>
          <div class="relative flex flex-col justify-center items-center pl-5 pr-5">
            <p class="text-black text-center text-4xl p-6 \">
              You must LOGIN First
            </p>
          </div>
        </div>
      </div>
    </div>
    <div v-show="session">
      <div class="popup">
        <div class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-screen h-screen bg-black opacity-60"></div>
      </div>
      <div class="popup">
        <div
          class="p-5 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-1/7 h-2/7 bg-gray-200 border-4 border-gray-700 rounded-xl">
          <div class="relative text-right">
            <RouterLink :to="{ name: 'UserLogin' }">
              <button class="font-bold text-xl hover:bg-red-700 hover:text-white pl-2 pr-2 pb-1 rounded-md">
                x
              </button>
            </RouterLink>
          </div>
          <div class="relative flex flex-col justify-center items-center pl-5 pr-5 pb-6">
            <p class="text-black text-center text-4xl p-6 \">
              Session is expired
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div v-show="delSuccess">
    <div class="popup">
      <div class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-screen h-screen bg-black opacity-60"></div>
    </div>
    <div class="popup">
      <div
        class="p-5 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-1/7 h-2/7 bg-sky-200 border-4 border-sky-500 rounded-xl">
        <div class="relative flex flex-col justify-center items-center pl-5 pr-5">
          <p class="text-black text-center text-4xl p-6 \">DELETED!!</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
