<script lang="ts">
    import { onMount } from "svelte"
    import { clamp, scale } from "./math"
    import NetworkKV from "./networkkv"

    export let xKey: string
    export let yKey: string

    let networkKv = NetworkKV.getDefault()

    let offsetCoords: [number, number] | null = null
    let mouseCoords: [number, number] | null = null
    let serverCoords: [number, number] = [0, 0]
    let width: number
    let height: number

    let element: HTMLElement

    onMount(() => {
        return networkKv.addListener(
            xKey + "~",
            (_key, value) => {
                serverCoords[0] = scale(-value, -1, 1, 0, width) - 3
            },
            true,
        )
    })
    onMount(() => {
        return networkKv.addListener(
            yKey + "~",
            (_key, value) => {
                serverCoords[1] = height + -scale(-value, -1, 1, 0, height) - 3
            },
            true,
        )
    })

    function handleMouseDown(event: MouseEvent) {
        offsetCoords = [event.offsetX, event.offsetY]
        mouseMoveCb(event.offsetX, event.offsetY)
    }

    function handleMouseUp() {
        offsetCoords = null
        mouseCoords = null
        networkKv.put(xKey, 0)
        networkKv.put(yKey, 0)
    }

    handleMouseUp()

    function handleMouseMove(event: MouseEvent) {
        mouseMoveCb(
            event.clientX - element.offsetLeft,
            event.clientY - element.offsetTop,
        )
        if (mouseCoords !== null) {
            event.preventDefault()
        }
    }

    function mouseMoveCb(offsetX: number, offsetY: number) {
        if (offsetCoords === null) {
            return
        }

        mouseCoords = [offsetX, offsetY]

        const x = clamp(
            scale(
                offsetX - offsetCoords[0],
                0 - offsetCoords[0],
                width - offsetCoords[0],
                -1 - scale(offsetCoords[0] / width, 0, 1, -1, 1),
                1 - scale(offsetCoords[0] / width, 0, 1, -1, 1),
            ),
            -1,
            1,
        )

        const y = -clamp(
            scale(
                offsetY - offsetCoords[1],
                0 - offsetCoords[1],
                height - offsetCoords[1],
                -1 - scale(offsetCoords[1] / height, 0, 1, -1, 1),
                1 - scale(offsetCoords[1] / height, 0, 1, -1, 1),
            ),
            -1,
            1,
        )

        networkKv.put(xKey, -x)
        networkKv.put(yKey, -y)
    }
</script>

<svelte:window on:mousemove={handleMouseMove} on:mouseup={handleMouseUp} />

<div
    class="axis-container"
    bind:clientWidth={width}
    bind:clientHeight={height}
    bind:this={element}
>
    <div class="axis" on:mousedown={handleMouseDown} />
    {#if offsetCoords !== null}
        <div class="hline" style="top: {offsetCoords[1]}px" />
        <div class="vline" style="left: {offsetCoords[0]}px" />
    {/if}
    {#if mouseCoords !== null}
        <div
            class="client-indicator"
            style="left: {mouseCoords[0] - 4}px; top: {mouseCoords[1] - 4}px"
        />
    {/if}
    <div
        class="server-indicator"
        style="left: {serverCoords[0] - 3}px; top: {serverCoords[1] - 3}px"
    />
</div>

<style>
    .axis-container {
        width: 100%;
        height: 100%;
        position: relative;
    }

    .axis {
        cursor: crosshair;
        position: absolute;
        border: 1px solid darkgray;
        width: 100%;
        height: 100%;
    }

    .hline {
        border: 1px solid gray;
        position: absolute;
        top: 50%;
        width: 100%;
        pointer-events: none;
    }

    .vline {
        border: 1px solid gray;
        position: absolute;
        left: 50%;
        height: 100%;
        pointer-events: none;
    }

    .server-indicator {
        border: 1px solid green;
        position: absolute;
        width: 6px;
        height: 6px;
        pointer-events: none;
    }

    .client-indicator {
        border: 1px solid red;
        position: absolute;
        width: 8px;
        height: 8px;
        pointer-events: none;
    }
</style>
