<script setup>
import { ref, onMounted, onBeforeUpdate } from "vue";
import { fetchUser, fetchUserId, fetchToken } from "../code/get.js";
import { useRoute } from "vue-router";
import { useRouter } from "vue-router";
import { useUser } from "../stores/user.js";

//variables
const isDesktop = ref(false);
const router = useRouter();
const myUser = useUser();
let usernameOld;
let nameOld;
let emailOld;
let roleOld;
const { params } = useRoute();
let allUser = ref({});
let roles = ref(["admin", "announcer"]);
let useButton = ref(true);
let newDate = ref();
const confirmPwd = ref();
const emailNotValid = ref(false);
const emailPattern = ref(/([a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\.[a-zA-Z0-9._-]+)/g);
// const pwdPattern = ref(
//   /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@*?#$%^&+=!:_])(?!.*\s)/g
// );
const pwdNotMatch = ref(false);
const conPwdSize = ref(false);
const pwdNotValidSize = ref(false);
const errorResponse = ref();
const successPop = ref(false);
let passNoMatch = ref(false);
const usernameErr = ref(false);
const usernameMes = ref();
const emailErr = ref(false);
const emailMes = ref();
let nameMes = ref();
let nameErr = ref(false);
let passMes = ref();
let passErr = ref(false);
const loginfirst = ref(false)
const session = ref(false)
const tranfer = ref(false)
const haveNoRight = ref(false)

//tokens
const jwtToken = localStorage.getItem("jwtToken");
const refreshToken = localStorage.getItem("refreshToken");

//convert date
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

//responsive
const checkResolution = () => {
  if (window.innerWidth < 1536) {
    isDesktop.value = false;
  } else {
    isDesktop.value;
  }
};

onMounted(async () => {
  if (!jwtToken) {
    loginfirst.value = true
    return
    }
    else{
  //add
  console.log(params.id);
  if (params.id === undefined) {
    allUser.value = {
      username: "",
      name: "",
      password: "",
      email: "",
      role: "",
    };
    allUser.value.role = "announcer";
  }
  //edit
  else {
    allUser.value = await fetchUserId(params.id);
    if(allUser.value==403){
      haveNoRight.value = true
    }
    usernameOld = allUser.value.username;
    nameOld = allUser.value.name;
    emailOld = allUser.value.email;
    roleOld = allUser.value.role;
    //createdOn format
    if (allUser.value.createdOn !== null) {
      allUser.value.createdOn = convertDate(allUser.value.createdOn);
    }

    //updatedOn format
    if (allUser.value.updatedOn !== null) {
      allUser.value.updatedOn = convertDate(allUser.value.updatedOn);
    }
  }
}
  checkResolution();
  window.addEventListener("resize", checkResolution);
  return () => {
    window.removeEventListener("resize", checkResolution);
  };
});

onBeforeUpdate(async () => {
  //add
  if (params.id === undefined) {
    console.log(allUser.value.password);
    if (allUser.value !== undefined) {
      if (allUser.value.password.trim() == "") {
        passErr.value = false;
        pwdNotValidSize.value = true;
      }
      if (!allUser.value.password.trim() == "") {
        if (allUser.value.password.length < 8) {
          pwdNotValidSize.value = false;
        } else if (allUser.value.password.length >= 8) {
          pwdNotValidSize.value = true;
        }
      }
      if (confirmPwd.value !== undefined) {
        if (confirmPwd.value == allUser.value.password) {
          pwdNotMatch.value = false;
        }
        if (confirmPwd.value.length < 8) {
          conPwdSize.value = true;
          pwdNotMatch.value = false;
        }
        if (confirmPwd.value.length >= 8) {
          conPwdSize.value = false;
          if (confirmPwd.value !== allUser.value.password) {
            pwdNotMatch.value = true;
          }
          if (confirmPwd.value == allUser.value.password) {
            pwdNotMatch.value = false;
          }
        }
        if (confirmPwd.value.trim() == "") {
          conPwdSize.value = false;
        }
      }
      if (allUser.value.email !== undefined) {
        if (!allUser.value.email.match(emailPattern.value)) {
          emailNotValid.value = false;
        }
        if (allUser.value.email.match(emailPattern.value)) {
          emailNotValid.value = true;
        }
        if (allUser.value.email.trim() == "") {
          // useButton.value = true;
          emailErr.value = false;
          emailNotValid.value = true;
        }
        if (allUser.value.name.trim() == "") {
          // useButton.value = true;
          nameErr.value = false;
        }

        if (allUser.value.username.trim() == "") {
          // useButton.value = true;
          usernameErr.value = false;
        }
      }
    }
  }

  //edit
  if (params.id !== undefined) {
    if (allUser.value.username === usernameOld) {
      useButton.value = true;
    }
    if (allUser.value.name === nameOld) {
      useButton.value = true;
    }
    if (allUser.value.email === emailOld) {
      useButton.value = true;
    }
    if (allUser.value.role === roleOld) {
      useButton.value = true;
    }

    if (allUser.value.username !== usernameOld) {
      useButton.value = false;
    }
    if (allUser.value.name !== nameOld) {
      useButton.value = false;
    }
    if (allUser.value.email !== emailOld) {
      useButton.value = false;
      emailNotValid.value = false;
    }
    if (allUser.value.role !== roleOld) {
      useButton.value = false;
    }
    if (allUser.value.email !== undefined) {
      if (!allUser.value.email.match(emailPattern.value)) {
        emailNotValid.value = false;
      }
      if (allUser.value.email.match(emailPattern.value)) {
        emailNotValid.value = true;
      }
    }

    if (allUser.value.email.trim() == "") {
      // useButton.value = true;
      emailErr.value = false;
      emailNotValid.value = true;
    }
    if (allUser.value.name.trim() == "") {
      // useButton.value = true;
      nameErr.value = false;
    }

    if (allUser.value.username.trim() == "") {
      // useButton.value = true;
      usernameErr.value = false;
    }
  }
});
//add
let addNewUser = async (addNewUser) => {
  if (addNewUser.password !== confirmPwd.value) {
    passNoMatch.value = true;
    setTimeout(() => {
      passNoMatch.value = false;
    }, 1000);
    return;
  } else
    try {
      const res = await fetch(`${import.meta.env.VITE_ROOT_API}/api/users`, {
        method: "POST",
        headers: {
          "content-type": "application/json",
          Authorization: `Bearer ${jwtToken}`,
        },
        body: JSON.stringify({
          username: addNewUser.username,
          password: addNewUser.password,
          name: addNewUser.name,
          email: addNewUser.email,
          role: addNewUser.role,
        }),
      });

      if (res.ok) {
        const newUser = await res.json();
        myUser.user.push(newUser);
        return true;
      }
      if (res.status === 401) {
        if (res.text() == 'JWT Token has expired') {
          const tokenRes = await fetchToken(refreshToken)
          if(tokenRes == 401){
            session.value = true
          }
        }
      }
      else {
        errorResponse.value = await res.json();
        let errorDetail = errorResponse.value.detail;
        console.log(errorDetail);
        for (let error of errorDetail) {
          console.log(error);
          errorResponse.value = error.errorMessage;
          console.log(error.field);
          if (error.field == "username") {
            usernameMes.value = error.errorMessage;
            console.log(usernameMes.value);
            usernameErr.value = true;
          }
          if (error.field == "email") {
            emailMes.value = error.errorMessage;
            console.log(emailMes.value);
            emailErr.value = true;
          }

          if (error.field == "name") {
            nameMes.value = error.errorMessage;
            console.log(nameMes.value);
            nameErr.value = true;
          }

          if (error.field == "password") {
            passMes.value = error.errorMessage;
            console.log(nameMes.value);
            passErr.value = true;
          }
          // else {
          //   usernameErr.value = false;
          //   emailErr.value = false;
          //   nameErr.value = false;
          //   passErr.value = false;
          // }
          console.log(errorResponse.value);
        }

        return false;
      }
    } catch (err) {
      console.log(err.message);

    }
};

//edit
let editUser = async (edit) => {
  try {
    const res = await fetch(
      `${import.meta.env.VITE_ROOT_API}/api/users/${edit.id}`,
      {
        method: "PUT",
        headers: {
          "content-type": "application/json",
          Authorization: `Bearer ${jwtToken}`,
        },
        body: JSON.stringify({
          username: edit.username,
          name: edit.name,
          email: edit.email,
          role: edit.role,
        }),
      }
    );

    if (res.ok) {
      const modifyUser = await res.json();
      myUser.user = myUser.user.map((user) => {
        if (user.id === modifyUser.id) {
          (user.username = modifyUser.username),
            (user.name = modifyUser.name),
            (user.email = modifyUser.email),
            (user.role = modifyUser.role);
        }
        return user;
      });
      return true;
    }
    else if (res.status === 401) {
      if (res.text() == 'JWT Token has expired') {
        await fetchToken(refreshToken)
      } 
    }
    else {
      errorResponse.value = await res.json();
      let errorDetail = errorResponse.value.detail;
      console.log(errorDetail);
      for (let error of errorDetail) {
        console.log(error);
        errorResponse.value = error.errorMessage;
        console.log(error.field);
        if (error.field == "username") {
          usernameMes.value = error.errorMessage;
          console.log(usernameMes.value);
          usernameErr.value = true;
        }
        if (error.field == "name") {
          nameMes.value = error.errorMessage;
          console.log(nameMes.value);
          nameErr.value = true;
        }
        if (error.field == "email") {
          emailMes.value = error.errorMessage;
          console.log(nameMes.value);
          emailErr.value = true;
        }
        // else {
        //   usernameErr.value = false;
        //   emailErr.value = false;
        //   nameErr.value = false;
        // }
        console.log(errorResponse.value);
      }
      return false;
    }
  } catch (err) {
    alert(err.message);
  }
};

function cancelAction() {
  router.go(-1);
}

let handleSubmit = async () => {
  // Call the appropriate function based on whether it's an add or edit
  const success =
    params.id === undefined
      ? await addNewUser(allUser.value)
      : await editUser(allUser.value);

  if (success) {
    // Navigate to the new page upon success
    successPop.value = true;
    setTimeout(() => {
      router.push({ name: "ShowUser" });
    }, 1000);
  }
};
</script>

<template>
  <!-- <div v-for="(user,index) in allUser">
  <p>{{ user.name }}</p>
</div> -->
  <div class="flex flex-row w-full h-full">
    <div class="flex flex-col">
      <p class="ann-app-title font-bold text-2xl w-40 pl-5 pt-3 pb-3">SAS</p>
      <button class="ann-menu btn btn-outline btn-success font-semibold w-36 ml-3 mt-2 mb-2">
        <RouterLink :to="{ name: 'Data' }">Announcement</RouterLink>
      </button>
      <button class="ann-menu btn btn-outline btn-success font-semibold w-36 ml-3 mt-2 mb-2">
        <RouterLink :to="{ name: 'ShowUser' }">User</RouterLink>
      </button>
      <button class="ann-menu btn btn-outline btn-success font-semibold w-36 ml-3 mt-2 mb-2">
        <RouterLink :to="{ name: 'MatchPwd' }">Match Password</RouterLink>
      </button>
    </div>
    <form class="w-full p-10 flex flex-col" @submit.prevent="handleSubmit">
      <div class="w-full p-10 flex flex-col">
        <div class="border w-full p-5 rounded-xl space-y-3 flex flex-col">
          <p class="font-bold text-3xl">User Detial:</p>
          <div>
            <div class="flex flex-row">
              <lable class="font-semibold">Username</lable>
              <p class="ann-error-username text-red-600 pl-3" v-show="usernameErr">
                {{ usernameMes }}
              </p>
            </div>
            <input type="text" class="ann-username border rounded-lg w-full h-10 mt-3" required maxlength="45"
              v-model.trim="allUser.username" @keydown.space.prevent />
          </div>
          <!-- <div class="peer-[.is-dirty]:peer-required:block hidden">Please fill out this field.</div> -->
          <!-- <label class="required">Name:</label>
        <input type="text" /> -->
          <div v-if="!params.id">
            <div>
              <div class="flex flex-row">
                <lable class="font-semibold">Password</lable>
                <p class="pl-3 text-red-500 ann-error-password" v-show="passErr">
                  {{ passMes }}
                </p>
                <p class="pl-3 text-red-500 ann-error-password" v-show="!pwdNotValidSize">
                  Password size must be between 8 and 14
                </p>
              </div>
              <input type="password" class="ann-password border rounded-lg w-full h-10 mt-3" required minlength="8"
                maxlength="14" v-model.trim="allUser.password" @keydown.space.prevent />
            </div>
            <div class="flex flex-row">
              <lable class="font-semibold mt-5">Confirm Password</lable>
              <p class="pl-3 text-red-500 mt-5 ann-error-password" v-show="pwdNotMatch">
                The password DOES NOT match
              </p>
              <p class="pl-3 text-red-500 mt-5 ann-error-confirm-username" v-show="conPwdSize">
                Confirm password size must be between 8 and 14
              </p>
            </div>
            <input type="password" class="ann-confirm-password border rounded-lg w-full h-10 mt-3" required minlength="8"
              maxlength="14" v-model.trim="confirmPwd" @keydown.space.prevent />
          </div>
          <div class="flex flex-row">
            <lable class="font-semibold">Name</lable>
            <p class="ann-error-name text-red-600 pl-3" v-show="nameErr">
              {{ nameMes }}
            </p>
          </div>
          <input type="text" class="ann-name border rounded-lg w-full h-10" required maxlength="100"
            v-model.trim="allUser.name" />
          <div>
            <div class="flex flex-row">
              <label class="font-semibold">Email</label>
              <p class="pl-3 text-red-500 ann-error-email" v-show="emailErr">
                {{ emailMes }}
              </p>
              <p class="pl-3 text-red-500" v-show="!emailNotValid">
                **email not valid**
              </p>
            </div>
            <input type="email" class="ann-email border rounded-lg w-full h-10 mt-3" required maxlength="150"
              v-model.trim="allUser.email" @keydown.space.prevent />
          </div>
          <div>
            <label class="font-semibold">Role</label>
            <select v-model="allUser.role" class="ann-role border rounded-lg w-1/5 h-10 mt-3">
              <option v-for="(role, index) in roles">
                {{ role }}
              </option>
            </select>
          </div>
          <div class="flex flex-row">
            <label class="font-bold" v-if="params.id">
              Created On<span style="padding-left: 5px; font-weight: normal" class="ann-created-on">{{ allUser.createdOn
              }}</span>
            </label>
            <label class="font-bold pl-3" v-if="params.id">
              Updated On
              <span style="padding-left: 5px; font-weight: normal" class="ann-updated-on">{{ allUser.updatedOn }}</span>
            </label>
          </div>
          <div class="flex flex-row">
            <button type="submit" value="Save" class="btn btn-outline btn-info ann-button w-24" v-if="!params.id"
              :disabled="!useButton">
              Save
            </button>
            <button type="submit" value="Save" class="btn btn-outline btn-info ann-button w-24" v-if="params.id"
              :disabled="useButton">
              Save
            </button>

            <button type="button" class="btn btn-outline btn-error ann-button ml-2 w-24" @click="cancelAction">
              Cancel
            </button>
          </div>
        </div>
      </div>
    </form>
    <div v-show="successPop">
      <div class="popup">
        <div class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-screen h-screen bg-black opacity-60"></div>
      </div>
      <div class="popup">
        <div
          class="p-5 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-1/7 h-2/7 bg-lime-200 border-4 border-lime-500 rounded-xl">
          <div class="relative flex flex-col justify-center items-center pl-5 pr-5">
            <p class="text-black text-center text-4xl p-6 \">SUCCESS !!</p>
          </div>
        </div>
      </div>
    </div>

    <div v-show="passNoMatch">
      <div class="popup">
        <div class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-screen h-screen bg-black opacity-60"></div>
      </div>
      <div class="popup">
        <div
          class="p-5 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-1/7 h-2/7 bg-red-200 border-4 border-red-500 rounded-xl">
          <div class="relative flex flex-col justify-center items-center pl-5 pr-5">
            <p class="text-black text-center text-4xl p-6 \">
              The password DOES NOT match
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
form {
  padding: 20px;
  margin: 0 auto;
}

label {
  display: block;
}

input+label {
  display: inline-block;
  margin-right: 10px;
}

input[type="text"],
input[type="email"] {
  border: 1px solid #999;
  padding: 5px;
  width: 100%;
}
</style>
