<script>
    export let menuOpen;
    export let showOpen;
    export let showClosed;
</script>

<style>
    /* Dropdown Button */
    .dropbtn {
        display: flex;
        justify-content: center;
        align-items: center;
        transition: ease all 500ms;
        background-color: transparent;
        border: 0.25rem solid #5989b1;
        border-radius: 0.25rem;
        color: #5989b1;
        padding: 16px;
        font-size: 16px;
        cursor: pointer;
        width: 20rem;
        height: 2rem;
    }

    /* Dropdown button on hover & focus */
    .dropbtn:hover, .dropbtn:focus {
        background-color: rgba(89, 137, 177, 0.25);
        border: 0.25rem solid #77b4e7;
        color: #77b4e7;
    }

</style>

<button on:click class="dropbtn">{!menuOpen ? showOpen : showClosed}</button>



