document.addEventListener("DOMContentLoaded", loadNotes);
document.addEventListener("DOMContentLoaded", checkSession);

function loadNotes() {
    fetch("GetNotesServlet")
        .then(response => response.json())
        .then(data => {
            const notesContainer = document.getElementById("notes-container");
            notesContainer.innerHTML = ""; 

            data.notes.forEach(note => {
                let titlePreview = note.title.split(" ").slice(0, 3).join(" "); // First 3 words of title
                let formattedDate = note.date.replace(/\.0$/, '');

                let noteTile = document.createElement("div");
                noteTile.classList.add("note-tile");
                noteTile.setAttribute("data-id", note.id);
                noteTile.setAttribute("data-title", note.title);
                noteTile.setAttribute("data-description", note.description);

                noteTile.innerHTML = `
                    <p><strong>${titlePreview}...</strong></p>
                    <div class="note-divider"></div>
                    <p class="note-date">Date Added: ${formattedDate}</p>
                    <div class="note-actions">
                        <span class="edit-link" onclick="editNote(${note.id})">Edit</span>
                        <span class="delete-link" onclick="deleteNote(${note.id}, this)">Delete</span>
                    </div>
                `;
                notesContainer.appendChild(noteTile);
            });
        })
        .catch(error => console.error("Error loading notes:", error));
}

function addNewNote() {
    window.location.href = "addnote.html";
}

function logout() {
    fetch("LogoutServlet", { method: "POST" })
        .then(response => {
            if (response.ok) {
                window.location.href = "index.html";
            }
        })
        .catch(error => console.error("Logout failed:", error));
}

function editNote(id) {
    window.location.href = `addnote.html?id=${id}`;
}

function deleteNote(id, element) {
    fetch(`DeleteNoteServlet?id=${id}`, { method: "POST" })
        .then(response => response.json())
        .then(data => {
            if (data.status === "success") {
                // Remove the specific note tile from the UI
                element.closest(".note-tile").remove();
            } else {
                alert("Failed to delete note.");
            }
        })
        .catch(error => console.error("Error deleting note:", error));
}

function checkSession() {
    fetch("CheckSessionServlet")
        .then(response => response.json())
        .then(data => {
            if (data.status !== "success") {
                window.location.href = "index.html"; // Redirect to login if session is invalid
            } else {
                document.getElementById("username-nav").textContent = ` ${data.name}`;
                loadNotes(); // Load notes only if user is authenticated
            }
        })
        .catch(error => console.error("Error checking session:", error));
}
