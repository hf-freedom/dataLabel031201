<template>
  <div>
    <el-submenu v-if="item.children && item.children.length" :index="item.path">
      <template slot="title">
        <i :class="item.icon" v-if="item.icon"></i>
        <span>{{ item.name }}</span>
      </template>
      <menu-item
        v-for="child in item.children"
        :key="child.path"
        :item="child"
      />
    </el-submenu>
    <el-menu-item v-else-if="item.type === 'menu'" :index="item.path">
      <i :class="item.icon" v-if="item.icon"></i>
      <span>{{ item.name }}</span>
    </el-menu-item>
  </div>
</template>

<script>
export default {
  name: 'MenuItem',
  props: ['item']
}
</script>
