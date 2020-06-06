<template>
  <div id="app">
    <!-- 此处添加keep-alive :目的就是为了防止 过度的销毁、创建。 -->
       <!-- 注意：有一个问题就是detail 属性时，根据id 点开不同的属性和页面，就不要keepalive。
          此处 ，就得需要一个 新属性，排除属性：exclude='Detail';
        -->
    <keep-alive exclude="Detail_item">
      <router-view></router-view>
    </keep-alive>

<!--    <nav-bar></nav-bar>-->
    <tabBar-views></tabBar-views>
  </div>
</template>

<script>
import MainTabBar from 'components/content/mainTabBar/MainTabBar'
export default {
  slot: 'App',
  components: {
  	'tabBar-views':MainTabBar,
  //  MainTabBar
  }
}
</script>

<style>
  @import "assets/css/base.css";

</style>
