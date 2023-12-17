<script>
    export let menuOpen;
    export let showOpen;
    export let showClosed;
</script>

<style>
    /* Dropdown Button */
    .default {

        color: black;
    }

    svg {
        position: absolute;
        right: 1rem;
    }

</style>
<div >
<button class={$$props.class} on:click>{!menuOpen ? showOpen : showClosed}
    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="4" stroke-linecap="round" stroke-linejoin="arcs"><line x1="3" y1="12" x2="21" y2="12"></line><line x1="3" y1="6" x2="21" y2="6"></line><line x1="3" y1="18" x2="21" y2="18"></line></svg>
</button>
</div>


