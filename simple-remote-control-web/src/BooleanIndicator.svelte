<script lang="ts">
    export let text: string | undefined = undefined
    export let enabledText: string | undefined = undefined
    export let disabledText: string | undefined = undefined
    export let value: boolean
</script>

<div class:active={value}>
    <slot>
        <p>
            {text ?? (value ? enabledText ?? "True" : disabledText ?? "False")}
        </p>
    </slot>
</div>

<style>
    div.active {
        background-color: green;
    }

    div {
        display: inline-block;
        background-color: red;
    }
</style>
