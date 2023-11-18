<script setup>
import { ref, watch, onMounted, computed, onBeforeUpdate, onBeforeMount, VueElement, onUpdated, provide } from 'vue'
import { fetchId, fetchCategory, fetchToken, fetchFile, fetchFileName } from '../code/get.js';
import { useRoute } from 'vue-router'
import { useRouter } from 'vue-router'
import { useAnnounce } from '../stores/announcement';

// import { QuillEditor } from '@vueup/vue-quill'
// import '@vueup/vue-quill/dist/vue-quill.snow.css';


const { params } = useRoute()
const router = useRouter();
let useButton = ref(true)
let notEmpty = ref()
let notEmptyDesc = ref()
let titleLimit = ref()
let limitTitle = ref(false)
let descLimit = ref()
let limitDesc = ref(false)
let noTiltle = ref(true)
let noDesc = ref(true)
const descript = ref({})
let quillEditor = ref()
// let lengthTitle=ref()
// let useEdit = ref(true)
let category = ref({})
let check = ref()
const myAnnounce = useAnnounce()
const isAdmin = ref(false)
const session = ref(false)
const loginfirst = ref(false)
const jwtToken = localStorage.getItem('jwtToken')
const refreshToken = localStorage.getItem('refreshToken')
const payloadObject = ref()
const haveNoRight = ref(false)

let dateClose = ref()
let timeClose = ref()
let datePublish = ref()
let timePublish = ref()
let title
let desc
let display
let cate
let timeP
let timeC
let dateP
let dateC
let data = ref({})
let lengthTitle = ref(data.value.announcementTitle)

// file
let fileData = ref([])
let nameData = ref({})
onMounted(async () => {
    if (!jwtToken) {
        loginfirst.value = true
        return
    }
    const base64Payload = jwtToken.split(".")[1];
    const decodePayload = atob(base64Payload);
    payloadObject.value = JSON.parse(decodePayload);
    if (payloadObject.value.role == 'announcer') {
    }
    if (payloadObject.value.role == 'admin') {
        isAdmin.value = true
    }
    let res = await fetchToken(refreshToken)
    if (res == 200) {
        const annRes = await myAnnounce.fetchAnnounce()
    }
    if (res == 401) {
        session.value = true
        localStorage.removeItem("jwtToken");
        localStorage.removeItem("refreshToken");
        return
    }
    else {
        category.value = await fetchCategory()
        //add
        if (params.id === undefined) {
            data.value = {
                announcementTitle: "",
                announcementDescription: "",
                publishDate: "",
                closeDate: "",
                announcementDisplay: "",
                announcementCategory: ""
            }
            data.value.announcementCategory = 'ทั่วไป'
            if (datePublish.value !== undefined) {
                timePublish.value = `06:00`
            }
            if (dateClose.value !== undefined) {
                timeClose.value = `18:00`
            }
        }
        //edit
        else {
            data.value = await fetchId(params.id)
            nameData.value = await fetchFileName(params.id)
            nameData.value.forEach(async (f) => {
                console.log(f.fileName)
                fileData.value.push(await fetchFile(f.fileName))
                console.log(fileData.value)
            })
            if (data.value === 403) {
                haveNoRight.value = true
            }
            title = data.value.announcementTitle
            desc = data.value.announcementDescription
            cate = data.value.announcementCategory

            if (data.value.announcementDisplay === "Y") {
                data.value.announcementDisplay = true
            }
            if (data.value.announcementDisplay === "N") {
                data.value.announcementDisplay = false
            }

            display = data.value.announcementDisplay

            if (data.value.publishDate !== null) {

                datePublish.value = new Date(data.value.publishDate).toLocaleString("ko-KR", { year: "numeric", month: "2-digit", day: "2-digit" }).replace(/. /g, '-').slice(0, 10)

                // publish time
                const hourPublish = new Date(data.value.publishDate).getHours();
                const minutePublish = new Date(data.value.publishDate).getMinutes();
                timePublish.value = `${hourPublish.toString().padStart(2, '0')}:${minutePublish.toString().padStart(2, '0')}`

                timeP = timePublish.value
                dateP = datePublish.value
            }


            if (data.value.closeDate !== null) {
                dateClose.value = new Date(data.value.closeDate).toLocaleString("ko-KR", { year: "numeric", month: "2-digit", day: "2-digit" }).replace(/. /g, '-').slice(0, 10)
                //close time
                const hourClose = new Date(data.value.closeDate).getHours();
                const minuteClose = new Date(data.value.closeDate).getMinutes();
                timeClose.value = `${hourClose.toString().padStart(2, '0')}:${minuteClose.toString().padStart(2, '0')}`

                timeC = timeClose.value
                dateC = dateClose.value

            }
            if (data.value.publishDate && data.value.closeDate) {

                datePublish.value = new Date(data.value.publishDate).toLocaleString("ko-KR", { year: "numeric", month: "2-digit", day: "2-digit" }).replace(/. /g, '-').slice(0, 10)
                dateClose.value = new Date(data.value.closeDate).toLocaleString("ko-KR", { year: "numeric", month: "2-digit", day: "2-digit" }).replace(/. /g, '-').slice(0, 10)
                // publish time
                const hourPublish = new Date(data.value.publishDate).getHours();
                const minutePublish = new Date(data.value.publishDate).getMinutes();
                timePublish.value = `${hourPublish.toString().padStart(2, '0')}:${minutePublish.toString().padStart(2, '0')}`

                //close time
                const hourClose = new Date(data.value.closeDate).getHours();
                const minuteClose = new Date(data.value.closeDate).getMinutes();
                timeClose.value = `${hourClose.toString().padStart(2, '0')}:${minuteClose.toString().padStart(2, '0')}`

                timeC = timeClose.value
                timeP = timePublish.value
                dateC = dateClose.value
                dateP = datePublish.value
            }
        }
    }
})

onBeforeUpdate(() => {
    console.log(fileInput.value.files);
    console.log(selectedFiles.value);
    console.log(nameData.value);
    //add
    if (params.id === undefined) {
        if (data.value.announcementTitle !== undefined) {
            if (data.value.announcementTitle.trim() !== '') {
                useButton.value = false
                noTiltle.value = false
            }
        }
        if (data.value.announcementCategory !== undefined) {
            if (data.value.announcementCategory.trim() !== '') {
                useButton.value = false
            }
        }
        if (data.value.announcementDescription !== undefined) {
            if (data.value.announcementDescription.trim() !== '') {
                useButton.value = false
                noDesc.value = false
            }
        }
        if (data.value.announcementTitle?.trim() == '') {
            // notEmpty.value = 'maximum of 200'
            // noTiltle.value = true
            limitTitle.value = false
        }
        if (quillEditor.value.getText()?.length <= 1) {
            useButton.value = true
        }
        if (data.value.announcementTitle?.length === 200) {
            titleLimit.value = 'Exceed the limit.'
            limitTitle.value = true
            noTiltle.value = false
        }
        if (data.value.announcementTitle?.length < 200) {
            limitTitle.value = false
            noTiltle.value = true
        }

        if (data.value.announcementDescription?.trim() == '') {
            // notEmptyDesc.value = 'maximum of 10000'
            // noDesc.value = true
            limitDesc.value = false
        }
        if (data.value.announcementDescription?.length === 10000) {
            limitDesc.value = false
        }
        if (data.value.announcementDescription?.length > 10000) {
            descLimit.value = 'Exceed the limit.'
            limitDesc.value = true
            noDesc.value = false
            useButton.value = true
        }


        if (data.value.announcementDescription?.length < 10000) {
            // notEmptyDesc.value = 'maximum of 10000'
            limitDesc.value = false
            noDesc.value = true
        }

        if (data.value.announcementTitle?.length <= 0) {
            useButton.value = true
        }
        if (data.value.announcementDescription <= 0) {
            useButton.value = true
        }
        if (data.value.announcementCategory === undefined || data.value.announcementCategory === "") {
            useButton.value = true
        }
        if (datePublish.value) {
            if (!timePublish.value)
                timePublish.value = `06:00`
        }
        if (dateClose.value) {
            if (!timeClose.value)
                timeClose.value = `18:00`
        }

        if (datePublish.value && timePublish.value) {
            data.value.publishDate = `${datePublish.value}T${timePublish.value}`
        }

        if (dateClose.value && timeClose.value) {
            data.value.closeDate = `${dateClose.value}T${timeClose.value}`
        }
    }

    //edit
    if (params.id !== undefined) {
        if (data.value.announcementTitle === title) {
            useButton.value = true
        }
        if (data.value.announcementDescription === desc) {
            useButton.value = true

        }
        if (datePublish.value === dateP) {
            useButton.value = true

        }
        if (dateClose.value === dateC) {
            useButton.value = true

        }
        if (timePublish.value === timeP) {
            useButton.value = true

        }
        if (timeClose.value === timeC) {
            useButton.value = true

        }
        if (data.value.announcementCategory === cate) {
            useButton.value = true

        }
        if (data.value.announcementDisplay === display) {
            useButton.value = true

        }
        if (data.value.announcementTitle !== title) {
            useButton.value = false

        }
        if (data.value.announcementDescription !== desc) {
            useButton.value = false


        }
        if (datePublish.value !== dateP) {
            useButton.value = false


        }
        if (dateClose.value !== dateC) {
            useButton.value = false

        }
        if (timePublish.value !== timeP) {
            useButton.value = false

        }
        if (timeClose.value !== timeC) {
            useButton.value = false

        }
        if (data.value.announcementCategory !== cate) {
            useButton.value = false
        }
        if (data.value.announcementDisplay !== display) {
            useButton.value = false
        }
        if (data.value.announcementTitle !== undefined) {
            if (data.value.announcementTitle.trim() == '') {
                // notEmpty.value = 'maximum of 200'
                // noTiltle.value = true
                useButton.value = true
                limitTitle.value = false
                noDesc.value = true
            }
            if (data.value.announcementTitle.trim() !== '') {
                // notEmpty.value = 'maximum of 200'
                // noTiltle.value = true
                limitTitle.value = false
                noDesc.value = true
            }
            if (data.value.announcementTitle.length === 200) {
                titleLimit.value = 'Exceed the limit.'
                limitTitle.value = true
                noTiltle.value = false
            }
            if (data.value.announcementTitle.length < 200) {
                // notEmpty.value = 'maximum of 200'
                // noTiltle.value = true
                limitTitle.value = false
            }
            if (data.value.announcementTitle.length <= 0) {
                useButton.value = true

            }

        }

        if (data.value.announcementDescription !== undefined) {
            if (data.value.announcementDescription.trim() == '') {
                // notEmpty.value = 'maximum of 200'
                // noTiltle.value = true
                useButton.value = true
                limitDesc.value = false
                noTiltle.value = true
            }
            if (data.value.announcementDescription.length === 10000) {
                descLimit.value = 'Exceed the limit.'
                limitDesc.value = false
                noDesc.value = false
                useButton.value = false
            }

            if (data.value.announcementDescription.length < 10000) {
                // notEmptyDesc.value = 'maximum of 10000'
                // noDesc.value = true
                limitDesc.value = false
            }
            if (data.value.announcementDescription.length > 10000) {
                descLimit.value = 'Exceed the limit.'
                limitDesc.value = true
                noDesc.value = false
                useButton.value = true
            }
            if (quillEditor.value.getText().length <= 1) {
                useButton.value = true
            }
            if (data.value.announcementTitle.length <= 0) {
                useButton.value = true
            }
            if (data.value.announcementDescription <= 0) {
                useButton.value = true
            }
        }
        if (data.value.announcementCategory === undefined || data.value.announcementCategory === "") {
            useButton.value = true
        }
        if (datePublish.value !== undefined) {
            if (!timePublish.value)
                timePublish.value = `06:00`
        }
        if (dateClose.value !== undefined) {
            if (!timeClose.value)
                timeClose.value = `18:00`
        }
        // for (let i = 0; i < selectedFiles.value.length; i++) {
        //     selectedFiles.value = new File([fileData.value], nameData.value.fileName)
        // }
        if (selectedFiles?.value.length !== 0) {
            useButton.value = false
        }
        let oldLength = nameData.value.length
        if (nameData?.value === oldLength) {
            useButton.value = true
        }
        if (nameData?.value.length !== oldLength) {
            useButton.value = false
        }
    }
})

function getBlobType(fileName) {
    const extension = fileName?.split('.').pop().toLowerCase();

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

function cancelAction() {
    router.go(-1)
}
let newDate = ref()
const convertDate = (date) => {
    newDate.value = new Date(date)
    newDate.value = newDate.value.toISOString()
    return newDate.value
}



const saveFile = async (titleName) => {
    const formData = new FormData();
    for (let i = 0; i < selectedFiles.value.length; i++) {
        formData.append('file', selectedFiles.value[i]);
    }
    formData.append('title', titleName)
    try {
        const res = await fetch(`${import.meta.env.VITE_ROOT_API}/api/files`, {
            method: "POST",
            body: formData,
        });

        if (res.ok) {
            console.log('Files uploaded successfully');
            const addedFiles = await res.text();
            console.log(addedFiles);
            // Perform any additional logic as needed
        } else {
            console.error('Failed to upload files');
        }
    } catch (error) {
        console.error('Error during file upload:', error);
    }
};


const updateFile = async (titleName) => {
    if (fileData.value !== undefined && nameData.value !== undefined) {
        for (let i = 0; i < fileData.value.length; i++) {
            console.log(fileData.value[i])
            console.log('aaaaaa')
            selectedFiles.value.push(new File([fileData.value[i]], nameData.value[i].fileName))
        }
    }
    // selectedFiles.value.push(new File([fileData.value], nameData.value.fileName))
    const formData = new FormData();
    for (let i = 0; i < selectedFiles.value.length; i++) {
        formData.append('file', selectedFiles.value[i]);
        console.log(selectedFiles.value);
    }
    formData.append('title', titleName)
    try {
        const res = await fetch(`${import.meta.env.VITE_ROOT_API}/api/files`, {
            method: "PUT",
            body: formData,
        });

        if (res.ok) {
            console.log('Files uploaded successfully');
            const addedFiles = await res.text();
            console.log(addedFiles);
            // Perform any additional logic as needed
        } else {
            console.error('Failed to upload files');
        }
    } catch (error) {
        console.error('Error during file upload:', error);
    }
};
// defineEmits(['edit', 'add'])
let addNew = (async (addNew) => {

    if (addNew.announcementCategory === 'ทั่วไป') {
        addNew.categoryId = 1

    }
    if (addNew.announcementCategory === 'ทุนการศึกษา') {
        addNew.categoryId = 2

    }
    if (addNew.announcementCategory === 'หางาน') {
        addNew.categoryId = 3

    }
    if (addNew.announcementCategory === 'ฝึกงาน') {
        addNew.categoryId = 4

    }
    if (addNew.announcementDisplay === true) {
        addNew.announcementDisplay = "Y"
    }
    if (addNew.announcementDisplay === false) {
        addNew.announcementDisplay = "N"
    }

    if (addNew.announcementDisplay === "") {
        addNew.announcementDisplay = "N"
    }

    const currentDate = new Date()
    let selectedDatePublish
    let selectedDateClose
    if (datePublish.value && timePublish.value) {

        selectedDatePublish = new Date(convertDate(addNew.publishDate))
    }
    if (dateClose.value && timeClose.value) {
        selectedDateClose = new Date(convertDate(addNew.closeDate))

    }
    // const selectedDatePublish = new Date(convertDate(addNew.publishDate))



    if (selectedDatePublish <= currentDate) {
        alert("The publishDate must be a future date")
    }
    if (selectedDatePublish > currentDate) {
        if (addNew.publishDate !== "") {
            addNew.publishDate = convertDate(addNew.publishDate)
        }
    }
    if (selectedDatePublish === currentDate) {
        if (addNew.publishDate !== "") {
            addNew.publishDate = convertDate(addNew.publishDate)
        }
    }

    if (selectedDateClose <= selectedDatePublish) {
        alert("The closeDate must be later than publish date")
    }
    else if (selectedDateClose <= currentDate) {
        alert("The closeDate must be a future date")
    }
    if (selectedDateClose > selectedDatePublish || !datePublish.value) {
        if (selectedDateClose >= currentDate) {
            if (addNew.closeDate !== "") {
                addNew.closeDate = convertDate(addNew.closeDate)
            }
        }
    }
    const jwtToken = localStorage.getItem('jwtToken');
    // Ensure the token is available before making the API call
    if (!jwtToken) {
        router.push({ name: "UserLogin" });
    }
    try {

        const res = await fetch(`${import.meta.env.VITE_ROOT_API}/api/announcements`, {
            method: "POST",
            headers: {
                'content-type': 'application/json',
                Authorization: `Bearer ${jwtToken}`
            },
            body: JSON.stringify({
                announcementTitle: addNew.announcementTitle,
                announcementCategory: addNew.announcementCategory,
                announcementDescription: addNew.announcementDescription,
                publishDate: addNew.publishDate,
                closeDate: addNew.closeDate,
                announcementDisplay: addNew.announcementDisplay,
                categoryId: addNew.categoryId
            })
        })

        if (res.ok) {
            const newValue = await res.json()

            myAnnounce.announcement.push(newValue)
            let title = addNew.announcementTitle
            await saveFile(title)
            router.push({ name: 'Data' });
            // location.reload('/admin/announcement')
        }
        else if (res.status == 403) {
            haveNoRight.value = true
        }

        else {
            const errorResponse = await res.json();
            alert(`There is an error : ${JSON.stringify(errorResponse.detail)}`);
            // alert('The new announcement is not created.')
        }
    }
    catch (err) {
        alert(err.message);
    }
})



let update = (async (edit) => {

    if (edit.announcementCategory === 'ทั่วไป') {
        edit.categoryId = 1

    }
    if (edit.announcementCategory === 'ทุนการศึกษา') {
        edit.categoryId = 2

    }
    if (edit.announcementCategory === 'หางาน') {
        edit.categoryId = 3

    }
    if (edit.announcementCategory === 'ฝึกงาน') {
        edit.categoryId = 4

    }

    if (edit.announcementDisplay === true) {
        edit.announcementDisplay = "Y"
    }
    if (edit.announcementDisplay === false) {
        edit.announcementDisplay = "N"
    }
    if (datePublish.value && timePublish.value) {
        if (edit.publishDate !== null) {
            edit.publishDate = `${datePublish.value}T${timePublish.value}`
        }
    }

    if (dateClose.value && timeClose.value) {
        if (edit.closeDate !== null) {
            edit.closeDate = `${dateClose.value}T${timeClose.value}`
        }
    }

    const current = new Date()
    let editPublishDate
    let editCloseDate
    if (datePublish.value && timePublish.value) {
        editPublishDate = new Date(convertDate(edit.publishDate))
    }
    if (dateClose.value && timeClose.value) {
        editCloseDate = new Date(convertDate(edit.closeDate))
    }
    if (editPublishDate <= current) {
        alert("The publishDate must be a future date")
    }
    if (editPublishDate >= current) {
        if (datePublish.value !== "" && timePublish.value !== "") {
            if (edit.publishDate !== null) {
                edit.publishDate = convertDate(edit.publishDate)
            }
        }
    }
    if (editCloseDate <= editPublishDate) {
        alert("The closeDate must be later than publish date")

    }
    else if (editCloseDate <= current) {
        alert("The closeDate must be a future date")
    }
    if (editCloseDate > editPublishDate || !datePublish.value) {
        if (editCloseDate > current) {
            if (dateClose.value !== "" && timeClose.value !== "") {
                if (edit.closeDate !== null) {
                    edit.closeDate = convertDate(edit.closeDate)
                }
            }
        }
    }

    //if (data.value.announcementTitle.trim() === "" || data.value.announcementCategory.trim() === "" || data.value.announcementDescription.trim() === "") {
    if (data.value.announcementTitle.trim() === "") {
        //alert(`Can't update announcement: Please enter title, description and select category.`)
        alert(`Can't update announcement: Title must not be null.`)
    }
    else if (data.value.announcementCategory.trim() === "") {
        alert(`Can't update announcement: Category must not be null.`)
    }
    else if (data.value.announcementDescription.trim() === "") {
        alert(`Can't update announcement: Description must not be null.`)
    }
    const jwtToken = localStorage.getItem('jwtToken');
    // Ensure the token is available before making the API call
    if (!jwtToken) {
        router.push({ name: "UserLogin" });
    }
    try {
        const res = await fetch(`${import.meta.env.VITE_ROOT_API}/api/announcements/${edit.id}`, {
            method: "PUT",
            headers: {
                'content-type': 'application/json',
                Authorization: `Bearer ${jwtToken}`,
            },
            body: JSON.stringify({
                announcementTitle: edit.announcementTitle,
                announcementCategory: edit.announcementCategory,
                announcementDescription: edit.announcementDescription,
                publishDate: edit.publishDate,
                closeDate: edit.closeDate,
                announcementDisplay: edit.announcementDisplay,
                categoryId: edit.categoryId
            })
        })

        if (res.ok) {
            const modifyAnn = await res.json()
            myAnnounce.announcement = myAnnounce.announcement.map((ann) => {
                if (ann.id === modifyAnn.id) {
                    ann.announcementTitle = modifyAnn.announcementTitle,
                        ann.announcementCategory = modifyAnn.announcementCategory,
                        ann.announcementDescription = modifyAnn.announcementDescription,
                        ann.publishDate = modifyAnn.publishDate,
                        ann.closeDate = modifyAnn.closeDate,
                        ann.announcementDisplay = modifyAnn.announcementDisplay,
                        ann.categoryId = modifyAnn.categoryId
                }
                return ann
            })
            let title = edit.announcementTitle
            await updateFile(title)
            // location.reload('/admin/announcement')
            router.push({ name: 'Data' });
        }
        else if (res.status == 403) {
            haveNoRight.value = true
        }

        else {
            const errorResponse = await res.json();
            alert(`There is an error : ${JSON.stringify(errorResponse.detail)}`);
            // alert('The announcements are not modified.');
        }

    }
    catch (err) {
        alert(err.message);
    }
})
function changeValue(event) {
    if (event.target.id === 'publish') {
        timePublish.value = ""
        data.value.publishDate = ""
    }

    if (event.target.id === 'close') {
        timeClose.value = ""
        data.value.closeDate = ""
    }
}
const selectedFiles = ref([])
const fileLimit = ref(false)
const fileInput = ref(null)
const myImage = ref('')
const previewImageFile = ref('')

const previewBinaryFile = (binaryFileObject) => {
    return URL.createObjectURL(binaryFileObject)
}

const handleFileChange = (event) => {
    const files = event.target.files
    for (let i = 0; i < files.length; i++) {
        const file = files[i]
        if (file.size <= 20 * 1024 * 1024) {
            selectedFiles.value.push(file)
            fileLimit.value = false
        }
        else {
            fileLimit.value = true
        }
    }
    if (selectedFiles.value.length > 5) {
        selectedFiles.value = selectedFiles.value.slice(0, 5)
    }
    if (nameData.value.length > 5) {
        nameData.value = nameData.value.slice(0, 5)
    }
}

const deleteFile = async (index, name, id) => {
    if (params.id) {
        // console.log("vvv")
        nameData.value.splice(index, 1)
        // let oldFiles
        // nameData.value = nameData.value.filter((file) => {
        //     oldFiles = file.idFiles == index
        //     return file.idFiles !== index
        // })
        // let type = getBlobType(oldFiles.fileName)
        fileData.value.splice(index, 1)
        // fileData.value = fileData.value.filter((file) => {
        //     return file.type !== type
        // })

        try {
            const res = await fetch(
                `${import.meta.env.VITE_ROOT_API}/api/files/fileStep/${id}`,
                {
                    method: "DELETE",
                    headers: {
                        "content-type": "application/json"
                    },
                    body: JSON.stringify({
                        fileName: name
                    })
                }
            );
            if (res.ok) {

            } else {
                const errorResponse = await res.json();
                alert(`There is an error : ${JSON.stringify(errorResponse)}`);
            }
        } catch (err) {
            alert(err);
        }
    }
}

const deleted = (index) => {
    selectedFiles.value.splice(index, 1)
}
</script>

<template>
    <div class="flex flex-col ml-16 mr-16">
        <div>
            <p class="mb-3 font-bold text-lg pt-5">Announcement Detail:</p>
        </div>
        <div class="flex flex-row">
            <div class="font-medium">Title<span class="text-red-600 pr-96 mr-36 pl-1">*</span></div>
            <p class="text-red-600 font-semibold ml-96 pl-80" v-show="noTiltle">{{ notEmpty }}</p>
            <p class="text-red-600 font-semibold ml-96 pl-80" v-show="limitTitle">{{ titleLimit }}</p>
            <p class="text-red-600 font-semibold ml-96 pl-80" v-show="editTitle">{{ notEditTitle }}</p>
        </div>
        <div class="pt-1">
            <input class="ann-title w-full border border-teal-500 rounded-lg" v-model="data.announcementTitle" type="text"
                maxlength="200" placeholder=" Plese insert title" id="title">
        </div>
        <div class="pt-3 font-medium">Category<span class="text-red-600 pr-72 pl-1">*</span></div>
        <div class="pt-1" id="selectCategory">
            <select v-model="data.announcementCategory"
                class="block appearance-none w-1/6  bg-white border-gray-400 hover:border-gray-500 px-4 py-2 pr-8 rounded-full shadow ann-category">
                <option v-for="(categorys, index) in category" :key="index">{{ categorys.categoryName }}</option>
            </select>
        </div>
        <div class="flex flex-row">
            <div class="pt-3 font-medium">Description<span class="text-red-600 pr-96 mr-96 pl-1">*</span></div>
            <p class="text-red-600 font-semibold ml-96 pl-8 pt-3" v-show="noDesc">{{ notEmptyDesc }}</p>
            <p class="text-red-600 font-semibold ml-96 pl-8 pt-3" v-show="limitDesc">{{ descLimit }}</p>
        </div>
        <div class="h-48 pt-1">
            <QuillEditor id="editor-container" them="snow" toolbar="full" v-model:content="data.announcementDescription"
                contentType="html" class="ann-description" placeholder=" Plese insert description" ref="quillEditor">
            </QuillEditor>
        </div>
        <div class="pt-14 font-medium">
            <p>Attach Files.</p>
        </div>
        <!-- <form id="fileForm" @submit.prevent="saveFile"> -->
        <form id="fileForm">
            <input type="file" name="file" ref="fileInput" multiple @change="handleFileChange" />
            <div class="flex flex-col">
                <p v-if="nameData.length !== 0" v-for="(file, index) in nameData" :key="index"
                    class="flex flex-row items-start">
                    <span v-if="nameData.length !== 0">{{ file.fileName }}</span>
                    <button type="button" @click="deleteFile(index, file.fileName, params.id)" class="pl-3">
                        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5"
                            stroke="currentColor" class="w-6 h-6 stroke-red-500 hover:stroke-red-900">
                            <path stroke-linecap="round" stroke-linejoin="round"
                                d="M14.74 9l-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 01-2.244 2.077H8.084a2.25 2.25 0 01-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 00-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 013.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 00-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 00-7.5 0" />
                        </svg>
                    </button>
                </p>
                <p v-for="(file, index) in selectedFiles" :key="index" class="flex flex-row items-start">
                    <span>{{ file.name }}</span>
                    <button type="button" @click="deleted(index)" class="pl-3">
                        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5"
                            stroke="currentColor" class="w-6 h-6 stroke-red-500 hover:stroke-red-900">
                            <path stroke-linecap="round" stroke-linejoin="round"
                                d="M14.74 9l-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 01-2.244 2.077H8.084a2.25 2.25 0 01-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 00-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 013.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 00-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 00-7.5 0" />
                        </svg>
                    </button>
                </p>
                <!-- <img v-show="previewImageFile" :src="previewImageFile"
                    style="max-width: 10%;max-height: 10%; margin-top: 10px;">
                <a class="text-blue-500 hover:text-blue-900 underline" v-show="previewImageFile" :href="previewImageFile"
                    target="_blank">{{ myImage }}</a> -->
                <!-- <button type="submit"
                    class="mt-2 bg-gray-300 rounded-lg w-12 text-md font-semibold hover:bg-gray-500 hover:text-white">save</button> -->
            </div>
            <div class="flex flex-col">
                <!-- <div v-for="(file, index) in selectedFiles" :key="index" class="flex flex-row items-start">
                    <span>{{ file.name }} - {{ formatFileSize(file.size) }}</span>
                    <button type="button" @click="deleteFile(index)" class="pl-3">
                        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5"
                            stroke="currentColor" class="w-6 h-6 stroke-red-500 hover:stroke-red-900">
                            <path stroke-linecap="round" stroke-linejoin="round"
                                d="M14.74 9l-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 01-2.244 2.077H8.084a2.25 2.25 0 01-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 00-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 013.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 00-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 00-7.5 0" />
                        </svg>
                    </button>
                </div> -->
                <!-- <div v-if="nameData.length !== 0" v-for="(file, index) in nameData" :key="index"
                    class="flex flex-row items-start mt-2">
                    {{ file.fileName }}
                    <button type="button" @click="deleteFile(index)" class="pl-3">
                        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5"
                            stroke="currentColor" class="w-6 h-6 stroke-red-500 hover:stroke-red-900">
                            <path stroke-linecap="round" stroke-linejoin="round"
                                d="M14.74 9l-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 01-2.244 2.077H8.084a2.25 2.25 0 01-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 00-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 013.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 00-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 00-7.5 0" />
                        </svg>
                    </button>
                </div> -->
            </div>
            <div v-show="fileLimit" class="text-red-500">File size exceeds 20MB limit</div>
            <div v-show="selectedFiles.length >= 5 || nameData.length >= 5" class="text-red-500">You can only attach up to 5
                files.</div>
        </form>
        <p>{{ msg }}</p>
        <div class="pt-3 font-medium">Publish Date</div>
        <div class="flex flex-row">
            <input class="ann-publish-date w-40 border border-teal-500 rounded-lg" type="date" @change="changeValue"
                id="publish" v-model="datePublish">
            <input class="ann-publish-time ml-3 w-40 border border-teal-500 rounded-lg" type="time" v-model="timePublish"
                :disabled="!datePublish">
        </div>

        <div class="pt-3 font-medium">Close Date</div>
        <div class="flex flex-row">

            <input class="ann-close-date w-40 border border-teal-500 rounded-lg" type="date" @change="changeValue"
                id="close" v-model="dateClose">
            <input class="ann-close-time ml-3 w-40 border border-teal-500 rounded-lg" type="time" v-model="timeClose"
                :disabled="!dateClose">

        </div>
        <div class="pt-3 font-medium">Display</div>
        <div>
            <input class="ann-display mr-1 border border-teal-500 rounded-lg" type="checkbox" ref="check"
                v-model="data.announcementDisplay">Check to show this announcement
        </div>

        <div class="pt-5 flex flex-row">
            <!-- <RouterLink :to="{ name: 'Data' }"> -->
            <button type="submit" class="btn btn-outline btn-info ann-button" id="submit" v-if="params.id"
                @click="update(data)" :disabled="useButton">Edit
            </button>
            <!-- </RouterLink> -->
            <!-- <RouterLink :to="{ name: 'Data' }"> -->
            <button type="submit" class="btn btn-outline btn-success ann-button" id="submit" @click="addNew(data)"
                :disabled="useButton" v-if="!params.id">
                <!-- <RouterLink :to="{ name: 'Data' }">Submit</RouterLink> -->Submit
            </button>
            <!-- </RouterLink> -->

            <button type="button" class="btn btn-outline btn-error ann-button ml-2" @click="cancelAction">Cancel</button>

        </div>
        <div v-show="loginfirst">
            <div class="popup">
                <div class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-screen h-screen bg-black opacity-60">
                </div>
            </div>
            <div class="popup">
                <div
                    class="p-5 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-1/7 h-2/7 bg-gray-200 border-4 border-gray-700 rounded-xl">
                    <div class="relative text-right">
                        <RouterLink :to="{ name: 'UserLogin' }">
                            <button
                                class="font-bold text-xl hover:bg-red-700 hover:text-white pl-2 pr-2 pb-1 rounded-md">x</button>
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
                <div class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-screen h-screen bg-black opacity-60">
                </div>
            </div>
            <div class="popup">
                <div
                    class="p-5 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-1/7 h-2/7 bg-gray-200 border-4 border-gray-700 rounded-xl">
                    <div class="relative text-right">
                        <RouterLink :to="{ name: 'UserLogin' }">
                            <button
                                class="font-bold text-xl hover:bg-red-700 hover:text-white pl-2 pr-2 pb-1 rounded-md">x</button>
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
                <div class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-screen h-screen bg-black opacity-60">
                </div>
            </div>
            <div class="popup">
                <div
                    class="p-5 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-1/7 h-2/7 bg-gray-200 border-4 border-gray-700 rounded-xl">
                    <div class="relative text-right">
                        <RouterLink :to="{ name: 'Data' }">
                            <button
                                class="font-bold text-xl hover:bg-red-700 hover:text-white pl-2 pr-2 pb-1 rounded-md">x</button>
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
