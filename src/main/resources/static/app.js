const API_URL = "/api/candidates";
let currentUsername = "Unknown"; // Default if fetch fails

fetch('/api/username')
  .then(response => response.json())
  .then(data => {
    currentUsername = data.username; // ✅ store it globally
    document.getElementById('username').textContent = currentUsername;
  })
  .catch(error => {
	
    console.error('Error fetching username:', error);
  });
  
  function validateForm() {
      let isValid = true;

      // Clear previous error messages
      document.querySelectorAll('.error-message').forEach(element => element.remove());

      const name = document.getElementById("name").value.trim();
      const email = document.getElementById("email").value.trim();
      const phone = document.getElementById("phone").value.trim();

      // Name validation
      if (name === "") {
          isValid = false;
          displayError("name", "Name is required.");
      }

      // Email validation
      if (email === "") {
          isValid = false;
          displayError("email", "Email is required.");
      }

      // Phone validation
      if (phone === "") {
          isValid = false;
          displayError("phone", "Phone number is required.");
      }

      return isValid;
  }

  function displayError(fieldId, message) {
      const field = document.getElementById(fieldId);
      const errorMessage = document.createElement("span");
      errorMessage.classList.add("error-message");
      errorMessage.style.color = "red";
      errorMessage.textContent = message;

      // Append the error message next to the input field
      field.parentElement.appendChild(errorMessage);
  }


// Function to add a new candidate
function addCandidate() {
    // Show loading spinner when the request is in progress
    showLoading();

    // Validate the form before submitting
    if (!validateForm()) {
        hideLoading();  // Stop the spinner if validation fails
        return; // Exit if validation fails
    }

    const formData = new FormData();

    formData.append("name", document.getElementById("name").value);
    formData.append("email", document.getElementById("email").value);
    formData.append("phone", document.getElementById("phone").value);
    formData.append("jobRole", document.getElementById("jobRole").value);
    formData.append("status", document.getElementById("status").value);
    formData.append("interviewDate", document.getElementById("interviewDate").value);
    formData.append("notes", document.getElementById("notes").value);

    const profilePicInput = document.getElementById("profilePicContainer");
    if (profilePicInput.files.length > 0) {
        formData.append("profilePic", profilePicInput.files[0]);
    }

    const resumeInput = document.getElementById("resumeLink");
    if (resumeInput.files.length > 0) {
        formData.append("resume", resumeInput.files[0]);
    }

    fetch("http://localhost:8080/api/candidates/add", {
        method: "POST",
        body: formData,
    })
    .then(response => {
        if (!response.ok) {
            throw new Error("Failed to add candidate");
        }
        return response.json();
    })
    .then(data => {
        console.log("Candidate added successfully", data);
		showToast("Candidate Added Successfully!", "success");  // Success toast
        hideLoading(); // Hide the spinner after success
    })
    .catch(error => {
        console.error("Error adding candidate:", error);
		showToast("Failed to add candidate. Please try again.", "error");  // Error toast
        hideLoading(); // Hide the spinner in case of error
    });
}

// Function to load candidates
function loadCandidates() {
	showLoading();

  fetch("/api/candidates")
    .then(res => res.json())
    .then(data => {
      const table = document.querySelector("#candidateTable tbody");
      table.innerHTML = "";

      data.forEach(candidate => {
        const row = table.insertRow();
        const profilePicContainerId = `profilePicContainer${candidate.id}`;

        // Status dropdown
        const statusOptions = ["Applied", "Interview Scheduled", "Selected", "Rejected"];
        const statusSelectHTML = `
          <select onchange="updateStatus(${candidate.id}, this.value)">
            ${statusOptions.map(status => `
              <option value="${status}" ${status === candidate.status ? "selected" : ""}>${status}</option>
            `).join("")}
          </select>
        `;

        row.innerHTML = `
          <td><div id="${profilePicContainerId}"></div></td>
          <td>${candidate.id}</td>
          <td>${candidate.name}</td>
          <td>${candidate.email}</td>
          <td>${statusSelectHTML}</td>
          <td>
            <button onclick="deleteCandidate(${candidate.id})">Delete</button>
            <button onclick="openUpdateModal(${candidate.id})">Update</button>
            <button onclick="downloadResume(${candidate.id})">View Resume</button>
          </td>
        `;
		hideLoading();

        showProfilePic(candidate.id, document.getElementById(profilePicContainerId));
      });
    })
    .catch(error => {
      console.error("Error loading candidates:", error);
	  hideLoading();
    });
}

function deleteCandidate(id) {
    // Show the loading spinner before starting the delete operation
    showLoading();

    fetch(`${"/api/candidates"}/${id}`, { method: "DELETE" })
        .then(() => {
            // Hide the loading spinner after the delete operation is complete
            hideLoading();

            // Show success message
			showToast("Candidate deleted successfully!", "success");  // Success toast
        })
        .catch((error) => {
            // Hide the loading spinner in case of error
            hideLoading();

            console.error("Error deleting candidate:", error);

            // Show failure message
			showToast("Failed to delete candidate. Please try again.", "error");  // Error toast

        });
}


// Function to update the status of a candidate
function updateStatus(id, newStatus) {
  fetch(`/api/candidates/${id}/status`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ status: newStatus })
  })
    .then(res => {
      if (!res.ok) throw new Error("Status update failed");
      return res.json();
    })
    .then(() => {
      console.log("Status updated for ID:", id);

      // ✅ After status update, add a log automatically
      addLogAuto(id, "Status Update", `Status updated to "${newStatus}" by ${currentUsername}`);
    })
    .catch(err => {
      alert("Error updating status: " + err.message);
    });
}


function addLogAuto(candidateId, action, note) {
  fetch("/api/candidate-log", {
    method: "POST",
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      candidate: { id: candidateId },
      action: action,
      note: note
    })
  })
  .then(() => console.log("Auto log added"))
  .catch(err => console.error("Error auto-adding log:", err));
}


// Function to download the resume of a candidate
function downloadResume(id) {
  fetch(`/api/candidates/${id}/resume`)
    .then(response => {
      if (!response.ok) throw new Error("Failed to fetch resume.");
      return response.blob(); // Convert the response to a Blob
    })
    .then(blob => {
      const url = window.URL.createObjectURL(blob); // Create an object URL for the Blob
      const link = document.createElement("a");    // Create an anchor tag dynamically
      link.href = url;
      link.download = "resume.pdf"; // Set default file name for the download
      document.body.appendChild(link); // Append the anchor tag to the DOM
      link.click(); // Programmatically click the link to start the download
      document.body.removeChild(link); // Remove the link after download
    })
    .catch(error => {
      console.error("Error fetching resume:", error);
    });
}

// Function to automatically show profile picture for each candidate during page load
function showProfilePic(candidateId, container) {
  fetch(`/api/candidates/${candidateId}/profilePic`)
    .then(res => {
      if (!res.ok) {
        throw new Error("Profile picture not found.");
      }
      return res.blob();  // Convert the response to a Blob
    })
    .then(imageBlob => {
      const imageUrl = URL.createObjectURL(imageBlob);
      const img = document.createElement("img");
      img.src = imageUrl;
      img.alt = "Profile Picture";
      img.style.width = "100px";
      img.style.height = "100px";
      
      // Add a click event to open the modal with full-size image
      img.addEventListener("click", () => openModal(imageUrl));

      container.innerHTML = '';  // Clear any previous content
      container.appendChild(img);
    })
    .catch(error => {
      console.error("Error fetching profile picture:", error);
      container.innerHTML = "<p>No profile picture available</p>";
    });
}

// Open the modal with the full-size image
function openModal(imageUrl) {
  const modal = document.getElementById("modal");
  const fullSizeImage = document.getElementById("fullSizeImage");
  const caption = document.getElementById("caption");

  // Set the source of the image in the modal
  fullSizeImage.src = imageUrl;
  caption.textContent = "Profile Picture";  // Optional caption

  modal.style.display = "block";  // Show the modal
}
function logout() {
    // Example simple logout logic
    localStorage.removeItem('username'); // remove saved username if you stored
    window.location.href = "login.html"; // redirect to login page
}

// Close the modal
function closeModal() {
  const modal = document.getElementById("modal");
  modal.style.display = "none";
}

// Close the modal when the user clicks the close button or outside the image
document.getElementById("closeModal").addEventListener("click", closeModal);

// When the user clicks anywhere outside the image, close the modal
window.onclick = function(event) {
  const modal = document.getElementById("modal");
  if (event.target === modal) {
    closeModal();
  }
};

// Opens the modal and loads logs
function openUpdateModal(candidateId) {
  loadLogs(candidateId);

  document.getElementById("updateModal").style.display = "block";
  document.getElementById("currentCandidateId").value = candidateId;
}

// Loads logs for a candidate
function loadLogs(candidateId) {
  fetch(`/api/candidate-log/${candidateId}`)
    .then(response => response.json())
    .then(logs => {
      const logList = document.getElementById("log-list");
      logList.innerHTML = ""; // Clear previous logs

      logs.forEach(log => {
        const li = document.createElement("li");
		let formattedTime = new Date(log.timestamp).toLocaleString();

        li.textContent = `[${currentUsername}]  [${formattedTime}] ${log.action} - ${log.note}`;
        logList.appendChild(li);
      });
    });
}

// Adds a new log
function addLog() {
  const candidateId = document.getElementById("currentCandidateId").value;
  const note = document.getElementById("log-note").value;
  const action = document.getElementById("log-action").value;

  fetch("/api/candidate-log", {
    method: "POST",
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      candidate: { id: candidateId }, // ✅ Nested candidate object
      note,
      action
    })
  }).then(() => {
    loadLogs(candidateId); // reload logs
    document.getElementById("log-note").value = ""; // reset
  });
}
function closeUpdateModal() {
  document.getElementById("updateModal").style.display = "none";
}

function showToast(message, type) {
    const toastContainer = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.classList.add('toast');

    if (type === 'success') {
        toast.style.backgroundColor = 'green';
    } else {
        toast.style.backgroundColor = 'red';
    }

    toast.innerText = message;
    toastContainer.appendChild(toast);

    // Remove toast after 3 seconds
    setTimeout(() => {	
        toast.remove();
		location.reload();  
    }, 3000);
}

function showLoading() {
    document.getElementById('loadingSpinner').style.display = 'block';
}

function hideLoading() {
    document.getElementById('loadingSpinner').style.display = 'none';
}

window.onload = function() {
    // Show the loading spinner when the page starts loading
    document.getElementById("loadingSpinner").style.display = "block";

    // Set a delay before fully loading the page (simulating a longer load time)
    setTimeout(function() {	
		loadCandidates();

        // Hide the spinner after the delay (for example, after 3 seconds)
        document.getElementById("loadingSpinner").style.display = "none";
    }, 500); // Adjust the delay time (3000ms = 3 seconds)
};
// Load candidates on page load and show profile pictures

