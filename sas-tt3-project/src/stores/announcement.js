import { defineStore,acceptHMRUpdate } from "pinia";
import {ref} from 'vue'
import { fetchData } from '../code/get.js'
export const useAnnounce=defineStore('notify',()=>{
    const announcement = ref([])
    const fetchAnnounce=async ()=>{
     announcement.value=await fetchData()
    }
    const getAllAnnounce=()=>{
        return announcement.value
    }
return {announcement,fetchAnnounce,getAllAnnounce}

})
export const useMode=defineStore('allMode',()=>{
    const mode = ref()
    const page =ref()
    const category=ref('ทั้งหมด')
    const categoryId=ref()
    const setMode=async (newMode)=>{
     mode.value=newMode
    }
    const setPage=async (newPage)=>{
        page.value=newPage
       }
       const setCategory=async (newCategory='ทั้งหมด')=>{
        category.value=newCategory
       }
       const setCategoryId=async (newId)=>{
        categoryId.value=newId
       }
return {mode,page,category,categoryId,setCategory,setMode,setPage,setCategoryId}

})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useAnnounce,useMode, import.meta.hot))
    }