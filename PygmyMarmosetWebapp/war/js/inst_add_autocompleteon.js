document.addEventListener("DOMContentLoaded", function() {
    const contextPath = document.body.dataset.contextPath;
    const courseId = document.body.dataset.courseId;

    pm.autocompleteOn(
        "#inst-username",
        contextPath + "/i/suggestUsernames/" + courseId
    );
});