//as I have a security context now I have to extract the token .. to send with the request in order for them to actually work
function csrfHeaders() {
  const token  = document.querySelector('meta[name="_csrf"]')?.content;
  const header = document.querySelector('meta[name="_csrf_header"]')?.content || 'X-CSRF-TOKEN';
  return token ? { [header]: token } : {};
}

//for the daily overview page
function toggleHabitDone(element) {
  const habitId  = element.getAttribute("data-id");
  const completed = element.checked;
  const body = `habitId=${encodeURIComponent(habitId)}&completed=${encodeURIComponent(completed)}`;

  fetch("/habits/toggle-day", {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
      ...csrfHeaders()
    },
    body,
    credentials: "same-origin"
  })
  .then(r => { if (!r.ok) throw new Error("HTTP " + r.status); })
  .catch(err => console.error(err));
}

//for the weekly overview page
function toggleHabitDoneAtDate(element) {
  const habitId  = element.getAttribute("data-id");
  const date     = element.getAttribute("data-date");
  const completed = element.checked;
  const body = `habitId=${encodeURIComponent(habitId)}&date=${encodeURIComponent(date)}&completed=${encodeURIComponent(completed)}`;

  fetch("/habits/toggle-week", {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
      ...csrfHeaders()
    },
    body,
    credentials: "same-origin"
  })
  .then(r => { if (!r.ok) throw new Error("HTTP " + r.status); })
  .catch(err => console.error(err));
}
