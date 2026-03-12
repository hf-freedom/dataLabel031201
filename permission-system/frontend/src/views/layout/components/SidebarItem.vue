<template>
  <div v-if="!item.meta || !item.meta.hidden">
    <el-sub-menu
      v-if="item.children && item.children.length > 0"
      :index="item.path"
    >
      <template #title>
        <el-icon v-if="item.meta && item.meta.icon">
          <component :is="item.meta.icon" />
        </el-icon>
        <span>{{ item.meta ? item.meta.title : item.name }}</span>
      </template>
      <el-menu-item
        v-for="child in item.children"
        :key="child.path"
        :index="child.path"
      >
        <el-icon v-if="child.meta && child.meta.icon">
          <component :is="child.meta.icon" />
        </el-icon>
        <span>{{ child.meta ? child.meta.title : child.name }}</span>
      </el-menu-item>
    </el-sub-menu>
    <el-menu-item v-else :index="item.path">
      <el-icon v-if="item.meta && item.meta.icon">
        <component :is="item.meta.icon" />
      </el-icon>
      <span>{{ item.meta ? item.meta.title : item.name }}</span>
    </el-menu-item>
  </div>
</template>

<script setup>
defineProps({
  item: {
    type: Object,
    required: true
  }
})
</script>
