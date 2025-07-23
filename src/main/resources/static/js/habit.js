//for the daily overview page
function toggleHabitDone(element) {
    const habitId = element.getAttribute("data-id");
    const completed = element.checked;
    const body = `habitId=${encodeURIComponent(habitId)}` +
                `&completed=${encodeURIComponent(completed)}`;

    console.log("Sending habit ID: ", habitId, " checked status: ", completed);

    fetch("/habits/toggle-day", {
        method: "POST",
        headers: {"Content-Type": "application/x-www-form-urlencoded"},
        body
    })
    .then(response => {
        if (!response.ok) {
          throw new Error("Server returned " + response.status);
        }
        console.log(`Habit ${habitId} now ${completed ? "DONE" : "UNDONE"}`);
    })
    .catch(error => {
        console.error("Error during fetch:", error);
    });
}


//for the weekly overview page
function toggleHabitDoneAtDate(element) {
    const habitId = element.getAttribute("data-id");
    const date = element.getAttribute("data-date");
    const completed  = element.checked;
    const body = `habitId=${encodeURIComponent(habitId)}` +
                `&date=${encodeURIComponent(date)}` +
                `&completed=${encodeURIComponent(completed)}`;

    console.log("Updating habit:", habitId, "at", date);

    fetch("/habits/toggle-week", {
        method: "POST",
        headers: {"Content-Type": "application/x-www-form-urlencoded"},
        body
    })
    .then(response => {
        if (!response.ok) {
          throw new Error("Server error");
        }
        console.log(`Habit ${habitId} now ${completed ? "DONE" : "UNDONE"} foor day ${date}`);
    })
    .catch(err => console.error("Error:", err));
}
