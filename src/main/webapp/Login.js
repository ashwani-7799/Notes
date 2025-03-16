document.addEventListener("DOMContentLoaded", () => {
    const form = document.querySelector("#login-form");
    if (!form) {
        console.error("Login form not found!");
        return;
    }

    const messageDiv = document.createElement("div");
    messageDiv.style.display = "none";
    messageDiv.style.marginTop = "10px";
    messageDiv.style.fontWeight = "bold";
    form.appendChild(messageDiv);

    form.addEventListener("submit", (event) => {
        event.preventDefault(); 

        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value.trim();

        if (!email || !password) {
            showMessage("Email and password are required!", "red");
            return;
        }

        // Send form data instead of JSON
        const formData = new URLSearchParams();
        formData.append("email", email);
        formData.append("password", password);

        fetch("LoginServlet", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            if (data.status === "success") {
                showMessage("Login successful! Redirecting...", "green");
                sessionStorage.setItem("user_id", data.user_id);
                sessionStorage.setItem("email", data.email);
                setTimeout(() => window.location.href = "dashboard.html", 1000);
            } else {
                showMessage(`Error: ${data.message}`, "red");
            }
        })
        .catch(error => {
            console.error("Error:", error);
            showMessage("Network error. Please try again later.", "red");
        });
    });

    function showMessage(text, color) {
        messageDiv.innerText = text;
        messageDiv.style.color = color;
        messageDiv.style.display = "block";
    }
});
