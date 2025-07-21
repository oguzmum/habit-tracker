function toggleHabitDone(element) {
  const habitId = element.getAttribute("data-id");

  console.log("Sending habit ID:", habitId);

  fetch("/habits/done", {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded"
    },
    body: "habitId=" + encodeURIComponent(habitId)
  })
  .then(response => {
    if (!response.ok) {
      throw new Error("Server returned " + response.status);
    }
    console.log("Habit updated");
  })
  .catch(error => {
    console.error("Error during fetch:", error);
  });
}