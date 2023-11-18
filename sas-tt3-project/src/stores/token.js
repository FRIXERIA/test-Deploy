import { defineStore, acceptHMRUpdate } from "pinia";
import { ref } from "vue";
import { fetchToken } from "../code/get.js";

export const useToken = defineStore("tokennotify", () => {
  const token = ref("");
  const fetch = async () => {
    token.value = await fetchToken();
  };
  const getToken = () => {
    return token.value;
  };
  return { token, getToken, fetch };
});

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useToken, import.meta.hot));
}
