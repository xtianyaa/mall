import { createRouter, createWebHistory } from "vue-router";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: "/login",
      component: () => import("../views/login/index.vue"),
      meta: { title: "登录" },
    },
    {
      path: "/",
      component: () => import("../layouts/basic-layout.vue"),
      redirect: "/dashboard",
      children: [
        {
          path: "/dashboard",
          component: () => import("../views/dashboard/index.vue"),
          meta: { title: "工作台" },
        },
        {
          path: "/yuan-mall-module/product/category",
          component: () => import("../views/yuan-mall-module/product/category/index.vue"),
          meta: { title: "商品分类" },
        },
        {
          path: "/yuan-mall-module/product/goods",
          component: () => import("../views/yuan-mall-module/product/goods/index.vue"),
          meta: { title: "商品管理" },
        },
        {
          path: "/yuan-mall-module/order",
          component: () => import("../views/yuan-mall-module/order/index.vue"),
          meta: { title: "订单管理" },
        },
        {
          path: "/yuan-mall-module/after-sale/order",
          component: () => import("../views/yuan-mall-module/after-sale/order/index.vue"),
          meta: { title: "售后订单" },
        },
        {
          path: "/yuan-mall-module/user",
          component: () => import("../views/yuan-mall-module/user/index.vue"),
          meta: { title: "用户管理" },
        },
        {
          path: "/yuan-mall-module/user/address",
          component: () => import("../views/yuan-mall-module/user/address/index.vue"),
          meta: { title: "收货地址" },
        },
        {
          path: "/yuan-mall-module/user/coupon",
          component: () => import("../views/yuan-mall-module/user/coupon/index.vue"),
          meta: { title: "用户优惠券" },
        },
        {
          path: "/yuan-mall-module/marketing/coupon",
          component: () => import("../views/yuan-mall-module/marketing/coupon/index.vue"),
          meta: { title: "优惠券管理" },
        },
        {
          path: "/yuan-mall-module/content/home",
          component: () => import("../views/yuan-mall-module/content/home/index.vue"),
          meta: { title: "首页装修" },
        },
        {
          path: "/yuan-mall-module/system/settings",
          component: () => import("../views/yuan-mall-module/system/settings/index.vue"),
          meta: { title: "系统设置" },
        },
      ],
    },
  ],
  scrollBehavior: () => ({ left: 0, top: 0 }),
});

router.beforeEach((to, _from, next) => {
  const token = window.localStorage.getItem("YA_ADMIN_TOKEN");
  if (to.path === "/login") {
    if (token) {
      next("/dashboard");
    } else {
      next();
    }
    return;
  }
  if (!token) {
    next("/login");
    return;
  }
  next();
});

export default router;
