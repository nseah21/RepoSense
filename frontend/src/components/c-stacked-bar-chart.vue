<template lang="pug">
#summary
  .summary-chart__contribution
    template(v-if="filterBreakdown")
      .summary-chart__contrib(
        v-for="(lengths, fileType) in fileTypeContributionBars"
      )
        .summary-chart__contrib--bar(
          v-for="length in lengths",
          v-bind:style="{ width: `${length}%`,\
            'background-color': fileTypeColors[fileType] }",
          v-bind:title="`${fileType}: ${fileTypeLinesChanged} lines, \
            total: ${totalLinesChanged} lines (contribution from ${minDate} to \
            ${maxDate})`"
        )
    template(v-else-if="isCommitDiff")
      .summary-chart__contrib(
        v-for="(width, color) in diffstat"
      )
        .summary-chart__contrib--bar(
          v-bind:style="{ width: `${width}%`,\
          'background-color': color }",
        )
      br
    template(v-else)
      .summary-chart__contrib(
        v-bind:title="`Total contribution from ${minDate} to ${maxDate}: \
          ${totalLinesChanged} lines`"
      )
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
    fileTypeContributionBars: {
      type: Object as PropType<{ [key: string]: number[] }>,
      default: () => {},
    },
    fileTypeColors: {
      type: Object as PropType<{ [key: string]: string }>,
      default: () => {},
    },
  },
};
</script>
