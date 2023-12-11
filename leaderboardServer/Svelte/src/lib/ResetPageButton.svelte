<script>
    export let input;
    export let scrollTop;
    let show;

    $: show = scrollTop < 400;
</script>

<style>
    /* Dropdown Button */
    .button {
        transform: translateY(-50px);
        position: absolute;
        bottom: 100px;
        right: 150px;
        display: block;
        align-items: center;
        justify-content: center;
        transition: ease all 500ms;
        background-color: transparent;
        border: 0.25rem solid #5989b1;
        border-radius: 0.25rem;
        color: #5989b1;
        padding: 16px;
        font-size: 16px;
        cursor: pointer;
        width: 7rem;
        height: 9rem;
    }

    /* Dropdown button on hover & focus */
    .button:hover, .button:focus {
        background-color: rgba(89, 137, 177, 0.25);
        border: 0.25rem solid #77b4e7;
        color: #77b4e7;
    }

    .show {
        opacity: 0;
        transform: none;
        pointer-events: none;
    }

</style>

<button on:click class="button" class:show={show}>
    <svg xmlns="http://www.w3.org/2000/svg" width="54" height="54" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="4" stroke-linecap="round" stroke-linejoin="arcs"><path d="M12 19V6M5 12l7-7 7 7"/></svg>
    {input}
</button>