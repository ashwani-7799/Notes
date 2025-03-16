function checkLoginStatus() {
    fetch("LoginServlet", { method: "GET" })
        .then(response => response.json())
        .then(data => {
            if (data.status === "success") {
                document.getElementById("content").style.display = "block";
                loadNoteForEdit();
            } else {
                console.error("User not logged in, redirecting...");
                window.location.href = "index.html";
            }
        })
        .catch(error => {
            console.error("Error checking login status:", error);
            window.location.href = "index.html";
        });
}

function loadNoteForEdit() {
    const urlParams = new URLSearchParams(window.location.search);
    const noteId = urlParams.get("id");
    if (noteId) {
        fetch(`GetSingleNoteServlet?id=${noteId}`)
            .then(response => response.json())
            .then(data => {
                if (data.status === "success") {
                    document.getElementById("noteTitle").value = data.title;
                    document.getElementById("noteDescription").value = data.description;
                } else {
                    console.error("Failed to load note details:", data.message);
                }
            })
            .catch(error => console.error("Error loading note for edit:", error));
    }
}

function discardNote() {
    document.getElementById("noteTitle").value = "";
    document.getElementById("noteDescription").value = "";
    window.location.href = "dashboard.html";
}

function saveNote() {
    let title = document.getElementById("noteTitle").value.trim();
    let description = document.getElementById("noteDescription").value.trim();
    let container = document.getElementById("content");

    document.querySelectorAll(".error-msg").forEach(error => error.remove());

    let hasError = false;

    if (title === "") {
        let error = document.createElement("p");
        error.innerText = "Title cannot be empty!";
        error.classList.add("error-msg");
        error.style.color = "red";
        error.style.fontSize = "14px";
        document.getElementById("noteTitle").after(error);
        hasError = true;
    }

    if (description === "") {
        let error = document.createElement("p");
        error.innerText = "Description cannot be empty!";
        error.classList.add("error-msg");
        error.style.color = "red";
        error.style.fontSize = "14px";
        document.getElementById("noteDescription").after(error);
        hasError = true;
    }

    if (hasError) {
        container.style.height = "590px";
        return;
    } else {
        container.style.height = "500px";
    }

    const urlParams = new URLSearchParams(window.location.search);
    const noteId = urlParams.get("id");
    const servletUrl = noteId ? "EditNoteServlet" : "AddNoteServlet";

    fetch(servletUrl, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ title, description, id: noteId ? noteId : undefined })
    })
    .then(response => response.json())
    .then(data => {
        if (data.status === "success") {
            alert(noteId ? "Note updated successfully!" : "Note added successfully!");
            window.location.href = "dashboard.html";
        } else {
            alert("Error: " + data.message);
        }
    })
    .catch(error => console.error("Error saving note:", error));
}

document.addEventListener("DOMContentLoaded", checkLoginStatus);
