<script lang="ts">
    import { onMount } from "svelte"
    import NetworkKV from "./networkkv"

    let key = "/LatencyChecker/Ping"

    let running: boolean

    let networkKv = NetworkKV.getDefault()
    let startTime: Date | undefined = undefined
    let endTime: Date | undefined = undefined
    let times: number[] = []

    onMount(() => {
        return networkKv.addListener(key + "~", (_key, _value) => {
            endTime = new Date()
            if (startTime !== undefined) {
                times = [endTime.getTime() - startTime.getTime(), ...times]
                times = times.slice(0, 10)
            }
        })
    })

    onMount(() => {
        let interval = setInterval(() => {
            if (running) {
                endTime = undefined
                startTime = new Date()
                networkKv.put(key, true)
            }
        }, 1000)

        return () => clearInterval(interval)
    })
</script>

<div>
    <label>
        <input type="checkbox" bind:checked={running} />
        Measure Latency
    </label>
    <div class="times">
        {#if running}
            {#each times as time}
                <p style="margin: 0">{time}ms</p>
            {/each}
        {/if}
    </div>
</div>

<style>
    .times {
        height: 10vh;
    }
</style>
