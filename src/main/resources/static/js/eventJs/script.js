document.addEventListener('DOMContentLoaded', function () {
    // Pagination
    const cards = document.querySelectorAll('.event-card');
    const rowsPerPage = 6;
    const totalRows = cards.length;
    const totalPages = Math.ceil(totalRows / rowsPerPage);
    let currentPage = 1;

    function showPage(page) {
        cards.forEach(card => card.style.display = 'none');
        const start = (page - 1) * rowsPerPage;
        const end = Math.min(start + rowsPerPage, totalRows);
        for (let i = start; i < end; i++) {
            cards[i].style.display = 'block';
        }
        updatePagination(page);
    }

    function updatePagination(currentPage) {
        const pagination = document.getElementById('pagination');
        if (!pagination) return;
        pagination.innerHTML = '';

        const prevLi = document.createElement('li');
        prevLi.className = `page-item ${currentPage === 1 ? 'disabled' : ''}`;
        prevLi.innerHTML = `<a class="page-link" href="#" onclick="changePage(${currentPage - 1})">Previous</a>`;
        pagination.appendChild(prevLi);

        for (let i = 1; i <= totalPages; i++) {
            const li = document.createElement('li');
            li.className = `page-item ${i === currentPage ? 'active' : ''}`;
            li.innerHTML = `<a class="page-link" href="#" onclick="changePage(${i})">${i}</a>`;
            pagination.appendChild(li);
        }

        const nextLi = document.createElement('li');
        nextLi.className = `page-item ${currentPage === totalPages ? 'disabled' : ''}`;
        nextLi.innerHTML = `<a class="page-link" href="#" onclick="changePage(${currentPage + 1})">Next</a>`;
        pagination.appendChild(nextLi);
    }

    window.changePage = function (page) {
        if (page >= 1 && page <= totalPages) {
            currentPage = page;
            showPage(page);
        }
    };

    if (totalRows > 0) {
        showPage(1);
    }

    // Delete Button
    document.querySelectorAll('.delete-btn').forEach(button => {
        button.addEventListener('click', function () {
            const eventId = this.getAttribute('data-event-id');
            document.getElementById('deleteEventId').value = eventId;
        });
    });

    // Edit Button
    document.querySelectorAll('.edit-btn').forEach(button => {
        button.addEventListener('click', function () {
            const eventId = this.getAttribute('data-event-id');
            fetch(`/events/${eventId}`)
                .then(response => response.json())
                .then(event => {
                    document.getElementById('editEventId').value = event.eventId || '';
                    document.getElementById('editEventName').value = event.eventName || '';
                    document.getElementById('editSummary').value = event.summary || '';
                    document.getElementById('editEventLocation').value = event.eventLocation || '';
                    document.getElementById('editEventDescription').value = event.eventDescription || '';
                    document.getElementById('editBeginDate').value = event.beginDate || '';
                    document.getElementById('editFinishDate').value = event.finishDate || '';
                    document.getElementById('editEventImage').src = event.eventImage || '';
                    document.getElementById('editEventImage').style.display = event.eventImage ? 'block' : 'none';
                    document.getElementById('editExistingImage').value = event.eventImage || '';
                })
                .catch(error => console.error('Error fetching event:', error));
        });
    });
});