<script lang="ts">
    // import { NetworkTables } from "./networktables/networktables.js"
    import { onMount } from "svelte"
    import BooleanIndicator from "./BooleanIndicator.svelte"
    import NetworkKV from "./networkkv"

    export let name: string
    export let key: string
    export let binding: string

    let networkKv = NetworkKV.getDefault()
    let active: boolean

    onMount(() => {
        return networkKv.addListener(
            key,
            (_, value) => {
                active = value
            },
            true,
        )
    })

    function set(newState: boolean) {
        active = newState
        networkKv.put(key, active)
    }

    function onKeydown(event: KeyboardEvent) {
        if (event.key.toLowerCase() === binding) {
            set(true)
        }
    }

    function onKeyup(event: KeyboardEvent) {
        if (event.key.toLowerCase() === binding) {
            set(false)
        }
    }
</script>

<svelte:window on:keypress={onKeydown} on:keyup={onKeyup} />

<div on:click={() => set(!active)}>
    <BooleanIndicator value={active}>
        <p>{name}</p>
    </BooleanIndicator>
</div>

<style>
    div {
        border: 1px solid black;
        user-select: none;
    }

    p {
        margin: 5px;
    }
</style>
