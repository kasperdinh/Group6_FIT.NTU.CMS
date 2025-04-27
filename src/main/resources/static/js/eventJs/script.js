document.addEventListener('DOMContentLoaded', function () {
    // Phân trang
    const table = document.getElementById('eventTable');
    const rows = table.querySelectorAll('.event-row');
    const rowsPerPage = 15;
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
});