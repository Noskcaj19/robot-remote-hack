<script lang="ts">
    import Axis from "./Axis.svelte"
    import Button from "./Button.svelte"
    import LatencyChecker from "./LatencyChecker.svelte"
    import { onMount } from "svelte"
    import WebsocketConnectionIndicator from "./WebsocketConnectionIndicator.svelte"
    import NetworkKV from "./networkkv.js"

    let networkKv = NetworkKV.getDefault()

    let buttons: [string, string, string][] = [
        ["X", "/RemoteXbox.0/Button.kX", "a"],
        ["Y", "/RemoteXbox.0/Button.kY", "s"],
        ["A", "/RemoteXbox.0/Button.kA", "d"],
        ["B", "/RemoteXbox.0/Button.kB", "f"],
    ]

    let interval: NodeJS.Timeout | null = null

    let gamepadConnected = false

    onMount(() => {
        let listener = () => {
            gamepadConnected = true
            interval = setInterval(() => {
                console.log(navigator.getGamepads())
                let gamepad = navigator.getGamepads()[0]!
                const [x1, y1, x2, y2] = gamepad.axes
                networkKv.put("/RemoteXbox.0/Axis.kRightX", x1)
                networkKv.put("/RemoteXbox.0/Axis.kRightY", -y1)
                networkKv.put("/RemoteXbox.0/Axis.kLeftX", x2)
                networkKv.put("/RemoteXbox.0/Axis.kLeftY", -y2)

                let [a, b, x, y] = gamepad.buttons.map((b) => b.pressed)

                networkKv.put("/RemoteXbox.0/Button.kX", x)
                networkKv.put("/RemoteXbox.0/Button.kY", y)
                networkKv.put("/RemoteXbox.0/Button.kA", a)
                networkKv.put("/RemoteXbox.0/Button.kB", b)
            }, 20)
        }
        window.addEventListener("gamepadconnected", listener)
        return () => window.removeEventListener("gamepadconnected", listener)
    })

    onMount(() => {
        let listener = () => {
            gamepadConnected = false
            clearInterval(interval!)
        }
        window.addEventListener("gamepaddisconnected", listener)
        return () => window.removeEventListener("gamepaddisconnected", listener)
    })
</script>

<div class="App">
    <div style="display: inline-block">
        <WebsocketConnectionIndicator />
    </div>
    <div />
    <div style="width: 250px; height: 250px;  display: inline-block">
        <Axis
            xKey="/RemoteXbox.0/Axis.kLeftX"
            yKey="/RemoteXbox.0/Axis.kLeftY"
        />
    </div>
    <div style="width: 250px; height: 250px;  display: inline-block">
        <Axis
            xKey="/RemoteXbox.0/Axis.kRightX"
            yKey="/RemoteXbox.0/Axis.kRightY"
        />
    </div>
    <div />
    {#each buttons as [name, key, binding]}
        <div style="display:inline-block">
            <Button {name} {key} {binding} />
        </div>
    {/each}
    <LatencyChecker />
</div>

<style>
    :global(body) {
        margin: 0;
        font-family: Arial, Helvetica, sans-serif;
    }
</style>
