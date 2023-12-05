import { createRouter, createWebHistory } from 'vue-router'
import {useStore} from "@/stores";


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: 'welcome',
      component: () => import('@/views/WelcomeView.vue'),
      redirect: 'login',
      children:[
        {
          path: 'login',
          name: 'welcome-login',
          component: () => import('@/components/welcome/LoginPage.vue'),
        },
        {
          path: 'register',
          name: 'register',
          component: () => import('@/components/welcome/RegisterPage.vue'),
        },
        {
          path: 'forget',
          name: 'welcome-forget',
          component: () => import("@/components/welcome/ForgetPage.vue")
        }
      ]
    },
    {
      path: '/index',
      name: 'index',
      redirect: '/index/todolist',
      component: () => import("@/views/IndexView.vue"),
      children: [
        {
          path: 'todolist',
          name: 'todolist',
          component: () => import("@/components/index/TodoList.vue")
        }
      ]
    }
  ]
})
router.beforeEach((to, from, next) => {
  const store = useStore()
  console.log(to)
  if(store.auth.user != null && to.path === '/login'){
    next('/index')
  }else if (store.auth.user == null && to.fullPath.startsWith('/index')){
    next('')
  } else if(to.matched.length === 0){
    next('/index')
  }
  else{
    next()
  }
})
export default router
