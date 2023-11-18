<script setup>
import { ref, onBeforeUpdate, onBeforeMount } from "vue";
import { fetchUser } from "../code/get.js";
import { useRoute } from "vue-router";
import router from "../router";
import { useUser } from "../stores/user.js";
//pop-up password
const success = ref(false);
const pwdIncorrect = ref(false);
const userNotExist = ref(false);

//variables
const useButton = ref(true);
const username = ref();
const password = ref();
const errorResponse = ref();
let myUser = useUser();
onBeforeUpdate(() => {
  //username
  if (username.value !== undefined) {
    useButton.value = false;
  } else if (username.value == "") {
    useButton.value = true;
  } else if (username.value == undefined) {
    useButton.value = true;
  }

  //password
  else if (password.value !== undefined) {
    useButton.value = false;
  } else if (password.value == "") {
    useButton.value = true;
  } else if (password.value == undefined) {
    useButton.value = true;
  }
});

onBeforeMount(async () => {
  // let jwtToken = localStorage.getItem('jwtToken')
  // if (jwtToken !== null) {
  //   router.push({ name: "ShowUser" });
  // }
});

const checkJWT = async (username, password) => {
  try {
    const res = await fetch(`${import.meta.env.VITE_ROOT_API}/api/token`, {
      method: "POST",
      headers: {
        "content-type": "application/json",
      },
      body: JSON.stringify({
        username: username,
        password: password,
      }),
    });

    if (res.ok) {
      // console.log("status 200");
      const newUser = await res.json();

      localStorage.setItem(
        "jwtToken",
        newUser.token
      );
      // await fetchUser();
      localStorage.setItem("refreshToken", newUser.refreshToken);
      success.value = true;
      setTimeout(() => {
        success.value = false;
      }, 2000);
      setTimeout(() => {
        router.push({ name: "UserAll" });
      }, 2000);
    }
    if (res.status === 401) {
      pwdIncorrect.value = true;
      setTimeout(() => {
        pwdIncorrect.value = false;
      }, 2000);
    }
    if (res.status === 404) {
      userNotExist.value = true;
      setTimeout(() => {
        userNotExist.value = false;
      }, 2000);
    }
  } catch (err) {
    const errorResponse = await res.json();
    alert(`There is an error : ${JSON.stringify(errorResponse)}`);
    // console.log("error");
    // alert(err.message);
  }
};
</script>

<template>
  <div class="p-8 relative">
    <!-- middle -->
    <div class="w-full p-10 flex flex-col justify-center items-center">
      <div class="border w-2/4 p-5 rounded-xl flex flex-col">
        <p class="font-bold text-5xl">SAS Login</p>
        <p class="font-bold text-xl pt-5 pb-3">Username</p>
        <!-- {{ testUser.username }} -->
        <input type="text" class="ann-username border-gray-300 border rounded-lg w-full h-12" v-model="username" />
        <p class="font-bold text-xl pt-5 pb-3">Password</p>
        <input type="password" class="ann-password border border-gray-300 rounded-lg w-full h-12" v-model="password" />
        <div class="flex flex-row">
          <button type="submit" class="ann-button btn btn-outline btn-success w-28 mt-5 mb-3" :disabled="useButton"
            @click="checkJWT(username, password)">
            LOGIN
          </button>
          <button type="reset" class="ann-button btn btn-outline btn-error w-28 mt-5 mb-3 ml-2">
            <RouterLink :to="{ name: 'UserAll' }">CANCEL</RouterLink>
          </button>
        </div>
      </div>
    </div>
    <!-- Login Success pop-up -->
    <div v-show="success">
      <div class="popup">
        <div class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-screen h-screen bg-black opacity-60"></div>
      </div>
      <div class="popup">
        <div
          class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-1/4 h-1/5 bg-lime-200 border-4 border-lime-500 rounded-xl">
          <div class="relative flex justify-center p-5">
            <p class="text-black text-center text-4xl absolute ann-message pt-7">
              Login Successful
            </p>
          </div>
        </div>
      </div>
    </div>
    <!-- Pwd Incorrect pop-up -->
    <div v-show="pwdIncorrect">
      <div class="popup">
        <div class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-screen h-screen bg-black opacity-60"></div>
      </div>
      <div class="popup">
        <div
          class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-red-200 h-1/5 border-4 border-red-500 rounded-xl"
          style="width: 450px">
          <div class="relative flex justify-center p-5">
            <p class="text-black text-center text-4xl absolute ann-message pt-7">
              Password Incorrect
            </p>
          </div>
        </div>
      </div>
    </div>
    <!-- Username does not exist -->
    <div v-show="userNotExist">
      <div class="popup">
        <div class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-screen h-screen bg-black opacity-60"></div>
      </div>
      <div class="popup">
        <div
          class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-red-200 h-1/5 border-4 border-red-500 rounded-xl"
          style="width: 500px">
          <div class="relative flex justify-center p-5">
            <p class="text-black text-center text-4xl absolute ann-message pt-2">
              A user with the specified username DOES NOT exist
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
