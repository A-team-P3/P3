<script>
import { createEventDispatcher } from 'svelte';
import Dropdown from "./Dropdown.svelte";
    export let menuOpen;
    export let showOpen;
    export let showClosed;
    export let leaderboardItems;
    export let currentLeaderboard;
    const dispatch = createEventDispatcher();

    const handleLeaderboardChange = (item) => {
        menuOpen = false;
        let splitted = item.split(" ");
        if(currentLeaderboard === splitted[1]) {
            return;
        }

        currentLeaderboard = splitted[1];
        dispatch("callResetPage");
    }



</script>

<style>

    .leaderboardDropdownMenu  {
        position: relative;
        left: 50%;
        transform: translate(-50%);
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        transition: ease all 500ms;
        background-color: transparent;
        border: 0.25rem solid #5989b1;
        border-radius: 0.25rem;
        color: #5989b1;
        padding: 16px;
        cursor: pointer;
        width: 20rem;
        height: 2rem;
    }
    .leaderboardDropdownMenu :global(.dropdown-button) {


        justify-content: center;
        align-items: center;
        transition: ease all 500ms;
        background-color: transparent;
        border: red;
        color: #5989b1;
        font-size: 16px;
        cursor: pointer;
        width: 20rem;
        height: 2rem;
    }
    /* Dropdown button on hover & focus */
    .leaderboardDropdownMenu:hover, .leaderboardDropdownMenu:focus {
        background-color: rgba(89, 137, 177, 0.25);
        border: 0.25rem solid #77b4e7;
        color: #77b4e7;
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

/* Show the dropdown menu */
.show {display:block;}

</style>

<div class="leaderboardDropdownMenu">
    <Dropdown class="dropdown-button" on:click={() => menuOpen = !menuOpen}
            menuOpen={menuOpen}
            showOpen={showOpen}
            showClosed={showClosed}
    />
    <div id="leaderboardDropdown" class:show={menuOpen} class="dropdown-content">
        {#each leaderboardItems as item}
            <button id="button" on:click={() => handleLeaderboardChange(item)}>{item}</button>

            <!---<Link link={item} on:click={handleLeaderboardChange}/>--->
        {/each}
    </div>
</div>