//for the daily overview page
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


//for the weekly overview page
function toggleHabitDoneAtDate(element) {
  const habitId = element.getAttribute("data-id");
  const date = element.getAttribute("data-date");

  console.log("Updating habit:", habitId, "at", date);

  fetch("/habits/done-week", {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded"
    },
    body: "habitId=" + encodeURIComponent(habitId) +
          "&date=" + encodeURIComponent(date)
  })
  .then(response => {
    if (!response.ok) {
      throw new Error("Server error");
    }
    console.log("Updated entry");
  })
  .catch(err => console.error("Error:", err));
}
