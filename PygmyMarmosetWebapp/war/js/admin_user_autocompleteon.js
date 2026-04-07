document.addEventListener("DOMContentLoaded", function() {
    const contextPath = document.body.dataset.contextPath;

    // Clear the field after Safari autofill has a chance to run
    setTimeout(function() {
        const field = document.getElementById("username_field");
        field.value = "";
        field.focus();

        pm.autocompleteOn(
            "#username_field",
            contextPath + "/a/suggestUsernames"
        );
    }, 50);
});