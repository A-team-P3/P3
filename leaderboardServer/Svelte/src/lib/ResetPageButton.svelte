<script>
    export let scrollTop;
    let show;

    $: show = scrollTop < 400;
</script>

<style>
    /* Dropdown Button */
    .button {
        display: flex;
        align-items: center;
        justify-content: center;
        position: absolute;
        bottom: 5%;
        right: 10%;
        transform: translateY(-50%);
        transition: ease all 500ms;
        background-color: transparent;
        border: 0.25rem solid #5989b1;
        border-radius: 300px;
        color: #5989b1;
        padding: 16px;
        font-size: 16px;
        cursor: pointer;
        width: 3rem;
        height: 3rem;
    }
    svg{
        transform: scale(350%);
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
    <svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="bevel"><path d="M12 19V6M5 12l7-7 7 7"/></svg>
</button>