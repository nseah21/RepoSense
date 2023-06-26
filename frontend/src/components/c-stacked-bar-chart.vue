<template lang="pug">
#summary
  template(v-if="filterBreakdown")
    .summary-chart__contrib--bar(
      v-for="width in widths",
      v-bind:style="{ width: `${width}%`,\
        'background-color': color }",
      v-bind:title="`${fileType}: ${fileTypeLinesChanged} lines, \
        total: ${totalLinesChanged} lines (contribution from ${minDate} to \
        ${maxDate})`"
    )
  template(v-else-if="isCommitDiff")
    .summary-chart__contrib--bar(
      v-for="(width, color) in diffstat",
      v-bind:style="{ width: `${width}%`,\
        'background-color': color }",
    )
  template(v-else)
    .summary-chart__contrib--bar(
      v-for="width in widths",
      v-bind:style="{ width: `${width}%` }"
    )
</template>

<script lang="ts">
import { PropType } from 'vue';

export default {
  props: {
    filterBreakdown: {
      type: Boolean,
      default: false,
    },
    isCommitDiff: {
      type: Boolean,
      default: false,
    },
    widths: {
      type: Array as PropType<number[]>,
      default: () => [],
    },
    color: {
      type: String,
      default: 'red',
    },
    fileType: {
      type: String,
      default: '?',
    },
    fileTypeLinesChanged: {
      type: Number,
      default: 0,
    },
    totalLinesChanged: {
      type: Number,
      default: 0,
    },
    minDate: {
      type: String,
      default: '?',
    },
    maxDate: {
      type: String,
      default: '?',
    },
    diffstat: {
      type: Object as PropType<{ [key: string]: number[] }>,
      default: () => {},
    },
  },
};
</script>
