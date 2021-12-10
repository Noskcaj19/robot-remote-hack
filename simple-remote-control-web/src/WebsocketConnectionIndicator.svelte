<script lang="ts">
    import BooleanIndicator from "./BooleanIndicator.svelte"
    import { onMount } from "svelte"
    import NetworkKV from "./networkkv"

    let connected = false
    let networkKv = NetworkKV.getDefault()

    onMount(() =>
        networkKv.addConnectionListener((newStatus) => (connected = newStatus)),
    )
</script>

<BooleanIndicator
    enabledText="Websocket Connected"
    disabledText="Websocket Disconnected"
    value={connected}
/>
