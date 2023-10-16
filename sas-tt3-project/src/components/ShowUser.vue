<script setup>
import { ref, onMounted, onBeforeMount, onBeforeUpdate } from "vue";
import { fetchToken, fetchUser } from "../code/get.js";
import { useRouter } from "vue-router";
import { useUser } from "../stores/user.js";
import { useToken } from "../stores/token.js";
import { useAnnounce } from "../stores/announcement.js";
const timeZoneName = ref(Intl.DateTimeFormat().resolvedOptions().timeZone);
const router = useRouter();
let showDetail = ref(false);
let myUser = useUser();
let showError = ref(true);
const myAnnounce = useAnnounce();
const options = ref({
  year: "numeric",
  month: "short",
  day: "numeric",
  hour: "numeric",
  minute: "numeric",
});
let create = ref();
let update = ref();
let allUser = ref({});
let newDate = ref();
let error = ref();
const isDesktop = ref(false);
const tranfer = ref(false);
let cannotDelete =ref(false)

//tokens
const jwtToken = localStorage.getItem("jwtToken");
const refreshToken = localStorage.getItem("refreshToken");
const payloadObject = ref();

//show
const isAdmin = ref(false);
const isAnnouncer = ref(false);

//pop-up
const delSuccess = ref(false);
const loginfirst = ref(false);
const session = ref(false);
const haveNoRight = ref(false);


const convertDate = (date) => {
  newDate.value = new Date(date);
  newDate.value = newDate.value.toLocaleString("en-GB", options.value);
  return newDate.value;
};

onMounted(async () => {
  if (!jwtToken) {
    loginfirst.value = true;
    return;
  }
  const base64Payload = jwtToken.split(".")[1];
  const decodePayload = atob(base64Payload);
  payloadObject.value = JSON.parse(decodePayload);

  if (payloadObject?.value?.role == "admin") {
    await myUser.fetch();
    isAdmin.value = true;
  }
  if (payloadObject?.value?.role == "announcer") {
    haveNoRight.value = true;
    return;
  }
  let res = await fetchToken(refreshToken);
  if (res == 200) {
    await myUser.fetch();
  }
  if (res == 401) {
    session.value = true;
    localStorage.removeItem("jwtToken");
    localStorage.removeItem("refreshToken");
    return;
  } else {
    await myUser.fetch();
    myUser.user = myUser.user.map((data) => {
      if (data.createdOn !== null) {
        data.createdOn = convertDate(data.createdOn);
      }
      if (data.updatedOn !== null) {
        data.updatedOn = convertDate(data.updatedOn);
      }

      return data;
    });
  }
  if (myUser.user.length === 0) {
    error.value = "No Users";
  }
  if (myUser.user.length !== 0) {
    showError.value = false;
  }
});

const deleteUser = async (deleteId) => {
  try {
    const res = await fetch(
      `${import.meta.env.VITE_ROOT_API}/api/users/${deleteId}`,
      {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${jwtToken}`,
        },
      }
    );
    if (res.ok) {
      myUser.user = myUser.user.filter((user) => {
        return user.id !== deleteId;
      });

      delSuccess.value = true;
      setTimeout(() => {
        delSuccess.value = false;
      }, 2000);
    } 
    // else if (res.status==400){
    // cannotDelete.value = true;
    //   setTimeout(() => {
    //     cannotDelete.value = false;
    //   }, 2000);
    //   return;
    // }
    else {
      const errorResponse = await res.json();
      alert(`There is an error : ${JSON.stringify(errorResponse)}`);
    }
  } catch (err) {
    alert(err);
  }
};
function doNothing() {
  router.go(0);
}

const deleteCheck = (id,username) => {
if (username == payloadObject.value.sub) {
  cannotDelete.value = true;
      setTimeout(() => {
        cannotDelete.value = false;
      }, 2000);
      return;
}
  if (
    confirm(
      "The announcements owned by this user will be transfered to you. Do you still want to delete this user?"
    )
  ) {
    deleteUser(id);
  } else {
    doNothing();
  }
};

const signOut = () => {
  localStorage.removeItem("jwtToken");
  localStorage.removeItem("refreshToken");
  loginfirst.value = false;
  router.push({ name: "UserLogin" });
};
</script>

<template>
  <div>
    <div class="flex flex-row w-full max-h-full h-full">
      <div class="flex flex-col">
        <p class="ann-app-title font-bold text-2xl w-40 pl-5 pt-3 pb-3">SAS</p>
        <button
          class="ann-menu btn btn-outline btn-success font-semibold w-36 ml-3 mt-2 mb-2"
        >
          <RouterLink :to="{ name: 'UserAll' }"
            >Announcement (Viewer)</RouterLink
          >
        </button>
        <button
          class="ann-menu btn btn-outline btn-success font-semibold w-36 ml-3 mt-2 mb-2"
        >
          <RouterLink :to="{ name: 'Data' }">Announcement</RouterLink>
        </button>
        <button
          class="ann-menu btn btn-outline btn-success font-semibold w-36 ml-3 mt-2 mb-2"
          v-if="isAdmin"
        >
          <RouterLink :to="{ name: 'ShowUser' }">User</RouterLink>
        </button>
        <button
          class="ann-menu btn btn-outline btn-success font-semibold w-36 ml-3 mt-2 mb-2"
          v-if="isAdmin"
        >
          <RouterLink :to="{ name: 'MatchPwd' }">Match Password</RouterLink>
        </button>
        <div class="mt-24">
          <button
            class="ann-menu btn btn-outline btn-error font-semibold w-36 ml-3 mt-2 mb-2"
            @click="signOut"
          >
            Sign Out
          </button>
        </div>
      </div>
      <div class="w-full p-5 flex flex-col">
        <p class="ann-title font-bold text-3xl flex justify-center">
          User Management
        </p>
        <div class="flex flex-row pb-5" v-if="!showDetail">
          <p class="ann-timezone font-semibold pt-5">
            Date/Time shown in Timezone:{{ timeZoneName }}
          </p>
          <RouterLink :to="{ name: 'AddUser' }"
            ><button
              class="ann-button btn btn-outline btn-success ann-button absolute right-11 h-13 w-40 mt-2"
            >
              Add User
            </button></RouterLink
          >
        </div>
        <div class="overflow-x-auto flex w-full max-h-full h-full items-center">
          <div v-if="!showDetail" class="items-center">
            <table
              class="table-fixed w-full border max-h-full h-full items-center"
            >
              <!-- head -->
              <thead class="bg-teal-100 h-20">
                <tr class="border max-h-full h-full">
                  <th>No.</th>
                  <th class="pr-2">Username</th>
                  <th class="pr-5">Name</th>
                  <th class="pr-5">Email</th>
                  <th>Role</th>
                  <th><span style="padding-right: 78px">Created On</span></th>
                  <th><span style="padding-right: 68px">Updated On</span></th>
                  <th><span style="padding-right: 20px">Action</span></th>
                </tr>
              </thead>
              <tbody
                v-for="(user, index) in myUser.user"
                :key="index"
                class="h-20 space-x-5"
                v-if="!showDetail"
              >
                <tr class="border ann-item max-h-full h-full">
                  <th>{{ index + 1 }}</th>
                  <td class="ann-username pl-11">{{ user.username }}</td>
                  <td class="ann-name pl-14">{{ user.name }}</td>
                  <td class="ann-email pl-14">{{ user.email }}</td>
                  <td class="ann-role">
                    <span style="padding-left: 85px">{{ user.role }}</span>
                  </td>
                  <td class="ann-created-on pl-14">{{ user.createdOn }}</td>
                  <td class="ann-updated-on pl-14">{{ user.updatedOn }}</td>
                  <td>
                    <div class="flex flex-row">
                      <router-link
                        :to="{ name: 'EditUser', params: { id: user.id } }"
                      >
                        <button
                          class="btn btn-outline btn-info ann-button ann-button"
                        >
                          edit
                        </button>
                      </router-link>
                      <button
                        class="btn btn-outline btn-error ann-button ann-button ml-2"
                        @click="deleteCheck(user.id,user.username)"
                      >
                        delete
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
            <div class="flex w-full text-center pt-10">
              <h1
                class="font-bold text-6xlpy-72 text-red-500 sm:text-6xl w-full"
                v-if="showError"
              >
                {{ error }}
              </h1>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-show="delSuccess">
      <div class="popup">
        <div
          class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-screen h-screen bg-black opacity-60"
        ></div>
      </div>
      <div class="popup">
        <div
          class="p-5 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-1/7 h-2/7 bg-sky-200 border-4 border-sky-500 rounded-xl"
        >
          <div
            class="relative flex flex-col justify-center items-center pl-5 pr-5"
          >
            <p class="text-black text-center text-4xl p-6 \">USER DELETED!!</p>
          </div>
        </div>
      </div>
    </div>
    <div v-show="loginfirst">
      <div class="popup">
        <div
          class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-screen h-screen bg-black opacity-60"
        ></div>
      </div>
      <div class="popup">
        <div
          class="p-5 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-1/7 h-2/7 bg-gray-200 border-4 border-gray-700 rounded-xl"
        >
          <div class="relative text-right">
            <RouterLink :to="{ name: 'UserLogin' }">
              <button
                class="font-bold text-xl hover:bg-red-700 hover:text-white pl-2 pr-2 pb-1 rounded-md"
              >
                x
              </button>
            </RouterLink>
          </div>
          <div
            class="relative flex flex-col justify-center items-center pl-5 pr-5"
          >
            <p class="text-black text-center text-4xl p-6 \">
              You must LOGIN First
            </p>
          </div>
        </div>
      </div>
    </div>
    <div v-show="session">
      <div class="popup">
        <div
          class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-screen h-screen bg-black opacity-60"
        ></div>
      </div>
      <div class="popup">
        <div
          class="p-5 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-1/7 h-2/7 bg-gray-200 border-4 border-gray-700 rounded-xl"
        >
          <div class="relative text-right">
            <RouterLink :to="{ name: 'UserLogin' }">
              <button
                class="font-bold text-xl hover:bg-red-700 hover:text-white pl-2 pr-2 pb-1 rounded-md"
              >
                x
              </button>
            </RouterLink>
          </div>
          <div
            class="relative flex flex-col justify-center items-center pl-5 pr-5 pb-6"
          >
            <p class="text-black text-center text-4xl p-6 \">
              Session is expired
            </p>
          </div>
        </div>
      </div>
    </div>
    <div v-show="haveNoRight">
      <div class="popup">
        <div
          class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-screen h-screen bg-black opacity-60"
        ></div>
      </div>
      <div class="popup">
        <div
          class="p-5 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-1/7 h-2/7 bg-gray-200 border-4 border-gray-700 rounded-xl"
        >
          <div class="relative text-right">
            <RouterLink :to="{ name: 'Data' }">
              <button
                class="font-bold text-xl hover:bg-red-700 hover:text-white pl-2 pr-2 pb-1 rounded-md"
              >
                x
              </button>
            </RouterLink>
          </div>
          <div
            class="relative flex flex-col justify-center items-center pl-5 pr-5"
          >
            <p class="text-black text-center text-4xl p-6">
              You don't have ACCESS
            </p>
          </div>
        </div>
      </div>
    </div>

    <div v-show="cannotDelete">
      <div class="popup">
        <div
          class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-screen h-screen bg-black opacity-60"
        ></div>
      </div>
      <div class="popup">
        <div
          class="p-5 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-1/7 h-2/7 bg-gray-200 border-4 border-gray-700 rounded-xl"
        >
          <div class="relative text-right">
            <RouterLink :to="{ name: 'ShowUser' }">
              <button
                class="font-bold text-xl hover:bg-red-700 hover:text-white pl-2 pr-2 pb-1 rounded-md"
              >
                x
              </button>
            </RouterLink>
          </div>
          <div
            class="relative flex flex-col justify-center items-center pl-5 pr-5"
          >
            <p class="text-black text-center text-4xl p-6">
              You cannot delete your own account.
            </p>
          </div>
        </div>
      </div>
    </div>

    <div v-show="tranfer">
      <div class="popup">
        <div
          class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-screen h-screen bg-black opacity-60"
        ></div>
      </div>
      <div class="popup">
        <div
          class="p-5 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-1/7 h-2/7 bg-amber-100 border-4 border-a-700 rounded-xl"
        >
          <div
            class="relative flex flex-col justify-center items-center pl-5 pr-5 pb-6"
          >
            <p class="text-black text-center text-4xl p-6 \">
              All Announcement of the deleted user will transfer the
              announcement owner to you.
            </p>
          </div>
          <div class="relative text-right">
            <button
              class="font-bold text-xl hover:bg-green-700 hover:text-white pl-2 pr-2 pb-1 rounded-md bg-green-400"
            >
              CONFIRM
            </button>
            <button
              class="font-bold text-xl hover:bg-red-700 hover:text-white pl-2 pr-2 pb-1 rounded-md bg-red-400 ml-2"
            >
              CANCEL
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
td {
  width: 200px;
  word-wrap: break-word;
}
</style>
