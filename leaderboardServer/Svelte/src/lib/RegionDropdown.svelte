<script>

    import Dropdown from "./Dropdown.svelte";
    export let menuOpen;
    export let showOpen;
    export let showClosed;

    let regionItems = ["EU", "NA", "AS", "SA"];

    $: console.log(menuOpen)
</script>


<style>

    .dropdown :global(.dropdown-button)  {
        font-size: 1rem;
        font-weight: 900;
        color: white;
        height: 2rem;
        width: 4rem;
        border: none;
        padding: 5px;
        background: transparent;
        transition: ease all 300ms;
        border-radius: 0.25rem;
    }
    .dropdown:hover {
        cursor: pointer;
        background-color: #2f2f40;
        color: #77B4E7FF;
    }

    .dropdown-container {
        position: relative;
    }
    .dropdown-content {
        transition: ease all 500ms;
        position: absolute;
        display: none;
        color: black ;
        background-color: #f6f6f6;
        min-width: 230px;
        border: 1px solid #ddd;
        z-index: 1;
        top: 100%;
        & button {
            color: black;
            padding: 12px 16px;
            text-decoration: none;
            display: block;
            width: 20rem;
            height: 3rem;
        }
    }

    .show {display:block;}

</style>

<div class="dropdown">
    <Dropdown class="dropdown-button" on:click={() => menuOpen = !menuOpen}
              menuOpen={menuOpen}
              showOpen={showOpen}
              showClosed={showClosed}
    />
    <div class="dropdown-container">
        <div class:show={menuOpen} class="dropdown-content">
            {#each regionItems as item}
                <button id="button">{item}</button>

                <!---<Link link={item} on:click={handleLeaderboardChange}/>--->
            {/each}
        </div>
    </div>
</div>