<script setup>
import { ref, onMounted, onBeforeUpdate } from "vue";
import { useRoute,useRouter } from "vue-router";
import { fetchToken } from "../code/get.js";

//pop-up password
const pwdMatched = ref(false);
const pwdNotMatched = ref(false);
const pwdNotHave = ref(false);
const haveNoRight = ref(false)

//variables
const { params } = useRoute();
const isDesktop = ref(false);
const useButton = ref(false);
const username = ref();
const password = ref();
const errorPop = ref(false);
const errorResponse = ref(false);
const session = ref(false)
const loginfirst = ref(false)
const payloadObject = ref()
const jwtToken = localStorage.getItem('jwtToken')
const refreshToken = localStorage.getItem('refreshToken')
const router = useRouter()
const isAdmin = ref(false)

onMounted(async () => {
  if (!jwtToken) {
    loginfirst.value = true
    return
  }
  const base64Payload = jwtToken.split(".")[1];
  const decodePayload = atob(base64Payload);
  payloadObject.value = JSON.parse(decodePayload);
  console.log(payloadObject.value);
  if (payloadObject.value.role == 'announcer') {
    haveNoRight.value = true
    // setTimeout(()=>{
    //   haveNoRight.value = false
    //   router.push({name:'Data'})
    // },1000)
    return
  }
  if (payloadObject.value.role == 'admin') {
    console.log('admin');
    isAdmin.value = true
  }

  // let res = await fetchToken(refreshToken)
  // console.log(res);
  // if (res == 200) {
  //   await myAnnounce.fetchAnnounce()
  //   console.log('token 200');
  // }
  // if (res == 401) {
  //   session.value = true
  //   console.log('token 401');
  //   return
  // }
});



const checkIsMatch = async (usname, pwd) => {

  const jwtToken = localStorage.getItem('jwtToken');
  const refreshToken = localStorage.getItem('refreshToken')
  // Ensure the token is available before making the API call
  if (!jwtToken) {
    console.error('JWT Token not found in local storage');
    return;
  }
  try {
    const res = await fetch(
      `${import.meta.env.VITE_ROOT_API}/api/users/match`,
      {
        method: "POST",
        headers: {
          "content-type": "application/json",
          Authorization: `Bearer ${jwtToken}`,
        },
        body: JSON.stringify({
          username: usname,
          password: pwd,
        }),
      }
    );
    if (res.ok) {
      pwdMatched.value = true;
      setTimeout(() => {
        pwdMatched.value = false;
      }, 2000);
    }
    if (res.status === 401) {
      pwdNotMatched.value = true;
      setTimeout(() => {
        pwdNotMatched.value = false;
      }, 2000);
      if (await res.text() == 'JWT Token has expired') {
        const tokenRes = await fetchToken(refreshToken)
        if(tokenRes == 401){
          // session.value = true
          pwdNotMatched.value = true;
      setTimeout(() => {
        pwdNotMatched.value = false;
      }, 2000);
        }
      } 
    }
    if (res.status === 404) {
      pwdNotHave.value = true;
      setTimeout(() => {
        pwdNotHave.value = false;
      }, 2000);
    }
  } catch (err) {
    alert(err.message);
  }
};

const signOut = () => {
  localStorage.removeItem("jwtToken");
  localStorage.removeItem("refreshToken");
  loginfirst.value = false
  router.push({ name: "UserLogin" });
};
</script>

<template>
  <div class="p-8 relative">
    <!-- left side -->
    <div class="flex flex-col absolute">
      <p class="ann-app-title font-bold text-4xl w-40 pl-5 pt-3 pb-3">SAS</p>
      <RouterLink to="/announcement">
        <button class="btn btn-outline btn-success font-semibold w-48 h-16 ml-3 mt-2 mb-2 text-xl">Announcement
          (Viewer)</button>
      </RouterLink>
      <button class="ann-menu btn btn-outline btn-success font-semibold w-48 h-16 ml-3 mt-2 mb-2">
        <RouterLink :to="{ name: 'Data' }" class="text-xl">Announcement</RouterLink>
      </button>
      <button class="ann-menu btn btn-outline btn-success font-semibold w-48 h-16 ml-3 mt-2 mb-2">
        <RouterLink :to="{ name: 'ShowUser' }" class="text-xl">User</RouterLink>
      </button>
      <button class="ann-menu btn btn-outline btn-success font-semibold w-48 h-16 ml-3 mt-2 mb-2">
        <RouterLink :to="{ name: 'MatchPwd' }" class="text-xl">Match Password</RouterLink>
      </button>
      <button class="ann-menu btn btn-outline btn-error font-semibold w-48 h-16 ml-3 mt-10 mb-2 text-xl"
        @click="signOut">sign out</button>
    </div>
    <!-- middle -->
    <div class="w-full p-10 flex flex-col justify-center items-center">
      <div class="border w-2/4 p-5 rounded-xl flex flex-col">
        <p class="font-bold text-5xl">Match Password</p>
        <p class="font-bold text-xl pt-5 pb-3">Username</p>
        <!-- {{ testUser.username }} -->
        <input type="text" class="ann-username border rounded-lg w-full h-12" v-model="username" />
        <p class="font-bold text-xl pt-5 pb-3">Password</p>
        <input type="password" class="border rounded-lg w-full h-12 ann-password" v-model="password" />
        <button type="submit" class="ann-button btn btn-outline btn-success w-36 mt-5 mb-3" :disabled="useButton"
          @click="checkIsMatch(username, password)">
          Match or Not
        </button>
      </div>
    </div>
    <!-- Matched pop-up -->
    <div v-show="pwdMatched">
      <div class="popup">
        <div class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-screen h-screen bg-black opacity-60"></div>
      </div>
      <div class="popup">
        <div
          class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-1/4 h-1/5 bg-lime-200 border-4 border-lime-500 rounded-xl">
          <div class="relative flex justify-center p-5">
            <p class="text-black text-center text-4xl absolute ann-message pt-7">
              Password Matched
            </p>
          </div>
        </div>
      </div>
    </div>
    <!-- Not Matched pop-up -->
    <div v-show="pwdNotMatched">
      <div class="popup">
        <div class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-screen h-screen bg-black opacity-60"></div>
      </div>
      <div class="popup">
        <div
          class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-red-200 h-1/5 border-4 border-red-500 rounded-xl"
          style="width: 450px">
          <div class="relative flex justify-center p-5">
            <p class="text-black text-center text-4xl absolute ann-message pt-7">
              Password NOT Matched
            </p>
          </div>
        </div>
      </div>
    </div>
    <!-- Not have pop-up -->
    <div v-show="pwdNotHave">
      <div class="popup">
        <div class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-screen h-screen bg-black opacity-60"></div>
      </div>
      <div class="popup">
        <div
          class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 h-1/5 bg-red-200 border-4 border-red-500 rounded-xl"
          style="width: 450px">
          <div class="relative flex justify-center p-5">
            <p class="text-black text-center text-4xl absolute ann-message pt-2">
              The specified username DOES NOT exist
            </p>
          </div>
        </div>
      </div>
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
              <button class="font-bold text-xl hover:bg-red-700 hover:text-white pl-2 pr-2 pb-1 rounded-md">x</button>
            </RouterLink>
          </div>
          <div class="relative flex flex-col justify-center items-center pl-5 pr-5">
            <p class="text-black text-center text-4xl p-6 \">You must LOGIN First</p>
          </div>
        </div>
      </div>
    </div>
    <div v-show="haveNoRight">
      <div class="popup">
        <div class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-screen h-screen bg-black opacity-60"></div>
      </div>
      <div class="popup">
        <div
          class="p-5 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-1/7 h-2/7 bg-gray-200 border-4 border-gray-700 rounded-xl">
          <div class="relative text-right">
            <RouterLink :to="{ name: 'UserAll' }">
              <button class="font-bold text-xl hover:bg-red-700 hover:text-white pl-2 pr-2 pb-1 rounded-md">x</button>
            </RouterLink>
          </div>
          <div class="relative flex flex-col justify-center items-center pl-5 pr-5">
            <p class="text-black text-center text-4xl p-6 ">You don't have ACCESS</p>
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
              <button class="font-bold text-xl hover:bg-red-700 hover:text-white pl-2 pr-2 pb-1 rounded-md">x</button>
            </RouterLink>
          </div>
          <div class="relative flex flex-col justify-center items-center pl-5 pr-5 pb-6">
            <p class="text-black text-center text-4xl p-6 \">Session is expired</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
