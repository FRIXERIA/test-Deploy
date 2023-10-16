import { useRouter } from "vue-router";
import router from "../router";
const ALL_DATA = `${import.meta.env.VITE_ROOT_API}/api/announcements`;
const USER_DATA = `${import.meta.env.VITE_ROOT_API}/api/users`;
const TOKEN_DATA = `${import.meta.env.VITE_ROOT_API}/api/token`;
const CHECK_TOKEN = `${import.meta.env.VITE_ROOT_API}/api/check`;
let status;

//get tokens
const jwtToken = localStorage.getItem("jwtToken");
const refreshToken = localStorage.getItem("refreshToken");

const fetchToken = async (refresh) => {
  try {
    const jwtToken = localStorage.getItem("jwtToken");
    const refreshToken = localStorage.getItem("refreshToken");
    // Ensure the token is available before making the API call
    if (!refreshToken) {
      console.error("JWT Token not found in local storage");
      return;
    }

    const res = await fetch(TOKEN_DATA, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${refresh}`,
      },
    });
    if (res.ok) {
      const newToken = await res.json();
      localStorage.setItem("jwtToken", newToken.token);
      return res.status;
    }
    if (res.status === 401) {
      // router.push({ name: "UserLogin" });
      return res.status;
    } else {
      const errorResponse = await res.json();
      alert(`There is an error : ${JSON.stringify(errorResponse)}`);
    }
  } catch (err) {
    console.log(err);
  }
};

const fetchData = async () => {
  try {
    const jwtToken = localStorage.getItem("jwtToken");
    const refreshToken = localStorage.getItem("refreshToken");
    // Ensure the token is available before making the API call
    if (!jwtToken) {
      console.error("JWT Token not found in local storage");
      return;
    }
    const res = await fetch(ALL_DATA, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${jwtToken}`,
      },
    });
    if (res.ok) {
      const announcement = await res.json();
      console.log(announcement);
      return announcement;
    }
    if (res.status === 401) {
      if ((await res.text()) == "JWT Token has expired") {
        await fetchToken(refreshToken);
      }
      return res.status
    }
    if (res.status === 403) {
      return res.status
    } 
    else {
      const errorResponse = await res.json();
      alert(`There is an error : ${JSON.stringify(errorResponse)}`);
    }
  } catch (error) {
    alert(error);
  }
};

const fetchId = async (id, num = false) => {
  try {
    let jwtToken = localStorage.getItem("jwtToken");
    let refreshToken = localStorage.getItem("refreshToken");
    // Ensure the token is available before making the API call
    // if (!jwtToken) {
    //   console.error("JWT Token not found in local storage");
    //   return;
    // }
    if(jwtToken!=null){
    const res = await fetch(
      `${import.meta.env.VITE_ROOT_API}/api/announcements/${id}?count=${num}`,
      {
        method: "GET",
        headers: {
          Authorization: `Bearer ${jwtToken}`,
        },
      }
    );
    if (res.ok) {
      const data = await res.json();
      return data;
    }
    if (res.status === 401) {
      if ((await res.text()) == "JWT Token has expired") {
        await fetchToken(refreshToken);
      }
      return res.status
    }
    if(res.status === 403){
      return res.status
    }
     else {
      const errorResponse = await res.json();
      alert(`There is an error : ${JSON.stringify(errorResponse)}`);
    }
  }
  if(!jwtToken){

    const res = await fetch(
      `${import.meta.env.VITE_ROOT_API}/api/announcements/${id}?count=${num}`,
      {
        method: "GET"
      }
    );
    if (res.ok) {
      const data = await res.json();
      return data;
    }
    if (res.status === 401) {
      if ((await res.text()) == "JWT Token has expired") {
        await fetchToken(refreshToken);
      }
    } 
    else {
      const errorResponse = await res.json();
      alert(`There is an error : ${JSON.stringify(errorResponse)}`);
    }
  }
  // if(payloadObject.role== 'announcer'){

  //   const res = await fetch(
  //     `${import.meta.env.VITE_ROOT_API}/api/announcements/${id}?count=${num}`,
  //     {
  //       method: "GET",
  //     }
  //   );
  //   if (res.ok) {
  //     const data = await res.json();
  //     return data;
  //   }
  //   if (res.status === 401) {
  //     if ((await res.text()) == "JWT Token has expired") {
  //       await fetchToken(refreshToken);
  //     }
  //   } 
  //   else {
  //     const errorResponse = await res.json();
  //     alert(`There is an error : ${JSON.stringify(errorResponse)}`);
  //   }
  // }

  } catch (error) {
    alert(error);
  }
};

const fetchCategory = async () => {
  try {
    const jwtToken = localStorage.getItem("jwtToken");
    const refreshToken = localStorage.getItem("refreshToken");
    // Ensure the token is available before making the API call
    // if (!jwtToken) {
    //   console.error('JWT Token not found in local storage');
    //   return;
    // }
    const res = await fetch(`${import.meta.env.VITE_ROOT_API}/api/category`, {
      method: "GET",
    });
    if (res.ok) {
      const data = await res.json();
      console.log(data.value);
      return data;
    }

    if (res.status === 401) {
      if ((await res.text()) == "JWT Token has expired") {
        await fetchToken(refreshToken);
      }
      return res.status
    }
    if(res.status === 403){
      return res.status
    }
  } catch (error) {
    console.log(error);
  }
};
const fetchPage = async (
  pages = 0,
  mode = "admin",
  category = null,
  size = 5
) => {
  const jwtToken = localStorage.getItem("jwtToken");
  const refreshToken = localStorage.getItem("refreshToken");
  try {
    if (category !== null) {
      const res = await fetch(
        `${
          import.meta.env.VITE_ROOT_API
        }/api/announcements/pages?page=${pages}&mode=${mode}&category=${category}`,
        {
          method: "GET",
        }
      );
      if (res.ok) {
        const announcement = await res.json();
        console.log(announcement);
        return announcement;
      } else {
        const errorResponse = await res.json();
        alert(`There is an error : ${JSON.stringify(errorResponse)}`);
      }
    }
    if (category === null) {
      const jwtToken = localStorage.getItem("jwtToken");
      const refreshToken = localStorage.getItem("refreshToken");
      // Ensure the token is available before making the API call
      // if (!jwtToken) {
      //   console.error('JWT Token not found in local storage');
      //   return;
      // }
      const res = await fetch(
        `${
          import.meta.env.VITE_ROOT_API
        }/api/announcements/pages?mode=${mode}&page=${pages}&size=${size}`,
        {
          method: "GET",
        }
      );
      if (res.ok) {
        const announcement = await res.json();
        console.log(announcement);
        return announcement;
      }
      // if(res.status===401){
      //   if(await res.text()=='JWT Token has expired'){
      //     await fetchToken(refreshToken)
      //   }
      // }
      else {
        const errorResponse = await res.json();
        alert(`There is an error : ${JSON.stringify(errorResponse)}`);
      }
    }
  } catch (error) {
    alert(error);
  }
};

const fetchUser = async () => {
  try {
    const jwtToken = localStorage.getItem("jwtToken");
    const refreshToken = localStorage.getItem("refreshToken");
    const res = await fetch(USER_DATA, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${jwtToken}`,
      },
    });
    if (res.ok) {
      const user = await res.json();
      console.log(user);
      return user;
    }
    if (res.status === 401) {
      if ((await res.text()) == "JWT Token has expired") {
        await fetchToken(refreshToken);
      }
      return res.status;
    }
    if (res.status == 403){
      // await fetchData()
      return res.status
    }
     else {
      const errorResponse = await res.json();
      alert(`There is an error : ${JSON.stringify(errorResponse)}`);
    }
  } catch (error) {
    alert(error);
    console.log(error);
  }
};

const fetchUserId = async (id) => {
  try {
    const jwtToken = localStorage.getItem("jwtToken");
    // Ensure the token is available before making the API call
    if (!jwtToken) {
      console.error("JWT Token not found in local storage");
      return;
    }
    const res = await fetch(
      `${import.meta.env.VITE_ROOT_API}/api/users/${id}`,
      {
        method: "GET",
        headers: {
          Authorization: `Bearer ${jwtToken}`,
        },
      }
    );
    if (res.ok) {
      const data = await res.json();
      return data;
    } 
     if(res.status==403){
      return res.status
     }
    
    else {
      const errorResponse = await res.json();
      alert(`There is an error : ${JSON.stringify(errorResponse)}`);
    }
  } catch (error) {
    alert(error);
  }
};

export {
  fetchData,
  fetchId,
  fetchCategory,
  fetchPage,
  fetchUser,
  fetchUserId,
  fetchToken,
};
