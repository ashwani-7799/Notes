document.addEventListener("DOMContentLoaded", () => {
    const form = document.querySelector('#register-form'); // Select form

    form.addEventListener('submit', (event) => {
        event.preventDefault(); // Prevent default form submission

        const formData = new FormData(form); // Create FormData object

        fetch("RegisterServlet", {
            method: "POST",
            body: formData // Send FormData directly
        })
        .then(response => response.json()) // Parse JSON response
        .then(data => {
            alert(data.message); // Show success/error message
            if (data.status === "success") {
                form.reset(); // Reset form after successful submission
                toggleForm(); // Switch to login form (if applicable)
            }
        })
        .catch(error => console.error('Error:', error)); // Handle errors
    });
});
