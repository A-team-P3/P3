<script>
    export let link;
</script>

<style>
    a {
        color: black;
        padding: 12px 16px;
        text-decoration: none;
        display: block;
        width: 20rem;
    }

    a:hover {background-color: #ddd}
</style>


<a href="#{link.toLowerCase()}">{link}</a>