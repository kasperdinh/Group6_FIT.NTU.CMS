document.addEventListener('DOMContentLoaded', function () {
    // Phân trang
    const table = document.getElementById('eventTable');
    const rows = table.querySelectorAll('.event-row');
    const rowsPerPage = 7;
    const totalRows = rows.length;
    const totalPages = Math.ceil(totalRows / rowsPerPage);
    let currentPage = 1;

    function showPage(page) {
        rows.forEach(row => row.style.display = 'none');
        const start = (page - 1) * rowsPerPage;
        const end = Math.min(start + rowsPerPage, totalRows);
        for (let i = start; i < end; i++) {
            rows[i].style.display = '';
        }
        updatePagination(page);
    }

    function updatePagination(currentPage) {
        const pagination = document.getElementById('pagination');
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

    // Xử lý sự kiện Delete
    document.querySelectorAll('.delete-btn').forEach(button => {
        button.addEventListener('click', function () {
            const eventId = this.getAttribute('data-event-id');
            document.getElementById('deleteEventId').value = eventId;
        });
    });
    document.querySelectorAll('.edit-btn').forEach(button => {
        button.addEventListener('click', function () {
            const eventId = this.getAttribute('data-event-id');
            const row = this.closest('.event-row');
            const cells = row.querySelectorAll('td');

            // Lấy dữ liệu từ hàng
            const eventData = {
                eventId: cells[0].textContent,
                eventName: cells[1].textContent,
                eventDate: cells[2].textContent,
                eventTime: cells[3].textContent,
                eventLocation: cells[4].textContent,
                eventDescription: cells[5].textContent,
                eventImage: cells[6].querySelector('img') ? cells[6].querySelector('img').src : ''
            };

            // Điền dữ liệu vào modal chỉnh sửa
            document.getElementById('editEventId').value = eventData.eventId;
            document.getElementById('editEventName').value = eventData.eventName;
            document.getElementById('editEventDate').value = eventData.eventDate;
            document.getElementById('editEventTime').value = eventData.eventTime;
            document.getElementById('editEventLocation').value = eventData.eventLocation;
            document.getElementById('editEventDescription').value = eventData.eventDescription;
            document.getElementById('editEventImage').src = eventData.eventImage || '';
            document.getElementById('editEventImage').style.display = eventData.eventImage ? 'block' : 'none';
            document.getElementById('editExistingImage').value = eventData.eventImage || '';
        });
    });
});