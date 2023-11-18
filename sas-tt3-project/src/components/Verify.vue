<script setup>
import { onMounted, ref, onBeforeUpdate, computed } from 'vue';
import { useRouter } from 'vue-router'

const router = useRouter();

let step = ref(0)
let sub = ref()
let unsubscriber = ref(false)
const email = ref()
const general = ref(false)
const changeGene = ref(0)
const scholarship = ref(false)
const changeScholar = ref(0)
const findJobs = ref(false)
const changeFind = ref(0)
const internship = ref(false)
const changeIntern = ref(0)
const selectGeneral = ref()
const selectScholarship = ref()
const remainingTime = ref()
const subscriber = ref()
const subEmail = ref()
const data = ref()
const otpInput = ref()
const payloadObject = ref()
const invalidOtp = ref(false)
const jwtToken = localStorage.getItem('jwtToken')
const subSuccess = ref(false)
const sendEmailBtn = ref(false)
const sendOtpBtn = ref(false)
const check = ref(false)
const delSuccess = ref(false)
const useBtn = ref(false)

onMounted(async () => {
    console.log(general.value);
    sendEmailBtn.value = true
    sendOtpBtn.value = true
    console.log(email.value);
    if (jwtToken) {
        const base64Payload = jwtToken.split(".")[1];
        const decodePayload = atob(base64Payload);
        payloadObject.value = JSON.parse(decodePayload);
        console.log(payloadObject.value);
        console.log(payloadObject.value.email);
        email.value = payloadObject.value.email
    }
})

onBeforeUpdate(async () => {
    if (email.value !== undefined) {
        sendEmailBtn.value = false
    }
    if (email.value?.trim() == '') {
        sendEmailBtn.value = true
    }
    if (otpInput.value !== undefined) {
        sendOtpBtn.value = false
    }
    if (otpInput.value == '') {
        sendOtpBtn.value = true
    }
    if (step.value < 0) {
        router.push('/announcement')
    }
    if (remainingTime.value === 0) {
        invalidOtp.value = false
    }

    //subscribed
    if (general.value === true) {
        changeGene.value = 1
    }
    if (scholarship.value === true) {
        changeScholar.value = 2
    }
    if (findJobs.value === true) {
        changeFind.value = 3
    }
    if (internship.value === true) {
        changeIntern.value = 4
    }
    if (general.value === false) {
        changeGene.value = 0
    }
    if (scholarship.value === false) {
        changeScholar.value = 0
    }
    if (findJobs.value === false) {
        changeFind.value = 0
    }
    if (internship.value === false) {
        changeIntern.value = 0
    }
    if(general.value === false && scholarship.value === false && findJobs.value === false && internship.value === false){
        useBtn.value = true
    } else {
        useBtn.value = false
    }
})

const timer = () => {
    remainingTime.value = 60000
    const countdown = setInterval(() => {
        remainingTime.value -= 100
        if (step.value === 0) {
            clearInterval(countdown)
        }
        if (remainingTime.value <= 0) {
            clearInterval(countdown)
            // resend.value = true
        }
    }, 100)
}

const seconds = computed(() => {
    const second = Math.floor((remainingTime.value % 60000) / 1000)
    return second < 10 ? `0${second}` : `${second}`
})

//อันนี้ได้ OTP
const verifying = async () => {
    console.log(step.value);
    try {
        const res = await fetch(`${import.meta.env.VITE_ROOT_API}/api/sendOTP`, {
            method: "POST",
            headers: {
                "content-type": "application/json",
            },
            body: JSON.stringify({
                email: email.value,
            }),
        });
        if (res.ok) {
            subEmail.value = await res.text()
            console.log(subEmail.value);
            step.value++
            timer()
            return subEmail.value
        }
    }
    catch (error) {
        console.error(error);
    }
}

const resendOTP = async () => {
    console.log(step.value);
    try {
        const res = await fetch(`${import.meta.env.VITE_ROOT_API}/api/sendOTP`, {
            method: "POST",
            headers: {
                "content-type": "application/json",
            },
            body: JSON.stringify({
                email: email.value,
            }),
        });
        if (res.ok) {
            subEmail.value = await res.text()
            console.log(subEmail.value);
            timer()
            return subEmail.value
        }
    }
    catch (error) {
        console.error(error);
    }
}

//อันนี้ได้ข้อมูลของอีเมลนั้นๆ
const verified = async (event) => {
    try {
        const res = await fetch(`${import.meta.env.VITE_ROOT_API}/api/verify`, {
            method: "POST",
            headers: {
                "content-type": "application/json",
                Verify: subEmail.value
            },
            body: JSON.stringify({
                email: email.value,
                sendOTP: otpInput.value
            }),
        });

        if (res.ok) {
            const text = await res.text();
            if (text.includes("You not Subscriber")) {
                console.log("You not Subscription")
                unsubscriber.value = true
                step.value++
                return
            }
            else {
                console.log(event.target.id)
                console.log('success');
                unsubscriber.value = false
                // invalidOtp.value = false
                step.value++
                return
            }
        }
        else if (res.status == "401") {
            console.log('otp is invalid');
            invalidOtp.value = true
        }
        else if (res.status == "403") {
            console.log('otp is expired');
            invalidOtp.value = false
        }
    }
    catch (error) {
        console.error(error);
    }
}

const requestData = async () => {
    step.value++
    try {
        const res = await fetch(`${import.meta.env.VITE_ROOT_API}/api/all`, {
            method: "POST",
            headers: {
                "content-type": "application/json"
            },
            body: JSON.stringify({
                email: email.value
            }),
        });

        if (res.ok) {
            console.log('Success')
            data.value = await res.json()
            step.value++
            return data.value
        }
    }
    catch (error) {
        console.error(error);
    }
}

//อันนี้คือกดซัพแล้ว
const subscribed = async () => {
    try {
        const res = await fetch(`${import.meta.env.VITE_ROOT_API}/api/sendMail`, {
            method: "POST",
            headers: {
                "content-type": "application/json",
                Verify: subEmail.value,
            },
            body: JSON.stringify({
                subscriptions: [
                    {
                        categoryId: changeGene.value,
                        subscriberEmail: email.value
                    },
                    {
                        categoryId: changeScholar.value,
                        subscriberEmail: email.value
                    },
                    {
                        categoryId: changeFind.value,
                        subscriberEmail: email.value
                    },
                    {
                        categoryId: changeIntern.value,
                        subscriberEmail: email.value
                    }
                ]
            }),
        });
        if (res.ok) {
            localStorage.removeItem(subEmail.value)
            subSuccess.value = true
            setTimeout(() => {
                subSuccess.value = false
                router.push('/announcement');
            }, 3000)
        }
    }
    catch (error) {
        console.error(error);
    }
}

const deleteSub = async () => {
    check.value = false
    try {
        const res = await fetch(`${import.meta.env.VITE_ROOT_API}/api/unsub`, {
            method: "DELETE",
            headers: {
                "content-type": "application/json",
                Verify: subEmail.value,
            },
            body: JSON.stringify({
                subscriptions: [
                    {
                        categoryId: changeGene.value,
                        subscriberEmail: email.value
                    },
                    {
                        categoryId: changeScholar.value,
                        subscriberEmail: email.value
                    },
                    {
                        categoryId: changeFind.value,
                        subscriberEmail: email.value
                    },
                    {
                        categoryId: changeIntern.value,
                        subscriberEmail: email.value
                    }
                ]
            }),
        });
        if (res.ok) {
            // const delData = await res.json()
            // console.log(delData);
            // console.log('deleted');
            localStorage.removeItem(subEmail.value)
            delSuccess.value = true
            setTimeout(() => {
                delSuccess.value = false
                router.push('/announcement');
            }, 3000)
        }
    } catch (error) {
        console.error(error);
    }
}

const handleInput = () => {
    otpInput.value = otpInput.value.replace(/\D/g, '').slice(0, 4)
}
</script>
 
<template>
    <section class="wrapper">
        <div class="absolute left-4 top-5 rounded-full p-3">
            <button>
                <RouterLink to="/announcement">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5"
                        stroke="currentColor" class="w-12 h-12 stroke-teal-600">
                        <path stroke-linecap="round" stroke-linejoin="round"
                            d="M2.25 12l8.954-8.955c.44-.439 1.152-.439 1.591 0L21.75 12M4.5 9.75v10.125c0 .621.504 1.125 1.125 1.125H9.75v-4.875c0-.621.504-1.125 1.125-1.125h2.25c.621 0 1.125.504 1.125 1.125V21h4.125c.621 0 1.125-.504 1.125-1.125V9.75M8.25 21h8.25" />
                    </svg>
                </RouterLink>
            </button>
        </div>
        <div class="absolute left-16 top-5 rounded-full p-3" v-show="step !== 2">
            <button @click="step === 4 ? step = 2 : step -= 1">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5"
                    stroke="currentColor" class="w-11 h-11 stroke-teal-600">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M9 15L3 9m0 0l6-6M3 9h12a6 6 0 010 12h-3" />
                </svg>
            </button>
        </div>
        <div class="container">
            <div class="img__container" v-show="step < 2">
                <img src="https://img.freepik.com/free-vector/emails-concept-illustration_114360-1355.jpg?w=740&t=st=1699534040~exp=1699534640~hmac=16e466ae047e4f13f83bf8d700452cd05a5c5caa94b1ca10c07df95df08ae663"
                    alt="mailpic" class="img">
            </div>
            <div class="img__container" v-show="step === 3">
                <img src="https://img.freepik.com/free-vector/doodle-vector-social-media-reaction-concept_53876-126626.jpg?w=1060&t=st=1699539630~exp=1699540230~hmac=917fbb414f08c04712f00a260bf97f0c1a8bb313918c9c6cb73ba42e7f7391d7"
                    alt="sub" class="img">
            </div>
            <div class="img__container" v-show="step === 4">
                <img src="https://img.freepik.com/free-photo/denied-checklist-3d-clipboard-with-cross-marks_107791-17011.jpg?w=900&t=st=1699693806~exp=1699694406~hmac=1cf1a208742d050d52169ec3dbe48eb796cc26d0e70ee6f19f9fed620ef64117"
                    alt="sub" class="img">
            </div>
            <div class="img__container" v-show="step === 2">
                <img src="https://img.freepik.com/free-vector/magnifying-glass-with-file-searching_52683-22967.jpg?w=740&t=st=1699693965~exp=1699694565~hmac=8f6070b69005c6700949eef772b89db11a77f26afe7a05da728360a1339ec24b"
                    alt="select" class="img">
            </div>
            <div class="content">
                <div v-show="step < 1">
                    <h2 class="subtitle p-3 bg-teal-600 rounded-full">1</h2>
                    <h1 class="title">Please verify your email.</h1>
                    <input v-model="email" @keydown.space.prevent type="email" class="mail" placeholder="Enter your email."
                        name="mail" required>
                    <button type="submit" class="subscribe hover:bg-teal-900" @click="verifying"
                        :disabled="sendEmailBtn">Send</button>
                    <p class="text">Your email address will be stored <br /> for sending messages.</p>
                </div>
                <div v-show="step === 1">
                    <h2 class="subtitle p-3 bg-teal-600 rounded-full">2</h2>
                    <h1 class="title">Verification Code</h1>
                    <p class="text">We have sent a verification code <br>to your email address.</p>
                    <div class="OTPs space-x-3 pb-1">
                        <input type="text" pattern="\d{4}" inputmode="numeric" class="OTP" required @input="handleInput"
                            v-model="otpInput">
                        <!-- <input type="text" inputmode="numeric" class="OTP" name="otp" required>
                        <input type="text" inputmode="numeric" class="OTP" name="otp" required>
                        <input type="text" inputmode="numeric" class="OTP" name="otp" required> -->
                    </div>
                    <p v-show="invalidOtp" class="text-red-500">Invalid OTP.</p>
                    <p v-show="remainingTime !== 0">{{ seconds }}s</p>
                    <p v-show="remainingTime === 0" class="text-red-500">OTP is expired.</p>
                    <button class="pb-2 text-sm text-blue-500 hover:text-blue-900 underline"
                        @click="resendOTP">Resend</button>
                    <button type="submit" class="subscribe hover:bg-teal-900" @click="verified"
                        :disabled="sendOtpBtn">Verify
                    </button>
                </div>
                <div v-show="step === 2">
                    <h2 class="subtitle p-3 bg-teal-600 rounded-full">3</h2>
                    <h1 class="title">Select Menu</h1>
                    <p class="text">You have successfully verified your email.<br>Please select the menu below.</p>
                    <button type="submit" value="toSub" class="subscribe  hover:bg-teal-900" ref="sub"
                        @click="step = 3">Subscirbe</button>
                    <button type="submit" id="unsub" class="unsubscribe hover:bg-red-900" @click="requestData"
                        :disabled="unsubscriber">Unsubscirbe</button>
                </div>
                <div v-show="step === 3">
                    <h2 class="subtitle p-3 bg-teal-600 rounded-full">4</h2>
                    <h1 class="title">Select Category</h1>
                    <p class="text">Please select the category you want<br> to subscribe below.</p>
                    <div class="cate">
                        <input type="checkbox" id="ทั่วไป" name="ทั่วไป" value="ทั่วไป" ref="selectGeneral"
                            v-model="general">
                        <label for="ทั่วไป"> ทั่วไป</label><br>
                        <input type="checkbox" id="ทุนการศึกษา" name="ทุนการศึกษา" ref="selectScholarship"
                            value="ทุนการศึกษา" v-model="scholarship">
                        <label for="ทุนการศึกษา"> ทุนการศึกษา</label><br>
                        <input type="checkbox" id="หางาน" name="หางาน" value="หางาน" v-model="findJobs">
                        <label for="หางาน"> หางาน</label><br>
                        <input type="checkbox" id="ฝึกงาน" name="ฝึกงาน" value="ฝึกงาน" v-model="internship">
                        <label for="ฝึกงาน"> ฝึกงาน</label><br>
                    </div>
                    <button type="submit" value="Subscribe" class="subscribe hover:bg-teal-900"
                        @click="subscribed" :disabled="useBtn">Subscirbe</button>
                </div>
                <div v-if="step === 4">
                    <h2 class="subtitle p-3 bg-teal-600 rounded-full">4</h2>
                    <h1 class="title">Select to unsubscribe</h1>
                    <p class="text">Please select the category you want<br> to unsubscribe below.</p>
                    <div class="cate" v-for="(cate, index) in data" :key="index">
                        <div v-show="cate.category === 1">
                            <input type="checkbox" id="ทั่วไป" name="ทั่วไป" value="ทั่วไป" ref="selectGeneral"
                                v-model="general">
                            <label for="ทั่วไป"> ทั่วไป</label><br>
                        </div>
                        <div v-show="cate.category === 2">
                            <input type="checkbox" id="ทุนการศึกษา" name="ทุนการศึกษา" ref="selectScholarship"
                                value="ทุนการศึกษา" v-model="scholarship">
                            <label for="ทุนการศึกษา"> ทุนการศึกษา</label><br>
                        </div>
                        <div v-show="cate.category === 3">
                            <input type="checkbox" id="หางาน" name="หางาน" value="หางาน" v-model="findJobs">
                            <label for="หางาน"> หางาน</label><br>
                        </div>
                        <div v-show="cate.category === 4">
                            <input type="checkbox" id="ฝึกงาน" name="ฝึกงาน" value="ฝึกงาน" v-model="internship">
                            <label for="ฝึกงาน"> ฝึกงาน</label><br>
                        </div>
                    </div>
                    <button type="submit" value="Subscribe" class="unsubscribe hover:bg-red-900"
                        @click="check=true" :disabled="useBtn">Unsubscirbe</button>
                </div>
            </div>
        </div>
    </section>
    <div v-show="subSuccess">
        <div class="popup">
            <div class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-screen h-screen bg-black opacity-60">
            </div>
        </div>
        <div class="popup">
            <div
                class="p-5 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-1/7 h-2/7 bg-green-200 border-4 border-green-500 rounded-xl">
                <div class="relative flex flex-col justify-center items-center pl-5 pr-5">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1"
                        stroke="currentColor" class="w-24 h-24 stroke-green-700">
                        <path stroke-linecap="round" stroke-linejoin="round"
                            d="M9 12.75L11.25 15 15 9.75M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                    <p class="text-black text-center text-3xl p-6 font-semibold">Thank you<br> for subscribing.</p>
                    <p class="text-lg text-gray-600">We have send you an email to</p>
                    <p class="text-lg text-gray-600">{{ email }}</p>
                </div>
            </div>
        </div>
    </div>
    <div v-show="check">
        <div class="popup">
            <div class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-screen h-screen bg-black opacity-60">
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
                    <p class="text-lg text-gray-600">If you confirm, we won't send you<br>any updates in that category.</p>
                    <div class="flex flex-row space-x-5 mt-5 h-12">
                        <button @click="deleteSub" class="w-24 rounded-lg bg-green-500 text-xl hover:bg-green-900 hover:text-white">YES</button>
                        <button @click="check=false" class="w-24 rounded-lg bg-red-500 text-xl hover:bg-red-900 hover:text-white">NO</button>
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
          class="p-5 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-1/7 h-2/7 bg-white border-4 border-sky-500 rounded-xl"
        >
          <div
            class="relative flex flex-col justify-center items-center pl-5 pr-5"
          >
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1"
                        stroke="currentColor" class="w-24 h-24 stroke-sky-500">
                        <path stroke-linecap="round" stroke-linejoin="round"
                            d="M9 12.75L11.25 15 15 9.75M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
            <p class="text-black text-center text-4xl p-6 \">Unsubscribed<br>successfully.</p>
          </div>
        </div>
      </div>
    </div>
</template>
 
<style scoped>
.wrapper {
    min-height: 100vh;
    color: #000;
    font-family: 'Muli', sans-serif;
    font-size: 1rem;
    background-color: rgb(218, 248, 241);
    display: -ms-grid;
    display: grid;
    place-items: center;
    text-align: center;
}

.cate {
    width: 100%;
    position: relative;
    text-align: left;
    font-size: 18px;
    margin-top: 30px;
    margin-bottom: 30px;
}

.OTPs {
    width: 100%;
    position: relative;
    text-align: center;
    font-size: 18px;
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 20px;
    margin-bottom: 10px;
}

.OTP {
    width: 150px;
    border-bottom: 3px solid rgba(0, 0, 0, 0.5);
    text-align: center;
    font-size: 36px;
}

.OTP:focus {
    border: 3px solid rgb(22, 214, 198);
}

.container {
    background-color: #fff;
    padding: 1em;
    border-radius: 1em;
    max-width: 768px;
    display: -ms-grid;
    display: grid;
    place-items: center;
    -webkit-box-shadow: 0px 17px 34px -20px rgb(80, 116, 108);
    box-shadow: 0px 17px 34px -20px rgb(80, 116, 108);
}

.title {
    font-size: 1.6rem;
    font-weight: 700;
}

.text {
    font-size: 1rem;
    margin-bottom: 0.8em;
}

.container {
    max-width: 400px;
    overflow: hidden;
    padding: 0;
}

.img {
    padding-top: 50%;
    width: 100%;
    height: auto;
    border-radius: 1em 1em 0 0;
    -o-object-fit: cover;
    object-fit: cover;
    -webkit-transform: translateY(-50%);
    transform: translateY(-50%);
}

.img__container {
    height: 200px;
    overflow: hidden;
    margin-bottom: 1.5em;
}

.title {
    margin-bottom: 0.9em;
}

.subtitle {
    font-size: 1rem;
    text-transform: uppercase;
    margin-bottom: 0.5em;
    font-weight: 700;
    color: #fff;
}

.content {
    display: -ms-grid;
    display: grid;
    place-items: center;
    padding: 0 2em;
    margin-bottom: 1.8em;
}

.mail {
    padding: 0.5em 0 0.5em 1.5em;
    line-height: 3;
    width: 100%;
    border: 1px solid #d9d9d9;
    border-radius: 0.5em;
    margin-bottom: 1em;
}

::-webkit-input-placeholder {
    color: #111127;
}

:-ms-input-placeholder {
    color: #111127;
}

::-ms-input-placeholder {
    color: #111127;
}

::placeholder {
    color: #111127;
}

.subscribe {
    color: #fff;
    font-size: 1.3rem;
    font-weight: 700;
    background-color: rgb(13 148 136);
    padding: 0.9em 0;
    display: inline-block;
    border: none;
    border-radius: 0.5em;
    width: 100%;
    margin-bottom: 1em;
    margin-top: .5em;
}

.subscribe:disabled {
    color: #fff;
    font-size: 1.3rem;
    font-weight: 700;
    background-color: rgb(187, 197, 196);
    padding: 0.9em 0;
    display: inline-block;
    border: none;
    border-radius: 0.5em;
    width: 100%;
    margin-bottom: 1em;
    margin-top: .5em;
    cursor: not-allowed;
}

.unsubscribe {
    color: #fff;
    font-size: 1.3rem;
    font-weight: 700;
    background-color: rgb(187, 20, 20);
    padding: 0.9em 0;
    display: inline-block;
    border: none;
    border-radius: 0.5em;
    width: 100%;
    margin-bottom: 1.3em;
}

.unsubscribe:disabled {
    color: #fff;
    font-size: 1.3rem;
    font-weight: 700;
    background-color: rgb(187, 197, 196);
    padding: 0.9em 0;
    display: inline-block;
    border: none;
    border-radius: 0.5em;
    width: 100%;
    margin-bottom: 1em;
    margin-top: .5em;
    cursor: not-allowed;
}
</style>