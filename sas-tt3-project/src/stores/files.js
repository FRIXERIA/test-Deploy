import { defineStore,acceptHMRUpdate } from "pinia";
import {ref} from 'vue'
import { fetchFile } from '../code/get.js'

export const useFiles=defineStore('filenotify',()=>{
    const fileName = ref([])
    const FileFetch=async ()=>{
     fileName.value=await fetchFile()
    }
    const getAllFile=()=>{
        return fileName.value
    }
return {fileName,FileFetch,getAllFile}

})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useFiles, import.meta.hot))
    }