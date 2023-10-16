import { createRouter, createWebHistory } from "vue-router";
import NotFound from "../views/NotFoundView.vue";
import Data from "../components/Announcement.vue";
import Detail from "../components/Detail.vue";
import Home from "../views/HomeView.vue";
import Edit from "../components/AddEdit.vue";
import UserAll from "../components/UserAll.vue";
import UserDetail from "../components/UserDetail.vue";
import ShowUser from "../components/ShowUser.vue";
import AddEditUser from "../components/AddEditUser.vue";
import MatchPwd from "../components/MatchPwd.vue";
import UserLogin from "../components/UserLogin.vue";
// const history = createWebHistory(import.meta.env.VITE_ROOT_URL)
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      redirect: "/announcement",
    },
    {
      path: "/login",
      name: "UserLogin",
      component: UserLogin,
    },
    {
      path: "/admin/announcement",
      name: "Data",
      component: Data,
    },
    {
      path: "/admin/announcement/:id",
      name: "Detail",
      component: Detail,
    },
    {
      path: "/admin/announcement/:id/edit",
      name: "Edit",
      component: Edit,
    },
    {
      path: "/admin/announcement/add",
      name: "Add",
      component: Edit,
    },
    {
      path: "/announcement",
      name: "UserAll",
      component: UserAll,
    },
    {
      path: "/announcement/:id",
      name: "UserDetail",
      component: UserDetail,
    },
    {
      path: "/admin/user",
      name: "ShowUser",
      component: ShowUser,
    },
    {
      path: "/admin/user/add",
      name: "AddUser",
      component: AddEditUser,
    },
    {
      path: "/admin/user/:id/edit",
      name: "EditUser",
      component: AddEditUser,
    },
    {
      path: "/admin/user/match",
      name: "MatchPwd",
      component: MatchPwd,
    },
    {
      path: "/:catchNotMatchPath(.*)",
      name: "NotFound",
      component: NotFound,
    },
  ],
});

// const sendToken = () => {
//   const jwtToken = localStorage.getItem("jwtToken");
//   if (!jwtToken) {
//     const base64Payload = jwtToken.split(".")[1];
//     const decodePayload = atob(base64Payload);
//     const payloadObject = JSON.parse(decodePayload);
//     router.beforeEach((to,from,next)=>{
//       if(to.name !== 'UserAll'){
//         console.log(payloadObject.sub);
//         if(payloadObject.role !== 'admin' && to.name === 'ShowUser'){
//           alert('you dont have access!!')
//         }
//         else alert('you have access!!')
//       }
//       else next()
//     })
//   }
// };

// const router = createRouter({ history, routes })
export default router;
